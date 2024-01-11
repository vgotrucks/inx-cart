package com.uniwaylabs.buildo.ui.home.MaterialList.ViewHolders

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoriesListDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoryDataModel
import com.uniwaylabs.buildo.ui.home.MaterialTypeList.MaterialTypeListAdapter
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialTypeListModel

class MaterialListL1ItemViewHolder(
    view: View,
    var context: Context,
    var itemInterface: MaterialListL1ItemInterface?) : RecyclerView.ViewHolder(view) {


    var listData: Array<CategoryDataModel> = emptyArray<CategoryDataModel>()
    var adapter: MaterialTypeListAdapter

    init {
        val recyclerView = view.findViewById<RecyclerView>(R.id.types_recycle)
        adapter = MaterialTypeListAdapter(context, itemInterface, listData)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    fun setData(listData: Array<CategoryDataModel>?) {
        this.listData = listData ?: emptyArray()
        adapter.updateListData(listData)
    }
}