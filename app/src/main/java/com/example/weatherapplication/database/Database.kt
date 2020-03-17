package com.example.weatherapplication.database

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Today::class, Forecast::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MyDatabase  : RoomDatabase() {
    abstract val databaseDao: DatabaseDAO

    companion object{
        fun getInstance(application: Application)=
            Room.databaseBuilder(
                application.applicationContext,
                MyDatabase::class.java,
                "Database.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}