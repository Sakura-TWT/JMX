package dev.jmx.client.ui.razor

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastCoerceIn
import dev.jmx.client.ui.glass.LocalJmxBackdrop
import dev.jmx.client.ui.glass.LocalJmxGlassPalette
import dev.jmx.client.ui.glass.jmxGlass

@Composable
fun RazorText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = style,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
fun RazorText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = style,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
fun RazorIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: androidx.compose.ui.graphics.Color = LocalJmxGlassPalette.current.primaryText
) {
    Image(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        colorFilter = ColorFilter.tint(tint)
    )
}

@Composable
fun RazorIcon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: androidx.compose.ui.graphics.Color = LocalJmxGlassPalette.current.primaryText
) {
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        colorFilter = ColorFilter.tint(tint)
    )
}

@Composable
fun RazorGlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val backdrop = LocalJmxBackdrop.current
    val palette = LocalJmxGlassPalette.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.965f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "razorButtonPress"
    )
    val shape = RoundedCornerShape(999.dp)

    Row(
        modifier = modifier
            .height(48.dp)
            .defaultMinSize(minWidth = 48.dp)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .then(
                if (backdrop != null) {
                    Modifier.jmxGlass(
                        backdrop = backdrop,
                        radius = 999.dp,
                        blurRadius = 8.dp,
                        lensHeight = 16.dp,
                        lensAmount = 28.dp,
                        surfaceColor = palette.glassSurface.copy(alpha = 0.28f),
                        depth = true,
                        shadowAlpha = 0.05f,
                        highlightAlpha = 0.48f,
                        innerShadowAlpha = 0.30f,
                        surfaceSheenAlpha = 0.05f
                    )
                } else {
                    Modifier.background(palette.glassSurface.copy(alpha = 0.34f), shape)
                }
            )
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 13.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
fun RazorBackTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onTitleClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val palette = LocalJmxGlassPalette.current
    val titleInteractionSource = remember { MutableInteractionSource() }
    val titleClickModifier = if (onTitleClick != null) {
        Modifier.clickable(
            interactionSource = titleInteractionSource,
            indication = null,
            onClick = onTitleClick
        )
    } else {
        Modifier
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RazorGlassButton(onClick = onBack) {
            Image(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(palette.primaryText)
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(18.dp))
                .then(titleClickModifier)
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            RazorText(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    color = palette.primaryText,
                    fontSize = 28.sp,
                    lineHeight = 31.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.4).sp
                )
            )
        }
        actions()
    }
}

@Composable
fun RazorChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false
) {
    val backdrop = LocalJmxBackdrop.current
    val palette = LocalJmxGlassPalette.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.965f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "razorChipPress"
    )
    val shape = RoundedCornerShape(999.dp)

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 34.dp)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .then(
                if (backdrop != null) {
                    Modifier.jmxGlass(
                        backdrop = backdrop,
                        radius = 999.dp,
                        blurRadius = if (selected) 8.dp else 5.dp,
                        lensHeight = if (selected) 16.dp else 10.dp,
                        lensAmount = if (selected) 24.dp else 14.dp,
                        surfaceColor = if (selected) {
                            palette.glassStrongSurface.copy(alpha = 0.42f)
                        } else {
                            palette.glassSurface.copy(alpha = 0.28f)
                        },
                        depth = selected,
                        shadowAlpha = if (selected) 0.05f else 0.025f,
                        highlightAlpha = if (selected) 0.50f else 0.34f,
                        innerShadowAlpha = if (selected) 0.34f else 0.18f,
                        surfaceSheenAlpha = if (selected) 0.07f else 0.03f
                    )
                } else {
                    Modifier.background(
                        if (selected) palette.glassStrongSurface else palette.glassSurface,
                        shape
                    )
                }
            )
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 13.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        RazorText(
            text = label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                color = if (selected) palette.primaryText else palette.secondaryText,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            )
        )
    }
}

@Composable
fun RazorLoadingIndicator(
    modifier: Modifier = Modifier,
    progress: Float? = null
) {
    val palette = LocalJmxGlassPalette.current
    val animatedSweep by animateFloatAsState(
        targetValue = ((progress ?: 0.72f) * 360f).fastCoerceIn(22f, 320f),
        animationSpec = spring(dampingRatio = 0.86f, stiffness = Spring.StiffnessLow),
        label = "razorLoadingSweep"
    )
    Canvas(modifier = modifier.size(22.dp)) {
        val stroke = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round)
        drawArc(
            color = palette.tertiaryText.copy(alpha = 0.22f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(1.2.dp.toPx(), 1.2.dp.toPx()),
            size = Size(size.width - 2.4.dp.toPx(), size.height - 2.4.dp.toPx()),
            style = stroke
        )
        drawArc(
            color = palette.accent,
            startAngle = -90f,
            sweepAngle = animatedSweep,
            useCenter = false,
            topLeft = Offset(1.2.dp.toPx(), 1.2.dp.toPx()),
            size = Size(size.width - 2.4.dp.toPx(), size.height - 2.4.dp.toPx()),
            style = stroke
        )
    }
}

@Composable
fun RazorLoadingRow(text: String, modifier: Modifier = Modifier) {
    val palette = LocalJmxGlassPalette.current
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RazorLoadingIndicator()
        Spacer(modifier = Modifier.width(8.dp))
        RazorText(
            text = text,
            style = TextStyle(
                color = palette.secondaryText,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
