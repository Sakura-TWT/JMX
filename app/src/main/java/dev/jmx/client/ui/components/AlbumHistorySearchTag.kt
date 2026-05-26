package dev.jmx.client.ui.components

import androidx.compose.runtime.Composable
import dev.jmx.client.ui.razor.RazorChip

@Composable
fun AlbumSearchHistoryTag(label: String, onClick: () -> Unit = {}) {
    RazorChip(label = label, onClick = onClick)
}
