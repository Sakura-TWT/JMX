package dev.jmx.client.data.remote.model

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val code: Int = -1) : NetworkResult<Nothing>()
}

fun <T> NetworkResult<T>.getOrThrow(): T {
    return when (this) {
        is NetworkResult.Success -> data
        is NetworkResult.Error -> throw RuntimeException(message)
    }
}
