package com.uniwaylabs.buildo.ui.adminForms

import com.uniwaylabs.buildo.firebaseDatabase.Database.AdminDB.AdminDatabase
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoriesListDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoryDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.SubCategoryDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseUrls.DatabaseUrls

class SubCategoryListActivity: TitleListActivityAdaptable() {

    private var category: String? = null
    override fun fetchList(){

        showLoading(true, "Fetching...")
        AdminDatabase<CategoriesListDataModel>().getItemsObjectDataSnapshot(this,dataPath){
            showLoading(false, title = "")

            val mData = try {
                it.getValue(CategoryDataModel::class.java)
            }catch (e: Exception){ null }

            category = mData?.title
            if(mData?.subCategories.isNullOrEmpty()){
                showEmptyState(true)
                adapter?.updateListData(emptyMap())
                return@getItemsObjectDataSnapshot
            }
            showEmptyState(false)
            adapter?.updateListData(mData?.subCategories)
        }
    }


    override fun onImageUploaded(url: String) {
        super.onImageUploaded(url)

        val model = selectedDataModel ?: SubCategoryDataModel(title = selectedTitle,imageUrl = url)

        if(model !is SubCategoryDataModel) return
        model.imageUrl = url
        model.category = category

        saveDataToDB(model, "$dataPath/${DatabaseUrls.sub_categories_path}/${model.title}")

    }
}