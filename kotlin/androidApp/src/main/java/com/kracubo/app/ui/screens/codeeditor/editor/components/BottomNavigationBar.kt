package com.kracubo.app.ui.screens.codeeditor.editor.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kracubo.app.ui.theme.TextColor
import com.kracubo.app.ui.theme.surface

@Composable
fun BottomNavigationBar(
    onTerminalClick: () -> Unit,
    onSearchClick:() -> Unit) {

    Surface(
        modifier = Modifier.fillMaxWidth(0.85f)
            .wrapContentWidth(),
        color = surface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, TextColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clickable(onClick = onTerminalClick)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = ">",
                        color = TextColor,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "_",
                        color = TextColor,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            IconButton(onClick = { onSearchClick() }) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TextColor
                )
            }
        }
    }
}

