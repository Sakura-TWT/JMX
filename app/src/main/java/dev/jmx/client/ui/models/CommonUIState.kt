package dev.jmx.client.ui.models

data class CommonUIState<T>(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val data: T? = null,
    val errorMsg: String? = null
) {}
