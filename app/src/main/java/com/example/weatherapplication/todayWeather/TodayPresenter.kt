package com.example.weatherapplication.todayWeather

import android.location.Location
import android.util.Log
import com.example.weatherapplication.interfaces.BasePresenter
import com.example.weatherapplication.database.DatabaseDAO
import com.example.weatherapplication.database.MyDatabase
import com.example.weatherapplication.database.Today
import com.example.weatherapplication.network.OpenWeatherMapAPI
import com.example.weatherapplication.utils.isInternetAvailable
import com.example.weatherapplication.network.WeatherAPIClient
import com.example.weatherapplication.utils.toDatabaseObject
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TodayPresenter @Inject constructor (private val client: OpenWeatherMapAPI,
                                          private val database: DatabaseDAO) : BasePresenter {

    private var view: TodayView? = null
    fun setView(view: TodayView){
        this.view = view
    }

    private var today: Today? = null

    fun loadCurrentWeather(single: Single<Location>?){
        loadData(single,  { view?.stopShowLoadingScreen()}, {}, true)
    }

    fun updateCurrentWeather(single: Single<Location>?){
        loadData(single,  {view?.stopShowUpdating(); view?.stopShowLoadingScreen() },
            {view?.stopShowUpdating()}, false)
    }

    private fun loadData(single: Single<Location>?, stopShowLoading: () -> Unit,
                         stopShowUpdating: () -> Unit, isLoading: Boolean){
        if(isInternetAvailable(view?.getContextOfView())){
            single?.let{
                single.subscribe(
                    { location ->
                        if(location != null){

                            client.getCurrentWeather(location.longitude.toFloat(), location.latitude.toFloat())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                    { weather ->
                                        val todayWeather: Today = weather.toDatabaseObject()
                                        view?.fillViews(todayWeather)
                                        loadToDatabase(todayWeather)
                                        today = todayWeather
                                        stopShowLoading()
                                    },
                                    { error ->
                                        view?.showErrorMessage("Server error")
                                    }
                                )
                        }else{
                            view?.showErrorMessage("Probably the GPS can not locate you")
                        }
                    },
                    {
                        _ ->
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
        stopShowUpdating()
    }

    fun shareAsText(){
        today?.let {
            val cityName = it.city.substring(0, it.city.indexOf(","))
            val temperature =
                it.temperature.subSequence(0, it.temperature.indexOf("|"))
            val text = "Current temperature at ${cityName} " +
                    "is ${temperature}."
            view?.shareAsText(text)
        }
    }

    override fun onDestroy() {
        view = null
    }

    private fun loadToDatabase(today: Today){
        database.let {
            it.deleteToday()
                .subscribeOn(Schedulers.io())
                .doOnComplete {
                    it.insertToday(today)
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
            it.getToday()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { list->
                        if (list.isNotEmpty()){
                            val todayWeather = list[0]
                            today = todayWeather
                            view?.fillViews(todayWeather)
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