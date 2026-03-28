package com.mahi.weatherapp.ui.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahi.weatherapp.common.FORECAST_WINDOW_DAYS
import com.mahi.weatherapp.common.isWithinForecastWindow
import com.mahi.weatherapp.common.toForecastLocalDate
import com.mahi.weatherapp.common.toForecastLocalDateTime
import com.mahi.weatherapp.common.Resource
import com.mahi.weatherapp.common.launchWithPrevious
import com.mahi.weatherapp.data.common.NetworkMonitor
import com.mahi.weatherapp.data.location.LocationProvider
import com.mahi.weatherapp.data.location.LocationResult
import com.mahi.weatherapp.domain.model.ForecastEntry
import com.mahi.weatherapp.domain.usecase.GetForecastUseCase
import com.mahi.weatherapp.ui.presentation.statesAndEvents.event.ForecastEvent
import com.mahi.weatherapp.ui.presentation.statesAndEvents.state.DailyForecast
import com.mahi.weatherapp.ui.presentation.statesAndEvents.state.ForecastState
import com.mahi.weatherapp.ui.presentation.statesAndEvents.state.HourlyForecast
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class ForecastViewModel(
    private val getForecastUseCase: GetForecastUseCase,
    networkMonitor: NetworkMonitor,
    private val locationProvider: LocationProvider
) : ViewModel() {
    
    private val _state = MutableStateFlow(ForecastState())
    val state: StateFlow<ForecastState> = _state

    private var loadJob: Job? = null
    private var lastRawData: List<ForecastEntry>? = null

    init {
        networkMonitor.isOnline
            .onEach { online ->
                _state.update { it.copy(isOnline = online) }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: ForecastEvent) {
        when (event) {
            is ForecastEvent.CityChanged -> _state.update {
                it.copy(
                    cityQuery = event.city,
                    locationError = null
                )
            }
            ForecastEvent.Search -> fetch()
            ForecastEvent.Retry -> fetch(force = _state.value.isOnline)
            ForecastEvent.Refresh -> fetch(force = _state.value.isOnline)
            ForecastEvent.UseDeviceLocation -> detectLocation()
            ForecastEvent.UseFallbackCity -> useFallbackCity()
            is ForecastEvent.ForecastDaysChanged -> {
                _state.update { it.copy(forecastDays = event.days) }
                lastRawData?.let { data ->
                    val grouped = aggregateForecast(data, event.days)
                    _state.update { it.copy(items = grouped) }
                }
            }
            is ForecastEvent.LocationPermissionChanged -> {
                _state.update { it.copy(isLocationPermissionGranted = event.granted) }
                // If permission was just granted and we have no data yet, kick off a location fetch.
                if (event.granted && _state.value.items.isEmpty() && !_state.value.isDetectingLocation) {
                    detectLocation()
                }
            }
        }
    }

    private fun detectLocation() {
        if (_state.value.isDetectingLocation) return

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isDetectingLocation = true,
                    isLoading = true,
                    locationError = null,
                    hasRequestedLocation = true
                )
            }

            when (val result = locationProvider.currentCity()) {
                is LocationResult.Success -> {
                    val city = result.city.ifBlank { DEFAULT_CITY }
                    _state.update {
                        it.copy(
                            cityQuery = city,
                            isDetectingLocation = false,
                            isLoading = false
                        )
                    }
                    fetch(force = _state.value.isOnline)
                }

                is LocationResult.Failure -> {
                    _state.update {
                        it.copy(
                            cityQuery = "",
                            isDetectingLocation = false,
                            isLoading = false,
                            locationError = result.reason
                        )
                    }
                }
            }
        }
    }

    private fun useFallbackCity() {
        _state.update {
            it.copy(
                cityQuery = "",
                hasRequestedLocation = true,
                locationError = LOCATION_PROMPT_MESSAGE,
                isDetectingLocation = false,
                isLoading = false
            )
        }
    }

    private fun fetch(force: Boolean = false) {

        Log.d("ForecastViewModel", "fetch: force=$force, cityQuery='${_state.value.cityQuery}'")
        val city = _state.value.cityQuery.takeIf { it.isNotBlank() } ?: return
        val normalized = city.trim().lowercase(Locale.getDefault())
        val isOnline = _state.value.isOnline
        loadJob = viewModelScope.launchWithPrevious(loadJob, cancelPrevious = force) {
            getForecastUseCase(
                cityQuery = normalized,
                forceRefresh = force,
                isOnline = isOnline
            ).collect { res ->
                when (res) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true, error = null) }
                    is Resource.Error -> _state.update {
                        it.copy(
                            isLoading = false,
                            error = res.message,
                            items = emptyList(),
                            isFromCache = false,
                            lastUpdated = null
                        )
                    }

                    is Resource.Success -> {
                        lastRawData = res.data   // cache raw entries so day-count changes can re-slice without a new fetch
                        val grouped = res.data?.let { aggregateForecast(it, _state.value.forecastDays) }.orEmpty()
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = null,
                                items = grouped,
                                isFromCache = res.fromCache,
                                lastUpdated = formatTimestamp(res.lastUpdatedMillis)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun aggregateForecast(
        entries: List<ForecastEntry>,
        days: Int
    ): List<DailyForecast> {
        val today = LocalDate.now()
        val lastDay = today.plusDays(FORECAST_WINDOW_DAYS)
        val byDate: Map<LocalDate, List<ForecastEntry>> =
            entries.groupBy { entry ->
                entry.dateTimeText.toForecastLocalDate() ?: today
            }
        return byDate.entries
            .filter { (date, _) -> date in today..lastDay }
            .sortedBy { it.key }
            .take(days)
            .map { (date, list) ->
                val sorted = list
                    .filter { it.dateTimeText.isWithinForecastWindow() }
                    .sortedBy {
                        it.dateTimeText.toForecastLocalDateTime() ?: java.time.LocalDateTime.MAX
                    }
                val min = sorted.minOfOrNull { it.minTemperature } ?: 0.0
                val max = sorted.maxOfOrNull { it.maxTemperature } ?: 0.0
                val avg = sorted.map { it.temperature }.average()
                val primary = sorted.firstOrNull()?.condition
                    ?: com.mahi.weatherapp.domain.model.WeatherCondition("", "", null)
                val slots = sorted.mapNotNull { entry ->
                    val dt = entry.dateTimeText.toForecastLocalDateTime()
                    dt?.let {
                        HourlyForecast(
                            timeLabel = it.toLocalTime()
                                .format(DateTimeFormatter.ofPattern("HH:mm")),
                            temperature = entry.temperature,
                            icon = entry.condition.icon,
                            description = entry.condition.description.ifBlank { entry.condition.main }
                        )
                    }
                }
                DailyForecast(
                    date = date,
                    minTemp = min,
                    maxTemp = max,
                    avgTemp = avg,
                    condition = primary,
                    slots = slots
                )
            }
    }

    private fun formatTimestamp(millis: Long?): String? {
        millis ?: return null
        val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM · HH:mm")
        val ldt = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime()
        return formatter.format(ldt)
    }

    private companion object {
        const val DEFAULT_CITY = "Bengaluru"
        const val LOCATION_PROMPT_MESSAGE = "Unable to access your location. Please enter a city manually."
    }
}
