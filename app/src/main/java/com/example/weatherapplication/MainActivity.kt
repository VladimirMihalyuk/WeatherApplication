package com.example.weatherapplication

import android.Manifest
import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.weatherapplication.todayWeather.TodayFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weatherapplication.forecast.ForecastFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.reactivex.subjects.PublishSubject
import androidx.appcompat.app.AlertDialog
import permissions.dispatcher.*
import android.provider.Settings
import android.view.View
import com.google.android.material.snackbar.Snackbar

@RuntimePermissions
class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val listOfFragment = listOf(TodayFragment(),
        ForecastFragment()
    )
    private val tabTitles = listOf("Today", "Forecast")
    private val tabIcons = listOf(R.drawable.wb_sunny_24px, R.drawable.weather_partly_rainy)


    private lateinit var swipeLayout:SwipeRefreshLayout
    var refreshingEvents: PublishSubject<Boolean> = PublishSubject.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeLayout = findViewById(R.id.swipe_container)

        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter


        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = tabTitles[position]
            tab.setIcon(tabIcons[position])
            viewPager.setCurrentItem(tab.position, true)
        }.attach()


        swipeLayout.setOnRefreshListener {
            refreshingEvents.onNext(true)
        }

        getLocationWithPermissionCheck()
    }

    fun stopShowingRefreshing() {
        swipeLayout.isRefreshing = false
    }


    @NeedsPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    fun getLocation(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                Log.d("WTF", "$location")
            }
    }

    @OnShowRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
    fun showRationaleForPhoneCall(request: PermissionRequest) {
        AlertDialog.Builder(this)
            .setTitle("Geolocation permission")
            .setMessage("Need your location for work")
            .setPositiveButton("Ok") { _,   _ -> request.proceed() }
            .setNegativeButton("Cancel") { _, _ -> request.cancel() }
            .show()
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_COARSE_LOCATION)
    fun onCameraDenied() {
        showSnackBar()
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_COARSE_LOCATION)
    fun onCameraNeverAskAgain() {
        showSnackBar()
    }

    fun showSnackBar(){
        val mySnackbar = Snackbar.make(swipeLayout, "Please open settings and turn on permission",
            Snackbar.LENGTH_LONG)
        mySnackbar.setAction("Open", OpenSettings())
        mySnackbar.show()
    }


    private inner class OpenSettings : View.OnClickListener {

        override fun onClick(v: View) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivityForResult(intent, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        getLocationWithPermissionCheck()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        onRequestPermissionsResult(requestCode, grantResults)
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = listOfFragment.size

        override fun createFragment(position: Int): Fragment = listOfFragment[position]
    }
}
