package com.uniwaylabs.buildo.ui.adminForms

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.FragmentActivity
import com.airbnb.lottie.LottieAnimationView
import com.uniwaylabs.buildo.BDDialogs.VTDialogViews
import com.uniwaylabs.buildo.LocalDatabase.BDSharedPreferences
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.ToastMessages
import com.uniwaylabs.buildo.firebaseDatabase.Database.AdminDB.AdminDatabase
import com.uniwaylabs.buildo.firebaseDatabase.Database.UserDB.UserDatabase
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoryDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialListItemModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseUrls.DatabaseUrls
import com.uniwaylabs.buildo.firebaseDatabase.StorageHandler.DatabaseStorageHandler
import com.uniwaylabs.buildo.ui.commans.ImageSliderFragment.ImageSliderFragment
import com.uniwaylabs.buildo.ui.commans.ImageSliderFragment.ImageSliderFragmentInterface
import com.uniwaylabs.buildo.ui.commans.ImageSliderFragment.SHOW_DELETE
import com.uniwaylabs.buildo.ui.commans.ImageSliderFragment.SLIDING_IMAGES
import com.uniwaylabs.buildo.ui.materialSpecification.FullScreenImageActivity
import com.uniwaylabs.buildo.utility.CustomToast
import java.io.Serializable
import java.util.Date

public const val ACTIVITY_DATA = "ACTIVITY_DATA"
public const val ACTIVITY_RESULT_CODE = "ACTIVITY_RESULT_CODE"

class CreateItemFormActivity :  FragmentActivity(), ImageSliderFragmentInterface {

    private var et_pincode:EditText? = null
    private var et_category: TextView? = null
    private var et_subcategory: TextView? = null
    private var et_item_name: EditText? = null
    private var et_item_price: EditText? = null
    private var et_market_price: EditText? = null
    private var et_item_specification: EditText? = null
    private var et_item_uom: EditText? = null
    private var et_item_policy: EditText? = null
    private var progressBar: LottieAnimationView? = null
    private var statusText: TextView? = null
    private var inStockSwitch: Switch? = null
    private var isPartialQuantityAllowed: Switch? = null

    private val CATEGORY_RESULT_CODE = 101
    private val SUB_CATEGORY_RESULT_CODE = 102
    private val PINCODE_RESULT_CODE = 103
    private var imageContainer: FrameLayout? = null
    private var uriProfile: Uri? = null
    private var imageUrls: ArrayList<String> = ArrayList()
    private var id: String = Date().time.toString()
    var fragment: ImageSliderFragment? = null

    var imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data: Intent? = result.data
        uriProfile = data?.data

        uriProfile?.let { getItemModel()?.path?.let { it1 -> uploadImage(it, "$it1/",Date().time.toString()) } }
    }


    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data: Intent? = result.data
        setTextOnResult(result.resultCode,data)
    }

    private val fullScreenImageActivity = registerForActivityResult(FullScreenImageActivity.Contract(), {})

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_item_form)
        et_pincode = findViewById(R.id.pincode_et)
        et_category = findViewById(R.id.category_et)
        et_subcategory = findViewById(R.id.sub_category_et)
        et_item_name = findViewById(R.id.item_name_et)
        et_item_price = findViewById(R.id.item_price_et)
        et_market_price = findViewById(R.id.item_market_price_et)
        et_item_specification = findViewById(R.id.item_spec_et)
        et_item_uom = findViewById(R.id.item_uom_et)
        et_item_policy = findViewById(R.id.policy_et)
        progressBar = findViewById(R.id.progressbarhistory)
        statusText = findViewById(R.id.text_view_result_h)
        val button = findViewById<AppCompatButton>(R.id.create_button)
        imageContainer = findViewById<View>(R.id.item_image_container) as FrameLayout
        inStockSwitch = findViewById(R.id.in_stock_switch)
        isPartialQuantityAllowed = findViewById(R.id.quantity_measurement_switch)
        showLoading(false, "")
        button.setOnClickListener {
            saveToDB()
        }
        findViewById<AppCompatButton>(R.id.add_image).setOnClickListener {

            if(getItemModel() == null) return@setOnClickListener
            openFileChooser()
        }
        findViewById<ImageButton>(R.id.backbutton).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        setOnClickListeners()
        et_pincode?.setText(BDSharedPreferences.shared.getSelectedPostalAreaCode(this))
        val model =  getSerializable(intent, ACTIVITY_DATA, MaterialListItemModel::class.java)
        set(model)
        findViewById<ImageButton>(R.id.delete_button).setOnClickListener {
            if (model == null) return@setOnClickListener

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete")
            builder.setMessage("Are you sure you want to delete this item?")
            builder.setPositiveButton("YES"){ dialogInterface, which ->
                AdminDatabase<String>().deleteFromDatabase(model?.path ?: "")
            }
            builder.setNeutralButton("CANCEL"){dialogInterface, which ->}
            val alert = builder.create()
            alert.setCancelable(true)
            alert.show()
        }
    }

    private fun set(model: MaterialListItemModel?){
        if(model?.pincode != null){
            et_pincode?.setText((model?.pincode).toString())
        }
        et_subcategory?.text = model?.sub_category
        et_category?.text = model?.category
        et_item_name?.setText(model?.itemName)
        et_item_price?.setText((model?.itemPrice ?: 0).toString())
        et_market_price?.setText((model?.marketPrice ?: 0).toString())
        et_item_specification?.setText(model?.specifications)
        et_item_uom?.setText(model?.unitOfMeasurement)
        et_item_policy?.setText(model?.itemPolicy)
        inStockSwitch?.isSelected = model?.inStock ?: false
        isPartialQuantityAllowed?.isSelected = model?.isPartialQuantityAllowed ?: false
        imageUrls = model?.imageURLs ?: ArrayList()
        id = model?.id ?: Date().time.toString()
    }

    class Contract: ActivityResultContract<MaterialListItemModel, String>(){

        override fun createIntent(context: Context, input: MaterialListItemModel): Intent = Intent(context, CreateItemFormActivity::class.java)
            .putExtra(ACTIVITY_DATA, input)

        override fun parseResult(resultCode: Int, intent: Intent?): String {
            return "Ok"
        }

    }

    override fun onResume() {
        super.onResume()
        setupFragment()
    }

    private fun setOnClickListeners(){
        et_category?.setOnClickListener {
            val pincode = et_pincode?.text.toString().trim()
            if (pincode.isEmpty()){
                Toast.makeText(this, "Please select pin-code to choose category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            openCategorySelector<CategoryListActivity>(path = "$pincode/", CATEGORY_RESULT_CODE)
        }

        et_subcategory?.setOnClickListener {
            val category = et_category?.text.toString() ?: ""
            val pincode = et_pincode?.text.toString().trim()
            if (category.trim().isEmpty()){
                Toast.makeText(this, "Please select category to choose sub category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            openCategorySelector<SubCategoryListActivity>(path = "$pincode/${DatabaseUrls.categories_path}/$category", SUB_CATEGORY_RESULT_CODE)
        }
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
            fragment?.arguments = Bundle().apply { putStringArray(
                SLIDING_IMAGES, imageUrls.toTypedArray()
            ) }.apply { putBoolean(SHOW_DELETE, true) }
            fragment?.let{ supportFragmentManager.beginTransaction().add(R.id.item_image_container, it).commit()}
        }
    }

    private inline fun <reified T>openCategorySelector(path: String, requestCode: Int){

        val intent = Intent(this,T::class.java)
        intent.putExtra(ACTIVITY_DATA,path)
        intent.putExtra(ACTIVITY_RESULT_CODE, requestCode)
        resultLauncher.launch(intent)
    }


    private fun saveToDB(){

        if (imageUrls.isEmpty()){
            Toast.makeText(this, "Please add images", Toast.LENGTH_SHORT).show()
            return
        }
        getItemModel().let { it?.path?.let { it1 -> saveDataToDB(it, it1) } }
    }

    private fun showLoading(show: Boolean, title: String){
        progressBar?.visibility = if (show) View.VISIBLE else View.GONE
        statusText?.text = title
        statusText?.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    private fun saveDataToDB(value: Any, path: String){
        showLoading(true, "Saving...")
        AdminDatabase<String>().setDataToItemDatabase(this, path,value){
            showLoading(false, "")
            finish()
        }
    }

    private fun setTextOnResult(requestCode: Int, intent: Intent?){
        if(requestCode == CATEGORY_RESULT_CODE){
            et_category?.text = intent?.getStringExtra(ACTIVITY_DATA) ?: ""
        }
        else if(requestCode == SUB_CATEGORY_RESULT_CODE){
            et_subcategory?.text = intent?.getStringExtra(ACTIVITY_DATA) ?: ""
        }
    }
    private fun getItemModel(): MaterialListItemModel?{
        val itemName: String = et_item_name?.text.toString().trim()
        val itemPrice: String = et_item_price?.text.toString().trim()
        val marketPrice: String = et_market_price?.text.toString().trim()
        val itemSpec: String = et_item_specification?.text.toString().trim()
        val itemUOM: String = et_item_uom?.text.toString().trim()
        val category = et_category?.text.toString().trim()
        val pincode = et_pincode?.text.toString().trim()
        if (itemName.trim().isEmpty() || itemPrice.trim().isEmpty() || marketPrice.trim().isEmpty() || itemSpec.trim().isEmpty() || itemUOM.trim().isEmpty() || category.trim().isEmpty() || pincode.trim().isEmpty()){
            Toast.makeText(this, "Please fill mandatory fields", Toast.LENGTH_SHORT).show()
            return null
        }

        if (marketPrice.toDouble() < itemPrice.toDouble()){
            Toast.makeText(this, "Item price should be less than market price", Toast.LENGTH_SHORT).show()
            return null
        }

        if(pincode.length > 6){
            Toast.makeText(this, "PinCode should be of 6 digits only", Toast.LENGTH_SHORT).show()
            return null
        }

        val model = MaterialListItemModel()
        model.itemName = itemName
        model.itemPrice = itemPrice.toDouble()
        model.marketPrice = marketPrice.toDouble()
        model.unitOfMeasurement = itemUOM
        model.specifications = itemSpec
        model.category = et_category?.text.toString()
        model.sub_category = et_subcategory?.text.toString()
        model.itemPolicy = et_item_policy?.text.toString()
        model.id = id
        model.imageURLs = imageUrls
        model.pincode = pincode.toInt()
        model.inStock = inStockSwitch?.isChecked ?: true
        model.isPartialQuantityAllowed = isPartialQuantityAllowed?.isChecked ?: false
        val subCategoryPath = if (model.sub_category?.trim().isNullOrEmpty()) DatabaseUrls.material_items_path else "${DatabaseUrls.sub_categories_path}/${model.sub_category}/${DatabaseUrls.material_items_path}"
        model.path = "${model.pincode ?: Date().time}/${DatabaseUrls.categories_path}/${model.category}/$subCategoryPath/$id"
        return model
    }

    override fun onImageTap(position: Int?, url: String?) {
        fullScreenImageActivity.launch(FullScreenImageActivity.ActivityData( imageUrls.toTypedArray(), position))
    }

    override fun onDeleteTap(position: Int?, url: String?) {
        imageUrls = imageUrls.filter { it != url } as ArrayList<String>
        fragment?.resetImage(imageUrls.toTypedArray())
    }


    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        imagePickerLauncher.launch(intent)
    }

    private fun uploadImage(
        uriProfile: Uri,
        storagePath: String,
        dataName: String,
    ) {

        showLoading(true, "Uploading Image...")
        DatabaseStorageHandler.instance?.uploadImageToDatabase(
            this,
            uriProfile, storagePath, dataName
        ) {
            showLoading(false, "")
            imageUrls.add(it)
            fragment?.resetImage(imageUrls.toTypedArray())
            CustomToast.showToast(
                this@CreateItemFormActivity,
                ToastMessages.uploadSuccessfully,
                Toast.LENGTH_LONG
            )
        }
    }
}