package com.example.weatherapplication.di

import com.example.weatherapplication.network.WeatherAPIClient
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
class NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient() = WeatherAPIClient.okHttpClient()

    @Provides
    @Singleton
    fun provideWeatherApi(okHttpClient: OkHttpClient)
            = WeatherAPIClient.provideApi(okHttpClient)
}