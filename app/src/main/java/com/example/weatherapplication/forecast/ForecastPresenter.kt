package com.example.weatherapplication.forecast

import android.location.Location
import com.example.weatherapplication.BasePresenter
import com.example.weatherapplication.forecast.adapter.toListWithHeaders
import com.example.weatherapplication.isInternetAvailable
import com.example.weatherapplication.network.WeatherAPIClient
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class ForecastPresenter(private var view: ForecastView?) : BasePresenter {

    private val client = WeatherAPIClient.getClient()

    fun loadCurrentWeather(single: Single<Location>?){
        loadData(single,  { view?.stopShowLoadingScreen()}, {})
    }

    fun updateCurrentWeather(single: Single<Location>?){
        loadData(single,  {view?.stopShowUpdating(); view?.stopShowLoadingScreen() },
            {view?.stopShowUpdating()})
    }

    private fun loadData(single: Single<Location>?, stopShowLoading: () -> Unit,
                         stopShowOnError: () -> Unit){
        if(isInternetAvailable(view?.getContextOfView())){
            single?.let{
                single.subscribe(
                    { location ->
                        if(location != null){
                            client.getForecast(location.longitude.toFloat(), location.latitude.toFloat())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                    { forecast ->
                                        view?.updateList(forecast.toListOfModels().toListWithHeaders())
                                        stopShowLoading()
                                    },
                                    { error ->
                                        view?.showErrorMessage("Server error") }
                                )
                        }else{
                            view?.showErrorMessage("Probably the GPS can not locate you")
                        }
                    },
                    { _ ->
                        view?.showErrorMessage("Probably the GPS can not locate you")
                    }
                )
            }

        } else {
            view?.showErrorMessage("Please turn on internet connection and try again")
        }
        stopShowOnError()
    }

    override fun onDestroy() {
        view = null
    }
}