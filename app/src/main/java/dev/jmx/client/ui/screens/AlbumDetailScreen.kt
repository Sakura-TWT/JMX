package dev.jmx.client.ui.screens

import android.net.Uri

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import dev.jmx.client.data.models.Album
import dev.jmx.client.store.DownloadManager
import dev.jmx.client.ui.components.AlbumContentTag
import dev.jmx.client.ui.components.AlbumCoverImage
import dev.jmx.client.ui.components.AlbumRoleTag
import dev.jmx.client.ui.components.AlbumWorkTag
import dev.jmx.client.ui.components.CommonScaffold
import dev.jmx.client.ui.glass.LocalJmxGlassPalette
import dev.jmx.client.ui.razor.AppleSection
import dev.jmx.client.ui.razor.RazorGlassButton
import dev.jmx.client.ui.razor.RazorIcon
import dev.jmx.client.ui.razor.RazorText
import dev.jmx.client.ui.viewModel.AlbumDetailViewModel
import dev.jmx.client.utils.shimmer
import org.koin.compose.getKoin
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
private fun DetailMetricPill(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
) {
    val palette = LocalJmxGlassPalette.current
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(palette.contentSurface.copy(alpha = 0.70f))
            .padding(horizontal = 10.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(palette.accent.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            RazorIcon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(18.dp),
                tint = palette.accent
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            RazorText(
                text = label,
                style = TextStyle(
                    color = palette.tertiaryText,
                    fontSize = 11.sp,
                    lineHeight = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            )
            RazorText(
                text = value,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    color = palette.primaryText,
                    fontSize = 15.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
private fun DetailMetaChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalJmxGlassPalette.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.76f, stiffness = Spring.StiffnessMedium),
        label = "detailMetaChipPress"
    )
    RazorText(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(999.dp))
            .background(palette.contentSurface.copy(alpha = 0.76f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = TextStyle(
            color = palette.secondaryText,
            fontSize = 12.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    )
}

@Composable
private fun DetailActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    selected: Boolean = false,
    tint: Color = if (selected) LocalJmxGlassPalette.current.accent else LocalJmxGlassPalette.current.primaryText,
    onClick: () -> Unit,
) {
    val palette = LocalJmxGlassPalette.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "detailActionPress"
    )
    Column(
        modifier = modifier
            .height(54.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(18.dp))
            .background(palette.contentSurface.copy(alpha = if (selected) 0.88f else 0.62f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        RazorIcon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            tint = tint
        )
        RazorText(
            text = label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                color = palette.secondaryText,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun DetailPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalJmxGlassPalette.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "detailPrimaryButtonPress"
    )
    Box(
        modifier = modifier
            .height(50.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(999.dp))
            .background(palette.accent)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        RazorText(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                color = Color.White,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
private fun AlbumDetailSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(
                modifier = Modifier
                    .width(128.dp)
                    .aspectRatio(3f / 4f)
                    .clip(RoundedCornerShape(22.dp))
                    .shimmer()
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .shimmer()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .height(22.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .shimmer()
                )
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .shimmer()
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(74.dp)
                .clip(RoundedCornerShape(22.dp))
                .shimmer()
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailHero(
    album: Album,
    onAuthorClick: (String) -> Unit,
) {
    val palette = LocalJmxGlassPalette.current
    AppleSection {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                AlbumCoverImage(
                    album = album,
                    modifier = Modifier.width(132.dp),
                    showIdChip = false
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RazorText(
                        text = album.name,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            color = palette.primaryText,
                            fontSize = 21.sp,
                            lineHeight = 1.22.em,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.35).sp
                        )
                    )
                    RazorText(
                        text = "JM${album.id}",
                        style = TextStyle(
                            color = palette.tertiaryText,
                            fontSize = 12.sp,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    if (album.authorList.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(7.dp),
                            verticalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            album.authorList.take(4).forEach {
                                key(it) {
                                    DetailMetaChip(
                                        text = it,
                                        onClick = { onAuthorClick(it) }
                                    )
                                }
                            }
                        }
                    }
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                        verticalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        DetailMetaChip(text = "${album.albumChapterList.size.coerceAtLeast(1)} 话", onClick = {})
                        if (album.isBuy) {
                            DetailMetaChip(text = "已购买", onClick = {})
                        }
                        if (album.price > 0) {
                            DetailMetaChip(text = "${album.price} 币", onClick = {})
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DetailMetricPill(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Favorite,
                    label = "喜欢",
                    value = album.likeCount.toString()
                )
                DetailMetricPill(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.RemoveRedEye,
                    label = "浏览",
                    value = album.readCount.toString()
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailTagSection(album: Album) {
    val palette = LocalJmxGlassPalette.current
    if (album.workList.isEmpty() && album.roleList.isEmpty() && album.tagList.isEmpty()) return

    AppleSection {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            RazorText(
                text = "标签",
                style = TextStyle(
                    color = palette.primaryText,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                album.workList.forEach { key("work-$it") { AlbumWorkTag(it) } }
                album.roleList.forEach { key("role-$it") { AlbumRoleTag(it) } }
                album.tagList.forEach { key("tag-$it") { AlbumContentTag(it) } }
            }
        }
    }
}

@Composable
private fun DetailBottomBar(
    album: Album,
    onLike: () -> Unit,
    onCollect: () -> Unit,
    onComment: () -> Unit,
    onRelate: () -> Unit,
    onDownload: () -> Unit,
    onChapter: () -> Unit,
    onRead: () -> Unit,
) {
    val palette = LocalJmxGlassPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(palette.page.copy(alpha = 0.90f))
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DetailActionButton(
                modifier = Modifier.weight(1f),
                icon = if (album.isLike) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                label = "喜欢",
                selected = album.isLike,
                tint = if (album.isLike) Color(0xFFFF3B30) else palette.primaryText,
                onClick = onLike
            )
            DetailActionButton(
                modifier = Modifier.weight(1f),
                icon = if (album.isCollect) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                label = "收藏",
                selected = album.isCollect,
                tint = if (album.isCollect) Color(0xFFFFCC00) else palette.primaryText,
                onClick = onCollect
            )
            DetailActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.AutoMirrored.Outlined.Message,
                label = "评论",
                onClick = onComment
            )
            DetailActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.AutoAwesome,
                label = "相关",
                onClick = onRelate
            )
            DetailActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Download,
                label = "下载",
                onClick = onDownload
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (album.albumChapterList.isNotEmpty()) {
                RazorGlassButton(
                    modifier = Modifier.weight(0.38f),
                    onClick = onChapter
                ) {
                    RazorText(
                        text = "章节",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            color = palette.primaryText,
                            fontSize = 15.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
            DetailPrimaryButton(
                modifier = Modifier.weight(0.62f),
                text = if (album.albumChapterList.isEmpty()) "开始阅读" else "从第 1 话开始",
                onClick = onRead
            )
        }
    }
}

@Composable
fun AlbumDetailScreen(
    id: Int,
    albumDetailViewModel: AlbumDetailViewModel = koinActivityViewModel(),
    downloadManager: DownloadManager = getKoin().get()
) {
    val mainNavController = LocalMainNavController.current
    val palette = LocalJmxGlassPalette.current
    val scrollState = rememberScrollState()
    val albumDetailState by albumDetailViewModel.albumDetailState.collectAsState()

    LaunchedEffect(Unit) {
        if (albumDetailState.data != null) {
            return@LaunchedEffect
        }
        albumDetailViewModel.getAlbumDetail(id)
    }

    CommonScaffold(
        title = "详情",
        bottomBar = {
            albumDetailState.data?.let { album ->
                DetailBottomBar(
                    album = album,
                    onLike = {
                        if (!album.isLike) albumDetailViewModel.likeAlbum(album.id)
                    },
                    onCollect = {
                        if (album.isCollect) {
                            albumDetailViewModel.unCollect(album.id)
                        } else {
                            albumDetailViewModel.collect(album.id)
                        }
                    },
                    onComment = { mainNavController.navigate("comment/${album.id}") },
                    onRelate = { mainNavController.navigate("albumRelate/${album.id}") },
                    onDownload = { downloadManager.downloadAlbum(album) },
                    onChapter = { mainNavController.navigate("albumChapter/${album.id}") },
                    onRead = { mainNavController.navigate("albumRead/${album.id}") }
                )
            }
        }
    ) {
        if (albumDetailState.isLoading && albumDetailState.data == null) {
            AlbumDetailSkeleton()
            return@CommonScaffold
        }

        albumDetailState.data?.let { album ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 168.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                DetailHero(
                    album = album,
                    onAuthorClick = { mainNavController.navigate("albumSearchResult/${Uri.encode(it)}") }
                )
                DetailTagSection(album = album)
                if (album.description.isNotBlank()) {
                    AppleSection {
                        RazorText(
                            text = album.description,
                            modifier = Modifier.padding(14.dp),
                            style = TextStyle(
                                color = palette.secondaryText,
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
