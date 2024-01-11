package com.uniwaylabs.buildo.ui.dashboard.CartList

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoryDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoryDataWithItemsModel
import com.uniwaylabs.buildo.ui.dashboard.CartList.ViewHolders.CartListActionItemViewHolder
import com.uniwaylabs.buildo.ui.dashboard.CartList.ViewHolders.CartListActionItemViewInterface
import com.uniwaylabs.buildo.ui.dashboard.CartList.ViewHolders.CartListSectionViewHolder
import com.uniwaylabs.buildo.ui.dashboard.CartListMaterial.CartListItemMaterialInterface

class CartListItemAdapter(var context: Context, var list: Array<CategoryDataWithItemsModel>,var cartListMaterialItemInterface: CartListItemMaterialInterface?, var actionInterface: CartListActionItemViewInterface?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cart_list_order_action_item, parent, false)
            return CartListActionItemViewHolder(view, context)
        }
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cart_list_item, parent, false)
        return CartListSectionViewHolder(view, context, cartListMaterialItemInterface)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (list.size < position) {
            return
        }
        if (position == 0) {
            val viewHolder = holder as CartListActionItemViewHolder
            viewHolder.actionInterface = actionInterface
            var amount: Double = 0.0
            var originalAmount = 0.0
            for(model in list ?: emptyArray()){
                var items = model?.subCategories?.flatMap { (it?.value?.material_items ?: emptyMap()).values.asIterable() }
                items?.toMutableList()?.addAll(model?.material_items?.values?.toTypedArray() ?: emptyArray())
                for(item in items?.toTypedArray() ?: emptyArray()){
                    amount += ((item.itemPrice ?: 0.0) * (item.quantity ?: 1.0))
                    originalAmount += ((item.marketPrice ?: 0.0) * (item.quantity ?: 1.0))
                }
            }
            viewHolder.text?.text = "₹$amount"
            viewHolder.tvSaved?.text = "Congratulations!, you saved ${originalAmount - amount}"
            viewHolder.buyNowButton?.text = "BUY NOW ($amount/-)"
            return
        }
        val viewHolder = holder as CartListSectionViewHolder
        val model = list[position - 1]
        var items = model?.subCategories?.flatMap { (it?.value?.material_items ?: emptyMap()).values.asIterable() }
        items?.toMutableList()?.addAll(model?.material_items?.values?.toTypedArray() ?: emptyArray())
        viewHolder.setListData(items?.toTypedArray())
        viewHolder.cartListMaterialItemInterface = cartListMaterialItemInterface
    }

    override fun getItemCount(): Int {
        return if (list.isEmpty()) 0 else list.size + 1
    }

    fun updateListData(list: Array<CategoryDataWithItemsModel>) {
        this.list = list
        notifyDataSetChanged()
    }
}