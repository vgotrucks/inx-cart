package com.uniwaylabs.buildo.utility

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.GenericTypeIndicator
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.DrawerMenuDataModels.UserAccountModel
import java.io.Serializable
import java.util.Objects
import kotlin.reflect.typeOf

class DataSnapDeserializer<T>{

    fun deserializeMap(data: DataSnapshot): Map<String, T>? {
        val mData: Map<String, T>?
        val arrayData: GenericTypeIndicator<Map<String, T>> =
            object : GenericTypeIndicator<Map<String, T>>() {}
        mData = try {
            data.getValue(arrayData)
        } catch (e: Exception) {
            null
        }
        return mData
    }

    fun deserializeObject(data: DataSnapshot): T? {
        var mData: T?
        val arrayData: GenericTypeIndicator<T> =
            object : GenericTypeIndicator<T>() {}
        mData = try {
            data.getValue(arrayData)
        } catch (e: java.lang.Exception) {
            null
        }
        return mData
    }


}