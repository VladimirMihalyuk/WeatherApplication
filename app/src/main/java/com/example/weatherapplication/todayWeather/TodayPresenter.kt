package com.example.weatherapplication.todayWeather

import android.location.Location
import com.example.weatherapplication.BasePresenter
import com.example.weatherapplication.isInternetAvailable
import com.example.weatherapplication.kelvinToCelsius
import com.example.weatherapplication.network.WeatherAPIClient
import com.example.weatherapplication.network.data.CurrentWeather
import com.google.android.gms.tasks.Task

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TodayPresenter(private var view: TodayView?) : BasePresenter {

    private var currentWeather: CurrentWeather? = null

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
                task.addOnSuccessListener {location ->
                    if(location != null){

                        client.getCurrentWeather(location.longitude.toFloat(), location.latitude.toFloat())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                { weather ->
                                    view?.fillViews(weather)
                                    currentWeather = weather
                                    stopShowLoading()
                                },
                                { error ->
                                    view?.showErrorMessage("Server error")
                                }
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