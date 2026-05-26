package dev.jmx.client.store

import dev.jmx.client.storage.HistorySearchStorage
import dev.jmx.client.task.AppInitTask
import dev.jmx.client.task.AppTaskInfo
import dev.jmx.client.utils.log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HistorySearchManager(
    private val historySearchStorage: HistorySearchStorage
) : AppInitTask {

    private val _historySearchState = MutableStateFlow(listOf<String>())
    val historySearchState = _historySearchState.asStateFlow()

    fun addItem(item: String) {
        val list = listOf(item) + _historySearchState.value.filterNot { it == item }
        _historySearchState.update {
            list
        }
        historySearchStorage.set(list)
    }

    fun clear() {
        historySearchStorage.remove()
        _historySearchState.update {
            historySearchStorage.get()
        }
    }

    override suspend fun init() {
        log("加载历史搜索数据")
        _historySearchState.update {
            historySearchStorage.get()
        }
        log("已加载历史搜索数据")
    }

    private var appTaskInfo = AppTaskInfo(
        taskName = "加载历史搜索数据",
        sort = 4,
    )

    override fun getAppTaskInfo(): AppTaskInfo = appTaskInfo
}