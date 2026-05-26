package dev.jmx.client.ui.screens.readScreen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jmx.client.ui.razor.RazorGlassButton
import dev.jmx.client.ui.razor.RazorIcon
import dev.jmx.client.ui.razor.RazorText

@Composable
fun TipCloseButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    RazorGlassButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RazorIcon(
                imageVector = Icons.Default.Check,
                contentDescription = "",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(4.dp))
            RazorText(
                text = "知道了",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}
