package dev.jmx.client.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jmx.client.store.InitManager
import dev.jmx.client.task.AppInitTask
import dev.jmx.client.utils.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GlobalViewModel(
    private val appInitTaskList: List<AppInitTask>,
    private val initManager: InitManager
) : ViewModel() {

    fun init() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                appInitTaskList.sortedBy { it.getAppTaskInfo().sort }.forEach { it.init() }
            }
            if (appInitTaskList.any { it.getAppTaskInfo().isError }) {
                // TODO fix
            }
            initManager.deferred.complete("")
            log("完成全局初始化")
        }
    }
}
