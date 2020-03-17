package com.example.weatherapplication.todayWeather


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.weatherapplication.*
import com.example.weatherapplication.activity.MainActivity
import com.example.weatherapplication.database.Today
import com.example.weatherapplication.forecast.ForecastPresenter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_today.view.*
import javax.inject.Inject

class TodayFragment : Fragment(), TodayView {

    @Inject
    lateinit var presenter: TodayPresenter

    lateinit var bigPicture: ImageView
    lateinit var city: TextView
    lateinit var temperature: TextView
    lateinit var cloudiness: TextView
    lateinit var windDirection: TextView
    lateinit var windSpeed: TextView
    lateinit var precipitation: TextView
    lateinit var pressure: TextView
    lateinit var loading: FrameLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_today, container, false)

        ((activity as MainActivity).application as WeatherApplication)
            .appComponent.inject(this)
        presenter.setView(this)


        bigPicture = view.findViewById(R.id.bigPicture)
        city = view.findViewById(R.id.city)
        temperature = view.findViewById(R.id.temperature)
        cloudiness = view.findViewById(R.id.cloudness)
        windDirection = view.findViewById(R.id.windDirection)
        windSpeed = view.findViewById(R.id.windSpeed)
        precipitation  = view.findViewById(R.id.precipitation)
        pressure = view.findViewById(R.id.pressure)
        loading = view.findViewById(R.id.loading)

        view.share.setOnClickListener {
            presenter.shareAsText()
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        startShowLoadingScreen()
        presenter.loadCurrentWeather((activity as MainActivity).location)

        (activity as MainActivity).refreshingEvents.subscribe{ it ->
            if(it){presenter.updateCurrentWeather((activity as MainActivity).location) }}
    }

    override fun showErrorMessage(text: String) {
        Snackbar.make(bigPicture, text, Snackbar.LENGTH_LONG).show()
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

    override fun fillViews(today: Today){
        val resourceId = context?.resources?.
            getIdentifier(today.image, "drawable", context?.packageName)!!
        bigPicture.setImageResource(resourceId)
        val cityName = today.city.substring(0, today.city.indexOf(","))
        (activity as MainActivity).updateTitle(cityName)
        city.text = today.city
        temperature.text = today.temperature
        cloudiness.text = today.cloudiness
        precipitation.text = today.precipitation
        pressure.text =  today.pressure
        windSpeed.text = today.windSpeed
        windDirection.text = today.windDirection
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun stopShowUpdating() {
        (activity as MainActivity).stopShowingRefreshing()
    }

    override fun startShowLoadingScreen() {
        loading.visibility = View.VISIBLE

    }

    override fun stopShowLoadingScreen() {
        loading.visibility = View.INVISIBLE
    }
}
