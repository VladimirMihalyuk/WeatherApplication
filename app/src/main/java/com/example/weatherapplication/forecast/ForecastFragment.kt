package com.example.weatherapplication.forecast


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.activity.MainActivity
import com.example.weatherapplication.R
import com.example.weatherapplication.WeatherApplication
import com.example.weatherapplication.forecast.adapter.ForecastAdapter
import com.example.weatherapplication.forecast.adapter.ForecastListItem
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ForecastFragment : Fragment(), ForecastView {


    @Inject
    lateinit var presenter: ForecastPresenter

    val startList = mutableListOf<ForecastListItem>()
    private lateinit var adapter: ForecastAdapter
    lateinit var list: RecyclerView
    lateinit var loading:FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forecast, container, false)

        ((activity as MainActivity).application as WeatherApplication)
            .appComponent.inject(this)
        presenter.setView(this)

        list = view.findViewById(R.id.list)
        loading = view.findViewById(R.id.loading)

        adapter = ForecastAdapter(startList, context!!)

        list.adapter = adapter
        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        startShowLoadingScreen()
        presenter.loadCurrentWeather((activity as MainActivity).location)

        (activity as MainActivity).refreshingEvents.subscribe{ it ->
            if(it){presenter.updateCurrentWeather((activity as MainActivity).location)  }}

    }

    override fun updateList(list: List<ForecastListItem>){
        adapter.updateList(list)
    }

    override fun stopShowUpdating() {
        (activity as MainActivity).stopShowingRefreshing()
    }

    override fun getContextOfView(): Context? {
        return super.getContext()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun showErrorMessage(text: String) {
        Snackbar.make(list, text, Snackbar.LENGTH_LONG).show()
    }

    override fun startShowLoadingScreen() {
        loading.visibility = View.VISIBLE

    }

    override fun stopShowLoadingScreen() {
        loading.visibility = View.INVISIBLE
    }
}


