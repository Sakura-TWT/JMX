package dev.jmx.client.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import dev.jmx.client.store.LocalSettingManager
import org.koin.compose.getKoin

val LocalExtendedColors = staticCompositionLocalOf<ExtendedColorScheme> {
    error("未提供默认扩展主题变量")
}

object ExtendedTheme {
    val colors: ExtendedColorScheme
        @Composable
        get() = LocalExtendedColors.current
}

@Composable
fun AppTheme(
    localSettingManager: LocalSettingManager = getKoin().get(),
    content: @Composable () -> Unit
) {
    val localSettingState = localSettingManager.localSettingState.collectAsState()
    val theme by remember {
        derivedStateOf {
            localSettingState.value.theme
        }
    }
    val isDark = when (theme) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }
    CompositionLocalProvider(
        LocalExtendedColors provides if (isDark) extendedDark else extendedLight,
        content = content
    )
}
