package dev.jmx.client.ui.screens.tabScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.jmx.client.ui.glass.GlassActionButton
import dev.jmx.client.ui.glass.GlassTopBar
import dev.jmx.client.ui.razor.RazorIcon
import dev.jmx.client.ui.screens.LocalMainNavController

@Composable
private fun HomeTopBarComponent() {
    val mainNavController = LocalMainNavController.current
    GlassTopBar(
        title = "JMX",
        subtitle = "今日漫画精选"
    ) {
        GlassActionButton(onClick = { mainNavController.navigate("albumRecommend") }) {
            RazorIcon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "每周推荐"
            )
        }
        GlassActionButton(onClick = { mainNavController.navigate("albumSearch") }) {
            RazorIcon(
                imageVector = Icons.Default.Search,
                contentDescription = "搜索"
            )
        }
    }
}

@Composable
private fun AboutTopBarComponent() {
    GlassTopBar(
        title = "About",
        subtitle = "Liquid Glass Client"
    )
}

@Composable
private fun UserTopBarComponent() {
    val mainNavController = LocalMainNavController.current
    GlassTopBar(
        title = "Profile",
        subtitle = "账户与偏好"
    ) {
        GlassActionButton(onClick = { mainNavController.navigate("appLocalSetting") }) {
            RazorIcon(
                imageVector = Icons.Default.Settings,
                contentDescription = "设置"
            )
        }
        GlassActionButton(onClick = { mainNavController.navigate("download") }) {
            RazorIcon(
                imageVector = Icons.Default.Download,
                contentDescription = "下载"
            )
        }
    }
}

@Composable
fun TopBarComponent() {
    val tabNavController = LocalTabNavController.current
    val backStackEntryState by tabNavController.currentBackStackEntryAsState()
    when (backStackEntryState?.destination?.route) {
        "home" -> HomeTopBarComponent()
        "about" -> AboutTopBarComponent()
        "user" -> UserTopBarComponent()
    }
}
