package dev.jmx.client.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jmx.client.data.models.AlbumImageImageState
import dev.jmx.client.data.models.ImageResultState
import dev.jmx.client.ui.glass.LocalJmxGlassPalette
import dev.jmx.client.ui.razor.RazorGlassButton
import dev.jmx.client.ui.razor.RazorLoadingIndicator
import dev.jmx.client.ui.razor.RazorText
import kotlinx.coroutines.launch

@Composable
fun AlbumImageImage(
    modifier: Modifier = Modifier,
    albumImageImageState: AlbumImageImageState,
    contentScale: ContentScale = ContentScale.FillBounds
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val imageResult = albumImageImageState.imageResultState
    val palette = LocalJmxGlassPalette.current

    val retryImageDecode = {
        coroutineScope.launch {
            albumImageImageState.decode(context)
        }
    }

    Box(modifier = modifier) {
        when (imageResult) {
            is ImageResultState.Loading -> {
                RazorLoadingIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is ImageResultState.Failure -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    RazorText(
                        text = imageResult.reason,
                        style = TextStyle(
                            color = palette.secondaryText,
                            fontSize = 13.sp,
                            lineHeight = 17.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    RazorGlassButton(
                        onClick = {
                            retryImageDecode()
                        }
                    ) {
                        RazorText(
                            text = "重试",
                            style = TextStyle(
                                color = palette.primaryText,
                                fontSize = 14.sp,
                                lineHeight = 17.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            is ImageResultState.Success -> {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = contentScale,
                    bitmap = imageResult.decodeImageBitmap,
                    contentDescription = "第${albumImageImageState.index}张图片",
                )
            }

        }
    }
}
