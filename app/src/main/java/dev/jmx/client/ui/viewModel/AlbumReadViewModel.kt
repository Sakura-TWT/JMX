package dev.jmx.client.ui.viewModel

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import dev.jmx.client.data.models.AlbumImageImageState
import dev.jmx.client.repository.AlbumRepository
import dev.jmx.client.data.remote.model.AlbumImageListResponse
import dev.jmx.client.data.remote.model.NetworkResult
import dev.jmx.client.store.LocalSettingManager
import dev.jmx.client.ui.models.CommonUIState
import dev.jmx.client.utils.log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

class AlbumReadViewModel(
    private val albumRepository: AlbumRepository,
    private val imageLoader: ImageLoader,
    private val localSettingManager: LocalSettingManager,
) : ViewModel() {
    var isShowToolBar = mutableStateOf(false)
    var currentIndexState = mutableIntStateOf(0)
    private val _albumImageState = MutableStateFlow(
        CommonUIState<List<AlbumImageImageState>>(
            isLoading = true
        )
    )
    val albumImageState = _albumImageState.asStateFlow()

    val size: Int get() = _albumImageState.value.data?.size ?: 0

    private val prefetchSet = mutableSetOf<Int>()

    fun getAlbumImageList(albumId: Int, shunt: String, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            currentIndexState.intValue = 0
            isShowToolBar.value = false
            prefetchSet.clear()
            _albumImageState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    data = null,
                    errorMsg = ""
                )
            }
            when (val data = albumRepository.getAlbumImageList(albumId, shunt)) {
                is NetworkResult.Error -> {
                    _albumImageState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is NetworkResult.Success<AlbumImageListResponse> -> {
                    _albumImageState.update {
                        it.copy(
                            data = data.data.list.mapIndexed { index, item ->
                                AlbumImageImageState(
                                    index,
                                    albumId,
                                    item,
                                    data.data.__scrambleId,
                                    data.data.__speed,
                                    imageLoader,
                                )
                            }
                        )
                    }
                    onSuccess?.invoke()
                }
            }
            _albumImageState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }
    fun decodeIndex(index: Int, context: Context) {
        log("decode index $index")
        val count = localSettingManager.localSettingState.value.prefetchCount
        val start = max(0, index - count)
        val end = min(size - 1, index + count)
        decode(index, context) {
            for (i in index + 1..end) {
                log("pre decode index $i")
                decode(i, context)
            }
            for (i in index - 1 downTo start) {
                log("pre decode index $i")
                decode(i, context)
            }
        }
    }

    fun prev(context: Context) {
        hideToolBar()
        val index = max(0, currentIndexState.intValue - 1)
        currentIndexState.intValue = index
        decodeIndex(index, context)
    }

    fun next(context: Context) {
        hideToolBar()
        val index = min(size - 1, currentIndexState.intValue + 1)
        currentIndexState.intValue = index
        decodeIndex(index, context)
    }

    private fun decode(index: Int, context: Context, onComplete: (() -> Unit)? = null) {
        val albumImageImageState = albumImageState.value.data?.getOrNull(index) ?: return
        if (prefetchSet.contains(index)) {
            onComplete?.invoke()
            return
        }
        viewModelScope.launch {
            albumImageImageState.decode(context)
            onComplete?.invoke()
        }
        prefetchSet.add(index)
    }

    fun triggerToolBar() {
        isShowToolBar.value = !isShowToolBar.value
    }

    fun hideToolBar() {
        isShowToolBar.value = false
    }

    fun showToolBar() {
        isShowToolBar.value = true
    }
}
