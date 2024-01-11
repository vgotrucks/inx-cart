package com.uniwaylabs.buildo.ui.adminForms

import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import com.uniwaylabs.buildo.firebaseDatabase.Database.AdminDB.AdminDatabase
import com.uniwaylabs.buildo.firebaseDatabase.Database.DBService.FirebaseDatabase
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoriesListDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoryDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.DrawerMenuDataModels.UserAccountModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseUrls.DatabaseUrls
import com.uniwaylabs.buildo.firebaseDatabase.Repositories.RepositoryData
import org.json.JSONObject

class CategoryListActivity: TitleListActivityAdaptable() {

    override fun fetchList(){

        showLoading(true, "Fetching...")
        AdminDatabase<CategoriesListDataModel>().getItemsObjectDataSnapshot(this,dataPath){
            showLoading(false, title = "")

            val mData = try {
                it.getValue(CategoriesListDataModel::class.java)
            }catch (e: Exception){ null }

            if(mData?.categories.isNullOrEmpty()){
                showEmptyState(true)
                adapter?.updateListData(emptyMap())
                return@getItemsObjectDataSnapshot
            }
            showEmptyState(false)
            adapter?.updateListData(mData?.categories)
        }
    }

    override fun onImageUploaded(url: String) {
        super.onImageUploaded(url)

        val model = selectedDataModel ?: CategoryDataModel(title = selectedTitle,imageUrl = url)

        if(model !is CategoryDataModel) return
        model.imageUrl = url

        saveDataToDB(model, "$dataPath/${DatabaseUrls.categories_path}/${model.title}")

    }
}