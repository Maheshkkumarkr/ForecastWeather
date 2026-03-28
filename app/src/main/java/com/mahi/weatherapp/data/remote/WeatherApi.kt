package com.mahi.weatherapp.data.remote

import android.util.Log
import com.mahi.weatherapp.BuildConfig
import com.mahi.weatherapp.data.remote.dto.ForecastResponseDto
import com.mahi.weatherapp.data.remote.dto.GeocodingDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class WeatherApi(private val client: HttpClient) {

    private val apiKey: String = BuildConfig.OPENWEATHER_API_KEY
    companion object{
        private const val TAG = "WeatherApi"
    }

    suspend fun geocodeCity(query: String, limit: Int = 1): List<GeocodingDto> {
        return client.get("geo/1.0/direct") {
            parameter("q", query)
            parameter("limit", limit)
            parameter("appid", apiKey)
        }.body<List<GeocodingDto>>().also {
            Log.e(TAG, "geocodeCity:$it")
        }
    }

    suspend fun getForecast(lat: Double, lon: Double, units: String = "metric"): ForecastResponseDto {
        return client.get("data/2.5/forecast") {
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("units", units)
            parameter("appid", apiKey)
        }.body<ForecastResponseDto>().also {
            Log.e(TAG, "getForecast:$it")
        }
    }
}
