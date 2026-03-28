package com.mahi.weatherapp.di

import androidx.room.Room
import com.mahi.weatherapp.data.common.NetworkMonitor
import com.mahi.weatherapp.data.local.WeatherDatabase
import com.mahi.weatherapp.data.remote.HttpClientFactory
import com.mahi.weatherapp.data.remote.WeatherApi
import com.mahi.weatherapp.data.repository.ForecastRepositoryImpl
import com.mahi.weatherapp.domain.repository.ForecastRepository
import com.mahi.weatherapp.domain.usecase.GetForecastUseCase
import com.mahi.weatherapp.ui.presentation.viewmodel.ForecastViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.logging.LogLevel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

//val networkModule = module {
//    single<HttpClient> {
//        HttpClientFactory.create(
//            engine = Android.create(),
//            enableLogging = true,
//            logLevel = LogLevel.ALL
//        )
//    }
////
////    single {
////        WeatherApi(client = get())
////    }
//
//    singleOf(::WeatherApi)
//}

//val databaseModule = module {
//    single {
//        Room.databaseBuilder(
//            get(),
//            WeatherDatabase::class.java,
//            WeatherDatabase.DB_NAME
//        ).build()
//    }
//}

//val repositoryModule = module {
//    singleOf(::ForecastRepositoryImpl).bind<ForecastRepository>()
//    singleOf(::GetForecastUseCase)
//    singleOf(::NetworkMonitor)
//    viewModelOf(::ForecastViewModel)
//}

val appModules = listOf(
    networkModule,
    databaseModule,
    repositoryModule
)

