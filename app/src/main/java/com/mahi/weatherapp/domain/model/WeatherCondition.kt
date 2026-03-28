package com.mahi.weatherapp.domain.model

data class WeatherCondition(
    val main: String,
    val description: String,
    val icon: String?
)
