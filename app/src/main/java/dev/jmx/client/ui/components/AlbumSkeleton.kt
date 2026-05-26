package dev.jmx.client.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.jmx.client.ui.glass.LocalJmxGlassPalette
import dev.jmx.client.utils.shimmer

@Composable
fun AlbumSkeleton(
    modifier: Modifier = Modifier
) {
    val palette = LocalJmxGlassPalette.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(palette.contentSurface.copy(alpha = 0.42f))
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.75f)
                .clip(RoundedCornerShape(18.dp))
                .shimmer()
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 3.dp, vertical = 3.dp)
                .fillMaxWidth(0.82f)
                .height(14.dp)
                .clip(RoundedCornerShape(999.dp))
                .shimmer()
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 3.dp)
                .padding(bottom = 8.dp)
                .fillMaxWidth(0.52f)
                .height(12.dp)
                .clip(RoundedCornerShape(999.dp))
                .shimmer()
        )
    }
}
