package com.kracubo.app.ui.screens.codeeditor.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex

@Composable
fun CodeEditor(
    modifier: Modifier = Modifier,
    code: String,
    searchingText: String
) {
    val lines = remember(code) { code.lines() }
    val fontSize = 14.sp
    val lineHeight = 20.sp
    val density = LocalDensity.current
    val lineHeightDp = remember(density) { with(density) { lineHeight.toDp() } }

    val horizontalScrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .horizontalScroll(horizontalScrollState)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            itemsIndexed(lines) { index, lineContent ->
                Row(
                    modifier = Modifier
                        .height(lineHeightDp)
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .zIndex(1f)
                            .graphicsLayer {
                                translationX = horizontalScrollState.value.toFloat()
                            }
                            .background(Color(0xFF1E1E1E)) //0xFF1E1E1E
                            .width(56.dp)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = Color(0xFF606366),
                            fontSize = fontSize,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.End,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(Color(0xFF3C3F41)) //0xFF3C3F41
                        )
                    }
                    Text(
                        text = buildAnnotatedString {
                            append(highlightKotlin(lineContent))

                            if(searchingText.isNotEmpty()){
                                var startIndex = lineContent.indexOf(searchingText)
                                while (startIndex >= 0) {
                                    addStyle(
                                        style = SpanStyle(background = Color.Red, color = Color.White),
                                        start = startIndex,
                                        end = startIndex + searchingText.length
                                    )
                                    startIndex = lineContent.indexOf(searchingText, startIndex + searchingText.length)
                                }
                            }
                        },
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .align(Alignment.CenterVertically),
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = fontSize,
                            color = Color(0xFFA9B7C6)
                        ),
                        softWrap = false
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(lineHeightDp * 4)) }

        }
    }
}

