package com.example.weatherapplication.forecast


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weatherapplication.R
import com.example.weatherapplication.network.WeatherAPIClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_forecast.view.*

/**
 * A simple [Fragment] subclass.
 */
class ForecastFragment : Fragment() {


    val startList = mutableListOf<ForecastListItem>()
    private lateinit var adapter: ForecastAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forecast, container, false)
        adapter =  ForecastAdapter(startList, context!!)
        view.list.adapter = adapter
        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val client = WeatherAPIClient.getClient()


        val disposable = client.getForecast(22.2F, 22.2F)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { forecast -> updateList(forecast.toListOfModels().toListWithHeaders()) },
                { error -> }
            )
    }

    fun updateList(list: List<ForecastListItem>){
        startList.addAll(list)
        adapter.notifyDataSetChanged()
    }
}


