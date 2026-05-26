package dev.jmx.client.ui.screens.readScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jmx.client.ui.components.DashedHorizontalDivider
import dev.jmx.client.ui.components.DashedVerticalDivider
import dev.jmx.client.ui.razor.RazorIcon
import dev.jmx.client.ui.razor.RazorText

@Composable
private fun IconText(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    text: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RazorIcon(
            imageVector = imageVector,
            contentDescription = "",
            tint = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        RazorText(
            text = text,
            style = TextStyle(
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
fun Tip(
    modifier: Modifier = Modifier,
    readMode: String? = "scroll",
) {
    if (readMode == "scroll") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = .7f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {
                    }
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            IconText(
                imageVector = Icons.Default.ChevronLeft,
                text = "上一页",
            )
            DashedHorizontalDivider(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(.8f)
            )
            IconText(
                imageVector = Icons.Default.Settings,
                text = "工具栏",
            )
            DashedHorizontalDivider(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(.8f)
            )
            IconText(
                imageVector = Icons.Default.ChevronRight,
                text = "下一页",
            )
        }
    } else {
        Row(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = .7f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {
                    }
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconText(
                imageVector = Icons.Default.ChevronLeft,
                text = "上一页",
            )
            DashedVerticalDivider(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxHeight(.8f)
            )
            IconText(
                imageVector = Icons.Default.Settings,
                text = "工具栏",
            )
            DashedVerticalDivider(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxHeight(.8f)
            )
            IconText(
                imageVector = Icons.Default.ChevronRight,
                text = "下一页",
            )
        }
    }
}
