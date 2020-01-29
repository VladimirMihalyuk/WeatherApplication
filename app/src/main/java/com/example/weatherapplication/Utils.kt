package com.example.weatherapplication

fun Double?.kelvinToCelsius() = (this?.toInt() ?: 273) - 273


private val roundDegree = 360.0
val directions = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
fun windDegreeToDirection(degree: Int): String  =
    directions[((degree % roundDegree)  / (roundDegree / directions.size)). toInt()]