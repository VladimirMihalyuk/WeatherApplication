package com.example.weatherapplication


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.weatherapplication.network.WeatherAPIClient
import com.example.weatherapplication.network.data.CurrentWeather
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class TodayFragment : Fragment() {

    private val roundDegree = 360.0
    lateinit var bigPicture: ImageView
    lateinit var city: TextView
    lateinit var temperature: TextView
    lateinit var humidity: TextView
    lateinit var windDirection: TextView
    lateinit var windSpeed: TextView
    lateinit var precipitation: TextView
    lateinit var pressure: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_today, container, false)

        bigPicture = view.findViewById(R.id.bigPicture)
        city = view.findViewById(R.id.city)
        temperature = view.findViewById(R.id.temperature)
        humidity = view.findViewById(R.id.cloudness)
        windDirection = view.findViewById(R.id.windDirection)
        windSpeed = view.findViewById(R.id.windSpeed)
        precipitation  = view.findViewById(R.id.precipitation)
        pressure = view.findViewById(R.id.pressure)

        val client = WeatherAPIClient.getClient()
        val disposable = client.getCurrentWeather(-74.8F, 3.38F)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { it -> fillViews(it) }
        return view
    }

    fun fillViews(currentWeather: CurrentWeather){
        val resourceId = context?.resources?.
            getIdentifier("i" + (currentWeather.weather?.getOrNull(0)?.icon ?: "01d"),
                "drawable", context?.packageName)!!
        bigPicture.setImageResource(resourceId)
        city.text = "${currentWeather.name}, ${currentWeather.sys?.country}"
        val temperatureValue = (currentWeather.main?.temp?.toInt() ?: 273) - 273
        temperature.text = "${temperatureValue}°C |${currentWeather.weather?.getOrNull(0)?.main}"
        humidity.text = "${currentWeather.main?.humidity}%"

        val precipitationValue = ((currentWeather.snow?.threeHours ?:0.0) +
                (currentWeather.rain?.threeHours ?: 0.0))
        precipitation.text = "${Math.round(precipitationValue * 10.0) / 10.0 } mm"
        pressure.text =  "${currentWeather.main?.pressure} hPa"
        var speedValue = ((currentWeather.wind?.speed ?: 0.0) * 3.6).toInt()
        windSpeed.text = "${speedValue} km/h"
        windDirection.text = "${windDegreeToDirection(currentWeather.wind?.deg ?: 0)}"
    }

    val directions = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
    fun windDegreeToDirection(degree: Int): String  =
        directions[((degree % roundDegree)  / (roundDegree / directions.size)). toInt()]




}
