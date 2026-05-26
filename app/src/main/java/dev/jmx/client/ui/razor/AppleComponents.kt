package dev.jmx.client.ui.razor

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jmx.client.ui.glass.LocalJmxGlassPalette

@Composable
fun AppleIconButton(
    imageVector: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    tint: Color = if (selected) LocalJmxGlassPalette.current.accent else LocalJmxGlassPalette.current.primaryText,
) {
    val palette = LocalJmxGlassPalette.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "appleIconButtonPress"
    )

    Box(
        modifier = modifier
            .size(42.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .background(palette.contentSurface.copy(alpha = if (selected) 0.92f else 0.70f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        RazorIcon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier.size(21.dp),
            tint = tint
        )
    }
}

@Composable
fun AppleGroupedRow(
    title: String,
    value: String? = null,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    showDivider: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val palette = LocalJmxGlassPalette.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressAlpha by animateFloatAsState(
        targetValue = if (pressed) 0.08f else 0f,
        animationSpec = spring(dampingRatio = 0.88f, stiffness = Spring.StiffnessMedium),
        label = "appleGroupedRowPress"
    )
    val clickable = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
    } else {
        Modifier
    }

    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(palette.primaryText.copy(alpha = pressAlpha))
                .then(clickable)
                .padding(horizontal = 16.dp, vertical = 13.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leading?.invoke()
            RazorText(
                text = title,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    color = palette.primaryText,
                    fontSize = 16.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = (-0.15).sp
                )
            )
            value?.let {
                RazorText(
                    text = it,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        color = palette.tertiaryText,
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        }
        if (showDivider) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxWidth()
                    .padding(start = if (leading == null) 16.dp else 58.dp)
                    .height(0.6.dp)
                    .background(palette.primaryText.copy(alpha = 0.08f))
            )
        }
    }
}

@Composable
fun AppleSection(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val palette = LocalJmxGlassPalette.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(palette.contentSurface.copy(alpha = 0.74f))
    ) {
        content()
    }
}

@Composable
fun AppleChevron(modifier: Modifier = Modifier) {
    val palette = LocalJmxGlassPalette.current
    Canvas(modifier = modifier.size(13.dp)) {
        val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
        drawLine(
            color = palette.tertiaryText,
            start = Offset(size.width * 0.36f, size.height * 0.22f),
            end = Offset(size.width * 0.68f, size.height * 0.50f),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round
        )
        drawLine(
            color = palette.tertiaryText,
            start = Offset(size.width * 0.68f, size.height * 0.50f),
            end = Offset(size.width * 0.36f, size.height * 0.78f),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round
        )
    }
}
