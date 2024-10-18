package com.uniwaylabs.buildo.ui.materialSpecification

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.SizeTypeItemDataModel

interface  SizeTypeItemInterface{
    fun onTapSizeChip(position: Int?, model: SizeTypeItemDataModel?)
}

class SizeTypeItemAdapter(
    var context: Context?,
    private var itemInterface: SizeTypeItemInterface?,
    private var list: Array<SizeTypeItemDataModel>
) : RecyclerView.Adapter<SizeTypeItemAdapter.ViewHolder>() {

    private var selectedSizeText: String? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.size_type_chip_item, parent, false)
        return ViewHolder(view, itemInterface)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list.size < position) {
            return
        }
        val model = list.get(position)
        holder.set(model, model.displayText.equals(selectedSizeText ?: "-"))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(selectedSize: String?){
        this.selectedSizeText = selectedSize
        notifyDataSetChanged()
    }

    fun updateListData(list: Array<SizeTypeItemDataModel>?, selectedSize: String?) {
        this.list = list ?: emptyArray()
        this.selectedSizeText = selectedSize
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View, itemInterface: SizeTypeItemInterface?) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        val textView: TextView
        var itemInterface: SizeTypeItemInterface?
        private var item: SizeTypeItemDataModel? = null

        init {
            view.setOnClickListener(this)
            this.itemInterface = itemInterface
            textView = view.findViewById<TextView>(R.id.text_item_name)
        }

        fun set(model: SizeTypeItemDataModel?, isSelected: Boolean){
            this.item = model
            textView.text = model?.displayText
            set(isSelected)
        }

        override fun onClick(view: View) {
            itemInterface?.onTapSizeChip(this.layoutPosition, item)
            set(true)
        }

        private fun set(selected: Boolean){
            if (selected){
                textView?.background = context?.let { ContextCompat.getDrawable(it,R.drawable.box_line_rounder_corner_20dp_app_theme) }
                context?.let { textView?.setTextColor(ContextCompat.getColor(it, R.color.dark_blue))}
            }
            else{
                textView?.background = context?.let { ContextCompat.getDrawable(it,R.drawable.box_line_rounded_corners_20dp) }
                context?.let { textView?.setTextColor(ContextCompat.getColor(it, R.color.black))}
            }
        }
    }
}