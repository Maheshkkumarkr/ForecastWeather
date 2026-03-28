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
import com.mahi.weatherapp.domain.usecase.GetForecastUseCase
import com.mahi.weatherapp.ui.presentation.statesAndEvents.event.ForecastEvent
import com.mahi.weatherapp.ui.presentation.statesAndEvents.state.DailyForecast
import com.mahi.weatherapp.ui.presentation.statesAndEvents.state.ForecastState
import com.mahi.weatherapp.ui.presentation.statesAndEvents.state.HourlyForecast
import kotlinx.coroutines.CoroutineScope
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
    networkMonitor: NetworkMonitor
) : ViewModel() {

    private inline fun CoroutineScope.joinPreviousOrRun(
        previous: Job?,
        force: Boolean,
        crossinline block: suspend () -> Unit
    ): Job {
        Log.d("ForecastViewModel", "joinPreviousOrRun: force=$force, previous='${previous?.isActive}'")
        return launch {
            if (!force) {
                previous?.join()
            } else {
                previous?.cancel()
            }
            block()
        }
    }

    private val _state = MutableStateFlow(ForecastState())
    val state: StateFlow<ForecastState> = _state

    private var loadJob: Job? = null

    init {
        networkMonitor.isOnline
            .onEach { online ->
                _state.update { it.copy(isOnline = online) }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: ForecastEvent) {
        when (event) {
            is ForecastEvent.CityChanged -> _state.update { it.copy(cityQuery = event.city) }
            ForecastEvent.Search -> fetch()
            ForecastEvent.Retry -> fetch(force = /*true*/_state.value.isOnline) //!_state.value.isOnline false else true
            ForecastEvent.Refresh -> fetch(force = /*true*/_state.value.isOnline)
        }
    }

    /**
     * Launches a new coroutine and coordinates with a previous job.
     *
     * - If `force` is `false`, waits for `previous` to finish.
     * - If `force` is `true`, cancels `previous` before running `block`.
     *
     */
    private fun fetch(force: Boolean = false) {

        Log.d("ForecastViewModel", "fetch: force=$force, cityQuery='${_state.value.cityQuery}'")
        val city = _state.value.cityQuery.takeIf { it.isNotBlank() } ?: return
        val normalized = city.trim().lowercase(Locale.getDefault())
        loadJob = viewModelScope.
//            joinPreviousOrRun(loadJob, force) {
        launchWithPrevious(loadJob, cancelPrevious = force) {
            getForecastUseCase(normalized, force).collect { res ->
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
                        val grouped = res.data?.let { aggregateThreeDays(it) }.orEmpty()
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

    private fun aggregateThreeDays(entries: List<com.mahi.weatherapp.domain.model.ForecastEntry>): List<DailyForecast> {
        val today = LocalDate.now()
        val lastDay = today.plusDays(FORECAST_WINDOW_DAYS)
        val byDate: Map<LocalDate, List<com.mahi.weatherapp.domain.model.ForecastEntry>> =
            entries.groupBy { entry ->
                entry.dateTimeText.toForecastLocalDate() ?: today
            }
        return byDate.entries
            .filter { (date, _) -> date in today..lastDay }
            .sortedBy { it.key }
            .take(3)
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
}







