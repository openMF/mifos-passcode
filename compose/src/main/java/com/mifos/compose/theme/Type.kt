package com.mifos.compose.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = LatoFonts,
        fontWeight = FontWeight.Black,
        fontSize = 34.sp
    )
)

val PasscodeKeyButtonStyle = TextStyle(
    fontFamily = LatoFonts,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp
)

val skipButtonStyle = TextStyle(
    color = blueTint,
    fontSize = 20.sp,
    fontFamily = LatoFonts
)

val forgotButtonStyle = TextStyle(
    color = blueTint,
    fontSize = 14.sp,
    fontFamily = LatoFonts
)

val useTouchIdButtonStyle = TextStyle(
    color = blueTint,
    fontSize = 14.sp,
    fontFamily = LatoFonts
)