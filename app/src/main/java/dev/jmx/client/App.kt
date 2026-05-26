package dev.jmx.client

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jmx.client.cache.trimAppCaches
import dev.jmx.client.store.ToastManager
import dev.jmx.client.ui.glass.LocalJmxGlassPalette
import dev.jmx.client.ui.screens.AppScreen
import dev.jmx.client.ui.razor.RazorText
import dev.jmx.client.ui.viewModel.GlobalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.koin.compose.getKoin
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
private fun RazorToastHost(message: String?) {
    val palette = LocalJmxGlassPalette.current
    AnimatedVisibility(
        visible = message != null,
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding()
            .padding(start = 22.dp, end = 22.dp, bottom = 90.dp),
        enter = slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(durationMillis = 220)
        ) + fadeIn(animationSpec = tween(durationMillis = 180)),
        exit = slideOutVertically(
            targetOffsetY = { it / 2 },
            animationSpec = tween(durationMillis = 180)
        ) + fadeOut(animationSpec = tween(durationMillis = 150))
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(palette.primaryText.copy(alpha = 0.90f))
                .padding(horizontal = 18.dp, vertical = 13.dp),
            contentAlignment = Alignment.Center
        ) {
            RazorText(
                text = message.orEmpty(),
                style = TextStyle(
                    color = palette.page,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
fun App(
    globalViewModel: GlobalViewModel = koinActivityViewModel(),
    toastManager: ToastManager = getKoin().get()
) {
    var toastMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        globalViewModel.init()
    }
    LaunchedEffect(context) {
        withContext(Dispatchers.IO) {
            trimAppCaches(context.applicationContext)
        }
    }
    LaunchedEffect(Unit) {
        toastManager.message.collect { text ->
            toastMessage = text
            delay(2400)
            if (toastMessage == text) {
                toastMessage = null
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        AppScreen()
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            RazorToastHost(message = toastMessage)
        }
    }
}
