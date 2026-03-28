package com.mahi.weatherapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mahi.weatherapp.data.local.dao.CityDao
import com.mahi.weatherapp.data.local.dao.ForecastDao
import com.mahi.weatherapp.data.local.entity.CityEntity
import com.mahi.weatherapp.data.local.entity.ForecastEntity

@Database(
    entities = [CityEntity::class, ForecastEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
    abstract fun forecastDao(): ForecastDao

    companion object {
        const val DB_NAME = "weather.db"
    }
}
