package com.example.weatherapplication.network.data

import com.google.gson.annotations.SerializedName

data class Snow (
    @field:SerializedName("1h")
    val oneHour: Double? = null,

    @field:SerializedName("3h")
    val threeHours: Double? = null
)