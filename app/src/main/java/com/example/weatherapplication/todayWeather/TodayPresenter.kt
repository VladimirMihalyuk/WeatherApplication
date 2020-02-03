package com.example.weatherapplication.todayWeather

import android.location.Location
import com.example.weatherapplication.BasePresenter
import com.example.weatherapplication.isInternetAvailable
import com.example.weatherapplication.kelvinToCelsius
import com.example.weatherapplication.network.WeatherAPIClient
import com.example.weatherapplication.network.data.CurrentWeather
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TodayPresenter(private var view: TodayView?) : BasePresenter {

    private var currentWeather: CurrentWeather? = null

    private val client = WeatherAPIClient.getClient()

    fun loadCurrentWeather(single: Single<Location>?){
        loadData(single,  { view?.stopShowLoadingScreen()}, {})
    }

    fun updateCurrentWeather(single: Single<Location>?){
        loadData(single,  {view?.stopShowUpdating(); view?.stopShowLoadingScreen() },
            {view?.stopShowUpdating()})
    }

    private fun loadData(single: Single<Location>?,  stopShowLoading: () -> Unit,
                         stopShowOnError: () -> Unit){
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
                    },
                    {
                        _ ->
                        view?.showErrorMessage("Probably the GPS can not locate you")
                    }
                )
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