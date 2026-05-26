package dev.jmx.client.ui.components

import android.net.Uri
import androidx.compose.runtime.Composable
import dev.jmx.client.ui.razor.RazorChip
import dev.jmx.client.ui.screens.LocalMainNavController

@Composable
fun AlbumWorkTag(label: String) {
    val mainNavController = LocalMainNavController.current
    RazorChip(
        label = label,
        onClick = { mainNavController.navigate("albumSearchResult/${Uri.encode(label)}") }
    )
}
