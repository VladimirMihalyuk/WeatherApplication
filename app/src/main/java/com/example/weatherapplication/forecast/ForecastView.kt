package com.example.weatherapplication.forecast

import android.content.Context
import com.example.weatherapplication.BaseView
import com.example.weatherapplication.forecast.adapter.ForecastListItem

interface ForecastView: BaseView {

    fun updateList(list: List<ForecastListItem>)
}