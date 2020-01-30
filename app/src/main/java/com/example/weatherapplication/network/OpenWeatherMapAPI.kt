package com.example.weatherapplication.network

import com.example.weatherapplication.network.data.CurrentWeather
import com.example.weatherapplication.network.data.Forecast
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query


interface OpenWeatherMapAPI {

    @GET("weather")
    fun getCurrentWeather(@Query("lon") longitude: Float,
                  @Query("lat") latitude: Float): Observable<CurrentWeather>

    @GET("forecast")
    fun getForecast(@Query("lon") longitude: Float,
                          @Query("lat") latitude: Float): Single<Forecast>
}