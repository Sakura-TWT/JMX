package dev.jmx.client.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import dev.jmx.client.data.models.WeekData
import dev.jmx.client.ui.components.Album
import dev.jmx.client.ui.components.CommonScaffold
import dev.jmx.client.ui.components.FilterItem
import dev.jmx.client.ui.components.PullRefreshAndLoadMoreGrid
import dev.jmx.client.ui.components.SelectDialog
import dev.jmx.client.ui.components.SelectOption
import dev.jmx.client.ui.glass.LocalJmxGlassPalette
import dev.jmx.client.ui.models.CommonUIState
import dev.jmx.client.ui.pagingSource.WeekFilter
import dev.jmx.client.ui.viewModel.AlbumViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
private fun AlbumWeekCategorySelect(
    category: Pair<String, String>,
    weekDataState: CommonUIState<WeekData>,
    weekFilterState: WeekFilter,
    albumViewModel: AlbumViewModel = koinActivityViewModel(),
) {
    var showSelectDialog by remember { mutableStateOf(false) }
    val weekCategoryOptionList by remember(weekDataState) {
        derivedStateOf {
            val list = weekDataState.data?.categoryList ?: listOf()
            list.map { SelectOption(label = it.second, value = it.first) }
        }
    }
    FilterItem(
        label = category.second,
        onClick = {
            showSelectDialog = true
        },
        active = true
    )
    if (showSelectDialog) {
        SelectDialog(
            title = "选择日期",
            value = weekFilterState.categoryId,
            selectOptionList = weekCategoryOptionList,
            onSelect = {
                albumViewModel.changeWeekCategoryFilter(it)
                showSelectDialog = false
            },
            onDismissRequest = {
                showSelectDialog = false
            }
        )
    }
}

@Composable
fun AlbumWeekRecommendScreen(
    albumViewModel: AlbumViewModel = koinActivityViewModel()
) {
    val palette = LocalJmxGlassPalette.current
    val weekDataState by albumViewModel.weekDataState.collectAsState()
    val weekFilterState by albumViewModel.weekFilterState.collectAsState()
    val weekRecommendAlbumPagingItems = albumViewModel.weekAlbumPager.collectAsLazyPagingItems()
    val weekCategoryFilter by remember(weekFilterState) {
        derivedStateOf {
            val categoryList = weekDataState.data?.categoryList ?: listOf()
            categoryList.find { it.first == weekFilterState.categoryId }
        }
    }
    LaunchedEffect(Unit) {
        if (weekDataState.data != null) {
            return@LaunchedEffect
        }
        albumViewModel.getWeekData()
    }
    CommonScaffold(
        title = "每周推荐"
    ) {
        Column {
            if (weekDataState.data != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(10.dp)
                ) {
                    val typeList = weekDataState.data!!.typeList
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        typeList.forEach { item ->
                            key(item.first) {
                                FilterItem(
                                    label = item.second,
                                    onClick = {
                                        albumViewModel.changeWeekTypeFilter(item.first)
                                    },
                                    active = weekFilterState.typeId == item.first
                                )
                            }
                        }
                    }
                    weekCategoryFilter?.let {
                        AlbumWeekCategorySelect(
                            category = it,
                            weekDataState = weekDataState,
                            weekFilterState = weekFilterState
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(0.6.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(palette.primaryText.copy(alpha = 0.08f))
                )
            }
            PullRefreshAndLoadMoreGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                lazyPagingItems = weekRecommendAlbumPagingItems,
                key = { it.id },
                columns = GridCells.Fixed(3),
            ) {
                Album(it)
            }
        }
    }
}
