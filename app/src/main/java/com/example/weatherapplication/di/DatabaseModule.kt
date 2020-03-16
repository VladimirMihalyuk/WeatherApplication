package com.example.weatherapplication.di

import android.app.Application
import com.example.weatherapplication.database.MyDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class DatabaseModule {

   @Provides
   @Singleton
   fun provideDatabase(application: Application)
           = MyDatabase.getInstance(application).databaseDao
}