package com.mifos.compose.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.mifos.compose.R

val LatoFonts = FontFamily(
    Font(
        resId = R.font.lato_regular,
        weight = FontWeight.Normal,
        style = FontStyle.Normal
    ),
    Font(
        resId = R.font.lato_bold,
        weight = FontWeight.Bold,
        style = FontStyle.Normal
    ),
    Font(
        resId = R.font.lato_black,
        weight = FontWeight.Black,
        style = FontStyle.Normal
    )
)