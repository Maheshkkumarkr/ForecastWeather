package com.mahi.weatherapp.domain.repository

import com.mahi.weatherapp.domain.error.LocalError
import com.mahi.weatherapp.domain.error.RemoteError
import com.mahi.weatherapp.domain.error.Result
import com.mahi.weatherapp.domain.model.City
import com.mahi.weatherapp.domain.model.ForecastEntry

interface ForecastRepository {
    suspend fun geocode(cityQuery: String): Result<City, RemoteError>
    suspend fun fetchAndCacheForecast(city: City): Result<List<ForecastEntry>, RemoteError>
    suspend fun getCachedForecast(cityName: String): Result<List<ForecastEntry>, LocalError>
    suspend fun getLastFetchedAt(cityName: String): Result<Long?, LocalError>
}
