package com.mahi.weatherapp.ui.presentation.screen.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

/*
@Composable
fun SearchBar(
    city: String,
    forecastDays: Int,
    isLoading: Boolean,
    onCityChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRefresh: () -> Unit,
    onForecastDaysChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isOnline: Boolean
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var showDaysDropdown by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$forecastDays-day forecast",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Box {
                    IconButton(onClick = { showDaysDropdown = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Configure days",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = showDaysDropdown,
                        onDismissRequest = { showDaysDropdown = false }
                    ) {
                        (1..5).forEach { days ->
                            DropdownMenuItem(
                                text = { Text("$days Days") },
                                onClick = {
                                    onForecastDaysChange(days)
                                    showDaysDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = city,
                onValueChange = onCityChange,
                label = { Text("City") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        if (city.isNotBlank()) onSearch()
                    }
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        onSearch()
                    },
                    enabled = !isLoading && city.isNotBlank(),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.animateContentSize()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Text("Search")
                    }
                }

                FilledIconButton(
                    onClick = onRefresh,
                    enabled = !isLoading && city.isNotBlank() && isOnline,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
        }
    }
}*/


import androidx.compose.animation.*
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*

@Composable
fun SearchBar(
    city: String,
    forecastDays: Int,
    isLoading: Boolean,
    isOnline: Boolean, // 1. KOTLIN MASTERY: Moved before the default parameter
    onCityChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRefresh: () -> Unit,
    onForecastDaysChange: (Int) -> Unit,
    modifier: Modifier = Modifier // Default parameters must always be last
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var showDaysDropdown by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- HEADER ROW ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 2. MICRO-INTERACTION: Smoothly roll the number when it changes
                AnimatedContent(
                    targetState = forecastDays,
                    transitionSpec = {
                        // If increasing, slide up. If decreasing, slide down.
                        if (targetState > initialState) {
                            (slideInVertically { height -> height } + fadeIn()).togetherWith(
                                slideOutVertically { height -> -height } + fadeOut())
                        } else {
                            (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                                slideOutVertically { height -> height } + fadeOut())
                        }
                    },
                    label = "DaysAnimation"
                ) { animatedDays ->
                    Text(
                        text = "$animatedDays-Day Forecast",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 3. Use a Chip instead of a Pencil icon
                Box {
                    AssistChip(
                        onClick = { showDaysDropdown = true },
                        label = { Text("Days") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Configure days",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )

                    DropdownMenu(
                        expanded = showDaysDropdown,
                        onDismissRequest = { showDaysDropdown = false }
                    ) {
                        (1..5).forEach { days ->
                            // Highlight the currently selected option
                            val isSelected = days == forecastDays
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "$days Days",
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    onForecastDaysChange(days)
                                    showDaysDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            // --- INPUT ROW ---
            OutlinedTextField(
                value = city,
                onValueChange = onCityChange,
                label = { Text("City") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        if (city.isNotBlank()) onSearch()
                    }
                )
            )

            // --- ACTION ROW ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        onSearch()
                    },
                    enabled = !isLoading && city.isNotBlank(),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.animateContentSize()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Text("Search")
                    }
                }

                FilledIconButton(
                    onClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        onRefresh()
                    },
                    // We only allow refresh if we have a city AND we are online
                    enabled = !isLoading && city.isNotBlank() && isOnline,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
        }
    }
}