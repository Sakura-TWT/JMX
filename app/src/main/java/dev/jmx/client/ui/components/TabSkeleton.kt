package dev.jmx.client.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.jmx.client.utils.shimmer

private val widths = listOf(90.dp, 80.dp, 86.dp, 74.dp, 96.dp)

@Composable
fun TabSkeleton(index: Int) {
    Box(
        modifier = Modifier
            .width(widths[index % widths.size])
            .height(22.dp)
            .clip(RoundedCornerShape(999.dp))
            .shimmer()
    )
}
