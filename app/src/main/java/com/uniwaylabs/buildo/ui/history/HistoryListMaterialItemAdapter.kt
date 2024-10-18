package com.uniwaylabs.buildo.ui.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialListItemModel

class HistoryListMaterialItemAdapter(
    var context: Context,
    var list: Array<MaterialListItemModel>?,
    var listItemMaterialInterface: HistoryListItemMaterialInterface?) : RecyclerView.Adapter<HistoryListMaterialItemAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_list_item, parent, false)
        return ViewHolder(view, listItemMaterialInterface)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list!!.size < position) {
            return
        }
        holder.set(list!![position])
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    fun updateListData(list: Array<MaterialListItemModel>?) {
        this.list = list ?: emptyArray()
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View, var listItemInterface: HistoryListItemMaterialInterface?) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        var text: TextView
        var imageView: ImageView
        var listItem: MaterialListItemModel? = null
        var itemPriceTV: TextView? = null
        var quantityTV: TextView? = null
        var totalAmountTV: TextView? = null
        var statusBadge: TextView? = null
        var sizeType: TextView? = null

        init {
            view.setOnClickListener(this)
            text = view.findViewById(R.id.text_item_name)
            imageView = view.findViewById<View>(R.id.item_image) as ImageView
            itemPriceTV = view.findViewById(R.id.text_item_price)
            quantityTV = view.findViewById(R.id.text_item_market_price)
            totalAmountTV = view.findViewById(R.id.text_item_uom)
            statusBadge = view.findViewById(R.id.status_badge_tv)
            sizeType = view.findViewById(R.id.text_size_name)

            statusBadge?.setOnClickListener {
                listItemInterface?.didTapChangeStatus(listItem)
            }
        }

        fun set(model: MaterialListItemModel) {
            listItem = model
            val options = RequestOptions().error(R.drawable.ic_placeholder_image_48)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
            Glide.with(context).load(model.imageURLs?.first()).apply(options).into(imageView)
            text.text = model.itemName
            sizeType?.text = model?.defaultSize ?: "NA"
            itemPriceTV?.text = "${model.itemPrice}"
            quantityTV?.text = "X ${model.quantity} = "
            val totalAmount = (model.itemPrice ?: 0.0)*(model.quantity ?: 0.0)
            totalAmountTV?.text = "₹$totalAmount"

            if (model?.deliveryStatus == 1){
                statusBadge?.text = "Delivered"
                statusBadge?.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_round_corners_lightgreen10dp) }
                context?.let { statusBadge?.setTextColor(ContextCompat.getColor(it, R.color.systemGreenColor))}
            }
            else{
                statusBadge?.text = "Delivery Pending"
                statusBadge?.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_cash) }
                context?.let { statusBadge?.setTextColor(ContextCompat.getColor(it, R.color.black))}
            }
        }

        override fun onClick(p0: View?) {
            listItemInterface?.onClickHistoryListItem(listItem)
        }
    }
}