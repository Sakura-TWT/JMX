package dev.jmx.client.repository

import dev.jmx.client.data.models.CollectAlbumOrderFilter
import dev.jmx.client.data.remote.model.LoginResponse
import dev.jmx.client.data.remote.model.NetworkResult
import dev.jmx.client.data.remote.model.SignInDataResponse
import dev.jmx.client.data.remote.model.SignInResponse
import dev.jmx.client.data.remote.model.UserCollectAlbumListResponse
import dev.jmx.client.data.remote.model.UserHistoryAlbumListResponse
import dev.jmx.client.data.remote.model.UserHistoryCommentListResponse

interface UserRepository {
    suspend fun login(username: String, password: String): NetworkResult<LoginResponse>
    suspend fun getCollectAlbumList(
        page: Int = 1,
        order: CollectAlbumOrderFilter = CollectAlbumOrderFilter.COLLECT_TIME
    ): NetworkResult<UserCollectAlbumListResponse>

    suspend fun getHistoryAlbumList(page: Int = 1): NetworkResult<UserHistoryAlbumListResponse>
    suspend fun getHistoryCommentList(
        page: Int = 1,
        userId: Int
    ): NetworkResult<UserHistoryCommentListResponse>

    suspend fun getSignData(
        userId: Int,
    ): NetworkResult<SignInDataResponse>

    suspend fun signIn(
        userId: Int,
        dailyId: Int,
    ): NetworkResult<SignInResponse>
}