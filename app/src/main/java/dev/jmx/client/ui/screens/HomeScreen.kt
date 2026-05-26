package dev.jmx.client.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import dev.jmx.client.data.models.HomeAlbumSwiperItem
import dev.jmx.client.ui.components.Album
import dev.jmx.client.ui.components.AlbumSkeleton
import dev.jmx.client.ui.glass.LocalJmxBackdrop
import dev.jmx.client.ui.glass.LocalJmxGlassPalette
import dev.jmx.client.ui.glass.jmxGlass
import dev.jmx.client.ui.razor.RazorGlassButton
import dev.jmx.client.ui.razor.RazorLoadingIndicator
import dev.jmx.client.ui.razor.RazorPullRefreshBox
import dev.jmx.client.ui.razor.RazorText
import dev.jmx.client.ui.state.rememberTabIndexState
import dev.jmx.client.ui.viewModel.AlbumViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
private fun HomeSkeleton() {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.Top),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(start = 16.dp, top = 204.dp, end = 16.dp, bottom = 18.dp)
    ) {
        items(
            count = 18,
            key = { it }
        ) {
            AlbumSkeleton()
        }
    }
}

@Composable
private fun HomeEmptyState(
    isLoading: Boolean,
    errorMessage: String?,
    onRefresh: () -> Unit
) {
    val palette = LocalJmxGlassPalette.current
    RazorPullRefreshBox(
        modifier = Modifier.fillMaxSize(),
        isRefreshing = isLoading,
        onRefresh = onRefresh
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (isLoading) {
                    RazorLoadingIndicator(modifier = Modifier.size(28.dp))
                } else {
                    RazorText(
                        text = errorMessage?.takeIf { it.isNotBlank() } ?: "暂无主页数据",
                        style = TextStyle(
                            color = if (errorMessage.isNullOrBlank()) palette.secondaryText else Color(0xFFFF3B30),
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    RazorGlassButton(onClick = onRefresh) {
                        RazorText(
                            text = "重试",
                            style = TextStyle(
                                color = palette.primaryText,
                                fontSize = 14.sp,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeCategoryStrip(
    categories: List<HomeAlbumSwiperItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val density = LocalDensity.current
        val selectedWidth = 248.dp
        val edgePadding = if (maxWidth > selectedWidth) {
            (maxWidth - selectedWidth) / 2
        } else {
            18.dp
        }
        LaunchedEffect(selectedIndex, categories.size, edgePadding) {
            if (selectedIndex in categories.indices) {
                listState.animateScrollToItem(
                    index = selectedIndex,
                    scrollOffset = -with(density) { edgePadding.roundToPx() }
                )
            }
        }

        LazyRow(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentPadding = PaddingValues(horizontal = edgePadding),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(
                count = categories.size,
                key = { categories[it].id }
            ) { index ->
                val item = categories[index]
                val isSelected = selectedIndex == index
                Box(
                    modifier = Modifier
                        .then(
                            if (isSelected) {
                                Modifier.width(selectedWidth)
                            } else {
                                Modifier.width(164.dp)
                            }
                        )
                ) {
                    HomeCategoryChip(
                        title = item.title,
                        count = item.list.size,
                        selected = isSelected,
                        onClick = { onSelect(index) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeCategoryChip(
    title: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val palette = LocalJmxGlassPalette.current
    val background = if (selected) palette.primaryText else palette.contentSurface.copy(alpha = 0.30f)
    val primary = if (selected) {
        palette.page
    } else {
        palette.primaryText
    }
    val secondary = if (selected) {
        palette.page.copy(alpha = 0.72f)
    } else {
        palette.secondaryText
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = onClick
            )
            .padding(start = 16.dp, end = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(if (selected) palette.accent else palette.tertiaryText.copy(alpha = 0.50f))
        )
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            RazorText(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    color = primary,
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            RazorText(
                text = "$count 本",
                style = TextStyle(
                    color = secondary,
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

data class HomeCategoryOverlayState(
    val categories: List<HomeAlbumSwiperItem>,
    val selectedIndex: Int,
    val onSelect: (Int) -> Unit
)

@Composable
private fun HomeCategoryFloatingStrip(
    categories: List<HomeAlbumSwiperItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (selectedIndex !in categories.indices) return

    data class CategorySlot(
        val index: Int,
        val selected: Boolean,
        val targetX: Dp,
        val targetWidth: Dp,
        val targetAlpha: Float,
        val targetScale: Float,
        val zIndex: Float
    )

    val slots = listOfNotNull(
        (selectedIndex - 1).takeIf { it in categories.indices }?.let {
            CategorySlot(
                index = it,
                selected = false,
                targetX = (-216).dp,
                targetWidth = 154.dp,
                targetAlpha = 0.82f,
                targetScale = 0.94f,
                zIndex = 0f
            )
        },
        CategorySlot(
            index = selectedIndex,
            selected = true,
            targetX = 0.dp,
            targetWidth = 250.dp,
            targetAlpha = 1f,
            targetScale = 1f,
            zIndex = 1f
        ),
        (selectedIndex + 1).takeIf { it in categories.indices }?.let {
            CategorySlot(
                index = it,
                selected = false,
                targetX = 216.dp,
                targetWidth = 154.dp,
                targetAlpha = 0.82f,
                targetScale = 0.94f,
                zIndex = 0f
            )
        }
    )

    Box(
        modifier = modifier.height(68.dp),
        contentAlignment = Alignment.Center
    ) {
        slots.forEach { slot ->
            key(categories[slot.index].id) {
                val x by animateDpAsState(
                    targetValue = slot.targetX,
                    animationSpec = spring(
                        dampingRatio = 0.78f,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "categoryChipX"
                )
                val width by animateDpAsState(
                    targetValue = slot.targetWidth,
                    animationSpec = spring(
                        dampingRatio = 0.86f,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "categoryChipWidth"
                )
                val animatedAlpha by animateFloatAsState(
                    targetValue = slot.targetAlpha,
                    animationSpec = spring(
                        dampingRatio = 0.88f,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "categoryChipAlpha"
                )
                val scale by animateFloatAsState(
                    targetValue = slot.targetScale,
                    animationSpec = spring(
                        dampingRatio = 0.78f,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "categoryChipScale"
                )

                HomeCategoryFloatingChip(
                    title = categories[slot.index].title,
                    count = categories[slot.index].list.size,
                    selected = slot.selected,
                    onClick = { onSelect(slot.index) },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = x)
                        .width(width)
                        .zIndex(slot.zIndex)
                        .graphicsLayer {
                            this.alpha = animatedAlpha
                            scaleX = scale
                            scaleY = scale
                        }
                )
            }
        }
    }
}

@Composable
private fun HomeCategoryFloatingChip(
    title: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backdrop = LocalJmxBackdrop.current
    val palette = LocalJmxGlassPalette.current
    val shape = RoundedCornerShape(999.dp)
    val primary = palette.primaryText
    val secondary = if (selected) {
        palette.secondaryText.copy(alpha = 0.82f)
    } else {
        palette.secondaryText.copy(alpha = 0.72f)
    }
    val height = if (selected) 56.dp else 48.dp
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.972f else 1f,
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "categoryChipPress"
    )

    val chipModifier = modifier
        .height(height)
        .graphicsLayer {
            scaleX = pressScale
            scaleY = pressScale
        }
        .then(
            if (backdrop != null) {
                Modifier.jmxGlass(
                    backdrop = backdrop,
                    radius = 999.dp,
                    blurRadius = if (selected) 10.dp else 7.dp,
                    lensHeight = if (selected) 24.dp else 14.dp,
                    lensAmount = if (selected) 34.dp else 22.dp,
                    surfaceColor = if (selected) {
                        palette.glassStrongSurface.copy(alpha = 0.42f)
                    } else {
                        palette.glassSurface.copy(alpha = 0.20f)
                    },
                    depth = true,
                    shadowAlpha = if (selected) 0.08f else 0.03f,
                    highlightAlpha = if (selected) 0.54f else 0.38f,
                    innerShadowAlpha = if (selected) 0.40f else 0.24f,
                    surfaceSheenAlpha = if (selected) 0.08f else 0.04f
                )
            } else {
                Modifier.background(
                    if (selected) palette.glassStrongSurface else palette.contentSurface.copy(alpha = 0.30f),
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

    Row(
        modifier = chipModifier
            .fillMaxWidth()
            .padding(start = if (selected) 18.dp else 13.dp, end = if (selected) 16.dp else 12.dp),
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(if (selected) palette.accent else palette.tertiaryText.copy(alpha = 0.48f))
        )
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            RazorText(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    color = primary,
                    fontSize = if (selected) 15.sp else 13.sp,
                    lineHeight = if (selected) 18.sp else 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            RazorText(
                text = "$count 本",
                style = TextStyle(
                    color = secondary,
                    fontSize = if (selected) 11.sp else 10.sp,
                    lineHeight = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun HomeScreen(
    albumViewModel: AlbumViewModel = koinActivityViewModel(),
    onOverlayStateChange: (HomeCategoryOverlayState?) -> Unit = {}
) {
    val palette = LocalJmxGlassPalette.current
    val homeAlbumState by albumViewModel.homeAlbumState.collectAsState()
    LaunchedEffect(Unit) {
        if (homeAlbumState.list.isNotEmpty()) {
            return@LaunchedEffect
        }
        albumViewModel.getHomeAlbum()
    }
    if (homeAlbumState.list.isEmpty() && homeAlbumState.isLoading) {
        LaunchedEffect(Unit) {
            onOverlayStateChange(null)
        }
        HomeSkeleton()
        return
    }
    if (homeAlbumState.list.isEmpty()) {
        LaunchedEffect(Unit) {
            onOverlayStateChange(null)
        }
        HomeEmptyState(
            isLoading = homeAlbumState.isLoading,
            errorMessage = homeAlbumState.errorMsg,
            onRefresh = { albumViewModel.getHomeAlbum() }
        )
        return
    }

    val coroutineScope = rememberCoroutineScope()
    val selectedTabIndexState = rememberTabIndexState()
    val pagerState = rememberPagerState(initialPage = 0) {
        homeAlbumState.list.size
    }
    var pagerAnimationJob by remember { androidx.compose.runtime.mutableStateOf<Job?>(null) }
    val onTabClick: (index: Int) -> Unit = { index ->
        if (index in homeAlbumState.list.indices && index != selectedTabIndexState.value) {
            selectedTabIndexState.value = index
            pagerAnimationJob?.cancel()
            pagerAnimationJob = coroutineScope.launch {
                pagerState.animateScrollToPage(index)
            }
        }
    }
    val selectedTabIndex = selectedTabIndexState.value.coerceIn(0, homeAlbumState.list.lastIndex)
    LaunchedEffect(homeAlbumState.list.size) {
        if (selectedTabIndexState.value != selectedTabIndex) {
            selectedTabIndexState.value = selectedTabIndex
        }
    }
    LaunchedEffect(pagerState.currentPage, homeAlbumState.list.size) {
        if (pagerState.currentPage in homeAlbumState.list.indices) {
            selectedTabIndexState.value = pagerState.currentPage
        }
    }
    val currentOnTabClick by rememberUpdatedState(onTabClick)
    LaunchedEffect(homeAlbumState.list, selectedTabIndex) {
        onOverlayStateChange(
            HomeCategoryOverlayState(
                categories = homeAlbumState.list,
                selectedIndex = selectedTabIndex,
                onSelect = { currentOnTabClick(it) }
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.page)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState
            ) { page ->
                val gridState = rememberLazyGridState()
                val albumList = homeAlbumState.list.getOrNull(page)?.list ?: listOf()
                RazorPullRefreshBox(
                    modifier = Modifier.fillMaxSize(),
                    isRefreshing = homeAlbumState.isLoading,
                    onRefresh = {
                        albumViewModel.getHomeAlbum()
                    }
                ) {
                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Fixed(3),
                        verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.Top),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(start = 16.dp, top = 204.dp, end = 16.dp, bottom = 18.dp)
                    ) {
                        items(
                            items = albumList,
                            key = { it.id },
                        ) {
                            Album(it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeFloatingCategoryPanel(
    overlayState: HomeCategoryOverlayState,
    modifier: Modifier = Modifier
) {
    val backdrop = LocalJmxBackdrop.current
    if (backdrop == null) {
        return
    }
    HomeFloatingCategoryPanelContent(
        categories = overlayState.categories,
        selectedIndex = overlayState.selectedIndex,
        onSelect = overlayState.onSelect,
        modifier = modifier
    )
}

@Composable
private fun HomeFloatingCategoryPanelContent(
    categories: List<HomeAlbumSwiperItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LocalJmxBackdrop.current ?: return
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
    ) {
        HomeCategoryFloatingStrip(
            categories = categories,
            selectedIndex = selectedIndex,
            onSelect = onSelect,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}
