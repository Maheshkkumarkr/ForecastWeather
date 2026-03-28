package com.mahi.weatherapp.ui.presentation.statesAndEvents.state

import com.mahi.weatherapp.domain.model.WeatherCondition
import java.time.LocalDate

data class ForecastState(
    val cityQuery: String = "",
    val isLoading: Boolean = false,
    val isDetectingLocation: Boolean = false,
    val error: String? = null,
    val locationError: String? = null,
    val hasRequestedLocation: Boolean = false,
    val items: List<DailyForecast> = emptyList(),
    val isFromCache: Boolean = false,
    val lastUpdated: String? = null,
    val isOnline: Boolean = true,
    val forecastDays: Int = 3,
    val isLocationPermissionGranted: Boolean = false
)

data class DailyForecast(
    val date: LocalDate,
    val minTemp: Double,
    val maxTemp: Double,
    val avgTemp: Double,
    val condition: WeatherCondition,
    val slots: List<HourlyForecast>
)

data class HourlyForecast(
    val timeLabel: String,
    val temperature: Double,
    val icon: String?,
    val description: String
)