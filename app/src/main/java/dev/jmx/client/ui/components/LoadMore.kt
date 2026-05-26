package dev.jmx.client.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jmx.client.ui.glass.LocalJmxGlassPalette
import dev.jmx.client.ui.razor.RazorLoadingRow
import dev.jmx.client.ui.razor.RazorText

@Composable
fun LoadMore(isLoading: Boolean, hasMore: Boolean) {
    val palette = LocalJmxGlassPalette.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> RazorLoadingRow("加载中...")
            hasMore -> RazorText(
                text = "上拉加载更多",
                style = TextStyle(
                    color = palette.secondaryText,
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )
            else -> RazorText(
                text = "没有更多内容了",
                style = TextStyle(
                    color = palette.tertiaryText,
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}
