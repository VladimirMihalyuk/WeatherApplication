package com.example.weatherapplication.forecast

import com.example.weatherapplication.getDayOfWeek

enum class RowType {
    ELEMENT,
    HEADER
}

data class ForecastListItem(var type: RowType, var model: ForecastModel?, var header: String?)

fun List<ForecastModel>.toListWithHeaders():List<ForecastListItem>{
    val mutableList = mutableListOf<ForecastListItem>()
    mutableList.add(ForecastListItem(RowType.HEADER, null, "TODAY"))
    for(i in 0 until (this.size - 1)){
        mutableList.add(ForecastListItem(RowType.ELEMENT, this[i], null))
        if(this[i].date.getDayOfWeek() != this[i + 1].date.getDayOfWeek()){
            mutableList.add(ForecastListItem(RowType.HEADER,
                null, this[i + 1].date.getDayOfWeek()))

        }
    }
    mutableList.add(ForecastListItem(RowType.ELEMENT, this.last(), null))
    return mutableList.toList()
}