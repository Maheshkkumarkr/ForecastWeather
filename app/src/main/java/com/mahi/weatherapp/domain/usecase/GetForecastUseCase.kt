package com.mahi.weatherapp.domain.usecase

import android.util.Log
import com.mahi.weatherapp.common.isWithinForecastWindow
import com.mahi.weatherapp.common.toForecastLocalDate
import com.mahi.weatherapp.common.toForecastLocalDateTime
import com.mahi.weatherapp.common.Resource
import com.mahi.weatherapp.domain.error.AppError
import com.mahi.weatherapp.domain.error.RemoteError
import com.mahi.weatherapp.domain.error.Result
import com.mahi.weatherapp.domain.model.City
import com.mahi.weatherapp.domain.model.ForecastEntry
import com.mahi.weatherapp.domain.repository.ForecastRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Fetch forecast for the given city, cache it, and emit the result.
 * Strategy:
 *  - Fresh cache hit (within freshness window) → emit cached data immediately.
 *  - Stale cache / force refresh → attempt remote fetch.
 *    - Success → emit fresh data.
 *    - Error   → emit error only; stale data is NOT surfaced so the UI shows a
 *                clear error state instead of misleading outdated information.
 */
class GetForecastUseCase(
    private val repository: ForecastRepository
) {
    operator fun invoke(
        cityQuery: String,
        forceRefresh: Boolean = false,
        isOnline: Boolean = true
    ): Flow<Resource<List<ForecastEntry>>> = flow {
        Log.d(TAG, "invoke(cityQuery=$cityQuery, forceRefresh=$forceRefresh, isOnline=$isOnline)")
        emit(Resource.Loading())

        val cachedForecast = repository.getCachedForecast(cityQuery).successOrNull().orEmpty().withinForecastWindow()
        val lastFetched = repository.getLastFetchedAt(cityQuery).successOrNull()
        val now = System.currentTimeMillis()
        Log.d(
            TAG,
            "primary cache -> entries=${cachedForecast.size}, lastFetched=$lastFetched, now=$now"
        )

        if (canUseCache(cachedForecast, lastFetched, now, forceRefresh)) {
            Log.d(TAG, "using primary cache for cityQuery=$cityQuery")
            emit(
                Resource.Success(
                    data = cachedForecast,
                    fromCache = true,
                    lastUpdatedMillis = lastFetched
                )
            )
            return@flow
        }

        if (!isOnline) {
            Log.d(TAG, "offline and cache unavailable -> skipping geocode/api for query=$cityQuery")
            emit(Resource.Error(message = RemoteError.NoInternet.message))
            return@flow
        }

        Log.d(TAG, "primary cache miss -> invoking geocode for query=$cityQuery")

        when (val geoResult: Result<City, *> = repository.geocode(cityQuery)) {
            is Result.Error -> {
                val message = (geoResult.error as? AppError)?.message ?: geoResult.error.toString()
                Log.d(TAG, "Geocode failed: $message")
                emit(Resource.Error(message = message))
                return@flow
            }

            is Result.Success -> {
                val city = geoResult.data
                Log.d(TAG, "Geocode success -> canonical city=${city.name}, country=${city.country}")
                val canonicalCached = repository.getCachedForecast(city.name).successOrNull().orEmpty().withinForecastWindow()
                val canonicalLastFetched = repository.getLastFetchedAt(city.name).successOrNull()
                Log.d(
                    TAG,
                    "canonical cache -> city=${city.name}, entries=${canonicalCached.size}, lastFetched=$canonicalLastFetched"
                )
                if (canUseCache(canonicalCached, canonicalLastFetched, now, forceRefresh)) {
                    Log.d(TAG, "using canonical cache for city=${city.name}")
                    emit(
                        Resource.Success(
                            data = canonicalCached,
                            fromCache = true,
                            lastUpdatedMillis = canonicalLastFetched
                        )
                    )
                    return@flow
                }

                Log.d(TAG, "canonical cache miss -> fetching remote forecast for city=${city.name}")
                val remote = repository.fetchAndCacheForecast(city)
                when (remote) {
                    is Result.Success -> {
                        val fetchedAt = System.currentTimeMillis()
                        Log.d(TAG, "remote forecast success -> city=${city.name}, entries=${remote.data.size}")
                        if (remote.data.isEmpty()) {
                            Log.d(TAG, "remote forecast empty -> city=${city.name}")
                            emit(Resource.Error(message = "No forecast data found for this city."))
                        } else {
                            emit(
                                Resource.Success(
                                    // Return all entries. The ViewModel's aggregateThreeDays
                                    // groups and filters to the device's current date window.
                                    data = remote.data,
                                    fromCache = false,
                                    lastUpdatedMillis = fetchedAt
                                )
                            )
                        }
                    }
                    is Result.Error -> {
                        val message =
                            (remote.error as? AppError)?.message ?: remote.error.toString()
                        Log.d(TAG, "Remote fetch failed: $message")
                        emit(Resource.Error(message = message))
                    }
                }
            }
        }

    }

    private companion object {
        const val TAG = "GetForecastUseCase"
        const val THREE_HOURS_MILLIS = 3 * 60 * 60 * 1000L
        const val ONE_DAY_MILLIS = 24 * 60 * 60 * 1000L
        const val EXPECTED_SLOTS_PER_DAY = 8
        const val CLOCK_SKEW_TOLERANCE_MILLIS = 5 * 60 * 1000L
    }

    private fun <T, E> Result<T, E>.successOrNull(): T? = (this as? Result.Success)?.data

    /**
     * Uses a longer cache window when today's forecast appears complete
     * \(8 three\-hour slots\); otherwise falls back to a short window.
     */
    private fun chooseFreshnessWindow(forecast: List<ForecastEntry>): Long {
        val today = LocalDate.now()
        val slotsToday = forecast.count { entry -> entry.toLocalDate() == today }
        val hasFullDayCoverage = slotsToday >= EXPECTED_SLOTS_PER_DAY
        return if (hasFullDayCoverage) ONE_DAY_MILLIS else THREE_HOURS_MILLIS
    }

    /**
     * Decides whether cached forecast data can be reused.
     *
     * Returns `true` only when:
     * - refresh is not forced,
     * - cache is not empty and has a valid fetch timestamp,
     * - cache contains at least one entry for the device's current local date,
     * - the fetch timestamp is also from today,
     * - cache age is not invalid due to major clock skew,
     * - cache age is within the dynamic freshness window.
     */
    private fun canUseCache(
        cache: List<ForecastEntry>,
        lastFetched: Long?,
        nowMillis: Long,
        forceRefresh: Boolean
    ): Boolean {
//        if (forceRefresh || cache.isEmpty() || lastFetched == null) return false
        if (forceRefresh) {
            Log.d(TAG, "canUseCache=false reason=forceRefresh")
            return false
        }
        if (cache.isEmpty()) {
            Log.d(TAG, "canUseCache=false reason=cacheEmpty")
            return false
        }
        if (lastFetched == null) {
            Log.d(TAG, "canUseCache=false reason=missingLastFetched")
            return false
        }

        val today = LocalDate.now()
        val hasTodayCoverage = cache.any { it.toLocalDate() == today }
        if (!hasTodayCoverage) {
            Log.d(TAG, "canUseCache=false reason=noTodayCoverage today=$today")
            return false
        }

        val lastFetchedLocalDate = Instant.ofEpochMilli(lastFetched)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        if (lastFetchedLocalDate != today) {
            Log.d(
                TAG,
                "canUseCache=false reason=lastFetchedDateMismatch lastFetchedDate=$lastFetchedLocalDate today=$today"
            )
            return false
        }

        val ageMillis = nowMillis - lastFetched
        if (ageMillis < -CLOCK_SKEW_TOLERANCE_MILLIS) {
            Log.d(TAG, "canUseCache=false reason=clockSkew ageMillis=$ageMillis")
            return false
        }

        val freshnessWindow = chooseFreshnessWindow(cache)
        val accepted = ageMillis in 0..freshnessWindow
        Log.d(
            TAG,
            "canUseCache=$accepted ageMillis=$ageMillis freshnessWindow=$freshnessWindow cacheEntries=${cache.size}"
        )
        return accepted
    }

    private fun ForecastEntry.toLocalDate(): LocalDate? = dateTimeText.toForecastLocalDate()

    private fun List<ForecastEntry>.withinForecastWindow(): List<ForecastEntry> {
        return filter { entry -> entry.dateTimeText.isWithinForecastWindow() }
            .sortedBy { entry -> entry.dateTimeText.toForecastLocalDateTime() ?: java.time.LocalDateTime.MAX }
    }
}