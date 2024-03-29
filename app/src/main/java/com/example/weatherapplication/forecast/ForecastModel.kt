package com.example.weatherapplication.forecast

import com.example.weatherapplication.utils.convertToDate
import com.example.weatherapplication.utils.kelvinToCelsius
import com.example.weatherapplication.network.data.ForecastNetwork
import java.util.*

data class ForecastModel(
    val date: Date,

    val icon: String,

    val description: String,

    val degree: Int)

fun ForecastNetwork.toListOfModels(): List<ForecastModel>{
    val mutableList = mutableListOf<ForecastModel>()
    this.list?.let {
        for(item in list){
            if(item?.dtTxt != null){
                mutableList.add(
                    ForecastModel(
                        item.dtTxt.convertToDate(),
                        item.weather?.getOrNull(0)?.icon ?: "01d",
                        item.weather?.getOrNull(0)?.main ?: "Clouds",
                        item.main?.temp?.kelvinToCelsius() ?: 0
                        )
                )
            }
        }
    }
    return mutableList.toList()
}

