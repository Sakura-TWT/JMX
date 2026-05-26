package dev.jmx.client.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import dev.jmx.client.ui.components.Comment
import dev.jmx.client.ui.components.CommentSkeleton
import dev.jmx.client.ui.components.CommonScaffold
import dev.jmx.client.ui.components.PullRefreshAndLoadMoreGrid
import dev.jmx.client.ui.viewModel.UserViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
private fun UserHistoryCommentSkeleton() {
    FlowRow(
        modifier = Modifier.padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        for (i in 0 until 10) {
            key(i) {
                CommentSkeleton()
            }
        }
    }
}

@Composable
fun UserHistoryCommentScreen(
    userViewModel: UserViewModel = koinActivityViewModel()
) {
    val historyCommentLazyPagingItems = userViewModel.historyCommentPager.collectAsLazyPagingItems()
    CommonScaffold(
        title = "历史评论"
    ) {
        if (historyCommentLazyPagingItems.loadState.refresh is LoadState.Loading && historyCommentLazyPagingItems.itemCount == 0) {
            UserHistoryCommentSkeleton()
            return@CommonScaffold
        }
        PullRefreshAndLoadMoreGrid(
            lazyPagingItems = historyCommentLazyPagingItems,
            key = { it.id },
            columns = GridCells.Fixed(1)
        ) {
            Comment(it)
        }
    }
}
