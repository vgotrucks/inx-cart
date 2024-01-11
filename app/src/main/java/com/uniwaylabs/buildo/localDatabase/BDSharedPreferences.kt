package com.uniwaylabs.buildo.LocalDatabase

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.gson.Gson
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.DrawerMenuDataModels.UserAccountModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.PermissionModel


class BDSharedPreferences {
    private val KEY = "Code"
    private val ACCOUNT_DATA_KEY = "AccountKey"
    var ACCOUNT_DATA = "AccountData"
    private var POSTALCODE = "POSTALCODE"
    private var PERMISSION = "PERMISSION"
    public var PUSH_TOKEN = "PUSH-NOTIF_TOKEN"

    fun saveData(activity: Context, suitName: String?, data: Int) {
        val sharedPreferences =
            activity.applicationContext.getSharedPreferences(suitName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(KEY, data)
        editor.apply()
    }

    fun getIntData(activity: Context, suitName: String?): Int {
        val sharedPreferences = activity.getSharedPreferences(suitName, Context.MODE_PRIVATE)
        return sharedPreferences?.getInt(KEY, 0) ?: 0
    }


    fun getSelectedPostalAreaCode(activity: Context): String{
        return getStringData(activity, POSTALCODE) ?: ""
    }

    fun saveSelectedPostalAreaCode(activity: Context, pincode: String){
        saveData(activity,POSTALCODE,pincode)
    }

    fun savePermissionModel(activity: Context, model: PermissionModel){
        val sharedPreferences =
            activity.applicationContext.getSharedPreferences(PERMISSION, MODE_PRIVATE)
        if (sharedPreferences != null) {
            val gson = Gson()
            val serializedData: String = gson.toJson(model)
            val editor = sharedPreferences.edit()
            editor.putString(PERMISSION, serializedData)
            editor.apply()
        }
    }

    fun getPermissionModel(activity: Context): PermissionModel? {
        val sharedPreferences = activity.getSharedPreferences(PERMISSION, MODE_PRIVATE)
        if (sharedPreferences != null) {
            val gson = Gson()
            return gson.fromJson(sharedPreferences.getString(PERMISSION, ""), PermissionModel::class.java)
        }
        return null
    }

    fun saveData(activity: Context, suitName: String?, data: String?) {
        val sharedPreferences = activity.getSharedPreferences(suitName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY, data)
        editor.apply()
    }

    fun getStringData(activity: Context, suitName: String?): String? {
        val sharedPreferences = activity.getSharedPreferences(suitName, Context.MODE_PRIVATE)
        return if (sharedPreferences != null) {
            sharedPreferences.getString(KEY, "")
        } else ""
    }

    fun saveAccountData(activity: Context, userAccountModel: UserAccountModel?) {
        val sharedPreferences =
            activity.applicationContext.getSharedPreferences(ACCOUNT_DATA, MODE_PRIVATE)
        if (sharedPreferences != null) {
            val gson = Gson()
            val serializedData: String = gson.toJson(userAccountModel)
            val editor = sharedPreferences.edit()
            editor.putString(ACCOUNT_DATA_KEY, serializedData)
            editor.apply()
        }
    }

    fun getAccountData(activity: Context): UserAccountModel? {
        val sharedPreferences = activity.getSharedPreferences(ACCOUNT_DATA, MODE_PRIVATE)
        if (sharedPreferences != null) {
            val gson = Gson()
            return gson.fromJson(
                sharedPreferences.getString(ACCOUNT_DATA_KEY, ""),
                UserAccountModel::class.java
            )
        }
        return null
    }

    companion object {
        @JvmField
        var shared = BDSharedPreferences()
        var ACTIVITY_STATUS = "ActivityStatusUser"
    }
}