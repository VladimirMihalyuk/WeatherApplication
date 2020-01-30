package com.example.weatherapplication.forecast

import com.example.weatherapplication.BasePresenter
import com.example.weatherapplication.forecast.adapter.toListWithHeaders
import com.example.weatherapplication.isInternetAvailable
import com.example.weatherapplication.network.WeatherAPIClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class ForecastPresenter(private var view: ForecastView?,
                        private val longitude: Float,
                        private val latitude: Float) : BasePresenter {

    private val client = WeatherAPIClient.getClient()


    fun loadData(){
        if(isInternetAvailable(view?.getContextOfView())){
            val disposable = client.getForecast(22.2F, 22.2F)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { forecast ->
                        view?.updateList(forecast.toListOfModels().toListWithHeaders())
                        view?.stopShowLoading()},
                    { error ->
                        view?.showErrorMessage("Server error")
                        view?.stopShowLoading() }
                )
        } else {
            view?.showErrorMessage("Please turn on internet connection and try again")
            view?.stopShowLoading()
        }


    }

    override fun onDestroy() {
        view = null
    }
}