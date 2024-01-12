package com.uniwaylabs.buildo.ui.orderDetailsForm

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.airbnb.lottie.LottieAnimationView
import com.uniwaylabs.buildo.BDDialogs.VTDialogViews
import com.uniwaylabs.buildo.BaseAppCompactActivity
import com.uniwaylabs.buildo.LocalDatabase.BDSharedPreferences
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.ToastMessages
import com.uniwaylabs.buildo.firebaseDatabase.Database.AdminDB.AdminDatabase
import com.uniwaylabs.buildo.firebaseDatabase.Database.UserDB.UserDatabase
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoriesListWithItemDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialListItemModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.OrderItemDetailsDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.UserOrderDetails
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseUrls.DatabaseUrls
import com.uniwaylabs.buildo.firebaseDatabase.Repositories.RepositoryData
import com.uniwaylabs.buildo.ui.SuccessActivity
import com.uniwaylabs.buildo.userPermissions.UserPermissionHandler
import com.uniwaylabs.buildo.utility.LocationProvider
import com.uniwaylabs.buildo.utility.LocationProviderInterface
import java.util.Date


class OrderDetailsFormActivity: BaseAppCompactActivity() {


    object OrderDetailsFormActivity{
        var ITEM_DATA_CONSTANT: String = "ITEM_DATA"
    }

    private var etName: EditText? = null
    private var etMobileNumber: EditText? = null
    private var etAddress: EditText? = null
    private var etPincode: TextView? = null
    private var progressBar: LottieAnimationView? = null
    private val LOCATION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details_form)
        progressBar = findViewById(R.id.progressbarhistory)
        etName = findViewById(R.id.name)
        etMobileNumber = findViewById(R.id.mobile_number)
        etAddress = findViewById(R.id.address_et)
        etPincode = findViewById(R.id.pincode)
        etPincode?.text = BDSharedPreferences.shared.getSelectedPostalAreaCode(this)
        findViewById<AppCompatButton>(R.id.order_button).setOnClickListener {
            placeOrder()
        }
        findViewById<ImageButton>(R.id.backbutton).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        findViewById<ImageButton>(R.id.currentLocation).setOnClickListener {
            getLocation()
        }
        fetchList()
    }

    private fun fetchList(){

        showLoading(true, "Fetching...")
        UserDatabase<CategoriesListWithItemDataModel>().getItemsObjectDataSnapshotWithoutEventListener(this,"${DatabaseUrls.user_order_details_path}/"){
            showLoading(false, title = "")

            val mData = try {
                it.getValue(UserOrderDetails::class.java)
            }catch (e: Exception){ null }
            set(mData)
        }
    }

    private fun placeOrder(){
        val model = getUserDetailsModel() ?: return
        showLoading(true, "")
        UserDatabase<String>().setDataToItemDatabase(this, "${DatabaseUrls.user_order_details_path}/",model){}
        saveToOrders(model)
    }

    private fun saveToOrders(model: UserOrderDetails){
        showLoading(true, "")
        val pincode = BDSharedPreferences.shared.getSelectedPostalAreaCode(this)
        UserDatabase<Map<String, Any?>>().getItemsObjectDataSnapshotWithoutEventListener(this, "${DatabaseUrls.cart_list_path}/$pincode/") {

            val mData = try {
                it.getValue(CategoriesListWithItemDataModel::class.java)
            }catch (e: Exception){ null }

            val postData = OrderItemDetailsDataModel()
            postData.orderDetails = model
            postData.materials = mData
            val orderDate = Date().time
            postData.orderedDate = orderDate


            val list = mData?.categories?.values?.toTypedArray() ?: emptyArray()
            var amount: Double = 0.0
            var originalAmount = 0.0
            for(model in list ?: emptyArray()){
                var items: ArrayList<MaterialListItemModel> =
                    (model?.subCategories?.flatMap { (it?.value?.material_items ?: emptyMap()).values.asIterable() } ?: ArrayList()) as ArrayList<MaterialListItemModel>
                items?.addAll((model?.material_items ?: emptyMap()).values?.toMutableList() ?: ArrayList())
                for(item in items?.toTypedArray() ?: emptyArray()){
                    amount += ((item.itemPrice ?: 0.0) * (item.quantity ?: 1.0))
                    originalAmount += ((item.marketPrice ?: 0.0) * (item.quantity ?: 1.0))
                }
            }

            postData.invoiceAmount = amount
            postData.offAmount = originalAmount - amount
            val orderID = "ODR${(orderDate/10000).toInt()}"
            postData.orderID = orderID
            val orderPath = "$pincode/${DatabaseUrls.placed_orders}/${RepositoryData.getInstance().userId}/${DatabaseUrls.orders}/$orderID"
            postData.materialListPath = "$orderPath/materials"
            AdminDatabase<Map<String, Any?>>().setDataToItemDatabase(
                this, orderPath, postData) {
                showLoading(false, "")
                UserDatabase<String>().deleteFromDatabase(DatabaseUrls.cart_list_path)
                navigateToSuccessView()
            }
        }
    }

    private fun navigateToSuccessView(){
        startActivity(Intent(this,SuccessActivity::class.java))
    }

    fun set(data: UserOrderDetails?){

        if (data == null) return
        etName?.setText(data.receiverName ?: "")
        etMobileNumber?.setText(data.mobileNumber ?: "")

        if (BDSharedPreferences.shared.getSelectedPostalAreaCode(this) == data.pincode){
            etAddress?.setText(data.address ?: "")
        }

    }

    private fun getUserDetailsModel(): UserOrderDetails?{

        val name = etName?.text.toString().trim()
        val mobileNumber = etMobileNumber?.text.toString().trim()
        val address = etAddress?.text.toString().trim()
        val pincode = etPincode?.text.toString().trim()

        if (name.isEmpty() || mobileNumber.isEmpty() || address.isEmpty() || pincode.isEmpty() ){
            Toast.makeText(this, "Please fill all mandatory fields", Toast.LENGTH_SHORT).show()
            return null
        }

        if(mobileNumber.count() != 10){
            Toast.makeText(this, "Mobile number is incorrect", Toast.LENGTH_SHORT).show()
            return null
        }

        val model = UserOrderDetails()
        model.receiverName = name
        model.mobileNumber = mobileNumber
        model.address = address
        model.pincode = pincode
        model.userID = RepositoryData.getInstance().userId
        return  model
    }


    fun showLoading(show: Boolean, title: String){
        progressBar?.visibility = if (show) View.VISIBLE else View.GONE
    }


    class Contract: ActivityResultContract<String, String>(){

        override fun createIntent(context: Context, input: String): Intent = Intent(context, com.uniwaylabs.buildo.ui.orderDetailsForm.OrderDetailsFormActivity::class.java)
            .putExtra(OrderDetailsFormActivity.ITEM_DATA_CONSTANT, input)

        override fun parseResult(resultCode: Int, intent: Intent?): String {
            return "Ok"
        }

    }

    fun getLocation(){
        UserPermissionHandler.permissionHandler.checkPermissionForLocation(
            this, LOCATION_REQUEST_CODE
        ) {
            setLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (VTDialogViews.shared.dialog != null) VTDialogViews.shared.dialog.cancel()
                    setLocation()
            } else {
                VTDialogViews.shared.showDialogForLocationPermissionNotGranted(this)
                Toast.makeText(
                    applicationContext,
                    ToastMessages.locationPermissionNotGranted,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setLocation(){

         LocationProvider(this, object: LocationProviderInterface{
            override fun onFetchLocation(lat: Double?, long: Double?) {

                if(lat == null || long == null){ return }
                val address = getAddressFromLatLng(lat, long);
                if(address != null)
                {
                    var _address = address?.getAddressLine(0);

                    if( _address.isNullOrEmpty()){
                        _address = address.subAdminArea +","+address.adminArea +","+address.postalCode;
                    }
                    val postalCode = address.postalCode ?: ""
                    if(postalCode == BDSharedPreferences.shared.getSelectedPostalAreaCode(this@OrderDetailsFormActivity)){
                        etAddress?.setText(_address)
                    }
                    else{
                        Toast.makeText(this@OrderDetailsFormActivity, "Sorry currently we are not serving at this location", Toast.LENGTH_SHORT).show()
                    }

                }
            }

        }).getDeviceLocation()
    }

    private fun getAddressFromLatLng(lat: Double, long: Double): Address? {
        val geocoder = Geocoder(this)
        val addresses: List<Address>?
        return try {
            addresses = geocoder.getFromLocation(lat, long, 5)
            addresses?.get(0)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            null
        }
    }
}