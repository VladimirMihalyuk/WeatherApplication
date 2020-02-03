package com.example.weatherapplication.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Today(
    @PrimaryKey
    val image: String,

    val city: String,

    val temperature: String,

    val cloudiness: String,

    val precipitation: String,

    val pressure:String,

    val windSpeed: String,

    val windDirection: String
)