package dev.jmx.client.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import dev.jmx.client.ui.components.Album
import dev.jmx.client.ui.components.AlbumSkeleton
import dev.jmx.client.ui.components.CommonScaffold
import dev.jmx.client.ui.components.PullRefreshAndLoadMoreGrid
import dev.jmx.client.ui.viewModel.UserViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
private fun UserHistoryAlbumSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .weight(1f)
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
}

@Composable
fun UserHistoryAlbumScreen(
    userViewModel: UserViewModel = koinActivityViewModel()
) {
    val historyAlbumLazyPagingItems = userViewModel.historyAlbumPager.collectAsLazyPagingItems()
    CommonScaffold(
        title = "历史浏览"
    ) {
        if (historyAlbumLazyPagingItems.loadState.refresh is LoadState.Loading && historyAlbumLazyPagingItems.itemCount == 0) {
            UserHistoryAlbumSkeleton()
            return@CommonScaffold
        }
        PullRefreshAndLoadMoreGrid(
            lazyPagingItems = historyAlbumLazyPagingItems,
            key = { it.id },
            columns = GridCells.Fixed(3),
        ) {
            Album(it)
        }
    }
}
