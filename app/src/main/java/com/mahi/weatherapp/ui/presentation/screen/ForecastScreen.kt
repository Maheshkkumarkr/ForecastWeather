package com.mahi.weatherapp.ui.presentation.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    onRefresh: () -> Unit
) {
    // 1. Detect Orientation
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Surface(modifier = modifier.fillMaxSize()) {
        if (isLandscape) {
            // --- LANDSCAPE MODE: Side-by-Side ---
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Pane: Search (Takes up roughly 1/3 of the screen)
                ForecastHeader(
                    state = state,
                    onCityChange = onCityChange,
                    onSearch = onSearch,
                    onRefresh = onRefresh,
                    modifier = Modifier.weight(0.35f) // or .width(300.dp) for fixed tablet width
                )

                // Right Pane: The Forecast List (Takes up the remaining 2/3)
                ForecastContent(
                    state = state,
                    onRetry = onRetry,
                    modifier = Modifier.weight(0.65f)
                )
            }
        } else {
            // --- PORTRAIT MODE: Stacked Vertically ---
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                ForecastHeader(
                    state = state,
                    onCityChange = onCityChange,
                    onSearch = onSearch,
                    onRefresh = onRefresh
                )

                Spacer(modifier = Modifier.height(16.dp))

                ForecastContent(
                    state = state,
                    onRetry = onRetry,
                    modifier = Modifier.weight(1f) // Takes remaining height safely
                )
            }
        }
    }
}