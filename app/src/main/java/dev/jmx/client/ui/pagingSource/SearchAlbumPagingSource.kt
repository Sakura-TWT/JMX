package dev.jmx.client.ui.pagingSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.jmx.client.data.models.Album
import dev.jmx.client.data.models.AlbumSearchOrderFilter
import dev.jmx.client.repository.AlbumRepository
import dev.jmx.client.data.remote.model.AlbumListResponse
import dev.jmx.client.data.remote.model.NetworkResult

data class SearchAlbumFilter(
    val order: AlbumSearchOrderFilter = AlbumSearchOrderFilter.NEWEST,
    val searchContent: String = "",
)

class SearchAlbumPagingSource(
    private val albumRepository: AlbumRepository,
    private val filter: SearchAlbumFilter,
    private val onFindSingleAlbumId: (id: Int?) -> Unit = {}
) : PagingSource<Int, Album>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Album> {
        val currentPage = params.key ?: 1
        return when (val data =
            albumRepository.getAlbumList(currentPage, filter.order, filter.searchContent)) {
            is NetworkResult.Error -> {
                LoadResult.Error(Exception(data.message))
            }

            is NetworkResult.Success<AlbumListResponse> -> {
                if (data.data.redirect_aid != null) {
                    onFindSingleAlbumId(data.data.redirect_aid.toInt())
                    LoadResult.Page(
                        data = listOf(),
                        prevKey = null,
                        nextKey = null
                    )
                } else {
                    onFindSingleAlbumId(null)
                    val list = data.data.toAlbumList()
                    val total = data.data.total.toInt()
                    val isLastPage = currentPage >= (total + params.loadSize - 1) / params.loadSize
                    LoadResult.Page(
                        data = list,
                        prevKey = if (currentPage == 1) null else currentPage - 1,
                        nextKey = if (isLastPage) null else currentPage + 1
                    )
                }
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Album>): Int? = null
}