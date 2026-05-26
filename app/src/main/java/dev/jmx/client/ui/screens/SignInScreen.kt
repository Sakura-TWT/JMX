package dev.jmx.client.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.ContentHeightMode
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.yearMonth
import dev.jmx.client.ui.components.CommonScaffold
import dev.jmx.client.ui.glass.GlassPanel
import dev.jmx.client.ui.glass.LocalJmxGlassPalette
import dev.jmx.client.ui.razor.RazorGlassButton
import dev.jmx.client.ui.razor.RazorLoadingIndicator
import dev.jmx.client.ui.razor.RazorText
import dev.jmx.client.ui.viewModel.UserViewModel
import kotlinx.coroutines.flow.filter
import org.koin.compose.viewmodel.koinActivityViewModel
import java.time.LocalDate
import kotlin.math.max

private val weekTextMap = mapOf(
    1 to "一",
    2 to "二",
    3 to "三",
    4 to "四",
    5 to "五",
    6 to "六",
    7 to "日",
)

@Composable
fun rememberFirstVisibleMonthAfterScroll(state: CalendarState): CalendarMonth {
    val visibleMonth = remember(state) { mutableStateOf(state.firstVisibleMonth) }
    LaunchedEffect(state) {
        snapshotFlow { state.isScrollInProgress }
            .filter { scrolling -> !scrolling }
            .collect { visibleMonth.value = state.firstVisibleMonth }
    }
    return visibleMonth.value
}

@Composable
fun SignInScreen(
    userViewModel: UserViewModel = koinActivityViewModel()
) {
    val today = remember { LocalDate.now() }
    val daysOfWeek = remember { daysOfWeek() }
    val currentMonth = remember(today) { today.yearMonth }
    val startMonth = remember { currentMonth.minusMonths(500) }
    val endMonth = remember { currentMonth.plusMonths(500) }
    val signDataState by userViewModel.signDataState.collectAsState()
    val signInState by userViewModel.signInState.collectAsState()
    val palette = LocalJmxGlassPalette.current
    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first(),
        outDateStyle = OutDateStyle.EndOfGrid,
    )
    val visibleMonth = rememberFirstVisibleMonthAfterScroll(calendarState)
    val signMaxDay by remember {
        derivedStateOf {
            signDataState.data?.dateMap?.entries?.fold(mutableListOf(0, 0)) { acc, item ->
                if (item.value.isSign) {
                    acc[1] += 1
                    acc[0] = max(acc[0], acc[1])
                } else {
                    acc[1] = 0
                }
                acc
            }?.get(0) ?: 0
        }
    }
    val title = visibleMonth.yearMonth.toString() + when {
        signDataState.data != null -> " · ${signDataState.data!!.eventName}"
        else -> ""
    }

    LaunchedEffect(Unit) {
        userViewModel.getSignInData()
    }

    CommonScaffold(title = "每日签到") {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 132.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                GlassPanel(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 36.dp,
                    useContentSurface = true
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            RazorText(
                                text = title,
                                style = TextStyle(
                                    color = palette.primaryText,
                                    fontSize = 22.sp,
                                    lineHeight = 27.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.35).sp
                                )
                            )
                            RazorText(
                                text = if (signDataState.isLoading) "正在同步签到数据" else "已连续签到 $signMaxDay 天",
                                style = TextStyle(
                                    color = palette.secondaryText,
                                    fontSize = 14.sp,
                                    lineHeight = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                        RazorGlassButton(
                            onClick = {
                                if (!signDataState.isLoading) {
                                    userViewModel.getSignInData()
                                }
                            }
                        ) {
                            if (signDataState.isLoading) {
                                RazorLoadingIndicator(modifier = Modifier.size(21.dp))
                            } else {
                                Image(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "刷新",
                                    modifier = Modifier.size(22.dp),
                                    colorFilter = ColorFilter.tint(palette.primaryText)
                                )
                            }
                        }
                    }
                }
            }

            item {
                GlassPanel(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 36.dp,
                    useContentSurface = true
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(22.dp))
                                .background(palette.contentSurface.copy(alpha = 0.74f))
                                .padding(vertical = 10.dp),
                        ) {
                            for (dayOfWeek in daysOfWeek) {
                                RazorText(
                                    modifier = Modifier.weight(1f),
                                    text = weekTextMap[dayOfWeek.value]!!,
                                    style = TextStyle(
                                        color = palette.secondaryText,
                                        fontSize = 14.sp,
                                        lineHeight = 17.sp,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                        HorizontalCalendar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(382.dp),
                            state = calendarState,
                            calendarScrollPaged = true,
                            contentHeightMode = ContentHeightMode.Fill,
                            dayContent = { day ->
                                if (day.position == DayPosition.MonthDate) {
                                    val data = signDataState.data?.dateMap?.get(day.date.dayOfMonth)
                                    Day(
                                        day = day,
                                        isToday = day.date == today,
                                        isSign = data?.isSign == true,
                                        hasExtraBonus = data?.hasExtraBonus == true,
                                    )
                                }
                            }
                        )
                    }
                }
            }

            item {
                GlassPanel(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 36.dp,
                    useContentSurface = true
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        RazorText(
                            text = "连续签到进度",
                            style = TextStyle(
                                color = palette.primaryText,
                                fontSize = 20.sp,
                                lineHeight = 25.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.25).sp
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (i in 0 until 7) {
                                key(i) {
                                    Column(
                                        modifier = Modifier.width(36.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        if (i < signMaxDay) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(CircleShape)
                                                    .size(22.dp)
                                                    .background(palette.accent.copy(alpha = 0.15f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Image(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(15.dp),
                                                    colorFilter = ColorFilter.tint(palette.accent)
                                                )
                                            }
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .clip(CircleShape)
                                                    .size(22.dp)
                                                    .background(palette.tertiaryText.copy(alpha = 0.14f))
                                            )
                                        }
                                        RazorText(
                                            text = "${i + 1}",
                                            style = TextStyle(
                                                color = palette.secondaryText,
                                                fontSize = 12.sp,
                                                lineHeight = 15.sp,
                                                textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }
                                    if (i < 6) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            RazorText(
                                text = "连续签到三天额外获得 ${signDataState.data?.threeDaysCoin ?: 0} 金币，${signDataState.data?.threeDaysExp ?: 0} 经验",
                                style = TextStyle(
                                    color = palette.secondaryText,
                                    fontSize = 14.sp,
                                    lineHeight = 19.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            RazorText(
                                text = "连续签到七天额外获得 ${signDataState.data?.sevenDaysCoin ?: 0} 金币，${signDataState.data?.sevenDaysExp ?: 0} 经验",
                                style = TextStyle(
                                    color = palette.secondaryText,
                                    fontSize = 14.sp,
                                    lineHeight = 19.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }

            item {
                RazorGlassButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    onClick = {
                        if (!signDataState.isLoading && !signInState.isLoading) {
                            userViewModel.signIn()
                        }
                    }
                ) {
                    if (signInState.isLoading) {
                        RazorLoadingIndicator(modifier = Modifier.size(21.dp))
                        RazorText(
                            text = "签到中",
                            style = TextStyle(
                                color = palette.primaryText,
                                fontSize = 16.sp,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    } else {
                        RazorText(
                            text = "签到",
                            style = TextStyle(
                                color = palette.primaryText,
                                fontSize = 16.sp,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Day(
    day: CalendarDay,
    isToday: Boolean = false,
    isSign: Boolean = false,
    hasExtraBonus: Boolean = false,
) {
    val palette = LocalJmxGlassPalette.current
    val checkIcon = rememberVectorPainter(Icons.Default.Check)
    val starIcon = rememberVectorPainter(Icons.Default.Star)
    val primaryColor = palette.accent
    val bonusColor = palette.accent.copy(alpha = 0.16f)
    val shape = RoundedCornerShape(10.dp)

    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(2.dp)
            .clip(shape)
            .background(
                when {
                    isToday -> palette.accent.copy(alpha = 0.14f)
                    isSign -> palette.contentSurface.copy(alpha = 0.92f)
                    else -> palette.contentSurface.copy(alpha = 0.60f)
                }
            )
            .border(
                width = if (isToday) 1.dp else 0.dp,
                color = palette.accent.copy(alpha = if (isToday) 0.34f else 0f),
                shape = shape
            )
            .drawBehind {
                if (isSign) {
                    val side1 = size.minDimension * 0.48f
                    val side2 = size.minDimension * 0.26f
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(side1, 0f)
                        lineTo(0f, side1)
                        close()
                    }
                    drawPath(path = path, color = bonusColor)
                    with(checkIcon) {
                        draw(
                            size = Size(side2, side2),
                            colorFilter = ColorFilter.tint(primaryColor)
                        )
                    }
                }
                if (hasExtraBonus) {
                    val side1 = size.minDimension * 0.48f
                    val side2 = size.minDimension * 0.26f
                    val path = Path().apply {
                        moveTo(size.width, size.height)
                        lineTo(size.width - side1, size.height)
                        lineTo(size.width, size.height - side1)
                        close()
                    }
                    drawPath(path = path, color = bonusColor)
                    with(starIcon) {
                        translate(
                            left = size.width - side2,
                            top = size.height - side2
                        ) {
                            draw(
                                size = Size(side2, side2),
                                colorFilter = ColorFilter.tint(primaryColor)
                            )
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        RazorText(
            text = day.date.dayOfMonth.toString(),
            style = TextStyle(
                color = when {
                    day.position == DayPosition.OutDate -> palette.tertiaryText
                    else -> palette.primaryText
                },
                fontSize = 15.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        )
    }
}
