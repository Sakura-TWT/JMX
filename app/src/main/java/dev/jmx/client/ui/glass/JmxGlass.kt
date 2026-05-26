package dev.jmx.client.ui.glass

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.graphics.Brush
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import dev.jmx.client.ui.razor.RazorText
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberCombinedBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.drawPlainBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedRectangle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sign
import kotlin.math.tanh

val LocalJmxBackdrop = staticCompositionLocalOf<LayerBackdrop?> { null }

data class JmxGlassPalette(
    val page: Color,
    val pageAlt: Color,
    val glassSurface: Color,
    val glassStrongSurface: Color,
    val contentSurface: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val tertiaryText: Color,
    val accent: Color,
)

val LocalJmxGlassPalette = compositionLocalOf {
    JmxGlassPalette(
        page = Color(0xFFF6F6F3),
        pageAlt = Color(0xFFEDEDE8),
        glassSurface = Color(0xFFFAFAFA).copy(alpha = 0.38f),
        glassStrongSurface = Color.White.copy(alpha = 0.58f),
        contentSurface = Color.White.copy(alpha = 0.70f),
        primaryText = Color(0xFF111111),
        secondaryText = Color(0xFF616161),
        tertiaryText = Color(0xFF929292),
        accent = Color(0xFF007AFF),
    )
}

@Composable
fun rememberJmxGlassPalette(): JmxGlassPalette {
    val isDark = isSystemInDarkTheme()
    return if (isDark) {
        JmxGlassPalette(
            page = Color(0xFF0B0B0D),
            pageAlt = Color(0xFF151518),
            glassSurface = Color(0xFF141417).copy(alpha = 0.44f),
            glassStrongSurface = Color(0xFF1D1D20).copy(alpha = 0.62f),
            contentSurface = Color(0xFF1B1B1E).copy(alpha = 0.72f),
            primaryText = Color(0xFFF8F8F8),
            secondaryText = Color(0xFFB8B8BE),
            tertiaryText = Color(0xFF85858C),
            accent = Color(0xFF0A84FF),
        )
    } else {
        JmxGlassPalette(
            page = Color(0xFFF7F7F4),
            pageAlt = Color(0xFFECEDE8),
            glassSurface = Color(0xFFFAFAFA).copy(alpha = 0.38f),
            glassStrongSurface = Color.White.copy(alpha = 0.60f),
            contentSurface = Color.White.copy(alpha = 0.72f),
            primaryText = Color(0xFF111111),
            secondaryText = Color(0xFF606166),
            tertiaryText = Color(0xFF909196),
            accent = Color(0xFF007AFF),
        )
    }
}

@Composable
fun JmxGlassStage(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(LayerBackdrop) -> Unit
) {
    val backdrop = rememberLayerBackdrop()
    Box(modifier = modifier.fillMaxSize()) {
        content(backdrop)
    }
}

fun Modifier.jmxContentBackdrop(backdrop: LayerBackdrop): Modifier = this.layerBackdrop(backdrop)

fun Modifier.jmxGlass(
    backdrop: Backdrop,
    radius: androidx.compose.ui.unit.Dp = 28.dp,
    blurRadius: androidx.compose.ui.unit.Dp = 10.dp,
    lensHeight: androidx.compose.ui.unit.Dp = 22.dp,
    lensAmount: androidx.compose.ui.unit.Dp = 34.dp,
    surfaceColor: Color,
    depth: Boolean = true,
    shadowAlpha: Float = 0.08f,
    highlightAlpha: Float = 0.56f,
    innerShadowAlpha: Float = 0.54f,
    surfaceSheenAlpha: Float = 0.10f,
): Modifier = this.drawBackdrop(
    backdrop = backdrop,
    shape = { RoundedRectangle(radius) },
    effects = {
        vibrancy()
        blur(blurRadius.toPx())
        lens(
            lensHeight.toPx(),
            lensAmount.toPx(),
            depthEffect = depth,
            chromaticAberration = true
        )
    },
    highlight = { if (highlightAlpha > 0f) Highlight.Default.copy(alpha = highlightAlpha) else null },
    shadow = {
        Shadow(
            radius = 24.dp,
            color = Color.Black.copy(alpha = shadowAlpha)
        )
    },
    innerShadow = {
        if (innerShadowAlpha > 0f) {
            InnerShadow(
                radius = 8.dp,
                color = Color.White.copy(alpha = 0.12f),
                alpha = innerShadowAlpha
            )
        } else {
            null
        }
    },
    onDrawSurface = {
        if (surfaceColor.alpha > 0f) {
            drawRect(surfaceColor)
        }
        if (surfaceSheenAlpha > 0f) {
            drawRect(Color.White.copy(alpha = surfaceSheenAlpha), blendMode = BlendMode.Plus)
        }
    }
)

fun Modifier.jmxProgressiveTopBlur(
    backdrop: Backdrop,
    blurRadius: androidx.compose.ui.unit.Dp = 18.dp,
    solidFraction: Float = 0.34f,
    tintColor: Color = Color.White,
    tintTopAlpha: Float = 0.12f,
): Modifier = this.drawPlainBackdrop(
    backdrop = backdrop,
    shape = { RectangleShape },
    effects = {
        vibrancy()
        blur(blurRadius.toPx())
    },
    onDrawSurface = {
        if (tintTopAlpha > 0f) {
            val solid = solidFraction.fastCoerceIn(0f, 0.92f)
            drawRect(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to tintColor.copy(alpha = tintTopAlpha),
                        solid to tintColor.copy(alpha = tintTopAlpha * 0.58f),
                        1f to Color.Transparent
                    )
                )
            )
        }
    },
    onDrawFront = {
        val solid = solidFraction.fastCoerceIn(0f, 0.92f)
        val mid = ((solid + 1f) * 0.5f).fastCoerceIn(solid, 0.98f)
        drawRect(
            brush = Brush.verticalGradient(
                colorStops = arrayOf(
                    0f to Color.Black,
                    solid to Color.Black,
                    mid to Color.Black.copy(alpha = 0.46f),
                    1f to Color.Transparent
                )
            ),
            blendMode = BlendMode.DstIn
        )
    }
)

@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    cornerRadius: androidx.compose.ui.unit.Dp = 28.dp,
    useContentSurface: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val backdrop = LocalJmxBackdrop.current
    val palette = LocalJmxGlassPalette.current
    val surface = if (useContentSurface) palette.contentSurface else palette.glassStrongSurface
    val baseModifier = modifier.then(
        if (backdrop != null) {
            Modifier.jmxGlass(
                backdrop = backdrop,
                radius = cornerRadius,
                blurRadius = if (useContentSurface) 6.dp else 10.dp,
                lensHeight = if (useContentSurface) 10.dp else 18.dp,
                lensAmount = if (useContentSurface) 12.dp else 28.dp,
                surfaceColor = surface,
                depth = !useContentSurface,
                shadowAlpha = if (useContentSurface) 0.03f else 0.08f
            )
        } else {
            Modifier
                .clip(RoundedRectangle(cornerRadius))
                .background(surface)
        }
    )

    Box(baseModifier, content = content)
}

@Composable
fun GlassActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val backdrop = LocalJmxBackdrop.current
    val palette = LocalJmxGlassPalette.current
    val animationScope = rememberCoroutineScope()
    val highlight = remember(animationScope) { InteractiveHighlight(animationScope) }
    val buttonModifier = modifier
        .height(48.dp)
        .defaultMinSize(minWidth = 48.dp)
        .clip(Capsule())
        .then(
            if (backdrop != null) {
                Modifier.drawBackdrop(
                    backdrop = backdrop,
                    shape = { Capsule() },
                    effects = {
                        vibrancy()
                        blur(3.dp.toPx())
                        lens(14.dp.toPx(), 26.dp.toPx(), depthEffect = true, chromaticAberration = true)
                    },
                    highlight = { Highlight.Default.copy(alpha = 0.52f) },
                    shadow = {
                        Shadow(radius = 18.dp, color = Color.Black.copy(alpha = 0.10f))
                    },
                    innerShadow = {
                        InnerShadow(radius = 8.dp, alpha = 0.42f)
                    },
                    layerBlock = {
                        val progress = highlight.pressProgress
                        val scale = lerp(1f, 1f + 4.dp.toPx() / size.height, progress)
                        val maxOffset = size.minDimension
                        val offset = highlight.offset
                        translationX = maxOffset * tanh(0.05f * offset.x / maxOffset)
                        translationY = maxOffset * tanh(0.05f * offset.y / maxOffset)
                        val angle = atan2(offset.y, offset.x)
                        scaleX = scale + 0.04f * abs(cos(angle) * offset.x / size.maxDimension)
                        scaleY = scale + 0.04f * abs(sin(angle) * offset.y / size.maxDimension)
                    },
                    onDrawSurface = {
                        drawRect(palette.glassSurface)
                    }
                )
            } else {
                Modifier.background(palette.glassSurface)
            }
        )
        .then(highlight.modifier)
        .then(highlight.gestureModifier)
        .clickable(
            interactionSource = null,
            indication = null,
            role = Role.Button,
            onClick = onClick
        )

    Row(
        modifier = buttonModifier.padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
    }
}

@Composable
fun GlassTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val palette = LocalJmxGlassPalette.current
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + scaleIn(initialScale = 0.98f),
        exit = fadeOut() + scaleOut(targetScale = 0.98f),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(start = 22.dp, end = 18.dp, top = 14.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                RazorText(
                    text = title,
                    style = TextStyle(
                        color = palette.primaryText,
                        fontSize = 34.sp,
                        lineHeight = 36.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.8).sp
                    )
                )
                if (subtitle != null) {
                    RazorText(
                        text = subtitle,
                        style = TextStyle(
                            color = palette.secondaryText,
                            fontSize = 15.sp,
                            lineHeight = 19.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = actions
            )
        }
    }
}

@Composable
fun LiquidBottomBar(
    selectedIndex: () -> Int,
    onTabSelected: (index: Int) -> Unit,
    tabsCount: Int,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val backdrop = LocalJmxBackdrop.current
    if (backdrop == null) {
        Row(modifier = modifier.height(64.dp), content = content)
        return
    }
    val isLightTheme = !isSystemInDarkTheme()
    val accentColor = if (isLightTheme) Color(0xFF007AFF) else Color(0xFF0A84FF)
    val containerColor = if (isLightTheme) {
        Color(0xFFFAFAFA).copy(alpha = 0.30f)
    } else {
        Color(0xFF121216).copy(alpha = 0.34f)
    }
    val tabsBackdrop = rememberLayerBackdrop()

    androidx.compose.foundation.layout.BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        val density = LocalDensity.current
        val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
        val animationScope = rememberCoroutineScope()
        var currentIndex by remember { mutableIntStateOf(selectedIndex().coerceIn(0, tabsCount - 1)) }
        val tabWidth = with(density) {
            (constraints.maxWidth.toFloat() - 8.dp.toPx()) / tabsCount
        }

        val offsetAnimation = remember { Animatable(0f) }
        val panelOffset by remember(density) {
            derivedStateOf {
                val fraction = (offsetAnimation.value / constraints.maxWidth).fastCoerceIn(-1f, 1f)
                with(density) {
                    4.dp.toPx() * fraction.sign * EaseOut.transform(abs(fraction))
                }
            }
        }

        val dampedDragAnimation = remember(animationScope) {
            DampedDragAnimation(
                animationScope = animationScope,
                initialValue = selectedIndex().coerceIn(0, tabsCount - 1).toFloat(),
                valueRange = 0f..(tabsCount - 1).toFloat(),
                visibilityThreshold = 0.001f,
                initialScale = 1f,
                pressedScale = 78f / 56f,
                requireLongPress = true,
                onDragStarted = {
                    updateValue(selectedIndex().coerceIn(0, tabsCount - 1).toFloat())
                },
                onDragStopped = {
                    val targetIndex = targetValue.fastRoundToInt().fastCoerceIn(0, tabsCount - 1)
                    currentIndex = targetIndex
                    animateToValue(targetIndex.toFloat())
                    animationScope.launch {
                        offsetAnimation.animateTo(
                            0f,
                            spring(dampingRatio = 1f, stiffness = 300f, visibilityThreshold = 0.5f)
                        )
                    }
                },
                onDrag = { _, dragAmount ->
                    updateValue(
                        (targetValue + dragAmount.x / tabWidth * if (isLtr) 1f else -1f)
                            .fastCoerceIn(0f, (tabsCount - 1).toFloat())
                    )
                    animationScope.launch {
                        offsetAnimation.snapTo(offsetAnimation.value + dragAmount.x)
                    }
                }
            )
        }

        val selectedIndexProvider by rememberUpdatedState(selectedIndex)
        LaunchedEffect(dampedDragAnimation, tabsCount) {
            snapshotFlow { selectedIndexProvider().coerceIn(0, tabsCount - 1) }
                .distinctUntilChanged()
                .collectLatest { index ->
                    if (index != currentIndex) {
                        currentIndex = index
                    }
                }
        }
        LaunchedEffect(dampedDragAnimation, tabsCount) {
            snapshotFlow { currentIndex.coerceIn(0, tabsCount - 1) }
                .distinctUntilChanged()
                .drop(1)
                .collectLatest { index ->
                    dampedDragAnimation.animateToValue(index.toFloat())
                    onTabSelected(index)
                }
        }
        val requestTabSelection: (Int) -> Unit = { index ->
            val targetIndex = index.coerceIn(0, tabsCount - 1)
            if (targetIndex != currentIndex) {
                currentIndex = targetIndex
            }
        }

        Box(
            Modifier
                .height(56.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            CompositionLocalProvider(
                LocalLiquidBottomTabScale provides {
                    lerp(1f, 1.2f, dampedDragAnimation.pressProgress)
                },
                LocalLiquidBottomTabClick provides requestTabSelection
            ) {
                Row(
                    Modifier
                        .graphicsLayer {
                            translationX = panelOffset
                        }
                        .drawBackdrop(
                            backdrop = backdrop,
                            shape = { Capsule() },
                            effects = {
                                vibrancy()
                                blur(4.dp.toPx())
                                lens(
                                    30.dp.toPx(),
                                    36.dp.toPx(),
                                    depthEffect = true
                                )
                            },
                            layerBlock = {
                                val progress = dampedDragAnimation.pressProgress
                                val scale = lerp(1f, 1f + 16.dp.toPx() / size.width, progress)
                                scaleX = scale
                                scaleY = scale
                            },
                            highlight = {
                                Highlight.Default.copy(alpha = 0.32f)
                            },
                            shadow = { Shadow(radius = 18.dp, color = Color.Black.copy(alpha = 0.07f)) },
                            innerShadow = { InnerShadow(radius = 8.dp, alpha = 0.24f) },
                            onDrawSurface = {
                                drawRect(containerColor)
                            }
                        )
                        .height(56.dp)
                        .fillMaxWidth()
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    content = content
                )
            }

            CompositionLocalProvider(
                LocalLiquidBottomTabScale provides {
                    lerp(1f, 1.2f, dampedDragAnimation.pressProgress)
                },
                LocalLiquidBottomTabClick provides requestTabSelection
            ) {
                Row(
                    Modifier
                        .clearAndSetSemantics {}
                        .alpha(0f)
                        .layerBackdrop(tabsBackdrop)
                        .graphicsLayer {
                            translationX = panelOffset
                        }
                        .drawBackdrop(
                            backdrop = backdrop,
                            shape = { Capsule() },
                        effects = {
                            val progress = dampedDragAnimation.pressProgress
                            vibrancy()
                            blur(4.dp.toPx())
                            lens(
                                30.dp.toPx() * progress,
                                36.dp.toPx() * progress,
                                depthEffect = true
                            )
                        },
                            highlight = {
                                val progress = dampedDragAnimation.pressProgress
                                Highlight.Default.copy(alpha = 0.56f * progress)
                            },
                            onDrawSurface = {
                                drawRect(containerColor)
                            }
                        )
                        .height(48.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                        .graphicsLayer(colorFilter = ColorFilter.tint(accentColor)),
                    verticalAlignment = Alignment.CenterVertically,
                    content = content
                )
            }

            Box(
                Modifier
                    .padding(horizontal = 4.dp)
                    .graphicsLayer {
                        translationX =
                            if (isLtr) {
                                dampedDragAnimation.value * tabWidth + panelOffset
                            } else {
                                size.width - (dampedDragAnimation.value + 1f) * tabWidth + panelOffset
                            }
                    }
                    .then(dampedDragAnimation.modifier)
                    .drawBackdrop(
                        backdrop = rememberCombinedBackdrop(backdrop, tabsBackdrop),
                        shape = { Capsule() },
                        effects = {
                            val progress = dampedDragAnimation.pressProgress
                            val idleProgress = lerp(0.18f, 1f, progress)
                            lens(
                                10.dp.toPx() * idleProgress,
                                14.dp.toPx() * idleProgress,
                                depthEffect = true,
                                chromaticAberration = progress > 0.05f
                            )
                        },
                        highlight = {
                            val progress = dampedDragAnimation.pressProgress
                            Highlight.Default.copy(alpha = progress)
                        },
                        shadow = {
                            Shadow(alpha = dampedDragAnimation.pressProgress)
                        },
                        innerShadow = {
                            val progress = dampedDragAnimation.pressProgress
                            InnerShadow(
                                radius = 8.dp * progress,
                                alpha = progress
                            )
                        },
                        layerBlock = {
                            scaleX = dampedDragAnimation.scaleX
                            scaleY = dampedDragAnimation.scaleY
                            val velocity = dampedDragAnimation.velocity / 10f
                            scaleX /= 1f - (velocity * 0.75f).fastCoerceIn(-0.2f, 0.2f)
                            scaleY *= 1f - (velocity * 0.25f).fastCoerceIn(-0.2f, 0.2f)
                        },
                        onDrawSurface = {
                            val progress = dampedDragAnimation.pressProgress
                            drawRect(
                                if (isLightTheme) Color.Black.copy(alpha = 0.10f)
                                else Color.White.copy(alpha = 0.10f),
                                alpha = 1f - progress
                            )
                            drawRect(Color.Black.copy(alpha = 0.03f * progress))
                        }
                    )
                    .height(56.dp)
                    .fillMaxWidth(1f / tabsCount)
            )
        }
    }
}

val LocalLiquidBottomTabScale = staticCompositionLocalOf { { 1f } }
val LocalLiquidBottomTabClick = staticCompositionLocalOf { { _: Int -> } }
val LocalLiquidBottomTabIndex = staticCompositionLocalOf { -1 }

@Composable
fun RowScope.LiquidBottomBarItem(
    index: Int,
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    label: String
) {
    val palette = LocalJmxGlassPalette.current
    val scale = LocalLiquidBottomTabScale.current
    val clickTab = LocalLiquidBottomTabClick.current
    val itemColor = if (selected) palette.primaryText else palette.secondaryText

    CompositionLocalProvider(LocalLiquidBottomTabIndex provides index) {
        Column(
            Modifier
                .clip(Capsule())
                .clickable(
                    interactionSource = null,
                    indication = null,
                    role = Role.Tab,
                    onClick = {
                        clickTab(index)
                        onClick()
                    }
                )
                .fillMaxHeight()
                .weight(1f)
                .graphicsLayer {
                    val scaleValue = scale()
                    scaleX = scaleValue
                    scaleY = scaleValue
                },
            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .alpha(if (selected) 1f else 0.72f),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            RazorText(
                text = label,
                style = TextStyle(
                    color = itemColor,
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
                )
            )
        }
    }
}
