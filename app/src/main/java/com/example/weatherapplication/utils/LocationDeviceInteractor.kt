package com.example.weatherapplication.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.SingleOnSubscribe

class LocationDeviceInteractor(activity: Activity) {

    private val locationManager =
        activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun  locationSingle() : Single<Location> {
        return Single.create(LocationSingleSubscribe(locationManager))
    }

    private inner class LocationSingleSubscribe(
        private val manager: LocationManager) : SingleOnSubscribe<Location> {



        @SuppressLint("MissingPermission")
        override fun subscribe(emitter: SingleEmitter<Location>) {

            val listener: LocationListener = object: LocationListener {
                override fun onLocationChanged(location: Location?) {
                    if(location != null){
                        removeUpdate(this)
                        emitter.onSuccess(location)
                    }

                }
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String?) {}
                override fun onProviderDisabled(provider: String?) {}
            }

            manager.requestSingleUpdate(
                LocationManager.GPS_PROVIDER, listener, Looper.getMainLooper())
        }
        private fun removeUpdate(listener: LocationListener){
            manager.removeUpdates(listener)
        }

    }
}