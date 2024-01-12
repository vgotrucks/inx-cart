package com.uniwaylabs.buildo.ui.home.MaterialList.ViewHolders

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.uniwaylabs.buildo.LocalDatabase.BDSharedPreferences
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.firebaseDatabase.Database.AdminDB.AdminDatabase
import com.uniwaylabs.buildo.firebaseDatabase.Database.UserDB.UserDatabase
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialListItemModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseUrls.DatabaseUrls
import java.util.Date

class MaterialListL3ItemViewHolder(
    view: View,
    var context: Context,
    var l3ItemInterface: MaterialListL3ItemInterface
) : RecyclerView.ViewHolder(view), View.OnClickListener {
    var text: TextView
    var imageView: ImageView
    var model: MaterialListItemModel? = null
    private var itemPriceTV: TextView? = null
    private var itemMarketPriceTV: TextView? = null
    private var offPriceTV: TextView? = null
    var progressBar: LottieAnimationView? = null
    var statusText: TextView? = null

    init {
        view.setOnClickListener(this)
        imageView = view.findViewById<View>(R.id.item_image) as ImageView
        text = view.findViewById<View>(R.id.text_item_name) as TextView
        itemPriceTV = view.findViewById(R.id.text_item_price)
        itemMarketPriceTV = view.findViewById(R.id.text_item_market_price)
        offPriceTV = view.findViewById(R.id.text_item_uom)
        progressBar = view.findViewById(R.id.progressbarhistory)
        statusText = view.findViewById(R.id.text_view_result_h)
        itemMarketPriceTV?.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        val cartButton = view.findViewById<AppCompatButton>(R.id.add_to_cart_button)
        cartButton?.setOnClickListener {
            addItemToCartDB()
        }
        cartButton?.visibility = if (BDSharedPreferences.shared.getPermissionModel(context)?.isView == true) View.INVISIBLE else View.VISIBLE
    }

    private fun showLoading(show: Boolean, title: String){
        progressBar?.visibility = if (show) View.VISIBLE else View.GONE
        statusText?.text = title
        statusText?.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun addItemToCartDB(){

        val path = model?.path
        if (path.isNullOrEmpty()) return

        showLoading(true, "")
        model?.quantity = 1.0
        UserDatabase<String>().setDataToItemDatabase(context, "${DatabaseUrls.cart_list_path}/${path}",model){
            showLoading(false, "")
        }
    }

    @SuppressLint("SetTextI18n")
    fun set(model: MaterialListItemModel) {
        this.model = model
        val options = RequestOptions().error(R.drawable.ic_placeholder_image_48)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .centerCrop()
        Glide.with(context).load(model.imageURLs?.first()).apply(options).into(imageView)
        text.text = model.itemName
        itemPriceTV?.text = "₹ ${model.itemPrice.toString()}"
        itemMarketPriceTV?.text = "₹${model.marketPrice.toString()}"
        offPriceTV?.text = "₹${((model.marketPrice ?: 0.0) - (model.itemPrice ?: 0.0))} OFF"

        if (model.inStock == false){
            offPriceTV?.text = "Out of stock"
            itemPriceTV?.text = ""
            itemMarketPriceTV?.text = ""
        }
        showLoading(false, "Adding to cart...")
    }

    override fun onClick(view: View) {
        l3ItemInterface.onClickL3Item(model)
    }
}