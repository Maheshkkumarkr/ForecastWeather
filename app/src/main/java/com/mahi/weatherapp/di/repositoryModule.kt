package com.mahi.weatherapp.di

import com.google.android.gms.location.LocationServices
import com.mahi.weatherapp.data.common.NetworkMonitor
import com.mahi.weatherapp.data.location.LocationProvider
import com.mahi.weatherapp.data.repository.ForecastRepositoryImpl
import com.mahi.weatherapp.domain.repository.ForecastRepository
import com.mahi.weatherapp.domain.usecase.GetForecastUseCase
import com.mahi.weatherapp.ui.presentation.viewmodel.ForecastViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::ForecastRepositoryImpl).bind<ForecastRepository>()
    singleOf(::GetForecastUseCase)
    singleOf(::NetworkMonitor)
    single {
        LocationProvider(get(),LocationServices.getFusedLocationProviderClient(androidContext()))
    }
    viewModelOf(::ForecastViewModel)
}