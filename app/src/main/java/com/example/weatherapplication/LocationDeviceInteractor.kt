package com.example.weatherapplication

import android.app.Activity
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import io.reactivex.Maybe
import io.reactivex.MaybeEmitter
import io.reactivex.MaybeOnSubscribe
import io.reactivex.disposables.Disposables
import java.util.concurrent.TimeUnit


class LocationDeviceInteractor(activity: Activity) {

    private val provider: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)


    private val lastLocationMaybe = Maybe.create<Location> { emitter ->
        try {
            provider.lastLocation.apply {
                addOnSuccessListener { location ->
                    if (location != null) emitter.onSuccess(location)
                    emitter.onComplete()
                }
                addOnFailureListener { emitter.onError(it) }
            }
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }

    private class UpdateLocationMaybeSubscribe(
        private val provider: FusedLocationProviderClient) : MaybeOnSubscribe<Location> {

        private val locationGPSRequest = LocationRequest().apply {
            numUpdates = 1
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        override fun subscribe(emitter: MaybeEmitter<Location>) {
            Log.d("WTF", "SUBSCRIBED")
            val callback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    Log.d("WTF", "CALLBACK")
                    val location = locationResult?.locations?.find { location -> location != null }
                    if (location != null) {
                        removeLocationUpdates(this)
                        emitter.onSuccess(location)
                    }
                    emitter.onComplete()
                }
            }

            provider.requestLocationUpdates(locationGPSRequest, callback, Looper.getMainLooper())
            emitter.setDisposable(Disposables.fromRunnable { removeLocationUpdates(callback) })
        }

        private fun removeLocationUpdates(callback: LocationCallback) {
            provider.removeLocationUpdates(callback)
        }
    }



    fun getLocation(): Maybe<Location> {
        return lastLocationMaybe
            .switchIfEmpty(Maybe.just(false)
                .flatMap { allow ->
                    Log.d("WTF", "RX2")
                    Maybe.create(UpdateLocationMaybeSubscribe(provider))
                })
    }

}