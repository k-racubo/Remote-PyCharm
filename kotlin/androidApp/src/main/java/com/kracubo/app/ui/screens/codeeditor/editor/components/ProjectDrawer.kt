package com.kracubo.app.ui.screens.codeeditor.editor.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kracubo.app.core.viewmodels.codeditor.CodeEditorViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProjectDrawer(
    isOpen: Boolean,
    vm: CodeEditorViewModel,
    onClose: () -> Unit,
    onFileSelected: (String, String) -> Unit
) {
    val scope = rememberCoroutineScope()

    var offsetX by remember { mutableFloatStateOf(0f) }

    val projectTree by vm.projectTree.collectAsState()

    LaunchedEffect(isOpen) {
        offsetX = if (isOpen) 0f
        else -300f
    }

    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn(animationSpec = tween(200)),
        exit = fadeOut(animationSpec = tween(200))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val alpha = ((300f + offsetX) / 300f).coerceIn(0.5f, 1f) * 0.5f

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = alpha))
                    .padding(start = 300.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 300.dp)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (offsetX < -150f) {
                                    scope.launch {
                                        animate(
                                            initialValue = offsetX,
                                            targetValue = -300f,
                                            animationSpec = tween(150)
                                        ) { value, _ -> offsetX = value }
                                        onClose()
                                    }
                                } else {
                                    scope.launch {
                                        animate(
                                            initialValue = offsetX,
                                            targetValue = 0f,
                                            animationSpec = tween(150)
                                        ) { value, _ -> offsetX = value }
                                    }
                                }
                            }
                        ) { change, dragAmount ->
                            change.consume()
                            offsetX = (offsetX + dragAmount).coerceIn(-300f, 0f)
                        }
                    }
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        scope.launch {
                            animate(
                                initialValue = offsetX,
                                targetValue = -300f,
                                animationSpec = tween(150)
                            ) { value, _ -> offsetX = value }
                            onClose()
                        }
                    }
            )

            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(300.dp)
                    .align(Alignment.CenterStart)
                    .offset { IntOffset(offsetX.toInt(), 0) }
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (offsetX < -150f) {
                                    scope.launch {
                                        animate(
                                            initialValue = offsetX,
                                            targetValue = -300f,
                                            animationSpec = tween(150)
                                        ) { value, _ -> offsetX = value }
                                        onClose()
                                    }
                                } else {
                                    scope.launch {
                                        animate(
                                            initialValue = offsetX,
                                            targetValue = 0f,
                                            animationSpec = tween(150)
                                        ) { value, _ -> offsetX = value }
                                    }
                                }
                            }
                        ) { change, dragAmount ->
                            change.consume()
                            offsetX = (offsetX + dragAmount).coerceIn(-300f, 0f)
                        }
                    },
                color = Color(0xFF2D2D30),
                shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Project Files", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f))

                    projectTree?.let {
                        FileTreeView(
                            node = it,
                            onFileSelected = { fileName, filePath ->
                                onFileSelected(fileName, filePath)
                                onClose()
                            }
                        )
                    }
                }
            }
        }
    }
}