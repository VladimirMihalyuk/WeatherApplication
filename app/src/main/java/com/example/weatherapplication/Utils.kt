package com.example.weatherapplication


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


fun Double?.kelvinToCelsius() = (this?.toInt() ?: 273) - 273

private val roundDegree = 360.0
val directions = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
fun windDegreeToDirection(degree: Int): String  =
    directions[((degree % roundDegree)  / (roundDegree / directions.size)). toInt()]

fun isInternetAvailable(context: Context?): Boolean {
    var result = false
    context?.let{
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }
    }

    return result
}

val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

fun String.convertToDate(): Date = format.parse(this)!!


//need to note hardcode lcoale
val sdf = SimpleDateFormat("EEEE", Locale("en"))
fun Date.getDayOfWeek(): String = sdf.format(this ).toUpperCase()

val formatOfTime = SimpleDateFormat("HH:mm")
fun Date.getHoursAsString() = formatOfTime.format(this)