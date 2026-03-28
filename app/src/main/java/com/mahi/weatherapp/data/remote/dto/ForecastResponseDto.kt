package com.mahi.weatherapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastResponseDto(
    val list: List<ForecastItemDto> = emptyList(),
    val city: CityDto? = null
)

@Serializable
data class ForecastItemDto(
    val dt: Long,
    val main: MainDto,
    val weather: List<WeatherDto> = emptyList(),
    @SerialName("dt_txt") val dateTimeText: String? = null
)

@Serializable
data class MainDto(
    @SerialName("temp") val temperature: Double,
    @SerialName("temp_min") val minTemperature: Double,
    @SerialName("temp_max") val maxTemperature: Double
)

@Serializable
data class WeatherDto(
    val id: Int? = null,
    val main: String = "",
    val description: String = "",
    val icon: String? = null
)

@Serializable
data class CityDto(
    val id: Long? = null,
    val name: String? = null,
    val country: String? = null,
    val population: Int? = null,
    val timezone: Int? = null,
    val sunrise: Long? = null,
    val sunset: Long? = null,
    val coord: CoordDto? = null
)

@Serializable
data class CoordDto(
    val lat: Double? = null,
    val lon: Double? = null
)
