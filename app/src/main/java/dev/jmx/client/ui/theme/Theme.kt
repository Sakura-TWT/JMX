package dev.jmx.client.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColorScheme(
    val contentTag: ColorFamily,
    val roleTag: ColorFamily,
    val workTag: ColorFamily,
)

val extendedLight = ExtendedColorScheme(
    contentTag = ColorFamily(
        contentTagLight,
        onContentTagLight,
        contentTagContainerLight,
        onContentTagContainerLight,
    ),
    roleTag = ColorFamily(
        roleTagLight,
        onRoleTagLight,
        roleTagContainerLight,
        onRoleTagContainerLight,
    ),
    workTag = ColorFamily(
        workTagLight,
        onWorkTagLight,
        workTagContainerLight,
        onWorkTagContainerLight,
    ),
)

val extendedDark = ExtendedColorScheme(
    contentTag = ColorFamily(
        contentTagDark,
        onContentTagDark,
        contentTagContainerDark,
        onContentTagContainerDark,
    ),
    roleTag = ColorFamily(
        roleTagDark,
        onRoleTagDark,
        roleTagContainerDark,
        onRoleTagContainerDark,
    ),
    workTag = ColorFamily(
        workTagDark,
        onWorkTagDark,
        workTagContainerDark,
        onWorkTagContainerDark,
    ),
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecifiedScheme = ColorFamily(
    Color.Unspecified,
    Color.Unspecified,
    Color.Unspecified,
    Color.Unspecified
)
