package com.mahi.weatherapp.ui.presentation.screen.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.mahi.weatherapp.ui.presentation.statesAndEvents.state.DailyForecast
import java.time.format.DateTimeFormatter


// --- 1. MODIFIER (Fading Edges) ---
@Composable
fun ForecastItemCard(item: DailyForecast, modifier: Modifier = Modifier) {
    // Cache the heavy date formatting
    val dateLabel = remember(item.date) {
        item.date.format(DateTimeFormatter.ofPattern("EEE, dd MMM"))
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            // We remove the horizontal padding here so the LazyRow can reach the edges
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- Daily Header Section ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp), // Re-apply padding specifically to the header
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (item.condition.icon != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://openweathermap.org/img/wn/${item.condition.icon}@2x.png")
                            .crossfade(true)
                            .build(),
                        contentDescription = item.condition.description,
                        // Fixed size prevents the text from jumping when the image loads
                        modifier = Modifier.size(56.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = dateLabel,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = item.condition.description.ifBlank { item.condition.main },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Min ${item.minTemp}° · Max ${item.maxTemp}° · Avg ${
                            "%.1f".format(
                                item.avgTemp
                            )
                        }°",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // --- Hourly Scroll Section ---
            if (item.slots.isNotEmpty()) {
                val lazyRowState = rememberLazyListState()

                LazyRow(
                    state = lazyRowState,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    // contentPadding keeps the first/last items aligned with the text above
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 4.dp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalFadingEdges(lazyRowState, fadeWidth = 32.dp) // Apply the magic!
                ) {
                    items(
                        items = item.slots,
                        key = { it.timeLabel }, // Performance: Compose knows exactly which item this is
                        contentType = { "hourly_slot" } // Performance: View recycling
                    ) { slot ->
                        HourlyChip(
                            slot
                        )
                    }
                }
            }
        }
    }
}