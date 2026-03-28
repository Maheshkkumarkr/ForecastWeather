package com.mahi.weatherapp.ui.presentation.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.mahi.weatherapp.ui.presentation.screen.component.ForecastContent
import com.mahi.weatherapp.ui.presentation.screen.component.ForecastHeader
import com.mahi.weatherapp.ui.presentation.statesAndEvents.state.ForecastState


@Composable
fun ForecastScreen(
    modifier: Modifier = Modifier,
    state: ForecastState,
    onCityChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    onForecastDaysChange: (Int) -> Unit,
    onLocationBadgeClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Surface(modifier = modifier.fillMaxSize()) {
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ForecastHeader(
                    state = state,
                    onCityChange = onCityChange,
                    onSearch = onSearch,
                    onRefresh = onRefresh,
                    onForecastDaysChange = onForecastDaysChange,
                    onLocationBadgeClick = onLocationBadgeClick,
                    modifier = Modifier.weight(0.35f)
                )

                ForecastContent(
                    state = state,
                    onRetry = onRetry,
                    modifier = Modifier.weight(0.65f)
                )
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                ForecastHeader(
                    state = state,
                    onCityChange = onCityChange,
                    onSearch = onSearch,
                    onRefresh = onRefresh,
                    onForecastDaysChange = onForecastDaysChange,
                    onLocationBadgeClick = onLocationBadgeClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                ForecastContent(
                    state = state,
                    onRetry = onRetry,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}