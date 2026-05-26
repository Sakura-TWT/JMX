package dev.jmx.client.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.jmx.client.utils.shimmer

// 根据 index 给出不同的宽度，模拟真实 Tab 的长短不一
private val widths = listOf(40.dp, 60.dp, 55.dp, 45.dp, 50.dp)

@Composable
fun FilterItemSkeleton(index: Int) {
    val currentWidth = widths[index % widths.size]
    Box(
        modifier = Modifier
            .padding(vertical = 5.dp)
            .width(currentWidth)
            .height(18.dp)
            .clip(RoundedCornerShape(6.dp))
            .shimmer()
    )
}
