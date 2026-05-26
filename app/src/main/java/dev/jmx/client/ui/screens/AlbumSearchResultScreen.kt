package dev.jmx.client.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import dev.jmx.client.data.models.AlbumSearchOrderFilter
import dev.jmx.client.ui.components.Album
import dev.jmx.client.ui.components.AlbumSkeleton
import dev.jmx.client.ui.components.CommonScaffold
import dev.jmx.client.ui.components.FilterItem
import dev.jmx.client.ui.components.PullRefreshAndLoadMoreGrid
import dev.jmx.client.ui.glass.LocalJmxGlassPalette
import dev.jmx.client.ui.viewModel.AlbumDetailViewModel
import dev.jmx.client.ui.viewModel.AlbumViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
private fun AlbumSearchResultSkeleton(
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
fun AlbumSearchResultScreen(
    albumViewModel: AlbumViewModel = koinActivityViewModel(),
    albumDetailViewModel: AlbumDetailViewModel = koinActivityViewModel(),
) {
    val palette = LocalJmxGlassPalette.current
    val mainNavController = LocalMainNavController.current
    val albumSearchLazyPagingItems = albumViewModel.searchAlbumPager.collectAsLazyPagingItems()
    val albumSearchFilterState by albumViewModel.searchAlbumFilterState.collectAsState()
    val searchAlbumIdState by albumViewModel.searchAlbumIdState.collectAsState()
    LaunchedEffect(searchAlbumIdState) {
        if (searchAlbumIdState != null) {
            albumDetailViewModel.reset(searchAlbumIdState)
            mainNavController.navigate("albumDetail/${searchAlbumIdState}") {
                popUpTo("albumSearchResult/{searchContent}") {
                    inclusive = true
                }
            }
        }
    }
    CommonScaffold(
        title = "搜索：${albumSearchFilterState.searchContent}",
        onTitleClick = {
            if (!mainNavController.popBackStack("albumSearch", inclusive = false)) {
                mainNavController.navigate("albumSearch") {
                    launchSingleTop = true
                }
            }
        }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 10.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                AlbumSearchOrderFilter.entries.forEach { item ->
                    key(item.label) {
                        FilterItem(
                            label = item.label,
                            onClick = {
                                albumViewModel.changeSearchAlbumOrderFilter(item)
                            },
                            active = item.value == albumSearchFilterState.order.value
                        )
                    }
                }
            }
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(0.6.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(palette.primaryText.copy(alpha = 0.08f))
            )
            if (albumSearchLazyPagingItems.loadState.refresh is LoadState.Loading && albumSearchLazyPagingItems.itemCount == 0) {
                AlbumSearchResultSkeleton(
                    modifier = Modifier.weight(1f)
                )
                return@CommonScaffold
            }
            PullRefreshAndLoadMoreGrid(
                modifier = Modifier.weight(1f),
                lazyPagingItems = albumSearchLazyPagingItems,
                key = { it.id },
                columns = GridCells.Fixed(3),
            ) {
                Album(it)
            }
        }
    }
}
