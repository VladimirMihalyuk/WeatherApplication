package com.example.weatherapplication.todayWeather

import com.example.weatherapplication.interfaces.BaseView
import com.example.weatherapplication.database.Today

interface TodayView: BaseView {
    fun fillViews(today: Today)

    fun shareAsText(text: String)
}