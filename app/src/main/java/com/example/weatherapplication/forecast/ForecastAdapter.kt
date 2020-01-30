package com.example.weatherapplication.forecast

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.R

class ForecastAdapter(private var list: List<ForecastListItem>,
                   private var context: Context) : RecyclerView.Adapter<DefaultViewHolder>() {

    override fun getItemCount()= list.size


    override fun getItemViewType(position: Int) = list[position].type.ordinal



    override fun onBindViewHolder(holder: DefaultViewHolder, position: Int) {
        val row = list[position]

        if (row.type == RowType.ELEMENT) {
            if(row.model != null){
                val id = context.resources?.
                    getIdentifier("i" + row.model!!.icon,
                        "drawable", context?.packageName)!!
                holder.setElement(row.model!!, id)
            }

        } else {
            if(row.header!= null){
                holder.setHeader(row.header!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        Log.d("WTF", "$viewType")
        val inflatedView : View = when (viewType) {
            RowType.ELEMENT.ordinal -> layoutInflater.inflate(R.layout.list_item, parent,false)
            else -> layoutInflater.inflate(R.layout.header_of_list_item, parent,false)
        }
        return DefaultViewHolder(inflatedView)
    }

}