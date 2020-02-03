package com.example.weatherapplication.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.*

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

@Entity
data class Forecast(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    val date: Date,

    val icon: String,

    val description: String,

    val degree: Int)


class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}