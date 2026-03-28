package com.mahi.weatherapp.ui.presentation.screen.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mahi.weatherapp.ui.theme.customColors

/**
 * Tappable badge showing the current location permission state.
 *
 * - Granted  → green badge with MyLocation icon  ("GPS On")
 * - Denied   → amber/warning badge with LocationOff icon ("GPS Off")
 *
 * Tapping in either state opens the app's System Settings page so the
 * user can grant or revoke the permission without re-installing.
 */
@Composable
fun LocationPermissionBadge(
    isGranted: Boolean,
    onBadgeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val successContainer  = MaterialTheme.customColors.successContainer
    val onSuccessContainer = MaterialTheme.customColors.onSuccessContainer
    val warningContainer   = MaterialTheme.colorScheme.tertiaryContainer
    val onWarningContainer = MaterialTheme.colorScheme.onTertiaryContainer

    val containerColor by animateColorAsState(
        targetValue = if (isGranted) successContainer else warningContainer,
        animationSpec = tween(durationMillis = 500),
        label = "LocationBadgeContainer"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isGranted) onSuccessContainer else onWarningContainer,
        animationSpec = tween(durationMillis = 500),
        label = "LocationBadgeContent"
    )

    Card(
        modifier = modifier.clickable(role = Role.Button, onClick = onBadgeClick),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isGranted) Icons.Default.MyLocation else Icons.Default.LocationOff,
                contentDescription = if (isGranted) "Location permission granted" else "Location permission denied – tap to open settings",
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = if (isGranted) "GPS On" else "GPS Off",
                color = contentColor,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

