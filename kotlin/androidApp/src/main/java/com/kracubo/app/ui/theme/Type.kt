package com.kracubo.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.kracubo.app.R

val customFontFamily = FontFamily(
    Font(R.font.jetbrainsmonoregular)
)
val customTypography = Typography(
    headlineLarge = TextStyle(fontFamily = customFontFamily, fontSize = 30.sp),
    headlineMedium = TextStyle(fontFamily = customFontFamily, fontSize = 24.sp),
    headlineSmall = TextStyle(fontFamily = customFontFamily, fontSize = 20.sp),

    bodyLarge = TextStyle(fontFamily = customFontFamily, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = customFontFamily, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = customFontFamily, fontSize = 12.sp)
)