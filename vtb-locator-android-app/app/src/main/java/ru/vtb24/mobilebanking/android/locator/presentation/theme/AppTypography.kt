package ru.vtb24.mobilebanking.android.locator.presentation.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ru.vtb24.mobilebanking.android.locator.R

private val gilroy = FontFamily(
    Font(R.font.vtb_group_ui_demi_bold, FontWeight.Bold),
    Font(R.font.vtb_group_ui_regular, FontWeight.Normal),
    Font(R.font.vtb_group_ui_medium, FontWeight.Medium),
    Font(R.font.vtb_group_ui_semibold, FontWeight.SemiBold),
)

data class AppTypography(
    val headline0: TextStyle = TextStyle(
        fontFamily = gilroy,
        fontWeight = FontWeight.Bold,
        fontSize = 64.sp,
        lineHeight = 96.sp
    ),
    val headline1: TextStyle = TextStyle(
        fontFamily = gilroy,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 64.sp
    ),
    val headline2: TextStyle = TextStyle(
        fontFamily = gilroy,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        lineHeight = 56.sp
    ),
    val headline3: TextStyle = TextStyle(
        fontFamily = gilroy,
        fontWeight = FontWeight.SemiBold,
        fontSize = 37.sp,
    ),
    val headline4: TextStyle = TextStyle(
        fontFamily = gilroy,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
    ),
    val body: TextStyle = TextStyle(
        fontFamily = gilroy,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp
    ),
//    val button: TextStyle = TextStyle(
//        fontFamily = rubik,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp
//    ),
//    val caption: TextStyle = TextStyle(
//        fontFamily = openSans,
//        fontWeight = FontWeight.Normal,
//        fontSize = 12.sp
//    )
)

internal val LocalTypography = staticCompositionLocalOf { AppTypography() }