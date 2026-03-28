package com.mahi.weatherapp.ui.presentation.screen.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mahi.weatherapp.ui.presentation.statesAndEvents.state.ForecastState


@Composable
fun ForecastContent(
    state: ForecastState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = state,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "ForecastStateTransition",
        modifier = modifier
    ) { targetState ->
        when {
            targetState.isLoading && targetState.items.isEmpty() -> LoadingState()
            targetState.error != null && targetState.items.isEmpty() && !state.isOnline ->
                ErrorState(targetState.error, onRetry)
            targetState.items.isEmpty() -> EmptyState()
            else -> ForecastList(targetState.items)
        }
    }
}
