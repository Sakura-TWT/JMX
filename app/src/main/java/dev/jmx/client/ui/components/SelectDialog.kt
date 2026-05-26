package dev.jmx.client.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.jmx.client.ui.glass.LocalJmxGlassPalette
import dev.jmx.client.ui.razor.RazorText

data class SelectOption(
    val label: String,
    val value: String,
    val description: String? = null,
)

@Composable
private fun Checkmark(selected: Boolean, modifier: Modifier = Modifier) {
    val palette = LocalJmxGlassPalette.current
    val progress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow),
        label = "iosCheckmark"
    )
    Canvas(modifier = modifier.size(22.dp)) {
        if (progress <= 0f) return@Canvas
        val stroke = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round)
        val p1 = Offset(size.width * 0.20f, size.height * 0.54f)
        val p2 = Offset(size.width * 0.42f, size.height * 0.73f)
        val p3 = Offset(size.width * 0.82f, size.height * 0.28f)
        drawLine(
            color = palette.accent.copy(alpha = progress),
            start = p1,
            end = Offset(
                x = p1.x + (p2.x - p1.x) * progress.coerceAtMost(1f),
                y = p1.y + (p2.y - p1.y) * progress.coerceAtMost(1f)
            ),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round
        )
        if (progress > 0.45f) {
            val tail = ((progress - 0.45f) / 0.55f).coerceIn(0f, 1f)
            drawLine(
                color = palette.accent.copy(alpha = progress),
                start = p2,
                end = Offset(
                    x = p2.x + (p3.x - p2.x) * tail,
                    y = p2.y + (p3.y - p2.y) * tail
                ),
                strokeWidth = stroke.width,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun SelectRow(
    option: SelectOption,
    selected: Boolean,
    showDivider: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalJmxGlassPalette.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressAlpha by animateFloatAsState(
        targetValue = if (pressed) 0.08f else 0f,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMedium),
        label = "iosSelectRowPress"
    )

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(palette.primaryText.copy(alpha = pressAlpha))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                RazorText(
                    text = option.label,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        color = palette.primaryText,
                        fontSize = 17.sp,
                        lineHeight = 22.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        letterSpacing = (-0.2).sp
                    )
                )
                option.description?.takeIf { it.isNotBlank() }?.let {
                    RazorText(
                        text = it,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            color = palette.tertiaryText,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
            }
            Checkmark(selected = selected)
        }
        if (showDivider) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxWidth()
                    .padding(start = 20.dp)
                    .background(palette.primaryText.copy(alpha = 0.08f))
                    .height(0.6.dp)
            )
        }
    }
}

@Composable
fun SelectDialog(
    title: String,
    value: String?,
    selectOptionList: List<SelectOption> = listOf(),
    onSelect: (String) -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {
    val palette = LocalJmxGlassPalette.current
    val density = LocalDensity.current
    val maxHeight = with(density) {
        (LocalWindowInfo.current.containerSize.height * 0.56f).toDp()
    }
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        appeared = true
    }
    val translate by animateFloatAsState(
        targetValue = if (appeared) 0f else 44f,
        animationSpec = spring(dampingRatio = 0.86f, stiffness = Spring.StiffnessMediumLow),
        label = "iosSheetTranslate"
    )
    val alpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.90f, stiffness = Spring.StiffnessMediumLow),
        label = "iosSheetAlpha"
    )
    val sheetColor = if (palette.page.luminance() > 0.5f) {
        Color.White.copy(alpha = 0.96f)
    } else {
        Color(0xFF1C1C1E).copy(alpha = 0.96f)
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.30f))
                .clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = onDismissRequest
                )
                .padding(horizontal = 12.dp)
                .navigationBarsPadding(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 430.dp)
                    .padding(bottom = 10.dp)
                    .graphicsLayer {
                        translationY = translate
                        this.alpha = alpha
                    },
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(sheetColor)
                        .clickable(
                            interactionSource = null,
                            indication = null,
                            onClick = {}
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        RazorText(
                            text = title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(
                                color = palette.primaryText,
                                fontSize = 13.sp,
                                lineHeight = 17.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        RazorText(
                            text = "选择后立即生效",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(
                                color = palette.tertiaryText,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(palette.primaryText.copy(alpha = 0.08f))
                            .height(0.6.dp)
                    )
                    LazyColumn(modifier = Modifier.heightIn(max = maxHeight)) {
                        itemsIndexed(selectOptionList, key = { _, item -> item.value }) { index, option ->
                            SelectRow(
                                option = option,
                                selected = option.value == value,
                                showDivider = index < selectOptionList.lastIndex,
                                onClick = { onSelect(option.value) }
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(sheetColor)
                        .clickable(
                            interactionSource = null,
                            indication = null,
                            onClick = onDismissRequest
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    RazorText(
                        text = "取消",
                        style = TextStyle(
                            color = palette.accent,
                            fontSize = 17.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}
