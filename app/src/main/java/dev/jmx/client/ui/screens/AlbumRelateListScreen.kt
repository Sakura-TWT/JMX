package dev.jmx.client.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import dev.jmx.client.ui.components.Album
import dev.jmx.client.ui.components.CommonScaffold
import dev.jmx.client.ui.viewModel.AlbumDetailViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
fun AlbumRelateListScreen(
    albumDetailViewModel: AlbumDetailViewModel = koinActivityViewModel(),
) {
    val albumDetailState by albumDetailViewModel.albumDetailState.collectAsState()
    CommonScaffold(title = "相关本子") {
        if (albumDetailState.data != null) {
            val relateList = albumDetailState.data!!.relateAlbumList
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                contentPadding = PaddingValues(10.dp),
            ) {
                items(
                    relateList,
                    key = { it.id },
                ) {
                    Album(album = it)
                }
            }
        }
    }
}