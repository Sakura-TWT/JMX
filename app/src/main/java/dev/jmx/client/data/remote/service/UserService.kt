package dev.jmx.client.data.remote.service

import dev.jmx.client.data.remote.annotation.GInit
import dev.jmx.client.data.remote.model.LoginResponse
import dev.jmx.client.data.remote.model.ResponseWrapper
import dev.jmx.client.data.remote.model.SignInDataResponse
import dev.jmx.client.data.remote.model.SignInResponse
import dev.jmx.client.data.remote.model.UserCollectAlbumListResponse
import dev.jmx.client.data.remote.model.UserHistoryAlbumListResponse
import dev.jmx.client.data.remote.model.UserHistoryCommentListResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface UserService {

    @GInit
    @POST("login")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): ResponseWrapper<LoginResponse>

    @GET("favorite")
    suspend fun getCollectAlbumList(
        @Query("page") page: Int,
        @Query("o") order: String,
        @Query("folder_id") folderId: Int = 0
    ): ResponseWrapper<UserCollectAlbumListResponse>

    @GET("watch_list")
    suspend fun getHistoryAlbumList(
        @Query("page") page: Int,
    ): ResponseWrapper<UserHistoryAlbumListResponse>

    @GET("forum")
    suspend fun getCommentList(
        @Query("page") page: Int,
        @Query("uid") userId: Int
    ): ResponseWrapper<UserHistoryCommentListResponse>

    @GET("daily")
    suspend fun getSignInData(
        @Query("user_id") userId: Int,
    ): ResponseWrapper<SignInDataResponse>

    @POST("daily_chk")
    @Multipart
    suspend fun signIn(
        @Part("user_id") userId: Int,
        @Part("daily_id") dailyId: Int,
    ): ResponseWrapper<SignInResponse>
}
