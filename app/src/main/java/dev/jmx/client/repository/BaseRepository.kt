package dev.jmx.client.repository

import android.util.Log
import dev.jmx.client.data.remote.model.NetworkResult
import dev.jmx.client.data.remote.model.ResponseWrapper
import dev.jmx.client.store.InitManager
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

open class BaseRepository(
    private val initManager: InitManager
) {

    suspend fun <T> safeApiCall(apiCall: suspend () -> ResponseWrapper<T>): NetworkResult<T> {
        return try {
            val response = apiCall()
            if (response.code == 200) {
                NetworkResult.Success(response.data!!)
            } else {
                NetworkResult.Error(response.errorMsg!!)
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    suspend fun safeStringCall(apiCall: suspend () -> String): NetworkResult<String> {
        return try {
            val response = apiCall()
            NetworkResult.Success(response)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun handleException(e: Exception): NetworkResult.Error {
        Log.d("api", e.stackTraceToString())
        return when (e) {
            is SocketTimeoutException -> NetworkResult.Error("网络连接超时")
            is ConnectException -> NetworkResult.Error("网络连接失败")
            is UnknownHostException -> NetworkResult.Error("网络不可用")
            is HttpException -> {
                val errMsg = when (e.code()) {
                    401 -> "未授权，请重新登录"
                    else -> "网络错误：${e.code()}"
                }
                NetworkResult.Error(errMsg)
            }

            else -> NetworkResult.Error(
                e.message ?: "未知错误"
            )
        }
    }
}
