package dev.jmx.client.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import dev.jmx.client.data.models.Album
import dev.jmx.client.ui.glass.LocalJmxGlassPalette
import dev.jmx.client.ui.razor.RazorLoadingIndicator
import dev.jmx.client.ui.razor.RazorPullRefreshBox

@Composable
fun AlbumLazyGrid(
    list: List<Album>,
    isRefreshing: Boolean,
    isMoreLoading: Boolean,
    hasMore: Boolean,
    gridState: LazyGridState = rememberLazyGridState(),
    onRefresh: () -> Unit = {},
    onLoadMore: () -> Unit = {},
    modifier: Modifier = Modifier,
    columns: GridCells = GridCells.Fixed(3),
    verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(10.dp),
    horizontalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(10.dp),
    contentPadding: PaddingValues = PaddingValues(8.dp),
    stickyHeaderContent: @Composable (() -> Unit)? = null
) {
    val palette = LocalJmxGlassPalette.current
    val shouldLoadMore =
        remember(
            gridState.layoutInfo.visibleItemsInfo,
            gridState.layoutInfo.totalItemsCount,
            isRefreshing,
            hasMore
        ) {
            derivedStateOf {
                val layoutInfo = gridState.layoutInfo
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                lastVisibleItem?.index == layoutInfo.totalItemsCount - 1 &&
                        !isRefreshing &&
                        hasMore
            }
        }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore.value) {
            onLoadMore()
        }
    }
    RazorPullRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
    ) {
        LazyVerticalGrid(
            state = gridState,
            columns = columns,
            verticalArrangement = verticalArrangement,
            horizontalArrangement = horizontalArrangement,
            contentPadding = contentPadding,
        ) {
            if (stickyHeaderContent !== null) {
                stickyHeader {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .background(palette.page.copy(alpha = 0.88f))
                    ) {
                        stickyHeaderContent()
                    }
                }
            }
            items(
                items = list,
                key = { it.id },
            ) { item ->
                Album(item)
            }
            if (list.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    LoadMore(
                        isLoading = isMoreLoading,
                        hasMore = hasMore
                    )
                }
            }
        }
    }
    if (isRefreshing && list.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                // 禁止点击，让点击穿透
                .pointerInput(Unit) { },
            contentAlignment = Alignment.Center
        ) {
            RazorLoadingIndicator()
        }
    }
}
