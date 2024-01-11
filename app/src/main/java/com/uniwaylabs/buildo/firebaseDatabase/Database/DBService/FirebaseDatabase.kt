package com.uniwaylabs.buildo.firebaseDatabase.Database.DBService

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.uniwaylabs.buildo.ToastMessages
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.DrawerMenuDataModels.UserAccountModel
import com.uniwaylabs.buildo.utility.DataSnapDeserializer

class FirebaseDatabase<T> {

    fun getItemsMap( dbReference: DatabaseReference?,
        context: Activity,
        path: String,
        mInterface:  (data: Map<String, T>?) -> Unit
    ) {

        dbReference?.child(path)
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mInterface(DataSnapDeserializer<T>().deserializeMap(snapshot))
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        context.applicationContext,
                        "Couldn't load data,Please check your internet connectivity",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    fun getItemObject(dbReference: DatabaseReference?,
        context: Activity,
        path: String,
        mInterface: (data: T?) -> Unit
    ) {
        dbReference?.child(path)
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mInterface(DataSnapDeserializer<T>().deserializeObject(snapshot))
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        context.applicationContext,
                        "Couldn't load data,Please check your internet connectivity",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    fun getItemObjectDataSnapShot(dbReference: DatabaseReference?,
                      context: Context,
                      path: String,
                      mInterface: (data: DataSnapshot) -> Unit
    ) {
        dbReference?.child(path)
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mInterface(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        context.applicationContext,
                        "Couldn't load data,Please check your internet connectivity",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    fun getItemObjectDataSnapShotWithoutEventListener(dbReference: DatabaseReference?,
                                  context: Context,
                                  path: String,
                                  mInterface: (data: DataSnapshot) -> Unit
    ) {
        dbReference?.child(path)
            ?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mInterface(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        context.applicationContext,
                        "Couldn't load data,Please check your internet connectivity",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    /**This is save data against unique id id: data*/
    fun pushDataToItemDatabase(dbReference: DatabaseReference?,
        context: Context,
        path: String,
        value: Any?,
        mInterface: (reference: DatabaseReference) -> Unit
    ) {
        dbReference?.child(path)?.push()?.setValue(
            value
        ) { error, ref ->
            if (error != null) {
                Toast.makeText(
                    context.applicationContext,
                    ToastMessages.dataSaveError,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                mInterface(ref)
            }
        }
    }

    fun setDataToItemDatabase(dbReference: DatabaseReference?,
        context: Context,
        path: String,
        value: Any?,
        mInterface: (reference: DatabaseReference) -> Unit
    ) {
        dbReference?.child(path)?.setValue(
            value
        ) { error, ref ->
            if (error != null) {
                Toast.makeText(
                    context.applicationContext,
                    ToastMessages.dataSaveError,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                mInterface(ref)
            }
        }
    }

    fun deleteFromDatabase(dbReference: DatabaseReference?,
                                  path: String,
    ) {
        dbReference?.child(path)?.removeValue()
    }
}