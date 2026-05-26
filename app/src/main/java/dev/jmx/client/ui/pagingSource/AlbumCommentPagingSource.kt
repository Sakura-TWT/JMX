package dev.jmx.client.ui.pagingSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.jmx.client.data.models.Comment
import dev.jmx.client.repository.AlbumRepository
import dev.jmx.client.data.remote.model.CommentListResponse
import dev.jmx.client.data.remote.model.NetworkResult

class AlbumCommentPagingSource(
    private val albumRepository: AlbumRepository,
    private val albumId: Int,
) : PagingSource<Int, Comment>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Comment> {
        val currentPage = params.key ?: 1
        return when (val data =
            albumRepository.getCommentList(currentPage, albumId)) {
            is NetworkResult.Error -> {
                LoadResult.Error(Exception(data.message))
            }

            is NetworkResult.Success<CommentListResponse> -> {
                val list = data.data.toCommentList()
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

    override fun getRefreshKey(state: PagingState<Int, Comment>): Int? = null
}
