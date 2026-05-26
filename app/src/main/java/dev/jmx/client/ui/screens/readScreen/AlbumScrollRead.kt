package dev.jmx.client.ui.screens.readScreen

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import dev.jmx.client.data.models.ImageResultState
import dev.jmx.client.ui.components.AlbumImageImage
import dev.jmx.client.ui.viewModel.AlbumReadViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.koinViewModel

@OptIn(FlowPreview::class)
@Composable
fun AlbumScrollRead(
    lazyListState: LazyListState,
    albumReadViewModel: AlbumReadViewModel = koinViewModel(),
    onUpdateSliderValue: (value: Float) -> Unit
) {
    var currentIndexState by albumReadViewModel.currentIndexState
    val albumImageState by albumReadViewModel.albumImageState.collectAsState()
    val list = albumImageState.data ?: listOf()
    val context = LocalContext.current

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect {
                if (currentIndexState != it) {
                    currentIndexState = it
                    onUpdateSliderValue(it.toFloat())
                    albumReadViewModel.decodeIndex(currentIndexState, context)
                }
            }
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(list, key = {
            "${it.albumId}_${it.originSrc}"
        }) {
            AlbumImageImage(
                albumImageImageState = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(
                        when (val state = it.imageResultState) {
                            is ImageResultState.Success -> {
                                state.decodeImageAspectRatio
                            }

                            else -> {
                                9f / 16
                            }
                        }
                    )
            )
        }
    }
}
