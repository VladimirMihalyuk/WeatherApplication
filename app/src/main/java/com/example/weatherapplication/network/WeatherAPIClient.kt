package com.example.weatherapplication.network

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private val API_KEY = "85be79e8e28176b6ae2f54c4bb2dcd02"
private val BASE_URL = "http://api.openweathermap.org/data/2.5/"

class WeatherAPIClient{
    companion object{
        private var INSTANCE:OpenWeatherMapAPI? = null

        fun getClient():OpenWeatherMapAPI{
            synchronized(this) {
                if(INSTANCE == null){

                    val httpClient = OkHttpClient.Builder()
                    httpClient.addInterceptor(ApiKeyInterceptor())

                    INSTANCE = Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .baseUrl(BASE_URL)
                        .client(httpClient.build())
                        .build()
                        .create(OpenWeatherMapAPI::class.java)
                }
                return INSTANCE!!
            }

        }
    }
}


private class ApiKeyInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        val originalHttpUrl = original.url
        val url = originalHttpUrl.newBuilder()
            .addQueryParameter("APPID", API_KEY)
            .build()
        val request = original.newBuilder().url(url).build()

        return chain.proceed(request)
    }
}

