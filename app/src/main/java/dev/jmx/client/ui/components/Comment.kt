package dev.jmx.client.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import dev.jmx.client.data.models.Comment
import dev.jmx.client.ui.glass.LocalJmxGlassPalette
import dev.jmx.client.ui.razor.RazorText
import dev.jmx.client.store.RemoteSettingManager
import org.koin.compose.getKoin

@Composable
fun Comment(
    comment: Comment,
    remoteSettingManager: RemoteSettingManager = getKoin().get(),
    action: (@Composable () -> Unit)? = null
) {
    val remoteSetting by remoteSettingManager.remoteSettingState.collectAsState()
    val palette = LocalJmxGlassPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        AsyncImage(
            model = "${remoteSetting.imgHost}/media/users/${comment.avatar}",
            contentDescription = "${comment.nickname}的头像",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
        )
        Column(
            modifier = Modifier.weight(1f),
        ) {
            RazorText(
                text = comment.nickname,
                style = TextStyle(
                    color = palette.primaryText,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            RazorText(
                text = comment.time,
                style = TextStyle(
                    color = palette.secondaryText,
                    fontSize = 12.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            val content = buildAnnotatedString {
                append(
                    AnnotatedString.fromHtml(
                        htmlString = comment.content,
                    ).trim()
                )
            }
            RazorText(
                text = content,
                style = TextStyle(
                    color = palette.primaryText,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Normal
                )
            )
            action?.let {
                Spacer(modifier = Modifier.height(6.dp))
                it.invoke()
            }
        }
    }
}
