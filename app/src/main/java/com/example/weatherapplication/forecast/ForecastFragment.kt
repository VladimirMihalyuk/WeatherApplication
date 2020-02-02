package com.example.weatherapplication.forecast


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.MainActivity
import com.example.weatherapplication.R
import com.example.weatherapplication.forecast.adapter.ForecastAdapter
import com.example.weatherapplication.forecast.adapter.ForecastListItem
import com.example.weatherapplication.forecast.adapter.toListWithHeaders
import com.example.weatherapplication.network.WeatherAPIClient
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_forecast.view.*

/**
 * A simple [Fragment] subclass.
 */
class ForecastFragment : Fragment(), ForecastView {


    val startList = mutableListOf<ForecastListItem>()
    private lateinit var adapter: ForecastAdapter
    private lateinit var presenter: ForecastPresenter
    lateinit var list: RecyclerView
    lateinit var loading:FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forecast, container, false)

        list = view.findViewById(R.id.list)
        loading = view.findViewById(R.id.loading)

        adapter = ForecastAdapter(startList, context!!)
        presenter = ForecastPresenter(this)

        list.adapter = adapter
        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        startShowLoadingScreen()
        presenter.loadCurrentWeather((activity as MainActivity).location)

        (activity as MainActivity).refreshingEvents.subscribe{it ->
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


