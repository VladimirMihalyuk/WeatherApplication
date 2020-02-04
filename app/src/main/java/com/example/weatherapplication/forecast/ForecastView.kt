package com.example.weatherapplication.forecast

import com.example.weatherapplication.interfaces.BaseView
import com.example.weatherapplication.forecast.adapter.ForecastListItem

interface ForecastView: BaseView {

    fun updateList(list: List<ForecastListItem>)
}