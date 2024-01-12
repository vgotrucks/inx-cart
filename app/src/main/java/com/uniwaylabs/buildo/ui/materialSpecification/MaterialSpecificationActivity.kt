package com.uniwaylabs.buildo.ui.materialSpecification

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Paint
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.FragmentActivity
import com.airbnb.lottie.LottieAnimationView
import com.uniwaylabs.buildo.BaseFragmentActivity
import com.uniwaylabs.buildo.LocalDatabase.BDSharedPreferences
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.firebaseDatabase.Database.AdminDB.AdminDatabase
import com.uniwaylabs.buildo.firebaseDatabase.Database.UserDB.UserDatabase
import com.uniwaylabs.buildo.ui.commans.ImageSliderFragment.ImageSliderFragment
import com.uniwaylabs.buildo.ui.commans.ImageSliderFragment.ImageSliderFragmentInterface
import com.uniwaylabs.buildo.ui.commans.ImageSliderFragment.SLIDING_IMAGES
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialListItemModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.OrderItemDetailsDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseUrls.DatabaseUrls
import com.uniwaylabs.buildo.ui.adminForms.CreateItemFormActivity
import java.io.Serializable
import java.util.Date

@Suppress("DEPRECATION")
class MaterialSpecificationActivity : BaseFragmentActivity(), ImageSliderFragmentInterface{

    var textView: TextView? = null
    var imageView: ImageView? = null
    private var imageContainer: FrameLayout? = null
    private val fullScreenImageActivity = registerForActivityResult(FullScreenImageActivity.Contract(), {})
    private var itemPriceTV: TextView? = null
    private var itemMarketPriceTV: TextView? = null
    private var offPriceTV: TextView? = null
    private var specificationsTV: TextView? = null
    private var policyTV: TextView? = null
    private var imageUrls: ArrayList<String> = ArrayList()
    var fragment: ImageSliderFragment? = null
    private var model: MaterialListItemModel? = null
    var progressBar: LottieAnimationView? = null
    var statusText: TextView? = null
    private var editButton: ImageButton? = null

    object MaterialSpecificationActivity{
        var ITEM_DATA_CONSTANT: String = "ITEM_DATA"
    }

    private val createItemFormActivity = registerForActivityResult(CreateItemFormActivity.Contract(), {})

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_material_specification)
        textView = findViewById(R.id.text_item_name)
        imageContainer = findViewById<View>(R.id.item_image_container) as FrameLayout
        itemPriceTV = findViewById(R.id.text_item_price)
        itemMarketPriceTV = findViewById(R.id.text_item_market_price)
        offPriceTV = findViewById(R.id.text_item_uom)
        specificationsTV = findViewById(R.id.tv_spec)
        policyTV = findViewById(R.id.tv_policy)
        progressBar = findViewById(R.id.progressbarhistory)
        statusText = findViewById(R.id.text_view_result_h)
        editButton = findViewById(R.id.edit)
        findViewById<ImageButton>(R.id.backbutton).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        itemMarketPriceTV?.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        val model = getSerializable(intent, MaterialSpecificationActivity.ITEM_DATA_CONSTANT, MaterialListItemModel::class.java)
        model?.let {
            set(it)
            fetchUpdatedItem(it.path)
        }
        this.model = model
        val cartButton = findViewById<AppCompatButton>(R.id.add_to_cart_button)
        cartButton?.setOnClickListener {
            addItemToCartDB()
        }

        editButton?.setOnClickListener {

            if (BDSharedPreferences.shared.getPermissionModel(this)?.isCreate != true) return@setOnClickListener
            createItemFormActivity.launch(model)
        }

        editButton?.visibility = if (BDSharedPreferences.shared.getPermissionModel(this)?.isCreate == true) View.VISIBLE else View.INVISIBLE
        cartButton?.visibility = if (BDSharedPreferences.shared.getPermissionModel(this)?.isView == true) View.INVISIBLE else View.VISIBLE

    }

    private fun showLoading(show: Boolean, title: String){
        progressBar?.visibility = if (show) View.VISIBLE else View.GONE
        statusText?.text = title
        statusText?.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun fetchUpdatedItem(path: String?){

        AdminDatabase<String>().getItemsObjectDataSnapshot(this, path ?: ""){
            val mData = try {
                it.getValue(MaterialListItemModel::class.java)
            }catch (e: Exception){ null }

            if(mData == null) return@getItemsObjectDataSnapshot
            this.model = mData
            set(mData)
        }
    }

    private fun addItemToCartDB(){

        val path = model?.path
        if (path.isNullOrEmpty()) return

        showLoading(true, "")
        model?.quantity = 1.0
        UserDatabase<String>().setDataToItemDatabase(this, "${DatabaseUrls.cart_list_path}/${path}",model){
            showLoading(false, "")
        }
    }

    override fun onResume() {
        super.onResume()
        setupFragment()
    }

    private fun <T : Serializable?> getSerializable(intent: Intent, name: String, clazz: Class<T>): T?
    {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getSerializableExtra(name, clazz)
        else
            intent.getSerializableExtra(name) as T?
    }

    private fun setupFragment(){
        if (imageContainer != null && fragment == null){
            fragment = ImageSliderFragment()
            fragment?.sliderInterface = this
            fragment?.arguments = Bundle().apply { putStringArray(SLIDING_IMAGES, imageUrls.toTypedArray()) }
           supportFragmentManager.beginTransaction().add(R.id.item_image_container, fragment!!).commit()
        }
    }

    private fun set(model: MaterialListItemModel) {
        textView!!.text = model.itemName
        itemPriceTV?.text = "₹ ${model.itemPrice.toString()}"
        itemMarketPriceTV?.text = "₹ ${model.marketPrice.toString()}"
        offPriceTV?.text = "₹ ${((model.marketPrice ?: 0.0) - (model.itemPrice ?: 0.0))} OFF"
        imageUrls = model.imageURLs ?: ArrayList()
        specificationsTV?.text = model.specifications
        policyTV?.text = model.itemPolicy
        if (model.inStock == false){
            offPriceTV?.text = "Out of stock"
            itemPriceTV?.text = ""
            itemMarketPriceTV?.text = ""
        }

    }

    class Contract: ActivityResultContract<MaterialListItemModel,String>(){

        override fun createIntent(context: Context, input: MaterialListItemModel): Intent = Intent(context, com.uniwaylabs.buildo.ui.materialSpecification.MaterialSpecificationActivity::class.java)
               .putExtra(MaterialSpecificationActivity.ITEM_DATA_CONSTANT, input)

        override fun parseResult(resultCode: Int, intent: Intent?): String {
           return "Ok"
        }

    }

    override fun onImageTap(position: Int?, url: String?) {
        fullScreenImageActivity.launch(FullScreenImageActivity.ActivityData(imageUrls.toTypedArray(), position))
    }

    override fun onDeleteTap(position: Int?, url: String?) {
        TODO("Not yet implemented")
    }

}