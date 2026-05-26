package dev.jmx.client.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dev.jmx.client.data.models.Album
import dev.jmx.client.database.dao.DownloadAlbumDao
import dev.jmx.client.repository.AlbumRepository
import dev.jmx.client.data.remote.model.CollectAlbumResponse
import dev.jmx.client.data.remote.model.AlbumDetailResponse
import dev.jmx.client.data.remote.model.CommentAlbumResponse
import dev.jmx.client.data.remote.model.LikeAlbumResponse
import dev.jmx.client.data.remote.model.NetworkResult
import dev.jmx.client.store.RemoteSettingManager
import dev.jmx.client.store.ToastManager
import dev.jmx.client.ui.models.CommonUIState
import dev.jmx.client.ui.pagingSource.AlbumCommentPagingSource
import dev.jmx.client.utils.log
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlbumDetailViewModel(
    private val albumRepository: AlbumRepository,
    private val toastManager: ToastManager,
    private val downloadAlbumDao: DownloadAlbumDao,
    private val remoteSettingManager: RemoteSettingManager,
) : ViewModel() {
    private val _albumDetailState = MutableStateFlow<CommonUIState<Album>>(
        CommonUIState(
            isLoading = true,
        )
    )
    val albumDetailState = _albumDetailState.asStateFlow()

    fun getAlbumDetail(id: Int) {
        viewModelScope.launch {
            _albumDetailState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errorMsg = "",
                )
            }
            when (val data = albumRepository.getAlbumDetail(id)) {
                is NetworkResult.Error -> {
                    _albumDetailState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is NetworkResult.Success<AlbumDetailResponse> -> {
                    _albumDetailState.update {
                        it.copy(
                            data = data.data.toAlbum()
                        )
                    }
                }
            }
            _albumDetailState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    private val _likeAlbumState = MutableStateFlow(CommonUIState(data = null))
    val likeAlbumState = _likeAlbumState.asStateFlow()
    fun likeAlbum(id: Int) {
        viewModelScope.launch {
            _likeAlbumState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errorMsg = ""
                )
            }
            when (val data = albumRepository.likeAlbum(id)) {
                is NetworkResult.Error -> {
                    _likeAlbumState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is NetworkResult.Success<LikeAlbumResponse> -> {
                    toastManager.showAsync("喜欢成功")
                    if (_albumDetailState.value.data != null) {
                        _albumDetailState.update {
                            it.copy(
                                data = it.data!!.copy(
                                    isLike = true,
                                    likeCount = it.data.likeCount + 1
                                )
                            )
                        }
                    }
                }
            }
            _likeAlbumState.update {
                it.copy(
                    isLoading = false,
                )
            }
        }
    }

    private val _collectAlbumState = MutableStateFlow(CommonUIState(data = null))
    val collectAlbumState = _collectAlbumState.asStateFlow()
    fun collect(id: Int) {
        viewModelScope.launch {
            _collectAlbumState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errorMsg = ""
                )
            }
            when (val data = albumRepository.collectAlbum(id)) {
                is NetworkResult.Error -> {
                    _collectAlbumState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is NetworkResult.Success<CollectAlbumResponse> -> {
                    toastManager.showAsync("收藏成功")
                    if (_albumDetailState.value.data != null) {
                        _albumDetailState.update {
                            it.copy(
                                data = it.data!!.copy(
                                    isCollect = true,
                                )
                            )
                        }
                    }
                }
            }
            _collectAlbumState.update {
                it.copy(
                    isLoading = false,
                )
            }
        }
    }

    fun unCollect(id: Int) {
        viewModelScope.launch {
            _collectAlbumState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errorMsg = ""
                )
            }
            when (val data = albumRepository.unCollectAlbum(id)) {
                is NetworkResult.Error -> {
                    _collectAlbumState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is NetworkResult.Success<CollectAlbumResponse> -> {
                    toastManager.showAsync("取消收藏成功")
                    if (_albumDetailState.value.data != null) {
                        _albumDetailState.update {
                            it.copy(
                                data = it.data!!.copy(
                                    isCollect = false,
                                )
                            )
                        }
                    }
                }
            }
            _collectAlbumState.update {
                it.copy(
                    isLoading = false,
                )
            }
        }
    }

    fun reset(id: Int?) {
        if (id != null && id == _albumDetailState.value.data?.id) {
            return
        }
        _albumDetailState.update {
            CommonUIState(
                isLoading = true,
            )
        }
    }

    private val _commentAlbumIdState = MutableStateFlow(0)
    val commentAlbumIdState = _commentAlbumIdState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val commentPager = _commentAlbumIdState.flatMapLatest { albumId ->
        Pager(
            config = PagingConfig(pageSize = 20, prefetchDistance = 6, initialLoadSize = 20),
            pagingSourceFactory = {
                AlbumCommentPagingSource(
                    albumRepository,
                    albumId
                )
            }
        ).flow
    }.cachedIn(viewModelScope)

    fun changeCommentAlbumId(albumId: Int) {
        _commentAlbumIdState.update {
            albumId
        }
    }

    private val _commentAlbumState = MutableStateFlow(CommonUIState(data = null))
    val commentAlbumState = _commentAlbumState.asStateFlow()
    fun comment(
        content: String,
        albumId: Int,
        commentId: Int? = null,
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            _commentAlbumState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errorMsg = ""
                )
            }
            when (val data = albumRepository.comment(content, albumId, commentId)) {
                is NetworkResult.Error -> {
                    _commentAlbumState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is NetworkResult.Success<CommentAlbumResponse> -> {
                    log("commentArg $content, $albumId, $commentId")
                    toastManager.showAsync(data.data.msg)
                    if (data.data.status == "ok") {
                        onSuccess?.invoke()
                    }
                }
            }
            _commentAlbumState.update {
                it.copy(
                    isLoading = false,
                )
            }
        }
    }
}