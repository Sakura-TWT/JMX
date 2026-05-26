package dev.jmx.client.repository

import dev.jmx.client.data.remote.model.NetworkResult
import dev.jmx.client.data.remote.model.RemoteSettingResponse

interface RemoteSettingRepository {
    suspend fun getRemoteSetting(): NetworkResult<RemoteSettingResponse>
}