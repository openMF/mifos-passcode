package com.assignment.compose.theme

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
    color = keyTint,
    fontSize = 20.sp,
    fontFamily = LatoFonts
)

val forgotButtonStyle = TextStyle(
    color = keyTint,
    fontSize = 14.sp,
    fontFamily = LatoFonts
)