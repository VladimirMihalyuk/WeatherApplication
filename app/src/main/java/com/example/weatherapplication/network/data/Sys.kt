package com.example.weatherapplication.network.data


import com.google.gson.annotations.SerializedName

data class Sys(

	@field:SerializedName("pod")
	val pod: String? = null
)