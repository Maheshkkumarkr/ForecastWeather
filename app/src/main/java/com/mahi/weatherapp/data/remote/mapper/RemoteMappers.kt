package com.mahi.weatherapp.data.remote.mapper

import com.mahi.weatherapp.data.remote.dto.CityDto
import com.mahi.weatherapp.data.remote.dto.ForecastItemDto
import com.mahi.weatherapp.data.remote.dto.GeocodingDto
import com.mahi.weatherapp.data.remote.dto.WeatherDto
import com.mahi.weatherapp.domain.model.City
import com.mahi.weatherapp.domain.model.ForecastEntry
import com.mahi.weatherapp.domain.model.WeatherCondition

fun GeocodingDto.toDomain(): City =
    City(
        name = name,
        country = country,
        latitude = lat,
        longitude = lon
    )

fun CityDto?.toDomain(): City? =
    this?.let {
        val coord = coord
        if (coord?.lat != null && coord.lon != null && name != null) {
            City(
                name = name,
                country = country,
                latitude = coord.lat,
                longitude = coord.lon
            )
        } else null
    }

fun ForecastItemDto.toDomain(): ForecastEntry {
    val firstWeather: WeatherDto? = weather.firstOrNull()
    val condition = WeatherCondition(
        main = firstWeather?.main.orEmpty(),
        description = firstWeather?.description.orEmpty(),
        icon = firstWeather?.icon
    )
    return ForecastEntry(
        dateTimeText = dateTimeText.orEmpty(),
        temperature = main.temperature,
        minTemperature = main.minTemperature,
        maxTemperature = main.maxTemperature,
        condition = condition
    )
}
