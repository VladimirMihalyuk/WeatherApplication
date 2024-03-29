package com.example.weatherapplication.activity

import android.Manifest
import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.weatherapplication.todayWeather.TodayFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weatherapplication.forecast.ForecastFragment
import io.reactivex.subjects.PublishSubject
import androidx.appcompat.app.AlertDialog
import android.provider.Settings
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.example.weatherapplication.R
import com.example.weatherapplication.utils.LocationDeviceInteractor
import com.example.weatherapplication.utils.isLocationAvailable
import com.google.android.material.snackbar.Snackbar
import permissions.dispatcher.*
import io.reactivex.Single


@RuntimePermissions
class MainActivity : AppCompatActivity() {


    var location: Single<Location>? = null

    private val listOfFragment = listOf<Fragment>(TodayFragment(),
        ForecastFragment()
    )

    private val listOfTitles = mutableListOf("Today", "Today")

    fun updateTitle(string: String){
        listOfTitles[1] = string
    }

    private val tabTitles = listOf("Today", "Forecast")
    private val tabIcons = listOf(
        R.drawable.wb_sunny_24px,
        R.drawable.weather_partly_rainy
    )


    private lateinit var swipeLayout:SwipeRefreshLayout
    var refreshingEvents: PublishSubject<Boolean> = PublishSubject.create()


    lateinit var interractor: LocationDeviceInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeLayout = findViewById(R.id.swipe_container)

        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                toolbar_title.setText(listOfTitles[position])
            }
        })


        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = tabTitles[position]
            tab.setIcon(tabIcons[position])
            viewPager.setCurrentItem(tab.position, true)
        }.attach()


        swipeLayout.setOnRefreshListener {
            getLocationFromServiceWithPermissionCheck()
            refreshingEvents.onNext(true)
        }

        getLocationFromServiceWithPermissionCheck()
    }

    fun stopShowingRefreshing() {
        swipeLayout.isRefreshing = false
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION )
    fun getLocationFromService(){
        if(isLocationAvailable(this)){
            interractor = LocationDeviceInteractor(this)

            location = interractor.locationSingle()
        } else {
            showSnackBar()
        }
    }


    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    fun showRationale(request: PermissionRequest) {
        AlertDialog.Builder(this)
            .setTitle("Geolocation permission")
            .setMessage("Need your location for work")
            .setPositiveButton("Ok") { _,   _ -> request.proceed() }
            .setNegativeButton("Cancel") { _, _ -> request.cancel() }
            .show()
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    fun onCameraDenied() {
        showSnackBarWithAction()
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    fun onCameraNeverAskAgain() {
        showSnackBarWithAction()
    }

    private fun showSnackBarWithAction(){
        val mySnackbar = Snackbar.make(swipeLayout, "Please open settings and turn on permission",
            Snackbar.LENGTH_LONG)
        mySnackbar.setAction("Open", OpenSettings())
        mySnackbar.show()
    }

    private fun showSnackBar(){
        Snackbar.make(swipeLayout, "Please turn on GPS",
            Snackbar.LENGTH_LONG).show()
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
        getLocationFromServiceWithPermissionCheck()
        refreshingEvents.onNext(true)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
        refreshingEvents.onNext(true)
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = listOfFragment.size

        override fun createFragment(position: Int): Fragment = listOfFragment[position]
    }
}
