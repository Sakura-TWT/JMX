package dev.jmx.client.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import dev.jmx.client.ui.glass.LocalJmxGlassPalette
import dev.jmx.client.ui.razor.RazorGlassButton
import dev.jmx.client.ui.razor.RazorLoadingIndicator
import dev.jmx.client.ui.razor.RazorPullRefreshBox
import dev.jmx.client.ui.razor.RazorText

@Composable
fun <T : Any> PullRefreshAndLoadMoreGrid(
    modifier: Modifier = Modifier,
    lazyPagingItems: LazyPagingItems<T>,
    key: ((item: T) -> Any),
    columns: GridCells,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(10.dp, Alignment.Top),
    horizontalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(10.dp),
    contentPadding: PaddingValues = PaddingValues(10.dp),
    itemContent: @Composable ((item: T) -> Unit),
) {
    val palette = LocalJmxGlassPalette.current
    val isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading

    RazorPullRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { lazyPagingItems.refresh() },
        modifier = modifier
    ) {
        LazyVerticalGrid(
            columns = columns,
            verticalArrangement = verticalArrangement,
            horizontalArrangement = horizontalArrangement,
            contentPadding = contentPadding
        ) {
            items(
                lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { key(it) },
            ) { index ->
                val item = lazyPagingItems[index]
                if (item != null) {
                    itemContent(item)
                }
            }

            when (val appendState = lazyPagingItems.loadState.append) {
                is LoadState.Loading -> {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            RazorLoadingIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                }

                is LoadState.Error -> {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RazorText(
                                text = "加载失败",
                                style = TextStyle(
                                    color = palette.accent,
                                    fontSize = 13.sp,
                                    lineHeight = 17.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            RazorGlassButton(onClick = { lazyPagingItems.retry() }) {
                                RazorText(
                                    text = "重试",
                                    style = TextStyle(
                                        color = palette.primaryText,
                                        fontSize = 14.sp,
                                        lineHeight = 17.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                }

                is LoadState.NotLoading -> {
                    if (appendState.endOfPaginationReached) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                RazorText(
                                    text = "- 没有更多数据了 -",
                                    style = TextStyle(
                                        color = palette.tertiaryText,
                                        fontSize = 12.sp,
                                        lineHeight = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
