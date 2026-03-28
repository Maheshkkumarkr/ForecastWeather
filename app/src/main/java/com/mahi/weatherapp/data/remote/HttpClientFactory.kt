package com.mahi.weatherapp.data.remote

import com.mahi.weatherapp.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


object HttpClientFactory {
    fun create(engine: HttpClientEngine, enableLogging: Boolean = false, logLevel: LogLevel = LogLevel.INFO): HttpClient {
        check(BuildConfig.OPENWEATHER_API_KEY.isNotBlank()) {
            "Missing OpenWeather API key. Add openWeatherApiKey to local.properties or OPENWEATHER_API_KEY environment variable."
        }

        return HttpClient(engine) {

            expectSuccess = true // Throws exceptions for non-2xx responses

            defaultRequest {
                // Ensure base URL is set correctly
                url.takeFrom(BuildConfig.OPENWEATHER_BASE_URL)
                
                // Use url.parameters to ensure they are merged with subsequent call parameters
                url.parameters.append("appid", BuildConfig.OPENWEATHER_API_KEY)
                url.parameters.append("units", "metric")
                
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }

            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                    }
                )
            }
            install(HttpTimeout) {
                connectTimeoutMillis = 60_000
                socketTimeoutMillis = 60_000
                requestTimeoutMillis = 60_000
            }

            if (enableLogging){
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = logLevel
                }
            }
        }
    }

}