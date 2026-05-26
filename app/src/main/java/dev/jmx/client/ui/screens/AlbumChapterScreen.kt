package dev.jmx.client.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jmx.client.ui.components.CommonScaffold
import dev.jmx.client.ui.glass.LocalJmxGlassPalette
import dev.jmx.client.ui.razor.RazorText
import dev.jmx.client.ui.viewModel.AlbumDetailViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
fun AlbumChapterScreen(
    albumDetailViewModel: AlbumDetailViewModel = koinActivityViewModel(),
) {
    val palette = LocalJmxGlassPalette.current
    val albumDetailState by albumDetailViewModel.albumDetailState.collectAsState()
    val albumChapterList = albumDetailState.data?.albumChapterList.orEmpty()
    val mainNavController = LocalMainNavController.current

    CommonScaffold(title = "选择章节") {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            columns = GridCells.Fixed(3)
        ) {
            itemsIndexed(albumChapterList, key = { _, item -> item.id }) { index, item ->
                RazorText(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(palette.contentSurface.copy(alpha = 0.76f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { mainNavController.navigate("albumRead/${item.id}") }
                        )
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    text = item.name.ifBlank { "第 ${index + 1} 话" },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        color = palette.primaryText,
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}
