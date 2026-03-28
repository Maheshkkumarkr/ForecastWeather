package com.mahi.weatherapp.common

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Number of days in the forecast window (today + 2 = 3-day forecast).
 * The OpenWeatherMap free-tier API provides up to 5 days of data (hard ceiling).
 * Adjust this value (max 4 = 5-day window) to show more days.
 */
const val FORECAST_WINDOW_DAYS = 2L

private val FORECAST_DATE_TIME_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

/**
 * OpenWeather forecast `dt_txt` values are treated as UTC, then converted to the device local zone.
 */
fun String.toForecastLocalDateTime(
    zoneId: ZoneId = ZoneId.systemDefault()
): LocalDateTime? {
    val utcDateTime = runCatching {
        LocalDateTime.parse(this, FORECAST_DATE_TIME_FORMATTER)
    }.getOrNull() ?: return null

    return utcDateTime
        .atOffset(ZoneOffset.UTC)
        .atZoneSameInstant(zoneId)
        .toLocalDateTime()
}

fun String.toForecastLocalDate(
    zoneId: ZoneId = ZoneId.systemDefault()
): LocalDate? = toForecastLocalDateTime(zoneId)?.toLocalDate()

fun String.isWithinForecastWindow(
    today: LocalDate = LocalDate.now(),
    zoneId: ZoneId = ZoneId.systemDefault()
): Boolean {
    val date = toForecastLocalDate(zoneId) ?: return false
    return date in today..today.plusDays(FORECAST_WINDOW_DAYS)
}

