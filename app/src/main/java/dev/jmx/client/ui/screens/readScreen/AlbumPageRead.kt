package dev.jmx.client.ui.screens.readScreen

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.jmx.client.ui.components.AlbumImageImage
import dev.jmx.client.ui.viewModel.AlbumReadViewModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.max
import kotlin.math.min

@Composable
fun AlbumPageRead(
    pagerState: PagerState,
    albumReadViewModel: AlbumReadViewModel = koinViewModel(),
    onUpdateSliderValue: (value: Float) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var currentIndexState by albumReadViewModel.currentIndexState
    val albumImageState by albumReadViewModel.albumImageState.collectAsState()
    val list = albumImageState.data ?: listOf()
    val context = LocalContext.current

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.isScrollInProgress }
            .filter { it }
            .collect {
                albumReadViewModel.hideToolBar()
            }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect {
                if (currentIndexState != it) {
                    currentIndexState = it
                    onUpdateSliderValue(it.toFloat())
                    albumReadViewModel.decodeIndex(currentIndexState, context)
                }
            }
    }

    HorizontalPager(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        // 1. 在 Initial 阶段观察按下，不消耗事件，确保 Pager 能收到
                        val down =
                            awaitFirstDown(
                                requireUnconsumed = false,
                                pass = PointerEventPass.Initial
                            )
                        // 2. 等待抬起
                        val up = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        // 3. 判定逻辑：只有在没被消费（说明不是滑动）且距离很短时触发
                        if (up != null && !up.isConsumed) {
                            val distance = (up.position - down.position).getDistance()
                            if (distance < 10.dp.toPx()) {
                                // --- 获取点击位置 ---
                                val screenWidth = size.width
                                val clickX = up.position.x

                                when {
                                    clickX < screenWidth / 3 -> {
                                        albumReadViewModel.hideToolBar()
                                        val targetIndex = max(0, currentIndexState - 1)
                                        currentIndexState = targetIndex
                                        albumReadViewModel.decodeIndex(targetIndex, context)
                                        coroutineScope.launch {
                                            pagerState.scrollToPage(targetIndex)
                                            onUpdateSliderValue(targetIndex.toFloat())
                                        }
                                    }

                                    clickX > screenWidth * 2 / 3 -> {
                                        albumReadViewModel.hideToolBar()
                                        val targetIndex = min(list.lastIndex, currentIndexState + 1)
                                        currentIndexState = targetIndex
                                        albumReadViewModel.decodeIndex(targetIndex, context)
                                        coroutineScope.launch {
                                            pagerState.scrollToPage(targetIndex)
                                            onUpdateSliderValue(targetIndex.toFloat())
                                        }
                                    }

                                    else -> {
                                        albumReadViewModel.triggerToolBar()
                                    }
                                }
                            }
                        }
                    }
                }
            },
        state = pagerState
    ) { page ->
        val item = list[page]
        AlbumImageImage(
            albumImageImageState = item,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}
