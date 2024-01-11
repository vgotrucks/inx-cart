package com.uniwaylabs.buildo.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.uniwaylabs.buildo.LocalDatabase.BDSharedPreferences
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.VTAppUpdate
import com.uniwaylabs.buildo.databinding.ActivityMainBinding
import com.uniwaylabs.buildo.firebaseDatabase.Database.UserDB.UserDatabase
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseHandler
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.PermissionModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseUrls.DatabaseUrls
import com.uniwaylabs.buildo.ui.adminForms.CreateItemFormActivity
import com.uniwaylabs.buildo.ui.profile.UserProfileActivity
import com.uniwaylabs.buildo.utility.DatabaseDeserializer


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var updateAppInstance: VTAppUpdate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
//            )
//        )
//
//       // setupActionBarWithNavController(navController, appBarConfiguration)

        updateAppInstance = VTAppUpdate.getInstance();
        updateAppInstance?.checkForUpdateAvailability(this);
        val profileImage = binding.actionBar.profile
        profileImage.setOnClickListener {

            if (BDSharedPreferences.shared.getPermissionModel(this)?.isCreate == false){
                val indent = Intent(this, UserProfileActivity::class.java)
                startActivity(indent)
            }
            else{
                val indent = Intent(this, CreateItemFormActivity::class.java)
                startActivity(indent)
            }
        }
        navView.setupWithNavController(navController)
        fetchPermissions()
        setPushToken()
    }

    private fun fetchPermissions(){

        UserDatabase<String>().getItemsObjectDataSnapshot(this, DatabaseUrls.permissions){
            val mData = try {
                it.getValue(PermissionModel::class.java)
            }catch (e: Exception){ null }

            if (mData == null) return@getItemsObjectDataSnapshot
            BDSharedPreferences.shared.savePermissionModel(this, mData);
        }

    }


    private fun setPushToken() {

        UserDatabase<String>().getItemsObjectDataSnapshotWithoutEventListener(this, DatabaseUrls.token_path){
            if (it.value != null && DatabaseDeserializer.shared.convertDataToString(it).trim().isNullOrEmpty())
                UserDatabase<String>().setDataToItemDatabase(this, DatabaseUrls.token_path, BDSharedPreferences.shared.getStringData(this, BDSharedPreferences.shared.PUSH_TOKEN) ?: ""){}
        }

    }
}