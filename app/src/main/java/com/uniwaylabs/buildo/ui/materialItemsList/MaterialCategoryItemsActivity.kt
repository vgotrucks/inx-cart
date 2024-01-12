package com.uniwaylabs.buildo.ui.materialItemsList

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import com.uniwaylabs.buildo.LocalDatabase.BDSharedPreferences
import com.uniwaylabs.buildo.firebaseDatabase.Database.AdminDB.AdminDatabase
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoryDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoryDataWithItemsModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialListItemModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseUrls.DatabaseUrls
import com.uniwaylabs.buildo.ui.adminForms.ACTIVITY_DATA
import com.uniwaylabs.buildo.ui.materialItemsList.MaterialItemsListActivity
import java.io.Serializable

class MaterialCategoryItemsActivity: MaterialItemsListActivity() {

    class Contract(val requestCode: String): ActivityResultContract<CategoryDataModel?, String>(){

        override fun createIntent(context: Context, input: CategoryDataModel?): Intent = Intent(context, MaterialCategoryItemsActivity::class.java)
            .putExtra(requestCode, input)

        override fun parseResult(resultCode: Int, intent: Intent?): String {
            return "Ok"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val model = getSerializable(intent, ACTIVITY_DATA, CategoryDataModel::class.java)
        model?.let { set(it) }
    }

    private fun set(model: CategoryDataModel){

        val path = "${BDSharedPreferences.shared.getSelectedPostalAreaCode(this)}/${DatabaseUrls.categories_path}/${model.title}"
        showLoading(true, "Fetching...")
        AdminDatabase<CategoryDataWithItemsModel>().getItemsObjectDataSnapshot(this,path){
            showLoading(false, title = "")

            val mData = try {
                it.getValue(CategoryDataWithItemsModel::class.java)
            }catch (e: Exception){ null }

            var items: ArrayList<MaterialListItemModel> =
                (mData?.subCategories?.flatMap { (it?.value?.material_items ?: emptyMap()).values.asIterable() } ?: ArrayList()) as ArrayList<MaterialListItemModel>
            items?.addAll((mData?.material_items ?: emptyMap()).values?.toMutableList() ?: ArrayList())
            if(items.isNullOrEmpty()){
                showEmptyState(true)
                adapter?.updateListData(emptyArray())
                return@getItemsObjectDataSnapshot
            }
            showEmptyState(false)
            adapter?.updateListData(items.toTypedArray())
        }
    }
}