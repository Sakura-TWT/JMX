package dev.jmx.client.ui.screens.tabScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.jmx.client.ui.glass.LocalJmxBackdrop
import dev.jmx.client.ui.glass.LocalJmxGlassPalette
import dev.jmx.client.ui.glass.JmxGlassStage
import dev.jmx.client.ui.glass.rememberJmxGlassPalette
import dev.jmx.client.ui.glass.jmxContentBackdrop
import dev.jmx.client.ui.glass.jmxProgressiveTopBlur
import dev.jmx.client.ui.screens.AboutScreen
import dev.jmx.client.ui.screens.HomeCategoryOverlayState
import dev.jmx.client.ui.screens.HomeFloatingCategoryPanel
import dev.jmx.client.ui.screens.HomeScreen
import dev.jmx.client.ui.screens.UserScreen

@Composable
fun TabScreen(tabName: String) {
    val tabNavController = rememberNavController()
    val palette = rememberJmxGlassPalette()
    var homeOverlayState by remember { mutableStateOf<HomeCategoryOverlayState?>(null) }
    val backStackEntryState by tabNavController.currentBackStackEntryAsState()
    val currentRoute = backStackEntryState?.destination?.route ?: tabName
    val shouldShowHomeOverlay = currentRoute == "home" && homeOverlayState != null
    JmxGlassStage {
        CompositionLocalProvider(
            LocalTabNavController provides tabNavController,
            LocalJmxGlassPalette provides palette,
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(palette.page)
                        .jmxContentBackdrop(it)
                ) {
                    NavHost(
                        modifier = Modifier.fillMaxSize(),
                        navController = tabNavController,
                        startDestination = tabName
                    ) {
                        composable("home") {
                            HomeScreen(onOverlayStateChange = { homeOverlayState = it })
                        }
                        composable("about") {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 128.dp)
                            ) {
                                AboutScreen()
                            }
                        }
                        composable("user") {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 128.dp)
                            ) {
                                UserScreen()
                            }
                        }
                    }
                }
                CompositionLocalProvider(LocalJmxBackdrop provides it) {
                    if (shouldShowHomeOverlay) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(330.dp)
                                    .jmxProgressiveTopBlur(
                                        backdrop = it,
                                        blurRadius = 20.dp,
                                        solidFraction = 0.42f,
                                        tintColor = palette.page,
                                        tintTopAlpha = 0.10f
                                    )
                            )
                        }
                    }
                    if (shouldShowHomeOverlay) {
                        val overlay = homeOverlayState ?: return@CompositionLocalProvider
                        HomeFloatingCategoryPanel(
                            overlayState = overlay,
                            modifier = Modifier.padding(top = 124.dp)
                        )
                    }
                    TopBarComponent()
                    BottomNavigationBarComponent(
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

val LocalTabNavController = staticCompositionLocalOf<NavHostController> {
    error("none")
}
