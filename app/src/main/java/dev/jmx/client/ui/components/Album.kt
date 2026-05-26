package dev.jmx.client.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jmx.client.data.models.Album
import dev.jmx.client.ui.glass.LocalJmxGlassPalette
import dev.jmx.client.ui.razor.RazorText
import dev.jmx.client.ui.screens.LocalMainNavController
import dev.jmx.client.ui.viewModel.AlbumDetailViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
fun Album(
    album: Album,
    modifier: Modifier = Modifier,
    albumDetailViewModel: AlbumDetailViewModel = koinActivityViewModel()
) {
    val mainNavController = LocalMainNavController.current
    val palette = LocalJmxGlassPalette.current

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(palette.contentSurface.copy(alpha = 0.44f))
            .clickable(
                interactionSource = null,
                indication = null
            ) {
                albumDetailViewModel.reset(album.id)
                mainNavController.navigate("albumDetail/${album.id}")
            }
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AlbumCoverImage(album)
        RazorText(
            modifier = Modifier.padding(horizontal = 3.dp),
            text = album.name,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            style = TextStyle(
                color = palette.primaryText,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.1).sp
            )
        )
        RazorText(
            modifier = Modifier
                .padding(horizontal = 3.dp)
                .padding(bottom = 4.dp),
            text = album.authorList.joinToString(",").ifBlank { "暂无作者" },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                color = palette.secondaryText,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
