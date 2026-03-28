package com.mahi.weatherapp.ui.presentation.statesAndEvents.event

sealed class ForecastEvent {
    data class CityChanged(val city: String) : ForecastEvent()
    data object Search : ForecastEvent()
    data object Retry : ForecastEvent()
    data object Refresh : ForecastEvent()
}