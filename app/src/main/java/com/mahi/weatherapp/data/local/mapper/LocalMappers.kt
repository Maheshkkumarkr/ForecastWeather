package com.mahi.weatherapp.data.local.mapper

import com.mahi.weatherapp.data.local.entity.CityEntity
import com.mahi.weatherapp.data.local.entity.ForecastEntity
import com.mahi.weatherapp.domain.model.City
import com.mahi.weatherapp.domain.model.ForecastEntry
import com.mahi.weatherapp.domain.model.WeatherCondition

fun CityEntity.toDomain(): City = City(
    name = name,
    country = country,
    latitude = latitude,
    longitude = longitude
)

fun City.toEntity(lastFetchedAt: Long): CityEntity = CityEntity(
    name = name,
    country = country,
    latitude = latitude,
    longitude = longitude,
    lastFetchedAt = lastFetchedAt
)

fun ForecastEntity.toDomain(): ForecastEntry = ForecastEntry(
    dateTimeText = dateTimeText,
    temperature = temperature,
    minTemperature = minTemperature,
    maxTemperature = maxTemperature,
    condition = WeatherCondition(main = main, description = description, icon = icon)
)

fun ForecastEntry.toEntity(cityName: String): ForecastEntity = ForecastEntity(
    cityName = cityName,
    dateTimeText = dateTimeText,
    temperature = temperature,
    minTemperature = minTemperature,
    maxTemperature = maxTemperature,
    main = condition.main,
    description = condition.description,
    icon = condition.icon
)
