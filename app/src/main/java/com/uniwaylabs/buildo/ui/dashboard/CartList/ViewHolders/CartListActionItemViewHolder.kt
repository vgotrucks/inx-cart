package com.uniwaylabs.buildo.ui.dashboard.CartList.ViewHolders

import android.content.Context
import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.uniwaylabs.buildo.R

public interface  CartListActionItemViewInterface{
    fun onClickBuyNow()
}

class CartListActionItemViewHolder(itemView: View, var context: Context?) :
    RecyclerView.ViewHolder(itemView), OnClickListener{

    var actionInterface: CartListActionItemViewInterface? = null
    var text: TextView? = null
    var tvSaved: TextView? = null
    var buyNowButton: AppCompatButton? = null

    init {
        buyNowButton = itemView.findViewById<AppCompatButton>(R.id.order_button)
        text = itemView.findViewById(R.id.total_amount_text)
        tvSaved = itemView.findViewById(R.id.sv_text)
        buyNowButton?.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        actionInterface?.onClickBuyNow()
    }

}