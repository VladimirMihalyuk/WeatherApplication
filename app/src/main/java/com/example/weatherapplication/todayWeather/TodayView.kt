package com.example.weatherapplication.todayWeather

import android.content.Context
import com.example.weatherapplication.BaseView
import com.example.weatherapplication.network.data.CurrentWeather

interface TodayView: BaseView {
    fun fillViews(currentWeather: CurrentWeather)

    fun shareAsText(text: String)
}