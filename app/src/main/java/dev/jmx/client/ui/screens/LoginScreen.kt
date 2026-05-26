package dev.jmx.client.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jmx.client.R
import dev.jmx.client.store.UserManager
import dev.jmx.client.ui.components.CommonScaffold
import dev.jmx.client.ui.glass.GlassPanel
import dev.jmx.client.ui.glass.LocalJmxGlassPalette
import dev.jmx.client.ui.razor.RazorGlassButton
import dev.jmx.client.ui.razor.RazorLoadingIndicator
import dev.jmx.client.ui.razor.RazorText
import dev.jmx.client.ui.viewModel.UserViewModel
import org.koin.compose.getKoin
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
private fun RazorLoginField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val palette = LocalJmxGlassPalette.current
    val shape = RoundedCornerShape(26.dp)
    var focused by remember { mutableStateOf(false) }
    val focusAlpha by animateFloatAsState(
        targetValue = if (focused) 0.58f else 0.16f,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = 360f),
        label = "loginFieldFocus"
    )
    val textModifier = if (focusRequester != null) {
        Modifier.focusRequester(focusRequester)
    } else {
        Modifier
    }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(
            color = palette.primaryText,
            fontSize = 17.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.SemiBold
        ),
        cursorBrush = SolidColor(palette.accent),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .then(textModifier)
            .onFocusChanged { focused = it.isFocused }
            .clip(shape)
            .background(palette.contentSurface.copy(alpha = if (focused) 0.86f else 0.66f))
            .border(1.dp, palette.accent.copy(alpha = focusAlpha), shape),
        decorationBox = { innerTextField ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
            ) {
                RazorText(
                    text = label,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        color = palette.secondaryText,
                        fontSize = 12.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (value.isEmpty()) {
                        RazorText(
                            text = "请输入$label",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(
                                color = palette.tertiaryText,
                                fontSize = 17.sp,
                                lineHeight = 22.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                    innerTextField()
                }
            }
        }
    )
}

@Composable
fun LoginScreen(
    userManager: UserManager = getKoin().get(),
    userViewModel: UserViewModel = koinActivityViewModel(),
) {
    val focusManager = LocalFocusManager.current
    val mainNavController = LocalMainNavController.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val usernameFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val isLogin by userManager.isLoginState.collectAsState(false)
    val loginState by userViewModel.loginState.collectAsState()
    val palette = LocalJmxGlassPalette.current

    LaunchedEffect(isLogin) {
        if (isLogin) {
            mainNavController.navigate("tab/user") {
                popUpTo("login") {
                    inclusive = true
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        usernameFocusRequester.requestFocus()
    }

    fun toLogin() {
        if (loginState.isLoading || username.isBlank() || password.isBlank()) {
            return
        }
        userViewModel.login(username, password)
    }

    CommonScaffold(title = "登录") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.jmx_logo),
                contentDescription = "JMX",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(124.dp)
                    .clip(RoundedCornerShape(34.dp))
                    .border(1.dp, palette.contentSurface.copy(alpha = 0.82f), RoundedCornerShape(34.dp)),
                contentScale = ContentScale.Crop
            )

            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 36.dp,
                useContentSurface = true
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    RazorText(
                        text = "账户访问",
                        style = TextStyle(
                            color = palette.primaryText,
                            fontSize = 24.sp,
                            lineHeight = 29.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.4).sp
                        )
                    )
                    RazorText(
                        text = "使用 JM 账户同步收藏、历史与签到数据。",
                        style = TextStyle(
                            color = palette.secondaryText,
                            fontSize = 14.sp,
                            lineHeight = 19.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    RazorLoginField(
                        label = "用户名",
                        value = username,
                        onValueChange = { username = it },
                        focusRequester = usernameFocusRequester,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { passwordFocusRequester.requestFocus() }
                        )
                    )

                    RazorLoginField(
                        label = "密码",
                        value = password,
                        onValueChange = { password = it },
                        focusRequester = passwordFocusRequester,
                        isPassword = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                toLogin()
                            }
                        )
                    )

                    if (loginState.isError && !loginState.errorMsg.isNullOrBlank()) {
                        RazorText(
                            text = loginState.errorMsg.orEmpty(),
                            style = TextStyle(
                                color = palette.accent,
                                fontSize = 13.sp,
                                lineHeight = 17.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    RazorGlassButton(
                        onClick = { toLogin() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (loginState.isLoading) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RazorLoadingIndicator(modifier = Modifier.size(20.dp))
                                RazorText(
                                    text = "登录中",
                                    style = TextStyle(
                                        color = palette.primaryText,
                                        fontSize = 16.sp,
                                        lineHeight = 19.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        } else {
                            RazorText(
                                text = "登录",
                                style = TextStyle(
                                    color = palette.primaryText,
                                    fontSize = 16.sp,
                                    lineHeight = 19.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
