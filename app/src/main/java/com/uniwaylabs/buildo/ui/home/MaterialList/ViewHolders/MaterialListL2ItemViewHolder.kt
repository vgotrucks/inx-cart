package com.uniwaylabs.buildo.ui.home.MaterialList.ViewHolders

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoryDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialListItemModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.SubCategoryDataModel
import com.uniwaylabs.buildo.ui.home.MaterialList.MaterialListL2Adapter

class MaterialListL2ItemViewHolder(
    var view: View,
    var context: Context,
    var itemInterface: MaterialListL2ItemInterface?) : RecyclerView.ViewHolder(view) {


    var listData: Array<SubCategoryDataModel> = emptyArray()
    var adapter: MaterialListL2Adapter
    var recyclerView: RecyclerView? = null
    private var tvTitle: TextView? = null

    init {
        recyclerView = view.findViewById<RecyclerView>(R.id.materials_recycle)
        tvTitle = view.findViewById(R.id.title_tv)
        adapter = MaterialListL2Adapter(context, itemInterface, emptyArray())
        val layoutManager = GridLayoutManager(context, 3)
        recyclerView?.layoutManager = layoutManager
        recyclerView?.adapter = adapter
    }

    fun set(listData: Array<SubCategoryDataModel>?) {
        this.listData = listData ?: emptyArray()
        adapter.updateListData(listData)
        view.visibility = if (listData.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    fun set(title: String?){
        tvTitle?.text = title
    }
}