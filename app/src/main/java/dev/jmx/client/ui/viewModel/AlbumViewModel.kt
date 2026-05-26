package dev.jmx.client.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dev.jmx.client.data.models.AlbumSearchOrderFilter
import dev.jmx.client.data.models.HomeAlbumSwiperItem
import dev.jmx.client.data.models.WeekData
import dev.jmx.client.repository.AlbumRepository
import dev.jmx.client.data.remote.model.HomeSwiperAlbumListItemResponse
import dev.jmx.client.data.remote.model.NetworkResult
import dev.jmx.client.data.remote.model.WeekResponse
import dev.jmx.client.ui.models.CommonUIState
import dev.jmx.client.ui.pagingSource.SearchAlbumFilter
import dev.jmx.client.ui.pagingSource.SearchAlbumPagingSource
import dev.jmx.client.ui.pagingSource.WeekAlbumPagingSource
import dev.jmx.client.ui.pagingSource.WeekFilter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlbumViewModel(
    private val albumRepository: AlbumRepository
) : ViewModel() {
    data class HomeAlbumUIState(
        val isLoading: Boolean = true,
        val isError: Boolean = false,
        val list: List<HomeAlbumSwiperItem> = listOf(),
        val errorMsg: String? = null
    )

    private val _homeAlbumState = MutableStateFlow(HomeAlbumUIState())
    val homeAlbumState = _homeAlbumState.asStateFlow()
    fun getHomeAlbum() {
        viewModelScope.launch {
            _homeAlbumState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errorMsg = ""
                )
            }
            when (val data = albumRepository.getHomeSwiperAlbumList()) {
                is NetworkResult.Error -> {
                    _homeAlbumState.update {
                        it.copy(isError = true, errorMsg = data.message)
                    }
                }

                is NetworkResult.Success<List<HomeSwiperAlbumListItemResponse>> -> {
                    _homeAlbumState.update {
                        it.copy(list = data.data.map { item -> item.toHomeAlbumSwiperItem() })
                    }
                }
            }
            _homeAlbumState.update {
                it.copy(isLoading = false)
            }
        }
    }

    private val _searchAlbumFilterState = MutableStateFlow(SearchAlbumFilter())
    val searchAlbumFilterState = _searchAlbumFilterState.asStateFlow()
    private val _searchAlbumIdState = MutableStateFlow<Int?>(null)
    val searchAlbumIdState = _searchAlbumIdState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchAlbumPager = _searchAlbumFilterState.flatMapLatest { filter ->
        Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 6,
                initialLoadSize = 20
            ),
            pagingSourceFactory = {
                SearchAlbumPagingSource(
                    albumRepository,
                    filter
                ) { id ->
                    _searchAlbumIdState.update {
                        id
                    }
                }
            }
        ).flow
    }.cachedIn(viewModelScope)

    fun changeSearchAlbumOrderFilter(order: AlbumSearchOrderFilter) {
        _searchAlbumIdState.update { null }
        _searchAlbumFilterState.update {
            it.copy(
                order = order
            )
        }
    }

    fun changeSearchAlbumContent(searchContent: String) {
        _searchAlbumIdState.update { null }
        _searchAlbumFilterState.update {
            it.copy(
                searchContent = searchContent
            )
        }
    }

    private val _weekDataState = MutableStateFlow(CommonUIState<WeekData>())
    val weekDataState = _weekDataState.asStateFlow()
    fun getWeekData() {
        viewModelScope.launch {
            _weekDataState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errorMsg = ""
                )
            }
            when (val data = albumRepository.getWeekData()) {
                is NetworkResult.Error -> {
                    _weekDataState.update {
                        it.copy(isError = true, errorMsg = data.message)
                    }
                }

                is NetworkResult.Success<WeekResponse> -> {
                    val d = data.data.toWeekData()
                    _weekDataState.update {
                        it.copy(data = d)
                    }
                    if (d.categoryList.isNotEmpty()) {
                        _weekFilterState.update {
                            it.copy(categoryId = d.categoryList[0].first)
                        }
                    }
                    if (d.typeList.isNotEmpty()) {
                        _weekFilterState.update {
                            it.copy(typeId = d.typeList[0].first)
                        }
                    }
                }
            }
            _weekDataState.update {
                it.copy(isLoading = false)
            }
        }
    }

    private val _weekFilterState = MutableStateFlow(WeekFilter())
    val weekFilterState = _weekFilterState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val weekAlbumPager = _weekFilterState.flatMapLatest { filter ->
        Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 6,
                initialLoadSize = 20
            ),
            pagingSourceFactory = {
                WeekAlbumPagingSource(
                    albumRepository,
                    filter
                )
            }
        ).flow
    }.cachedIn(viewModelScope)

    fun changeWeekCategoryFilter(categoryId: String?) {
        _weekFilterState.update {
            it.copy(
                categoryId = categoryId
            )
        }
    }

    fun changeWeekTypeFilter(typeId: String?) {
        _weekFilterState.update {
            it.copy(
                typeId = typeId
            )
        }
    }
}