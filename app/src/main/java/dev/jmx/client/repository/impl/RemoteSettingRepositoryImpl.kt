package dev.jmx.client.repository.impl

import dev.jmx.client.repository.BaseRepository
import dev.jmx.client.repository.RemoteSettingRepository
import dev.jmx.client.data.remote.model.NetworkResult
import dev.jmx.client.data.remote.model.RemoteSettingResponse
import dev.jmx.client.data.remote.service.RemoteSettingService
import dev.jmx.client.store.InitManager

class RemoteSettingRepositoryImpl(
    private val service: RemoteSettingService,
    initManager: InitManager
) : BaseRepository(initManager), RemoteSettingRepository {
    override suspend fun getRemoteSetting(): NetworkResult<RemoteSettingResponse> {
        return safeApiCall {
            service.getRemoteSetting()
        }
    }
}