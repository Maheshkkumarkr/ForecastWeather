package com.mahi.weatherapp.ui.presentation.statesAndEvents.event

sealed class ForecastEvent {
    data class CityChanged(val city: String) : ForecastEvent()
    data object Search : ForecastEvent()
    data object Retry : ForecastEvent()
    data object Refresh : ForecastEvent()
    data object UseDeviceLocation : ForecastEvent()
    data object UseFallbackCity : ForecastEvent()
    data class ForecastDaysChanged(val days: Int) : ForecastEvent()
    /** Fired every time ForecastRoute reads the actual permission state (e.g. on ON_RESUME). */
    data class LocationPermissionChanged(val granted: Boolean) : ForecastEvent()
}