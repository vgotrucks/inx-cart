package com.uniwaylabs.buildo.ui.history

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoryDataWithItemsModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialListItemModel

class HistoryListSectionViewHolder(view: View, var context: Context, var listMaterialItemInterface: HistoryListItemMaterialInterface?) : RecyclerView.ViewHolder(view) {
    var text: TextView
    val recyclerView: RecyclerView
    var numberOfItemsTV: TextView? = null
    var amountTV: TextView? = null

    init {
        text = view.findViewById<View>(R.id.text_item_type) as TextView
        recyclerView = view.findViewById<View>(R.id.cart_section_recycler) as RecyclerView
        val listData = emptyArray<MaterialListItemModel>()
        val adapter = HistoryListMaterialItemAdapter(context, listData, listMaterialItemInterface)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        numberOfItemsTV = view.findViewById<TextView>(R.id.number_of_items_text)
        amountTV = view.findViewById<TextView>(R.id.total)
    }

    fun setListData(list: Array<MaterialListItemModel>?) {
        val adapter = recyclerView.adapter as HistoryListMaterialItemAdapter?
        adapter!!.updateListData(list)
        val materialTypeName = list?.first()?.category
        text.text = materialTypeName
        var amount: Double = 0.0
        for(item in list ?: emptyArray()){
            amount += ((item.itemPrice ?: 0.0) * (item.quantity ?: 1.0))
        }
        numberOfItemsTV?.text = "(${list?.size ?: 0} Items)"
        amountTV?.text = "Total - ₹$amount"
    }

}