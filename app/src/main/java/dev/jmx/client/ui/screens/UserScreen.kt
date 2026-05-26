package dev.jmx.client.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Savings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import dev.jmx.client.R
import dev.jmx.client.store.RemoteSettingManager
import dev.jmx.client.store.UserManager
import dev.jmx.client.ui.glass.GlassPanel
import dev.jmx.client.ui.glass.LocalJmxGlassPalette
import dev.jmx.client.ui.razor.RazorGlassButton
import dev.jmx.client.ui.razor.RazorLoadingRow
import dev.jmx.client.ui.razor.RazorText
import dev.jmx.client.ui.viewModel.UserViewModel
import org.koin.compose.getKoin
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
private fun RazorProfileMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit = {}
) {
    val palette = LocalJmxGlassPalette.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else 1f,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMedium),
        label = "profileMenuPress"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(22.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(25.dp),
            colorFilter = ColorFilter.tint(palette.primaryText.copy(alpha = 0.82f))
        )
        RazorText(
            text = label,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                color = palette.primaryText,
                fontSize = 17.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Medium
            )
        )
        Image(
            painter = painterResource(R.drawable.chevron_right_icon),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            colorFilter = ColorFilter.tint(palette.tertiaryText)
        )
    }
}

@Composable
private fun RazorProfileDataTile(
    icon: ImageVector,
    value: String,
    modifier: Modifier = Modifier
) {
    val palette = LocalJmxGlassPalette.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Image(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(23.dp),
            colorFilter = ColorFilter.tint(palette.accent.copy(alpha = 0.86f))
        )
        RazorText(
            text = value,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
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
private fun RazorProfileDivider() {
    val palette = LocalJmxGlassPalette.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 3.dp)
            .height(1.dp)
            .background(palette.tertiaryText.copy(alpha = 0.16f))
    )
}

@Composable
fun UserScreen(
    userManager: UserManager = getKoin().get(),
    remoteSettingManager: RemoteSettingManager = getKoin().get(),
    userViewModel: UserViewModel = koinActivityViewModel()
) {
    val userState by userManager.userState.collectAsState()
    val isLogin by userManager.isLoginState.collectAsState(false)
    val remoteSetting by remoteSettingManager.remoteSettingState.collectAsState()
    val mainNavController = LocalMainNavController.current
    val palette = LocalJmxGlassPalette.current

    fun checkLoginThenDo(onDo: () -> Unit) {
        if (!isLogin) {
            mainNavController.navigate("login")
            return
        }
        onDo()
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 8 })
        ) {
            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 36.dp
            ) {
                if (isLogin && userState.data != null) {
                    val user = userState.data!!
                    Row(
                        modifier = Modifier.padding(18.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(9.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = "${remoteSetting.imgHost}/media/users/${user.avatar}",
                                contentDescription = user.username,
                                modifier = Modifier
                                    .size(88.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            RazorText(
                                text = user.username,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = TextStyle(
                                    color = palette.primaryText,
                                    fontSize = 14.sp,
                                    lineHeight = 17.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(13.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                RazorProfileDataTile(
                                    Icons.AutoMirrored.Filled.TrendingUp,
                                    "${user.currentLevelExp}/${user.nextLevelExp}",
                                    Modifier.weight(1f)
                                )
                                RazorProfileDataTile(
                                    Icons.Default.Leaderboard,
                                    "${user.level} 路 ${user.levelName}",
                                    Modifier.weight(1f)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                RazorProfileDataTile(
                                    Icons.Default.Savings,
                                    "${user.jCoin}",
                                    Modifier.weight(1f)
                                )
                                RazorProfileDataTile(
                                    Icons.Default.Bookmark,
                                    "${user.currentCollectCount}/${user.maxCollectCount}",
                                    Modifier.weight(1f)
                                )
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 30.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(palette.glassSurface.copy(alpha = 0.36f))
                        )
                        RazorGlassButton(onClick = { mainNavController.navigate("login") }) {
                            RazorText(
                                text = "点击登录",
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
            }
        }

        if (userState.isLoading) {
            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 28.dp,
                useContentSurface = true
            ) {
                RazorLoadingRow(
                    text = "同步账户中...",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }
        }

        GlassPanel(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 36.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                RazorProfileMenuItem(
                    icon = Icons.Default.Bookmarks,
                    label = "我的收藏",
                    onClick = {
                        checkLoginThenDo { mainNavController.navigate("userCollectAlbum") }
                    }
                )
                RazorProfileMenuItem(
                    icon = Icons.Default.History,
                    label = "历史观看",
                    onClick = {
                        checkLoginThenDo { mainNavController.navigate("userHistoryAlbum") }
                    }
                )
                RazorProfileMenuItem(
                    icon = Icons.AutoMirrored.Filled.Comment,
                    label = "我的评论",
                    onClick = {
                        checkLoginThenDo { mainNavController.navigate("userHistoryComment") }
                    }
                )
                RazorProfileMenuItem(
                    icon = Icons.Default.CalendarMonth,
                    label = "签到",
                    onClick = {
                        checkLoginThenDo { mainNavController.navigate("sign") }
                    }
                )
                if (isLogin) {
                    RazorProfileDivider()
                    RazorProfileMenuItem(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        label = "退出登录",
                        onClick = {
                            userViewModel.logout()
                        }
                    )
                }
            }
        }

        Box(modifier = Modifier.height(112.dp))
    }
}
