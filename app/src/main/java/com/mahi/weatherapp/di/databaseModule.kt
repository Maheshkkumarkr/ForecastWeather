package com.mahi.weatherapp.di

import androidx.room.Room
import com.mahi.weatherapp.data.local.WeatherDatabase
import org.koin.dsl.module


val databaseModule = module {
    single {
        Room.databaseBuilder(
            get(),
            WeatherDatabase::class.java,
            WeatherDatabase.DB_NAME
        ).build()
    }
}