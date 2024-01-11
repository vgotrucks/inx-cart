package com.uniwaylabs.buildo.utility

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.uniwaylabs.buildo.ToastMessages
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseHandler
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseUrls.DatabaseUrls

interface LocationProviderInterface{
    fun onFetchLocation(lat: Double?, long: Double?)
}

class LocationProvider(val activity: Activity,var  mInterface: LocationProviderInterface? ) {
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mLastKnownLocation: Location? = null
    private var locationCallback: LocationCallback? = null

    init {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
    }

    @SuppressLint("MissingPermission")
    fun getDeviceLocation() {
        mFusedLocationProviderClient?.lastLocation
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mLastKnownLocation = task.result
                    if (mLastKnownLocation != null) {
                        mInterface?.onFetchLocation(mLastKnownLocation?.latitude, mLastKnownLocation?.longitude)
                        updateDB(mLastKnownLocation?.latitude, mLastKnownLocation?.longitude)
                    } else {
                        val locationRequest = LocationRequest.create()
                        locationRequest.interval = 1000
                        locationRequest.fastestInterval = 5000
                        locationRequest.priority = Priority.PRIORITY_HIGH_ACCURACY
                        locationCallback = object : LocationCallback() {
                            override fun onLocationResult(locationResult: LocationResult) {
                                super.onLocationResult(locationResult)
                                mLastKnownLocation = locationResult.lastLocation
                                mInterface?.onFetchLocation(mLastKnownLocation?.latitude, mLastKnownLocation?.longitude)
                                updateDB(mLastKnownLocation?.latitude, mLastKnownLocation?.longitude)
                                if (locationCallback != null) {
                                    mFusedLocationProviderClient?.removeLocationUpdates(locationCallback!!)
                                }
                            }
                        }
                        mFusedLocationProviderClient?.requestLocationUpdates(locationRequest, locationCallback as LocationCallback, null)
                    }
                } else {
                    Toast.makeText(
                        activity,
                        ToastMessages.unableGettingLastLocation,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun updateDB(lat: Double?, long: Double?){

        DatabaseHandler.getInstance().saveDataToDatabase(activity, DatabaseUrls.location_latitude_path, lat)
        DatabaseHandler.getInstance().saveDataToDatabase(activity, DatabaseUrls.location_longitude_path, long)
    }
}