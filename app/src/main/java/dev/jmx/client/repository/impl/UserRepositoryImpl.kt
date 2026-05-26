package dev.jmx.client.repository.impl

import dev.jmx.client.data.models.CollectAlbumOrderFilter
import dev.jmx.client.data.remote.ApiClient
import dev.jmx.client.repository.BaseRepository
import dev.jmx.client.repository.UserRepository
import dev.jmx.client.data.remote.model.LoginResponse
import dev.jmx.client.data.remote.model.NetworkResult
import dev.jmx.client.data.remote.model.SignInDataResponse
import dev.jmx.client.data.remote.model.SignInResponse
import dev.jmx.client.data.remote.model.UserCollectAlbumListResponse
import dev.jmx.client.data.remote.model.UserHistoryAlbumListResponse
import dev.jmx.client.data.remote.model.UserHistoryCommentListResponse
import dev.jmx.client.data.remote.service.UserService
import dev.jmx.client.store.InitManager
import dev.jmx.client.store.LocalSettingManager
import okhttp3.Cookie

class UserRepositoryImpl(
    private val service: UserService,
    private val localSettingManager: LocalSettingManager,
    private val apiClient: ApiClient,
    initManager: InitManager
) : BaseRepository(initManager), UserRepository {

    override suspend fun login(username: String, password: String): NetworkResult<LoginResponse> {
        apiClient.clearCookie()
        val result = safeApiCall {
            service.login(username, password)
        }
        if (result is NetworkResult.Success) {
            persistAvsCookie(result.data.s)
        }
        return result
    }

    private fun persistAvsCookie(avs: String?) {
        if (avs.isNullOrBlank()) {
            return
        }
        val apiHost = localSettingManager.localSettingState.value.api
            .substringAfter("://")
            .substringBefore("/")
        if (apiHost.isBlank()) {
            return
        }
        val avsCookie = Cookie.Builder()
            .name("AVS")
            .value(avs)
            .domain(apiHost)
            .path("/")
            .build()
        apiClient.upsertCookie(avsCookie)
    }

    override suspend fun getCollectAlbumList(
        page: Int,
        order: CollectAlbumOrderFilter
    ): NetworkResult<UserCollectAlbumListResponse> {
        return safeApiCall {
            service.getCollectAlbumList(page, order.value)
        }
    }

    override suspend fun getHistoryAlbumList(page: Int): NetworkResult<UserHistoryAlbumListResponse> {
        return safeApiCall {
            service.getHistoryAlbumList(page)
        }
    }

    override suspend fun getHistoryCommentList(
        page: Int,
        userId: Int
    ): NetworkResult<UserHistoryCommentListResponse> {
        return safeApiCall {
            service.getCommentList(page, userId)
        }
    }

    override suspend fun getSignData(userId: Int): NetworkResult<SignInDataResponse> {
        return safeApiCall {
            service.getSignInData(userId)
        }
    }

    override suspend fun signIn(userId: Int, dailyId: Int): NetworkResult<SignInResponse> {
        return safeApiCall {
            service.signIn(userId, dailyId)
        }
    }
}
