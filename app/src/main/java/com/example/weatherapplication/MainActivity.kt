package com.example.weatherapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.weatherapplication.todayWeather.TodayFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import java.util.*
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class MainActivity : AppCompatActivity() {


    private val listOfFragment = listOf(TodayFragment(), ForecastFragment() )
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


    }

    fun stopShowingRefreshing() {
        swipeLayout.isRefreshing = false
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = listOfFragment.size

        override fun createFragment(position: Int): Fragment = listOfFragment[position]


    }
}
