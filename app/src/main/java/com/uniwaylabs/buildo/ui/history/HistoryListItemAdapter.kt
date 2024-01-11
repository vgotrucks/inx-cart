package com.uniwaylabs.buildo.ui.history

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoryDataWithItemsModel
import com.uniwaylabs.buildo.ui.dashboard.CartList.CartListItemModel
import com.uniwaylabs.buildo.ui.dashboard.CartList.ViewHolders.CartListActionItemViewHolder
import com.uniwaylabs.buildo.ui.dashboard.CartList.ViewHolders.CartListSectionViewHolder
import com.uniwaylabs.buildo.ui.dashboard.CartListMaterial.CartListItemMaterialInterface

class HistoryListItemAdapter(var context: Context, var list: Array<CategoryDataWithItemsModel>, var listMaterialItemInterface: HistoryListItemMaterialInterface?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cart_list_item, parent, false)
        return HistoryListSectionViewHolder(view, context, listMaterialItemInterface)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (list.size < position) {
            return
        }
        val viewHolder = holder as HistoryListSectionViewHolder
        val model = list[position]
        var items = model?.subCategories?.flatMap { (it?.value?.material_items ?: emptyMap()).values.asIterable() }
        items?.toMutableList()?.addAll(model?.material_items?.values?.toTypedArray() ?: emptyArray())
        viewHolder.setListData(items?.toTypedArray())
        viewHolder.listMaterialItemInterface = listMaterialItemInterface
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateListData(list: Array<CategoryDataWithItemsModel>) {
        this.list = list
        notifyDataSetChanged()
    }
}