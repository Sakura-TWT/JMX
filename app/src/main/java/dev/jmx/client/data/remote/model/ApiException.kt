package dev.jmx.client.data.remote.model

class ApiException(
    val errorCode: Int,
    override val message: String
): Exception(message)