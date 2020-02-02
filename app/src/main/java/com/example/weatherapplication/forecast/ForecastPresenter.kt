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

    fun loadCurrentWeather(task: Task<Location>?){
        loadData(task,  { view?.stopShowLoadingScreen()}, {})
    }

    fun updateCurrentWeather(task: Task<Location>?){
        loadData(task,  {view?.stopShowUpdating(); view?.stopShowLoadingScreen() },
            {view?.stopShowUpdating()})
    }

    private fun loadData(task: Task<Location>?,  stopShowLoading: () -> Unit,
                         stopShowOnError: () -> Unit){
        if(isInternetAvailable(view?.getContextOfView())){
            task?.let{
                task.addOnSuccessListener { location ->
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
                }
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