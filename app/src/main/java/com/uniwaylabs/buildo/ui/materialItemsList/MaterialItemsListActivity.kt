package com.uniwaylabs.buildo.ui.materialItemsList

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SearchView
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
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoriesListDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoryDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoryDataWithItemsModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialListItemModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseUrls.DatabaseUrls
import com.uniwaylabs.buildo.ui.home.MaterialList.MaterialListL3Adapter
import com.uniwaylabs.buildo.ui.home.MaterialList.ViewHolders.MaterialListL3ItemInterface
import com.uniwaylabs.buildo.ui.materialSpecification.MaterialSpecificationActivity
import java.io.Serializable

open class MaterialItemsListActivity : BaseAppCompactActivity(), MaterialListL3ItemInterface {

    var adapter: MaterialListL3Adapter? = null
    private val materialSpecialityActivity = registerForActivityResult(MaterialSpecificationActivity.Contract(), {})
    var progressBar: LottieAnimationView? = null
    var emptyState: ImageView? = null
    var statusText: TextView? = null
    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_material_items_list)

        progressBar = findViewById(R.id.progressbarhistory)
        emptyState = findViewById(R.id.image_view_sad_h)
        statusText = findViewById(R.id.text_view_result_h)
        searchView = findViewById(R.id.sv_item)
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.statusBarColor = getColor(R.color.appthemeColor)
//        window.decorView.systemUiVisibility
//        val windowInset = WindowInsetsControllerCompat(window, window.decorView)
//        windowInset.isAppearanceLightStatusBars = true
        setMaterialRecycler()
        findViewById<ImageButton>(R.id.backbutton).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        setSearchChangeListener()

    }

    override fun onStart() {
        super.onStart()
      dismissKeyboardOnSearch()
    }

    private fun dismissKeyboardOnSearch(){
        val imm =
            (searchView?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        imm.hideSoftInputFromWindow(searchView?.windowToken, 0)
    }

    private fun setSearchChangeListener(){

        searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                adapter?.applySearch(p0)
                return true
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                adapter?.applySearch(p0)
                dismissKeyboardOnSearch()
                return true
            }
        })

        searchView?.setOnCloseListener {
            adapter?.applySearch(null)
            true
        }
    }

    fun <T : Serializable?> getSerializable(intent: Intent, name: String, clazz: Class<T>): T?
    {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getSerializableExtra(name, clazz)
        else
            intent.getSerializableExtra(name) as T?
    }

    fun showLoading(show: Boolean, title: String){
        progressBar?.visibility = if (show) View.VISIBLE else View.GONE
        statusText?.text = title
        statusText?.visibility = if (show) View.VISIBLE else View.INVISIBLE
        emptyState?.visibility = View.INVISIBLE
    }

    fun showEmptyState(show: Boolean){
        statusText?.text = "No items found"
        emptyState?.visibility = if (show) View.VISIBLE else View.INVISIBLE
        statusText?.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    private fun setMaterialRecycler() {
        val recyclerView = findViewById<View>(R.id.materials_recycle) as RecyclerView
        adapter = MaterialListL3Adapter(this,  this)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    override fun onClickL3Item(model: MaterialListItemModel?) {
        model.let { materialSpecialityActivity.launch(it) }
    }
}