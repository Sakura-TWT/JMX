package dev.jmx.client.ui.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.delay

@Composable
fun NavigationInputBlocker(
    navController: NavHostController,
    blockMillis: Long = 360L,
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    var blockUntil by remember { mutableLongStateOf(0L) }
    var now by remember { mutableLongStateOf(0L) }

    LaunchedEffect(backStackEntry) {
        blockUntil = System.currentTimeMillis() + blockMillis
        while (true) {
            val current = System.currentTimeMillis()
            now = current
            if (current >= blockUntil) {
                break
            }
            delay(16L)
        }
    }

    if (now < blockUntil) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(blockUntil) {
                    awaitEachGesture {
                        do {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            event.changes.forEach { it.consume() }
                        } while (event.changes.any { it.pressed })
                    }
                }
        )
    }
}
