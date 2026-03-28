package com.mahi.weatherapp.domain.model

data class ForecastEntry(
    val dateTimeText: String,
    val temperature: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val condition: WeatherCondition
)
