package com.uniwaylabs.buildo.ui.home.MaterialList

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialListItemModel
import com.uniwaylabs.buildo.ui.home.MaterialList.ViewHolders.MaterialListL1ItemInterface
import com.uniwaylabs.buildo.ui.home.MaterialList.ViewHolders.MaterialListL1ItemViewHolder
import com.uniwaylabs.buildo.ui.home.MaterialList.ViewHolders.MaterialListL3ItemInterface
import com.uniwaylabs.buildo.ui.home.MaterialList.ViewHolders.MaterialListL3ItemViewHolder
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialTypeListModel

class MaterialListL3Adapter(
    var context: Context,
    l3ItemInterface: MaterialListL3ItemInterface
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var l3ItemInterface: MaterialListL3ItemInterface
    var list: Array<MaterialListItemModel> = emptyArray()
        set(value) {
            field = value
            resultantList = value
        }

    var resultantList: Array<MaterialListItemModel> = emptyArray()

    init {
        this.l3ItemInterface = l3ItemInterface
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.material_list_l3_type_item, parent, false)
        return MaterialListL3ItemViewHolder(view, context, l3ItemInterface)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var position = position
        if (resultantList.size < position) {
            return
        }
        val viewHolder = holder as MaterialListL3ItemViewHolder
        viewHolder.set(resultantList[position])
    }

    override fun getItemCount(): Int {
       return resultantList.size
    }

    fun updateListData(list: Array<MaterialListItemModel>) {
        this.list = list
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun applySearch(text: String?){

        val query = (text ?: "").trim()
        resultantList = if(query.isEmpty()) list else list.filter { it.itemName?.contains(query, true) ?: false }.toTypedArray()
        notifyDataSetChanged()
    }


}