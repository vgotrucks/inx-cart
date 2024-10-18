package com.uniwaylabs.buildo.ui.dashboard.CartListMaterial

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.firebaseDatabase.Database.UserDB.UserDatabase
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialListItemModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseUrls.DatabaseUrls
import com.uniwaylabs.buildo.firebaseDatabase.Repositories.RepositoryItems
import kotlin.math.roundToInt

public interface CartListItemMaterialInterface{
    fun onClickCartListItem(model: MaterialListItemModel?)
    fun reloadList()
    fun refreshList()
}

class CartListMaterialItemAdapter(
    var context: Context,
    var list: Array<MaterialListItemModel>?,
    var cartListItemMaterialInterface: CartListItemMaterialInterface?) : RecyclerView.Adapter<CartListMaterialItemAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_list_material_item, parent, false)
        return ViewHolder(view, cartListItemMaterialInterface)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list!!.size < position) {
            return
        }
        holder.set(list!![position])
        holder.fetch(list!![position].path)
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    fun updateListData(list: Array<MaterialListItemModel>?) {
        this.list = list ?: emptyArray()
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View, var cartItemInterface: CartListItemMaterialInterface?) : RecyclerView.ViewHolder(view), ValueEventListener {


        var text: TextView
        var imageView: ImageView
        var model: MaterialListItemModel? = null
        private var itemPriceTV: TextView? = null
        private var itemMarketPriceTV: TextView? = null
        private var offPriceTV: TextView? = null
        var progressBar: LottieAnimationView? = null
        private var etQuantity: EditText? = null
        private var reference: DatabaseReference? = null
        private var sizeType: TextView? = null
        private var uomTV: TextView? = null
        private var cachedUOM: String? = null
        init {

            text = view.findViewById(R.id.text_item_name)
            imageView = view.findViewById<View>(R.id.item_image) as ImageView
            itemPriceTV = view.findViewById(R.id.text_item_price)
            itemMarketPriceTV = view.findViewById(R.id.text_item_market_price)
            offPriceTV = view.findViewById(R.id.text_item_uom)
            progressBar = view.findViewById(R.id.progressbarhistory)
            itemMarketPriceTV?.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            etQuantity = view.findViewById(R.id.et_quantity)
            sizeType = view.findViewById(R.id.text_size_name)
            uomTV = view.findViewById(R.id.uom_text)
            text.setOnClickListener {
                cartItemInterface?.onClickCartListItem(model)
            }
            imageView.setOnClickListener {
                cartItemInterface?.onClickCartListItem(model)
            }
            etQuantity?.setOnEditorActionListener(object : OnEditorActionListener{
                override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                    if (p1 == EditorInfo.IME_ACTION_DONE){
                        val quantity = (etQuantity?.text ?: "").toString().trim()
                        val quantityInDouble = quantity.toDoubleOrNull() ?: 0.0

                        val result = if(quantityInDouble <= 0 ) 1.0 else quantityInDouble

                        if (result != model?.quantity){
                            saveQuantity(result)
                        }
                        return true
                    }
                   return false
                }

            })

            view.findViewById<ImageButton>(R.id.subtract_button).setOnClickListener {
                val quantity = (model?.quantity ?: 2.0) - 1.0
                if (quantity <= 0){
                    return@setOnClickListener
                }
                saveQuantity(quantity)
            }
            view.findViewById<ImageButton>(R.id.add_button).setOnClickListener {
                val quantity = (model?.quantity ?: 0.0) + 1.0
                if (quantity <= 0){
                    return@setOnClickListener
                }
                saveQuantity(quantity)
            }

            view.findViewById<ImageButton>(R.id.delete_button).setOnClickListener {
              deleteFromDB(model?.cartListPath)
            }
        }

        @SuppressLint("SetTextI18n")
        fun set(model: MaterialListItemModel?) {
            this.model = model
            val options = RequestOptions().error(R.drawable.ic_placeholder_image_48)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)


            Glide.with(context).load(model?.imageURLs?.first()).apply(options).into(imageView)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            sizeType?.text = model?.defaultSize ?: "NA"
            text.text = model?.itemName
            uomTV?.text = model?.unitOfMeasurement ?: "Piece"
            showLoading(false, "")
            updateCritical(model)
        }

        private fun updateCritical(item: MaterialListItemModel?): Boolean{

            val quantity = if (model?.isPartialQuantityAllowed == true) (model?.quantity ?: 1.0) else (model?.quantity ?: 1.0).roundToInt()
            val itemPrice = model?.itemPrice
            val marketPrice = model?.marketPrice

            setPrice(model?.itemPrice,model?.marketPrice,model?.quantity)
            etQuantity?.setText("$quantity")
            if (model?.inStock == false){
                offPriceTV?.text = "Out of stock"
                itemPriceTV?.text = ""
                itemMarketPriceTV?.text = ""
            }
            this?.model?.inStock = item?.inStock
            if (!item?.defaultSize.isNullOrEmpty()){
                item?.sizes?.forEach {
                    if(it.displayText.equals(model?.defaultSize)){

                        val itemPrice = model?.itemPrice
                        val marketPrice = model?.marketPrice
                        setPrice(it.itemPrice, it.marketPrice, model?.quantity)
                        this.model?.inStock = it.inStock
                        if (itemPrice != it.itemPrice || marketPrice != it.marketPrice){
                            return true
                        }

                    }
                }
            }

            if (itemPrice != item?.itemPrice || marketPrice != item?.marketPrice){
                return true
            }
            return false
        }

        private fun setPrice(itemPrice: Double?, marketPrice: Double?, quantity: Double?){

            val itemPricetText = getTotalPrice(itemPrice, quantity)
            val marketPriceText = getTotalPrice(marketPrice,quantity)
            itemPriceTV?.text = "₹ $itemPricetText"
            itemMarketPriceTV?.text = "₹$marketPriceText"
            val offPrice = marketPriceText - itemPricetText
            offPriceTV?.text = "₹$offPrice OFF"
            this.model?.itemPrice = itemPrice
            this.model?.marketPrice = marketPrice
        }
        private fun getTotalPrice(price: Double?, quantity: Double?): Double{
            return (price ?: 0.0) * (quantity ?: 1.0)
        }

        fun fetch(path: String?){
            if (path == null) return
            showLoading(true, "")
            this.reference?.removeEventListener(this)
            val reference =  RepositoryItems.instance.referenceOfDB.child(path)
            reference?.addValueEventListener(this)
            this.reference = reference
        }

        private fun showLoading(show: Boolean, title: String){
           // progressBar?.visibility = if (show) View.VISIBLE else View.GONE
        }

        private fun saveQuantity(text: Double?){

            if(model?.cartListPath.isNullOrEmpty()){
                return
            }
            model?.quantity = text
            UserDatabase<MaterialListItemModel>().setDataToItemDatabase(context, "${DatabaseUrls.cart_list_path}/${model?.cartListPath ?: ""}",model, {})
        }

        override fun onDataChange(snapshot: DataSnapshot) {

            showLoading(false, title = "")

            val mData = try {
                snapshot.getValue(MaterialListItemModel::class.java)
            }catch (e: Exception){ null }



            if (snapshot.value == null || mData?.id == null || mData?.inStock == false){
                deleteFromDB(path = model?.cartListPath)
                return@onDataChange
            }

            //All cart related data
            mData.quantity = model?.quantity
            val needUpdate = updateCritical(mData)
            if (this.model?.inStock == false){
                deleteFromDB(path = model?.cartListPath)
                return@onDataChange
            }

//            if (needUpdate){
//                saveQuantity(model?.quantity)
//            }
        }

        override fun onCancelled(error: DatabaseError) {
            showLoading(false, title = "")
            Toast.makeText(
                context.applicationContext,
                "Couldn't load data,Please check your internet connectivity",
                Toast.LENGTH_SHORT
            ).show()
        }

        private fun deleteFromDB(path: String?){

            if (path == null) return
            reference?.removeEventListener(this)
            UserDatabase<MaterialListItemModel>().deleteFromDatabase("${DatabaseUrls.cart_list_path}/$path")
        }
    }
}