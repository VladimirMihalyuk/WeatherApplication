package com.example.weatherapplication.todayWeather

import com.example.weatherapplication.BasePresenter
import com.example.weatherapplication.isInternetAvailable
import com.example.weatherapplication.kelvinToCelsius
import com.example.weatherapplication.network.WeatherAPIClient
import com.example.weatherapplication.network.data.CurrentWeather

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TodayPresenter(private var view: TodayView?,
                     private val longitude: Float,
                     private val latitude: Float) : BasePresenter {

    private var currentWeather: CurrentWeather? = null

    private val client = WeatherAPIClient.getClient()
    fun loadCurrentWeather(){
        if(isInternetAvailable(view?.getContextOfView())){
            val disposable = client.getCurrentWeather(longitude, latitude)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { weather ->
                        view?.fillViews(weather)
                        currentWeather = weather  },
                    { error ->
                        view?.showErrorMessage("Serever error") }
                )
        } else {
            view?.showErrorMessage("Please turn on internet connection and try again")
        }

    }

    fun shareAsText(){
        currentWeather?.let {
            val text = "Current temperature at ${currentWeather?.name} " +
                    "is ${currentWeather?.main?.temp?.kelvinToCelsius()}°C. " +
                    "${currentWeather?.weather?.getOrNull(0)?.main}"
            view?.shareAsText(text)
        }
    }

    override fun onDestroy() {
        view = null
    }
}