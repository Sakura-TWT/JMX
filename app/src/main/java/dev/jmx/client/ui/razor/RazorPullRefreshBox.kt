package dev.jmx.client.ui.razor

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun RazorPullRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    var pullDistance by remember { mutableFloatStateOf(0f) }
    val indicatorOffset by animateFloatAsState(
        targetValue = when {
            isRefreshing -> 52f
            else -> pullDistance.coerceIn(0f, 72f)
        },
        animationSpec = spring(dampingRatio = 0.78f, stiffness = 420f),
        label = "razorPullRefreshOffset"
    )

    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) {
            pullDistance = 0f
        }
    }

    Box(
        modifier = modifier.pointerInput(enabled, isRefreshing) {
            if (!enabled) return@pointerInput
            detectVerticalDragGestures(
                onDragEnd = {
                    if (pullDistance > 92f && !isRefreshing) {
                        onRefresh()
                    }
                    pullDistance = 0f
                },
                onDragCancel = {
                    pullDistance = 0f
                },
                onVerticalDrag = { change, dragAmount ->
                    if (dragAmount > 0f && !isRefreshing) {
                        pullDistance = (pullDistance + dragAmount * 0.48f).coerceIn(0f, 128f)
                        change.consume()
                    }
                }
            )
        }
    ) {
        content()
        if (isRefreshing || indicatorOffset > 1f) {
            RazorLoadingIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp)
                    .offset { IntOffset(0, indicatorOffset.roundToInt()) }
                    .size(24.dp)
            )
        }
    }
}
