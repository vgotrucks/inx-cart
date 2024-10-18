package com.uniwaylabs.buildo.ui.profile

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.uniwaylabs.buildo.BaseAppCompactActivity
import com.uniwaylabs.buildo.LocalDatabase.BDSharedPreferences
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.firebaseDatabase.Database.AdminDB.AdminDatabase
import com.uniwaylabs.buildo.firebaseDatabase.Database.UserDB.UserDatabase
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.AdminInfoDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.DrawerMenuDataModels.UserAccountModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.GETOrdersListItemsModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseUrls.DatabaseUrls

class UserProfileActivity: BaseAppCompactActivity() {

    var addressTV: TextView? = null
    var mobileNumber: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        addressTV = findViewById(R.id.address_tv)
        mobileNumber = findViewById(R.id.mobile_number)
        findViewById<ImageButton>(R.id.backbutton).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        findViewById<ImageView>(R.id.whatsapp).setOnClickListener {
            val url = "https://api.whatsapp.com/send?phone=917748920542&text=Hey! Please schedule at call with me"
            try {
                this.packageManager.getPackageInfo("com.whatsapp",PackageManager.GET_ACTIVITIES)
                this.startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) })
            }
            catch (e: Exception){
                this.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }
        findViewById<ImageView>(R.id.instabutton).setOnClickListener {
            val url = "https://www.instagram.com/inx.cart"
            try {
                this.packageManager.getPackageInfo("com.instagram.android",PackageManager.GET_ACTIVITIES)
                this.startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) })
            }
            catch (e: Exception){
                this.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }
        fetchUserDetails()
        fetchAdminDetails()
    }

    private fun fetchUserDetails(){
        UserDatabase<String>().getItemsObjectDataSnapshotWithoutEventListener(this, DatabaseUrls.account_path){
            val mData = try {
                it.getValue(UserAccountModel::class.java)
            }catch (e: Exception){ null }
            mobileNumber?.text = mData?.phone ?: "Not found"
        }
    }

    private fun fetchAdminDetails(){
        AdminDatabase<String>().getItemsObjectDataSnapshotWithoutEventListener(this, "${BDSharedPreferences.shared.getSelectedPostalAreaCode(this)}/"){
            val mData = try {
                it.getValue(AdminInfoDataModel::class.java)
            }catch (e: Exception){ null }
            addressTV?.text = mData?.address ?: "Not found"
        }
    }

}