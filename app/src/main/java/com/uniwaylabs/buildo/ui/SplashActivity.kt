package com.uniwaylabs.buildo.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.uniwaylabs.buildo.BaseAppCompactActivity
import com.uniwaylabs.buildo.LocalDatabase.BDSharedPreferences
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.firebaseDatabase.Database.AdminDB.AdminDatabase
import com.uniwaylabs.buildo.firebaseDatabase.Database.DBService.FirebaseDatabase
import com.uniwaylabs.buildo.firebaseDatabase.Database.UserDB.UserDatabase
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseHandler
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoriesListDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.DrawerMenuDataModels.UserAccountModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.PermissionModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseUrls.DatabaseUrls
import com.uniwaylabs.buildo.firebaseDatabase.Repositories.RepositoryData
import com.uniwaylabs.buildo.ui.authentication.GenerateOTPActivity
import com.uniwaylabs.buildo.ui.authentication.SignInActivity

class SplashActivity : BaseAppCompactActivity() {

    private var mAuthStateListener: AuthStateListener? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        mAuthStateListener = AuthStateListener { initmAuth() }
        fetchList()
    }

    fun fetchList(){

        val pincode = BDSharedPreferences.shared.getSelectedPostalAreaCode(this)

        if (pincode.isNullOrEmpty()) return

        AdminDatabase<CategoriesListDataModel>().getItemsObjectDataSnapshot(this,"$pincode/"){}
    }

    override fun onStart() {
        super.onStart()
        mAuth = FirebaseAuth.getInstance()
        mAuthStateListener?.let { mAuth?.addAuthStateListener(it) }
        BDSharedPreferences.shared.saveSelectedPostalAreaCode(this, "476337")
    }

    private fun startSplash() {
        Handler().postDelayed({
            val intent = Intent(applicationContext, MainActivity::class.java)
            if (isNetworkAvailable(this)) {
                startActivity(intent)
                finish()
            }
        }, SPLASH_TIME.toLong())
    }


    private fun initmAuth() {
        val user: FirebaseUser? = mAuth?.currentUser

        if (user == null) {
            navigateToOTPVerification()
            return
        }

        UserDatabase<String>().getItemsObjectDataSnapshotWithoutEventListener(this, "",){
                if(it.exists()){
                    startSplash()
                    return@getItemsObjectDataSnapshotWithoutEventListener
                }
               navigateToOTPVerification()
        }
    }

    private fun navigateToOTPVerification(){
        val intent = Intent(applicationContext, GenerateOTPActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStop() {
        super.onStop()
        if (mAuthStateListener != null) {
            if (mAuth != null) {
                mAuth!!.removeAuthStateListener(mAuthStateListener!!)
            }
        }
    }


    private fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    companion object {
        private const val SPLASH_TIME = 1000
    }

}