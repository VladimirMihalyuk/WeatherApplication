package com.example.weatherapplication.todayWeather

import com.example.weatherapplication.network.data.CurrentWeather

interface TodayView {
    fun fillViews(currentWeather: CurrentWeather)
    fun shareAsText(text: String)
    fun showErrorMessage()
}