package com.mahi.weatherapp.ui.presentation.screen.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.getValue
import com.mahi.weatherapp.ui.theme.customColors

@Composable
fun ConnectivityBadge(
    isOnline: Boolean,
    modifier: Modifier = Modifier
) {
    // 1. SEMANTIC COLORS: Safely extracted from our extended MaterialTheme
    val successContainer = MaterialTheme.customColors.successContainer
    val onSuccessContainer = MaterialTheme.customColors.onSuccessContainer

    val errorContainer = MaterialTheme.colorScheme.errorContainer
    val onErrorContainer = MaterialTheme.colorScheme.onErrorContainer

    // 2. ANIMATION MASTERY: Smoothly crossfade colors over 500ms when state flips
    val containerColor by animateColorAsState(
        targetValue = if (isOnline) successContainer else errorContainer,
        animationSpec = tween(durationMillis = 500),
        label = "BadgeContainerColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isOnline) onSuccessContainer else onErrorContainer,
        animationSpec = tween(durationMillis = 500),
        label = "BadgeContentColor"
    )

    // 3. THE UI: A crisp, tightly padded chip
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = MaterialTheme.shapes.small // Slightly sharper corners for badges
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), // Tighter vertical padding
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                contentDescription = if (isOnline) "Online" else "Offline",
                tint = contentColor,
                modifier = Modifier.size(16.dp) // Smaller icon for secondary UI
            )
            Text(
                text = if (isOnline) "Online" else "Offline",
                color = contentColor,
                style = MaterialTheme.typography.labelMedium, // Crisper typography for small elements
                fontWeight = FontWeight.Bold
            )
        }
    }
}