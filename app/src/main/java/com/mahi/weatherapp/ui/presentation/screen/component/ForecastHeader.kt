package com.mahi.weatherapp.ui.presentation.screen.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*

@Composable
fun ForecastHeader(
    state: ForecastState,
    onCityChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRefresh: () -> Unit,
    onForecastDaysChange: (Int) -> Unit,
    onLocationBadgeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {

        // --- 1. APP BAR (Title, Location badge & Connectivity badge) ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weather",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LocationPermissionBadge(
                        isGranted = state.isLocationPermissionGranted,
                        onBadgeClick = onLocationBadgeClick
                    )
                    ConnectivityBadge(isOnline = state.isOnline)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
        }

        // --- 2. OFFLINE CACHE BANNER ---
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

        // --- 3. GPS LOADING STATUS ---
        item {
            AnimatedVisibility(
                visible = state.isDetectingLocation,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Acquiring GPS location...",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }

        // --- 4. LOCATION ERROR STATUS ---
        item {
            val manualEntryPrompt = state.locationError
            AnimatedVisibility(
                visible = !manualEntryPrompt.isNullOrBlank() && !state.isDetectingLocation,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Warning,
                            contentDescription = "Location Error",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = manualEntryPrompt.orEmpty(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }

        // --- 5. SEARCH COMPONENT ---
        item {
            SearchBar(
                isOnline = state.isOnline,
                city = state.cityQuery,
                forecastDays = state.forecastDays,
                isLoading = state.isLoading || state.isDetectingLocation,
                onCityChange = onCityChange,
                onSearch = onSearch,
                onRefresh = onRefresh,
                onForecastDaysChange = onForecastDaysChange
            )
        }

        // --- 6. LAST UPDATED TIMESTAMP ---
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
