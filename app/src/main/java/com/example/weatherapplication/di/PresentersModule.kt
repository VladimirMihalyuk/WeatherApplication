package com.example.weatherapplication.di

import com.example.weatherapplication.database.DatabaseDAO
import com.example.weatherapplication.forecast.ForecastPresenter
import com.example.weatherapplication.network.OpenWeatherMapAPI
import com.example.weatherapplication.todayWeather.TodayPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class PresentersModule{
    @Provides
    @Singleton
    fun provideTodayPresenter(client: OpenWeatherMapAPI, database: DatabaseDAO)
            = TodayPresenter(client, database)

    @Provides
    @Singleton
    fun provideForecastPresenter(client: OpenWeatherMapAPI, database: DatabaseDAO)
            = ForecastPresenter(client, database)
}