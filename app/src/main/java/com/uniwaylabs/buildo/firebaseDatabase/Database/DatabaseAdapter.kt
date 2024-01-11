package com.uniwaylabs.buildo.firebaseDatabase.Database

import android.app.Activity
import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.uniwaylabs.buildo.firebaseDatabase.Database.DBService.FirebaseDatabase

public open class DatabaseAdapter<T> {

    fun getItemsMap(
        context: Activity,
        path: String,
        mInterface: (data: Map<String, T>?) -> Unit
    ){
        FirebaseDatabase<T>().getItemsMap(getDatabaseReference(),context,path,mInterface)
    }

    fun getItemsObject(
        context: Activity,
        path: String,
        mInterface: (data: T?) -> Unit
    ){
        FirebaseDatabase<T>().getItemObject(getDatabaseReference(),context,path,mInterface)
    }

    fun getItemsObjectDataSnapshot(
        context: Context,
        path: String,
        mInterface: (data: DataSnapshot) -> Unit
    ){
        FirebaseDatabase<T>().getItemObjectDataSnapShot(getDatabaseReference(),context,path,mInterface)
    }

    fun getItemsObjectDataSnapshotWithoutEventListener(
        context: Context,
        path: String,
        mInterface: (data: DataSnapshot) -> Unit
    ){
        FirebaseDatabase<T>().getItemObjectDataSnapShotWithoutEventListener(getDatabaseReference(),context,path,mInterface)
    }

    fun setDataToItemDatabase(context: Context,
                              path: String,
                              value: Any?,
                              mInterface: (reference: DatabaseReference) -> Unit
    ){
        FirebaseDatabase<T>().setDataToItemDatabase(getDatabaseReference(),context,path,value,mInterface)
    }

    fun deleteFromDatabase(path: String) {
        FirebaseDatabase<T>().deleteFromDatabase(getDatabaseReference(), path)
    }

    //OVERRIDE METHOD
    public open fun getDatabaseReference(): DatabaseReference?{
        return null
    }
}