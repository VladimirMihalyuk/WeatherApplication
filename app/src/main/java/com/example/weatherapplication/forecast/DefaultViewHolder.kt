package com.example.weatherapplication.forecast

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.R
import com.example.weatherapplication.getHoursAsString

class DefaultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val viewMap: MutableMap<Int, View> = HashMap()

    init {
        findViewItems(itemView)
        Log.d("WTF", "$viewMap")
    }


    fun setElement(element:ForecastModel, idOfImage:Int){
        val image = viewMap[R.id.imageView] as ImageView
        image.setImageResource(idOfImage)
        val degree = viewMap[R.id.temperature] as TextView
        degree.text = "${element.degree}°"
        val description = viewMap[R.id.description] as TextView
        description.text = element.description
        val time =  viewMap[R.id.time] as TextView
        time.text = element.date.getHoursAsString()
    }

    fun setHeader(string: String){
        Log.d("WTF", "${R.id.header}" )
        val text = viewMap[R.id.header] as TextView
        text.text = string
    }

    private fun findViewItems(itemView: View) {
        addToMap(itemView)
        if (itemView is ViewGroup) {
            val childCount = itemView.childCount
            (0 until childCount)
                .map { itemView.getChildAt(it) }
                .forEach { findViewItems(it) }
        }
    }

    private fun addToMap(itemView: View) {
        if (itemView.id == View.NO_ID) {
            itemView.id = View.generateViewId()
        }
        viewMap.put(itemView.id, itemView)
    }

}