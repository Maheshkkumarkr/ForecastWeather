package com.mahi.weatherapp.data.repository

import com.mahi.weatherapp.common.isWithinForecastWindow
import com.mahi.weatherapp.common.toForecastLocalDateTime
import com.mahi.weatherapp.data.local.WeatherDatabase
import com.mahi.weatherapp.data.local.mapper.toDomain
import com.mahi.weatherapp.data.local.mapper.toEntity
import com.mahi.weatherapp.data.remote.WeatherApi
import com.mahi.weatherapp.data.remote.mapper.toDomain
import com.mahi.weatherapp.data.remote.safeNetworkCall
import com.mahi.weatherapp.data.local.safeLocalCall
import com.mahi.weatherapp.domain.error.LocalError
import com.mahi.weatherapp.domain.error.RemoteError
import com.mahi.weatherapp.domain.error.Result
import com.mahi.weatherapp.domain.model.City
import com.mahi.weatherapp.domain.model.ForecastEntry
import com.mahi.weatherapp.domain.repository.ForecastRepository
import android.util.Log
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ForecastRepositoryImpl(
    private val api: WeatherApi,
    private val db: WeatherDatabase
) : ForecastRepository {

    override suspend fun geocode(cityQuery: String): Result<City, RemoteError> {
        Log.d(TAG, "Geocoding query=$cityQuery")
        return when (val remote = safeNetworkCall { api.geocodeCity(cityQuery, limit = 1) }) {
            is Result.Success -> {
                Log.d(TAG, "Geocoding results=${remote.data}")
                val city = remote.data.firstOrNull()?.toDomain()
                if (city != null) {
                    Log.d(TAG, "Geocoding city=$city")
                    Result.Success(city)
                } else {
                    Result.Error(RemoteError.Custom("City not found. Please check spelling."))
                }
            }

            is Result.Error -> Result.Error(remote.error)
        }
    }

    /**
     * Fetches and caches forecast for the given [city].
     * Returns cached data if available.
     *
     * Logic:
     *   - Store ALL entries the API returns. The API always returns data anchored
     *   - to the server's real current time, so we preserve everything. Date-window
     *   - filtering is applied only on cache reads, where the device's date is the anchor.
     */
    override suspend fun fetchAndCacheForecast(city: City): Result<List<ForecastEntry>, RemoteError> =
        safeNetworkCall {
            val response = api.getForecast(city.latitude, city.longitude)
            // Store ALL entries the API returns. The API always returns data anchored
            // to the server's real current time, so we preserve everything. Date-window
            // filtering is applied only on cache reads, where the device's date is the anchor.
            val entries = response.list.map { it.toDomain() }.sortedBy { it.dateTimeText }
            // Persist
            withContext(Dispatchers.IO) {
                db.withTransaction {
                    db.cityDao().upsert(city.toEntity(lastFetchedAt = System.currentTimeMillis()))
                    db.forecastDao().clear(city.name)
                    db.forecastDao().upsertAll(entries.map { it.toEntity(city.name) })
                }
            }
            entries
        }

        /**
         * Retrieves cached forecast for the given [cityName].
         * Automatically cleans up expired entries outside the forecast window.
         *
         * Logic:
         *   - Fetch all cached entries from the database
         *   - Filter to keep only entries within the forecast window
         *   - If any entries were removed during filtering, clear and re-insert only valid ones
         *   - This prevents stale data from accumulating in the cache
         */
    override suspend fun getCachedForecast(cityName: String): Result<List<ForecastEntry>, LocalError> =
        safeLocalCall {
            Log.d(TAG, "Reading cache for city=$cityName")
            val entities = db.forecastDao().getForecast(cityName)
            val filtered = entities.map { it.toDomain() }.withinForecastWindow()

            if (filtered.size != entities.size) {
                db.withTransaction {
                    db.forecastDao().clear(cityName)
                    if (filtered.isNotEmpty()) {
                        db.forecastDao().upsertAll(filtered.map { it.toEntity(cityName) })
                    }
                }
            }

            filtered
        }

    override suspend fun getLastFetchedAt(cityName: String): Result<Long?, LocalError> =
        safeLocalCall {
            db.cityDao().getCity(cityName)?.lastFetchedAt
        }

    private companion object {
        const val TAG = "ForecastRepository"
    }

    private fun List<ForecastEntry>.withinForecastWindow(): List<ForecastEntry> {
        return filter { entry -> entry.dateTimeText.isWithinForecastWindow() }
            .sortedBy { entry -> entry.dateTimeText.toForecastLocalDateTime() ?: java.time.LocalDateTime.MAX }
    }
}
