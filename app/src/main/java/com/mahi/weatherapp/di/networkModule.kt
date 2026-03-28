package com.mahi.weatherapp.di

import com.mahi.weatherapp.data.remote.HttpClientFactory
import com.mahi.weatherapp.data.remote.WeatherApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.logging.LogLevel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val networkModule = module {
    single<HttpClient> {
        HttpClientFactory.create(
            engine = Android.create(),
            enableLogging = true,
            logLevel = LogLevel.ALL
        )
    }
//
//    single {
//        WeatherApi(client = get())
//    }
    
    singleOf(::WeatherApi)
}