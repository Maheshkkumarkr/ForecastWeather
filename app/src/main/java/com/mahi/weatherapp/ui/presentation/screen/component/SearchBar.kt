package com.mahi.weatherapp.ui.presentation.screen.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(
    city: String,
    isLoading: Boolean,
    onCityChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    isOnline: Boolean
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    ElevatedCard(
        modifier = modifier.fillMaxWidth(), // Applies parent constraints first
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp) // Slightly larger padding for Material 3 standard
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp) // More breathing room
        ) {
            Text(
                text = "3-day forecast",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

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
                horizontalArrangement = Arrangement.spacedBy(8.dp), // 4. ALIGNMENT FIX
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        onSearch()
                    },
                    enabled = !isLoading && city.isNotBlank(), // Don't allow empty searches
                    modifier = Modifier
                        .weight(1f) // 5. SIZING: Button takes all remaining width
                        .heightIn(min = 48.dp) // 6. SIZING: Flexible height, strict minimum for touch target
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.animateContentSize() // 7. ANIMATION: Smoothly shifts text when loader appears
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp), // 8. SIZING: Must use size(), not height()
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary // Ensure contrast
                            )
                        }
                        Text("Search")
                    }
                }

                // Refresh Button
                FilledIconButton( // 9. SIZING: Gives it a clear visual boundary matching the button
                    onClick = onRefresh,
                    enabled = !isLoading && city.isNotBlank() && isOnline, // Don't allow empty searches or offline
                    modifier = Modifier.size(48.dp) // Match the minimum height of the Search button
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
        }
    }
}
