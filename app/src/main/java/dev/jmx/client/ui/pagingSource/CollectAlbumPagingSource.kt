package dev.jmx.client.ui.pagingSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.jmx.client.data.models.CollectAlbumOrderFilter
import dev.jmx.client.data.models.Album
import dev.jmx.client.repository.UserRepository
import dev.jmx.client.data.remote.model.NetworkResult
import dev.jmx.client.data.remote.model.UserCollectAlbumListResponse

class CollectAlbumPagingSource(
    private val userRepository: UserRepository,
    private val order: CollectAlbumOrderFilter,
) : PagingSource<Int, Album>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Album> {
        val currentPage = params.key ?: 1
        return when (val data =
            userRepository.getCollectAlbumList(currentPage, order)) {
            is NetworkResult.Error -> {
                LoadResult.Error(Exception(data.message))
            }

            is NetworkResult.Success<UserCollectAlbumListResponse> -> {
                val list = data.data.toAlbumList()
                val total = data.data.total
                val isLastPage = currentPage >= (total + params.loadSize - 1) / params.loadSize
                LoadResult.Page(
                    data = list,
                    prevKey = if (currentPage == 1) null else currentPage - 1,
                    nextKey = if (isLastPage) null else currentPage + 1
                )
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Album>): Int? = null
}