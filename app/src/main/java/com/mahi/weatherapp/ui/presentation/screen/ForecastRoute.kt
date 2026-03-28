package com.mahi.weatherapp.ui.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mahi.weatherapp.ui.presentation.statesAndEvents.event.ForecastEvent
import com.mahi.weatherapp.ui.presentation.viewmodel.ForecastViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun ForecastRoute(viewModel: ForecastViewModel = koinViewModel(), modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        if (state.items.isEmpty()) {
            viewModel.onEvent(ForecastEvent.Search)
        }
    }

    ForecastScreen(
        modifier = modifier,
        state = state,
        onCityChange = { viewModel.onEvent(ForecastEvent.CityChanged(it)) },
        onSearch = { viewModel.onEvent(ForecastEvent.Search) },
        onRetry = { viewModel.onEvent(ForecastEvent.Retry) },
        onRefresh = { viewModel.onEvent(ForecastEvent.Refresh) }
    )
}