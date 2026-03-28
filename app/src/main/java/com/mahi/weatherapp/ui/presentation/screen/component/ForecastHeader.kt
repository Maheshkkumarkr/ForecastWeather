package com.mahi.weatherapp.ui.presentation.screen.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mahi.weatherapp.ui.presentation.statesAndEvents.state.ForecastState


@Composable
fun ForecastHeader(
    state: ForecastState,
    onCityChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                ConnectivityBadge(isOnline = state.isOnline)
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            AnimatedVisibility(
                visible = !state.isOnline && state.isFromCache,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    OfflineBanner(lastUpdated = state.lastUpdated)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        item {
            SearchBar(
                isOnline = state.isOnline,
                city = state.cityQuery,
                isLoading = state.isLoading,
                onCityChange = onCityChange,
                onSearch = onSearch,
                onRefresh = onRefresh
            )
        }

        if (state.lastUpdated != null) {
            item {
                val cacheStatus = if (state.isFromCache) " · Cached" else ""
                Text(
                    text = "Updated ${state.lastUpdated}$cacheStatus",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                )
            }
        }
    }
}


/*
@Composable
fun ForecastHeader(
    state: ForecastState,
    onCityChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // If the header needs to scroll off-screen, it should be an item { } INSIDE the parent LazyColumn.
    Column(
        modifier = modifier.fillMaxWidth().verticalScroll(scrollState)
    ) {

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            ConnectivityBadge(isOnline = state.isOnline)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // AnimatedVisibility replaces animateContentSize
        AnimatedVisibility(
            visible = !state.isOnline && state.isFromCache,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            // We wrap the banner AND its bottom spacer so the spacing completely disappears when online
            Column {
                OfflineBanner(lastUpdated = state.lastUpdated)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        SearchBar(
            city = state.cityQuery,
            isLoading = state.isLoading,
            onCityChange = onCityChange,
            onSearch = onSearch,
            onRefresh = onRefresh
        )

        state.lastUpdated?.let { lastUpdatedTime ->
            val cacheStatus = if (state.isFromCache) " · Cached" else ""
            Text(
                text = "Updated $lastUpdatedTime$cacheStatus",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, start = 4.dp) // Slight start padding visually aligns it with the SearchBar's internal text
            )
        }
    }
}
*/
