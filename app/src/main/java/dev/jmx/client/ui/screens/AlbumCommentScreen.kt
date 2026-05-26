package dev.jmx.client.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Reply
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import dev.jmx.client.data.models.Comment
import dev.jmx.client.ui.components.Comment
import dev.jmx.client.ui.components.CommentSkeleton
import dev.jmx.client.ui.components.CommonScaffold
import dev.jmx.client.ui.components.PullRefreshAndLoadMoreGrid
import dev.jmx.client.ui.glass.LocalJmxGlassPalette
import dev.jmx.client.ui.razor.AppleIconButton
import dev.jmx.client.ui.razor.RazorIcon
import dev.jmx.client.ui.razor.RazorLoadingIndicator
import dev.jmx.client.ui.razor.RazorText
import dev.jmx.client.ui.viewModel.AlbumDetailViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
private fun CommentListSkeleton() {
    FlowRow(
        modifier = Modifier.padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        for (i in 0 until 10) {
            key(i) {
                CommentSkeleton()
            }
        }
    }
}

@Composable
private fun ReplyComment(comment: Comment) {
    val palette = LocalJmxGlassPalette.current
    val annotatedString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.SemiBold,
                color = palette.primaryText
            )
        ) {
            append(comment.username)
        }
        append(": ")
        append(AnnotatedString.fromHtml(htmlString = comment.content).trim())
    }
    RazorText(
        text = annotatedString,
        style = TextStyle(
            color = palette.secondaryText,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            fontWeight = FontWeight.Normal
        )
    )
}

@Composable
private fun CommentAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
) {
    val palette = LocalJmxGlassPalette.current
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RazorIcon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = palette.secondaryText
        )
        RazorText(
            text = text,
            style = TextStyle(
                color = palette.secondaryText,
                fontSize = 12.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun CommentWithAction(comment: Comment, onReply: (() -> Unit)? = null) {
    val palette = LocalJmxGlassPalette.current
    Comment(comment) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CommentAction(
                    icon = Icons.AutoMirrored.Outlined.Reply,
                    text = "回复",
                    onClick = { onReply?.invoke() }
                )
                Spacer(modifier = Modifier.weight(1f))
                CommentAction(
                    icon = Icons.Default.ThumbUpOffAlt,
                    text = comment.likeCount.toString(),
                    onClick = {}
                )
            }
            if (comment.replyCommentList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(palette.contentSurface.copy(alpha = 0.72f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    comment.replyCommentList.forEach {
                        key(it.id) {
                            ReplyComment(it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlbumCommentScreen(
    albumId: Int,
    albumDetailViewModel: AlbumDetailViewModel = koinActivityViewModel(),
) {
    val focusManager = LocalFocusManager.current
    val palette = LocalJmxGlassPalette.current
    val commentInputFocusRequester = remember { FocusRequester() }
    val commentLazyPagingItems = albumDetailViewModel.commentPager.collectAsLazyPagingItems()
    var replyComment by remember { mutableStateOf<Comment?>(null) }
    var commentText by remember { mutableStateOf("") }
    val commentAlbumState by albumDetailViewModel.commentAlbumState.collectAsState()

    LaunchedEffect(Unit) {
        albumDetailViewModel.changeCommentAlbumId(albumId)
    }

    fun sendComment() {
        val text = commentText.trim()
        if (text.isBlank() || commentAlbumState.isLoading) return
        albumDetailViewModel.comment(text, albumId, replyComment?.id) {
            commentText = ""
            replyComment = null
            focusManager.clearFocus()
            commentLazyPagingItems.refresh()
        }
    }

    CommonScaffold(
        title = "评论",
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.page.copy(alpha = 0.94f))
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                replyComment?.let {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(palette.contentSurface.copy(alpha = 0.74f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RazorText(
                            text = "回复 ${it.username}",
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(
                                color = palette.secondaryText,
                                fontSize = 13.sp,
                                lineHeight = 17.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        AppleIconButton(
                            imageVector = Icons.Default.Close,
                            contentDescription = "取消回复",
                            modifier = Modifier.size(28.dp),
                            onClick = { replyComment = null },
                            tint = palette.secondaryText
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(palette.contentSurface.copy(alpha = 0.78f))
                        .padding(start = 16.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        BasicTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(commentInputFocusRequester),
                            textStyle = TextStyle(
                                color = palette.primaryText,
                                fontSize = 15.sp,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(
                                onSend = { sendComment() }
                            ),
                            interactionSource = remember { MutableInteractionSource() }
                        )
                        if (commentText.isEmpty()) {
                            RazorText(
                                text = if (replyComment == null) "写下你的想法" else "回复 ${replyComment!!.username}",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = TextStyle(
                                    color = palette.tertiaryText,
                                    fontSize = 15.sp,
                                    lineHeight = 20.sp
                                )
                            )
                        }
                    }
                    AppleIconButton(
                        imageVector = Icons.AutoMirrored.Outlined.Send,
                        contentDescription = "发送",
                        selected = true,
                        onClick = { sendComment() }
                    )
                    if (commentAlbumState.isLoading) {
                        RazorLoadingIndicator(modifier = Modifier.size(22.dp))
                    }
                }
            }
        }
    ) {
        if (commentLazyPagingItems.loadState.refresh is LoadState.Loading && commentLazyPagingItems.itemCount == 0) {
            CommentListSkeleton()
            return@CommonScaffold
        }
        PullRefreshAndLoadMoreGrid(
            lazyPagingItems = commentLazyPagingItems,
            key = { it.id },
            columns = GridCells.Fixed(1)
        ) {
            CommentWithAction(it) {
                focusManager.clearFocus()
                commentInputFocusRequester.requestFocus()
                replyComment = it
            }
        }
    }
}
