package com.example.weatherapplication.forecast

import android.location.Location
import android.util.Log
import com.example.weatherapplication.interfaces.BasePresenter
import com.example.weatherapplication.database.DatabaseDAO
import com.example.weatherapplication.database.Forecast
import com.example.weatherapplication.forecast.adapter.toListWithHeaders
import com.example.weatherapplication.network.OpenWeatherMapAPI
import com.example.weatherapplication.utils.isInternetAvailable
import com.example.weatherapplication.utils.toListOfForecast
import com.example.weatherapplication.utils.toListOfForecastModels
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject


class ForecastPresenter @Inject constructor (private val client: OpenWeatherMapAPI,
                                              private val database: DatabaseDAO) : BasePresenter {


    private var view: ForecastView? = null

    fun setView(view:ForecastView){
        this.view = view
    }


    fun loadCurrentWeather(single: Single<Location>?){
        loadData(single,  { view?.stopShowLoadingScreen()}, {}, true)
    }

    fun updateCurrentWeather(single: Single<Location>?){
        loadData(single,  {view?.stopShowUpdating(); view?.stopShowLoadingScreen() },
            {view?.stopShowUpdating()}, false)
    }

    private fun loadData(single: Single<Location>?, stopShowLoading: () -> Unit,
                         stopShowOnError: () -> Unit, isLoading: Boolean){
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
                                        val listFromNetwork
                                                = forecast.toListOfModels()
                                        val listWithHeaders
                                                =   listFromNetwork.toListWithHeaders()

                                        view?.updateList(listWithHeaders)
                                        loadToDatabase(listFromNetwork.toListOfForecast())
                                        stopShowLoading()
                                    },
                                    { _ ->
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
            if(isLoading){
                loadFromDatabase(stopShowLoading)
            }
        }
        stopShowOnError()
    }

    override fun onDestroy() {
        view = null
    }


    private fun loadToDatabase(list: List<Forecast>){
        database.let {
            it.deleteForecast()
                .subscribeOn(Schedulers.io())
                .doOnComplete {
                    it.insertForecast(list)
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                            { },
                            {it -> Log.d("ERROR", "$it") })
                }
                .subscribe(
                    { },
                    {it -> Log.d("ERROR", "$it") })
        }
    }
    private fun loadFromDatabase(stopShowLoading: () -> Unit){
        database.let {
            it.getForecast()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result->
                        if(result.isNotEmpty()){
                            val list
                                    = result.filter { it.date >  Calendar.getInstance().time }
                            view?.updateList(list.toListOfForecastModels().toListWithHeaders())
                            stopShowLoading()
                        }
                    },
                    {it ->
                        Log.d("ERROR", "$it")
                    }
                )
        }
    }
}