package com.example.weatherapplication.forecast

import android.location.Location
import com.example.weatherapplication.BasePresenter
import com.example.weatherapplication.forecast.adapter.toListWithHeaders
import com.example.weatherapplication.isInternetAvailable
import com.example.weatherapplication.network.WeatherAPIClient
import com.google.android.gms.tasks.Task
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class ForecastPresenter(private var view: ForecastView?) : BasePresenter {

    private val client = WeatherAPIClient.getClient()


    fun loadData(task: Task<Location>?){
        if(isInternetAvailable(view?.getContextOfView())){
            task?.let{
                task.addOnSuccessListener { location ->
                    client.getForecast(location.longitude.toFloat(), location.latitude.toFloat())
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
                }
            }

        } else {
            view?.showErrorMessage("Please turn on internet connection and try again")
            view?.stopShowLoading()
        }


    }

    override fun onDestroy() {
        view = null
    }
}