package dev.jmx.client.ui.viewModel

import androidx.lifecycle.ViewModel
import dev.jmx.client.data.models.LocalSetting

class LocalSettingViewModel(
) : ViewModel() {

    fun changeLocalSetting(nLocalSetting: LocalSetting) {
//        secureStorage.saveLocalSetting(nLocalSetting)
    }
}