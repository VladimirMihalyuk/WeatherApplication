package com.example.weatherapplication.todayWeather


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.weatherapplication.*
import com.example.weatherapplication.network.data.CurrentWeather
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_today.view.*

class TodayFragment : Fragment(), TodayView {

    lateinit var bigPicture: ImageView
    lateinit var city: TextView
    lateinit var temperature: TextView
    lateinit var humidity: TextView
    lateinit var windDirection: TextView
    lateinit var windSpeed: TextView
    lateinit var precipitation: TextView
    lateinit var pressure: TextView


    private lateinit var presenter: TodayPresenter

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


        presenter = TodayPresenter(this, 30.3449F, 53.9168F)

        view.share.setOnClickListener {
            presenter.shareAsText()
        }



        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.loadCurrentWeather()
        (activity as MainActivity).refreshingEvents.subscribe{it ->
            if(it){presenter.loadCurrentWeather()
            Log.d("WTF", "$it")}}
    }

    override fun showErrorMessage(text: String) {
        Snackbar.make(bigPicture, text, Snackbar.LENGTH_LONG).show()
    }

    override fun fillViews(currentWeather: CurrentWeather){
        val resourceId = context?.resources?.
            getIdentifier("i" + (currentWeather.weather?.getOrNull(0)?.icon ?: "01d"),
                "drawable", context?.packageName)!!
        bigPicture.setImageResource(resourceId)
        city.text = "${currentWeather?.name}, ${currentWeather?.sys?.country}"
        val temperatureValue = currentWeather.main?.temp?.kelvinToCelsius()
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

    override fun shareAsText(text: String){
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    override fun getContextOfView(): Context? {
        return super.getContext()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun stopShowLoading() {
        (activity as MainActivity).stopShowingRefreshing()

    }
}
