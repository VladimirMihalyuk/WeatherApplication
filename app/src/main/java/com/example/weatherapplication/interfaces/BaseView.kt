package com.example.weatherapplication.interfaces

import android.content.Context

interface BaseView {
    fun stopShowUpdating()

    fun getContextOfView(): Context?

    fun startShowLoadingScreen()

    fun stopShowLoadingScreen()

    fun showErrorMessage(text: String)
}