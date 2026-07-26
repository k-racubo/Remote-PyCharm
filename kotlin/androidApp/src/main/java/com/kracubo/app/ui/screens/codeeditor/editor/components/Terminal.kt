package com.kracubo.app.ui.screens.codeeditor.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kracubo.app.core.viewmodels.codeditor.CodeEditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen(
    viewModel: CodeEditorViewModel,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val terminalLines by viewModel.terminalLines.collectAsState()
    val isRunning by viewModel.isProjectRunning

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Terminal",
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (isRunning) {
                                viewModel.stopProject()
                            } else {
                                viewModel.runProject()
                            }
                        }
                    ) {
                        if (isRunning) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .background(Color.Red, shape = RoundedCornerShape(1.dp))
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Run",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2B2B2B),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )

            TerminalContent(
                lines = terminalLines,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TerminalContent(
    lines: List<CodeEditorViewModel.TerminalLine>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(lines.size) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            lines.forEach { line ->
                TerminalLineView(line)
            }

            if (lines.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun TerminalLineView(line: CodeEditorViewModel.TerminalLine) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val color = when (line.type) {
            CodeEditorViewModel.LineType.OUTPUT -> Color(0xFFCCCCCC)
            CodeEditorViewModel.LineType.INFO -> Color(0xFF4FC3F7)
            CodeEditorViewModel.LineType.ERROR -> Color(0xFFEF5350)
            CodeEditorViewModel.LineType.COMMAND -> Color(0xFF4CAF50)
        }

        Text(
            text = line.text,
            color = color,
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(end = 8.dp)
        )
    }
}