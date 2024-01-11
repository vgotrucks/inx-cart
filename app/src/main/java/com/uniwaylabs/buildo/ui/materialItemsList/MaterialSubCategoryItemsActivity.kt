package com.uniwaylabs.buildo.ui.materialItemsList

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import com.uniwaylabs.buildo.LocalDatabase.BDSharedPreferences
import com.uniwaylabs.buildo.firebaseDatabase.Database.AdminDB.AdminDatabase
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoryDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoryDataWithItemsModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.SubCategoryDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.SubCategoryDataWithItemsModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseUrls.DatabaseUrls
import com.uniwaylabs.buildo.ui.adminForms.ACTIVITY_DATA

class MaterialSubCategoryItemsActivity: MaterialItemsListActivity() {

    class Contract(val requestCode: String): ActivityResultContract<SubCategoryDataModel?, String>(){

        override fun createIntent(context: Context, input: SubCategoryDataModel?): Intent = Intent(context, MaterialSubCategoryItemsActivity::class.java)
            .putExtra(requestCode, input)

        override fun parseResult(resultCode: Int, intent: Intent?): String {
            return "Ok"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val model = getSerializable(intent, ACTIVITY_DATA, SubCategoryDataModel::class.java)
        model?.let { set(it) }
    }

    private fun set(model: SubCategoryDataModel){

        val path = "${BDSharedPreferences.shared.getSelectedPostalAreaCode(this)}/${DatabaseUrls.categories_path}/${model.category ?: ""}/${DatabaseUrls.sub_categories_path}/${model.title}"
        showLoading(true, "Fetching...")
        AdminDatabase<SubCategoryDataWithItemsModel>().getItemsObjectDataSnapshot(this,path){
            showLoading(false, title = "")

            val mData = try {
                it.getValue(SubCategoryDataWithItemsModel::class.java)
            }catch (e: Exception){ null }

            val items = mData?.material_items?.values?.toTypedArray()
            if(items.isNullOrEmpty()){
                showEmptyState(true)
                adapter?.updateListData(emptyArray())
                return@getItemsObjectDataSnapshot
            }
            showEmptyState(false)
            adapter?.updateListData(items)
        }
    }
}