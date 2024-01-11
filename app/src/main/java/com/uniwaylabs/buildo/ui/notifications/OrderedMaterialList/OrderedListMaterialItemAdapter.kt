package com.uniwaylabs.buildo.ui.notifications.OrderedMaterialList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialListItemModel

class OrderedListMaterialItemAdapter(
    var context: Context,
    var list: Array<MaterialListItemModel>
) : RecyclerView.Adapter<OrderedListMaterialItemAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list.size < position) {
            return
        }
        val materialName = list[position].imageURLs?.first()
        // holder.text.setText(materialTypeName);
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateListData(list: Array<MaterialListItemModel>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var text: TextView? = null
        var imageView: ImageView

        init {
            //            text = (TextView) view.findViewById(R.id.text_home);
            imageView = view.findViewById<View>(R.id.item_image) as ImageView
            val options = RequestOptions().error(R.drawable.ic_placeholder_image_48)
            Glide.with(context)
                .load("https://i.ibb.co/1XxH5qh/Order-Ambuja-OPC-Cement-At-Discount-Rate-Builders9.jpg")
                .apply(options).into(imageView)
        }
    }
}