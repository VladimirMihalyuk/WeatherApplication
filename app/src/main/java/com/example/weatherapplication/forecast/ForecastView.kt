package com.example.weatherapplication.forecast

import android.content.Context
import com.example.weatherapplication.forecast.adapter.ForecastListItem

interface ForecastView {

    fun updateList(list: List<ForecastListItem>)

    fun stopShowLoading()

    fun showErrorMessage(text: String)

    fun getContextOfView(): Context?
}