package dev.jmx.client.ui.screens.readScreen

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastCoerceIn
import dev.jmx.client.ui.glass.LocalJmxBackdrop
import dev.jmx.client.ui.glass.LocalJmxGlassPalette
import dev.jmx.client.ui.glass.jmxGlass
import dev.jmx.client.ui.razor.RazorText
import dev.jmx.client.ui.viewModel.AlbumReadViewModel
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
private fun ChapterBubble(
    chapterLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backdrop = LocalJmxBackdrop.current
    val palette = LocalJmxGlassPalette.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.70f, stiffness = Spring.StiffnessMedium),
        label = "chapterBubblePress"
    )

    Box(
        modifier = modifier
            .size(76.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .then(
                if (backdrop != null) {
                    Modifier.jmxGlass(
                        backdrop = backdrop,
                        radius = 999.dp,
                        blurRadius = 7.dp,
                        lensHeight = 20.dp,
                        lensAmount = 28.dp,
                        surfaceColor = palette.glassStrongSurface.copy(alpha = 0.38f),
                        depth = true,
                        shadowAlpha = 0.05f,
                        highlightAlpha = 0.50f,
                        innerShadowAlpha = 0.32f,
                        surfaceSheenAlpha = 0.05f
                    )
                } else {
                    Modifier.background(palette.glassStrongSurface, CircleShape)
                }
            )
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            RazorText(
                text = "章节",
                style = TextStyle(
                    color = palette.accent,
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            if (chapterLabel.isNotBlank()) {
                RazorText(
                    text = chapterLabel,
                    modifier = Modifier.padding(horizontal = 10.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        color = palette.secondaryText,
                        fontSize = 11.sp,
                        lineHeight = 13.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}

@Composable
private fun RazorSlider(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalJmxGlassPalette.current
    var trackSize by remember { mutableStateOf(IntSize.Zero) }
    val fraction = if (valueRange.endInclusive <= valueRange.start) {
        0f
    } else {
        ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).fastCoerceIn(0f, 1f)
    }
    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = spring(dampingRatio = 0.80f, stiffness = Spring.StiffnessMediumLow),
        label = "razorSliderFraction"
    )

    fun updateFromX(x: Float) {
        val width = trackSize.width.toFloat().coerceAtLeast(1f)
        val nextFraction = (x / width).fastCoerceIn(0f, 1f)
        val next = valueRange.start + nextFraction * (valueRange.endInclusive - valueRange.start)
        onValueChange(next)
    }

    Box(
        modifier = modifier
            .height(42.dp)
            .onSizeChanged { trackSize = it }
            .pointerInput(valueRange) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    updateFromX(down.position.x)
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id }
                            ?: event.changes.firstOrNull()
                        if (change == null || !change.pressed) {
                            break
                        }
                        updateFromX(change.position.x)
                        change.consume()
                    }
                    onValueChangeFinished()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(28.dp)) {
            val centerY = size.height / 2f
            val stroke = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            drawLine(
                color = palette.primaryText.copy(alpha = 0.10f),
                start = Offset(0f, centerY),
                end = Offset(size.width, centerY),
                strokeWidth = stroke.width,
                cap = StrokeCap.Round
            )
            drawLine(
                color = palette.accent,
                start = Offset(0f, centerY),
                end = Offset(size.width * animatedFraction, centerY),
                strokeWidth = stroke.width,
                cap = StrokeCap.Round
            )
            drawCircle(
                color = palette.accent,
                radius = 8.dp.toPx(),
                center = Offset(size.width * animatedFraction, centerY)
            )
            drawCircle(
                color = palette.glassStrongSurface.copy(alpha = 0.72f),
                radius = 4.dp.toPx(),
                center = Offset(size.width * animatedFraction, centerY)
            )
        }
    }
}

@Composable
fun ToolsBar(
    modifier: Modifier = Modifier,
    sliderValue: Float,
    albumReadViewModel: AlbumReadViewModel,
    onSliderValueChange: (value: Float) -> Unit,
    onSliderValueChangeFinished: () -> Unit,
    showChapterButton: Boolean = false,
    chapterLabel: String = "",
    onChapterClick: () -> Unit = {},
) {
    val backdrop = LocalJmxBackdrop.current
    val palette = LocalJmxGlassPalette.current
    val size = albumReadViewModel.size
    val currentPage = sliderValue.roundToInt().coerceIn(0, max(0, size - 1)) + 1
    val panelShape = RoundedCornerShape(999.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 40.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showChapterButton) {
            ChapterBubble(
                chapterLabel = chapterLabel,
                onClick = onChapterClick
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .height(68.dp)
                .then(
                    if (backdrop != null) {
                        Modifier.jmxGlass(
                            backdrop = backdrop,
                            radius = 999.dp,
                            blurRadius = 6.dp,
                            lensHeight = 22.dp,
                            lensAmount = 32.dp,
                            surfaceColor = palette.glassStrongSurface.copy(alpha = 0.36f),
                            depth = true,
                            shadowAlpha = 0.05f,
                            highlightAlpha = 0.46f,
                            innerShadowAlpha = 0.30f,
                            surfaceSheenAlpha = 0.04f
                        )
                    } else {
                        Modifier.background(palette.glassStrongSurface, panelShape)
                    }
                )
                .clip(panelShape)
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RazorText(
                text = "$currentPage/$size",
                modifier = Modifier.widthIn(min = 58.dp),
                style = TextStyle(
                    color = palette.primaryText,
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )
            RazorSlider(
                modifier = Modifier.weight(1f),
                value = sliderValue,
                valueRange = 0f..max(0, size - 1).toFloat(),
                onValueChange = onSliderValueChange,
                onValueChangeFinished = onSliderValueChangeFinished
            )
        }
    }
}
