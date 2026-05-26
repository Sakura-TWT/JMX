package dev.jmx.client.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import dev.jmx.client.data.models.Album
import dev.jmx.client.store.RemoteSettingManager
import dev.jmx.client.ui.razor.RazorChip
import org.koin.compose.getKoin

@Composable
fun AlbumCoverImage(
    album: Album,
    modifier: Modifier = Modifier,
    showIdChip: Boolean = false,
    remoteSettingManager: RemoteSettingManager = getKoin().get(),
    imageLoader: ImageLoader = getKoin().get()
) {
    val context = LocalContext.current
    val remoteSetting by remoteSettingManager.remoteSettingState.collectAsState()
    Box(modifier = modifier) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("${remoteSetting.imgHost}/media/albums/${album.id}_3x4.jpg")
                .size(if (showIdChip) Size.ORIGINAL else Size(360, 480))
                .crossfade(false)
                .build(),
//            model = "https://i0.hdslb.com/bfs/manga-static/c62668e300b5212fe5504f6fa9b4b5c630f8ebeb.jpg@310w.avif",
            imageLoader = imageLoader,
            contentDescription = "${album.name}的封面",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(3f / 4f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp)),
        )
        if (showIdChip) {
            RazorChip(
                label = "JM${album.id}",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 10.dp, top = 10.dp),
                onClick = {}
            )
        }
    }
}
