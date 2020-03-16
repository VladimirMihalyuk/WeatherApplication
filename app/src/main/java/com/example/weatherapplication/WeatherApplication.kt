package com.example.weatherapplication

import android.app.Application

import com.example.weatherapplication.di.AppComponent
import com.example.weatherapplication.di.DaggerAppComponent


class WeatherApplication : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = initDagger(this)
    }

    private fun initDagger(app: Application): AppComponent =
        DaggerAppComponent.builder()
            .application(this)
            .build()

}