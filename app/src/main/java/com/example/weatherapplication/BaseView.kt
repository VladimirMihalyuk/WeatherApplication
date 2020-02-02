package com.example.weatherapplication

import android.content.Context

interface BaseView {
    fun stopShowUpdating()

    fun getContextOfView(): Context?

    fun startShowLoadingScreen()

    fun stopShowLoadingScreen()

    fun showErrorMessage(text: String)
}