package com.mahi.weatherapp.ui.presentation.screen.component

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


fun Modifier.horizontalFadingEdges(
    listState: LazyListState,
    fadeWidth: Dp = 32.dp
): Modifier = composed {
    // derivedStateOf prevents unnecessary recompositions when scrolling
    val showStart by remember { derivedStateOf { listState.canScrollBackward } }
    val showEnd by remember { derivedStateOf { listState.canScrollForward } }

    this
        // Flatten the layer to apply the alpha mask correctly without showing the background
        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithContent {
            drawContent() // Draw the LazyRow first

            val fadeWidthPx = fadeWidth.toPx()

            // Mask the left edge if we can scroll backward
            if (showStart) {
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, Color.Black),
                        startX = 0f,
                        endX = fadeWidthPx
                    ),
                    blendMode = BlendMode.DstIn
                )
            }

            // Mask the right edge if we can scroll forward
            if (showEnd) {
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Black, Color.Transparent),
                        startX = size.width - fadeWidthPx,
                        endX = size.width
                    ),
                    blendMode = BlendMode.DstIn
                )
            }
        }
}