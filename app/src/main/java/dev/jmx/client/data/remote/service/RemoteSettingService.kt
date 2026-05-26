package dev.jmx.client.data.remote.service

import dev.jmx.client.data.remote.annotation.GInit
import dev.jmx.client.data.remote.model.RemoteSettingResponse
import dev.jmx.client.data.remote.model.ResponseWrapper
import retrofit2.http.GET

interface RemoteSettingService {
    @GInit
    @GET("setting")
    suspend fun getRemoteSetting(): ResponseWrapper<RemoteSettingResponse>
}