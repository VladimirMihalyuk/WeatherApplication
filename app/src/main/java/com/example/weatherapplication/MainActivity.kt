package com.example.weatherapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.weatherapplication.network.WeatherAPIClient
import com.example.weatherapplication.todayWeather.TodayFragment
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    private val listOfFragment = listOf(TodayFragment(), ForecastFragment() )
    private val tabTitles = listOf("Today", "Forecast")
    private val tabIcons = listOf(R.drawable.wb_sunny_24px, R.drawable.weather_partly_rainy)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = tabTitles[position]
            tab.setIcon(tabIcons[position])
            viewPager.setCurrentItem(tab.position, true)
        }.attach()


        val client = WeatherAPIClient.getClient()

        client.getForecast(12.5F, 13.5F)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { it -> Log.d("WTF", "$it") }
    }



    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = listOfFragment.size

        override fun createFragment(position: Int): Fragment = listOfFragment[position]
    }
}
