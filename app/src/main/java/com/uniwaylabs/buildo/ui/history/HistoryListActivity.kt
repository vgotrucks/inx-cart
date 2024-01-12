package com.uniwaylabs.buildo.ui.history

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.uniwaylabs.buildo.BaseAppCompactActivity
import com.uniwaylabs.buildo.LocalDatabase.BDSharedPreferences
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.firebaseDatabase.Database.AdminDB.AdminDatabase
import com.uniwaylabs.buildo.firebaseDatabase.Database.UserDB.UserDatabase
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoriesListWithItemDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.GETOrderItemDetailsModel
import com.uniwaylabs.buildo.ui.dashboard.CartList.CartListItemAdapter
import com.uniwaylabs.buildo.ui.dashboard.CartList.CartListItemModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialListItemModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseUrls.DatabaseUrls
import com.uniwaylabs.buildo.ui.materialSpecification.MaterialSpecificationActivity
import com.uniwaylabs.buildo.ui.notifications.OrderedList.OrderedListItemModel
import java.io.Serializable
import java.util.Date

@Suppress("DEPRECATION")
class HistoryListActivity : BaseAppCompactActivity(), HistoryListItemMaterialInterface {

    private var adapter: HistoryListItemAdapter? = null
    var progressBar: LottieAnimationView? = null
    var emptyState: ImageView? = null
    var statusText: TextView? = null
    private var path: String? = null

    private val materialSpecialityActivity = registerForActivityResult(MaterialSpecificationActivity.Contract(), {})
    object HistoryListActivity{
        var ITEM_DATA_CONSTANT: String = "ITEM_DATA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_list)

        progressBar = findViewById(R.id.progressbarhistory)
        emptyState = findViewById(R.id.image_view_sad_h)
        statusText = findViewById(R.id.text_view_result_h)
        val model = getSerializable(intent, HistoryListActivity.ITEM_DATA_CONSTANT, GETOrderItemDetailsModel::class.java)
        model?.let { fetchList(it.materialListPath) }
        path = model?.materialListPath
        setMaterialRecycler()
        findViewById<ImageButton>(R.id.backbutton).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun fetchList(path: String?){

        showLoading(true, "Fetching...")
        AdminDatabase<CategoriesListWithItemDataModel>().getItemsObjectDataSnapshot(this,"$path"){
            showLoading(false, title = "")

            val mData = try {
                it.getValue(CategoriesListWithItemDataModel::class.java)
            }catch (e: Exception){ null }

            if(mData?.categories.isNullOrEmpty()){
                showEmptyState(true)
                adapter?.updateListData(emptyArray())
                return@getItemsObjectDataSnapshot
            }
            showEmptyState(false)
            adapter?.updateListData(mData?.categories?.values?.toTypedArray() ?: emptyArray())
        }
    }

    fun showLoading(show: Boolean, title: String){
        progressBar?.visibility = if (show) View.VISIBLE else View.GONE
        statusText?.text = title
        statusText?.visibility = if (show) View.VISIBLE else View.INVISIBLE
        emptyState?.visibility = View.INVISIBLE
    }

    fun showEmptyState(show: Boolean){
        statusText?.text = "OOPs! Currently, We are not at this place"
        emptyState?.visibility = if (show) View.VISIBLE else View.INVISIBLE
        statusText?.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }


    private fun setMaterialRecycler() {
        val recyclerView = findViewById<RecyclerView>(R.id.materials_recycle)
        adapter = HistoryListItemAdapter(this, emptyArray(), this)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }


    private fun <T : Serializable?> getSerializable(intent: Intent, name: String, clazz: Class<T>): T?
    {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getSerializableExtra(name, clazz)
        else
            intent.getSerializableExtra(name) as T?
    }


    class Contract: ActivityResultContract<GETOrderItemDetailsModel, String>(){

        override fun createIntent(context: Context, input: GETOrderItemDetailsModel): Intent = Intent(context, com.uniwaylabs.buildo.ui.history.HistoryListActivity::class.java)
            .putExtra(HistoryListActivity.ITEM_DATA_CONSTANT, input)

        override fun parseResult(resultCode: Int, intent: Intent?): String {
            return "Ok"
        }

    }

    override fun onClickHistoryListItem(model: MaterialListItemModel?) {
        model.let { materialSpecialityActivity.launch(it) }
    }

    override fun didTapChangeStatus(model: MaterialListItemModel?) {

        if (BDSharedPreferences.shared.getPermissionModel(this)?.isEdit != true ) return

        if(path == null)  return


        val subCategoryPath = if (model?.sub_category?.trim().isNullOrEmpty()) DatabaseUrls.material_items_path else "${DatabaseUrls.sub_categories_path}/${model?.sub_category}/${DatabaseUrls.material_items_path}"
        val itemPath = "$path/${DatabaseUrls.categories_path}/${model?.category}/$subCategoryPath/${model?.id}"

        val deliveryStatus = if (model?.deliveryStatus == 0) 1 else 0
        model?.deliveryStatus = deliveryStatus
        AdminDatabase<String>().setDataToItemDatabase(this, itemPath, model){}
    }


}