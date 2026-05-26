package dev.jmx.client.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import dev.jmx.client.data.models.CollectAlbumOrderFilter
import dev.jmx.client.ui.components.Album
import dev.jmx.client.ui.components.AlbumSkeleton
import dev.jmx.client.ui.components.CommonScaffold
import dev.jmx.client.ui.components.FilterItem
import dev.jmx.client.ui.components.PullRefreshAndLoadMoreGrid
import dev.jmx.client.ui.glass.LocalJmxGlassPalette
import dev.jmx.client.ui.viewModel.UserViewModel
import org.koin.compose.viewmodel.koinActivityViewModel


@Composable
private fun UserCollectAlbumSkeleton(
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
            .verticalScroll(rememberScrollState()),
        maxItemsInEachRow = 3,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top)
    ) {
        for (i in 0 until 18) {
            key(i) {
                AlbumSkeleton(
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun UserCollectAlbumScreen(
    userViewModel: UserViewModel = koinActivityViewModel()
) {
    val collectAlbumLazyPagingItems = userViewModel.collectAlbumPager.collectAsLazyPagingItems()
    val order by userViewModel.collectAlbumOrder.collectAsState()
    val palette = LocalJmxGlassPalette.current
    CommonScaffold(
        title = "我的收藏",
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                CollectAlbumOrderFilter.entries.forEach { item ->
                    key(item.label) {
                        FilterItem(
                            label = item.label,
                            onClick = {
                                userViewModel.changeCollectAlbumOrder(item)
                            },
                            active = item.value == order.value
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(palette.tertiaryText.copy(alpha = 0.14f))
            )
            if (collectAlbumLazyPagingItems.loadState.refresh is LoadState.Loading && collectAlbumLazyPagingItems.itemCount == 0) {
                UserCollectAlbumSkeleton(
                    modifier = Modifier.weight(1f)
                )
                return@CommonScaffold
            }
            PullRefreshAndLoadMoreGrid(
                modifier = Modifier.weight(1f),
                lazyPagingItems = collectAlbumLazyPagingItems,
                key = { it.id },
                columns = GridCells.Fixed(3),
            ) {
                Album(it)
            }
        }
    }
}
