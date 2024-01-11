package com.uniwaylabs.buildo.ui.home.MaterialList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoryDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialListItemModel
import com.uniwaylabs.buildo.ui.home.MaterialList.ViewHolders.MaterialListL1ItemInterface
import com.uniwaylabs.buildo.ui.home.MaterialList.ViewHolders.MaterialListL1ItemViewHolder
import com.uniwaylabs.buildo.ui.home.MaterialList.ViewHolders.MaterialListL2ItemInterface
import com.uniwaylabs.buildo.ui.home.MaterialList.ViewHolders.MaterialListL2ItemViewHolder
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialTypeListModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.SubCategoryDataModel

class MaterialListItemAdapter(
    var context: Context,
    var l1itemInterface: MaterialListL1ItemInterface?,
    l2ItemInterface: MaterialListL2ItemInterface
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var list: Array<CategoryDataModel> = emptyArray()
    var l2ItemInterface: MaterialListL2ItemInterface

    init {
        this.l2ItemInterface = l2ItemInterface
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if ( viewType == 0) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.material_list_l1_type_item, parent, false)
            return MaterialListL1ItemViewHolder(view, context, l1itemInterface)
        }
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.material_list_l2_type_item, parent, false)
        return MaterialListL2ItemViewHolder(view, context, l2ItemInterface)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var position = position
        if (list.size < position) {
            return
        }
        if ( position == 0) {
            val viewHolder = holder as MaterialListL1ItemViewHolder
            viewHolder.setData(list)
            return
        }
        position =  position - 1
        val subCategory = list.get(position).subCategories?.values?.toTypedArray() ?: emptyArray()
        val viewHolder = holder as MaterialListL2ItemViewHolder
        viewHolder.set(subCategory)
        viewHolder.itemView.layoutParams = if(subCategory.isEmpty()) ViewGroup.LayoutParams(0,0) else ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        viewHolder.itemView.visibility = if(subCategory.isEmpty()) View.GONE else View.VISIBLE
        viewHolder.set(list.get(position).title)
    }

    override fun getItemCount(): Int {
        return list.size + 1
    }

    fun updateListData(list: Array<CategoryDataModel>) {
        this.list = list
        notifyDataSetChanged()
    }

}