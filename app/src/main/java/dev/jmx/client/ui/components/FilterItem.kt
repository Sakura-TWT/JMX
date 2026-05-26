package dev.jmx.client.ui.components

import androidx.compose.runtime.Composable
import dev.jmx.client.ui.razor.RazorChip

@Composable
fun FilterItem(
    label: String,
    active: Boolean,
    onClick: (() -> Unit) = {}
) {
    RazorChip(
        label = label,
        selected = active,
        onClick = onClick
    )
}
