package com.mahi.weatherapp.ui.presentation.screen.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mahi.weatherapp.ui.presentation.statesAndEvents.state.DailyForecast


@Composable
fun ForecastList(items: List<DailyForecast>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 4. PERFORMANCE MASTERY: Added Keys and ContentTypes
        items(
            items = items,
            key = { it.date.toString() }, // Prevents entire list redraw if an item moves
            contentType = { "daily_forecast" } // Helps Compose recycle the view structure
        ) { item ->
            ForecastItemCard(
                item
            )
        }
    }
}
