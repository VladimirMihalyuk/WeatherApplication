package com.example.weatherapplication.di

import com.example.weatherapplication.forecast.ForecastFragment
import com.example.weatherapplication.network.WeatherAPIClient
import com.example.weatherapplication.todayWeather.TodayFragment
import dagger.Component
import javax.inject.Singleton
import android.app.Application
import dagger.BindsInstance




@Singleton
@Component(modules = [
    NetworkModule::class,
    DatabaseModule::class,
    PresentersModule::class
])
public interface AppComponent {
    fun inject(target: TodayFragment);

    fun inject(target: ForecastFragment);

    @Component.Builder
    interface Builder {

        fun build(): AppComponent
        @BindsInstance
        fun application(application: Application): Builder
    }
}