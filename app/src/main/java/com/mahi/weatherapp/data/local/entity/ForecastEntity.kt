package com.mahi.weatherapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forecast")
data class ForecastEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cityName: String,
    val dateTimeText: String,
    val temperature: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val main: String,
    val description: String,
    val icon: String?
)
