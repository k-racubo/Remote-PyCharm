package com.kracubo.app.ui.screens.codeeditor.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kracubo.app.core.extensions.OnDisconnectEffect
import com.kracubo.app.core.viewmodels.codeditor.CodeEditorViewModel
import com.kracubo.app.ui.screens.codeeditor.editor.components.BottomNavigationBar
import com.kracubo.app.ui.screens.codeeditor.editor.components.CodeEditor
import com.kracubo.app.ui.screens.codeeditor.editor.components.ProjectDrawer
import com.kracubo.app.ui.screens.codeeditor.editor.components.terminalScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeEditorScreen(
    onNavigateToMainMenu: () -> Unit,
    navigateToProjectsListScreen: () -> Unit
) {
    val context = LocalContext.current

    val viewModel: CodeEditorViewModel = viewModel()

    val emptyFileName = "Choose file"

    viewModel.OnDisconnectEffect(onNavigateToMainMenu)

    LaunchedEffect(viewModel.onProjectDownOnServer) {
        if (viewModel.onProjectDownOnServer) {
            viewModel.onProjectDownOnServer = false
            navigateToProjectsListScreen()
        }
    }

    var drawerOpen by remember { mutableStateOf(false) }

    var showTerminal by remember { mutableStateOf(false) }

    var showSearchTextField by remember { mutableStateOf(false) }

    var currentFile by remember { mutableStateOf(emptyFileName) }

    val fileContent by viewModel.fileContent.collectAsState()

    val searchText = viewModel.searchText

    val codeText = fileContent?.joinToString("\n") ?: "Thanks a lot for test our app"

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentFile, fontSize = 16.sp) },
                    navigationIcon = {
                        IconButton(onClick = { drawerOpen = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            if (viewModel.isProjectRunning.value) {
                                viewModel.stopProject()
                            } else
                                viewModel.runProject()
                        }) {
                            if (viewModel.isProjectRunning.value) {
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
            },
            bottomBar = {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .padding(bottom = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BottomNavigationBar(
                        onTerminalClick = { showTerminal = !showTerminal },
                        onSearchClick = { showSearchTextField = !showSearchTextField})
                }
            }
        ) { paddingValues ->
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
                if(showSearchTextField){
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 0.5.dp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF2B2B2B)
                    ) {
                        Text("dev in feature") //контент для поиска в файле
                    }
                }
                CodeEditor(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(), // здесь отображаются строки кода из считанного файла
                    code = codeText,
                    searchingText = searchText
                )
            }
        }

        ProjectDrawer(
            isOpen = drawerOpen,
            vm = viewModel,
            onClose = { drawerOpen = false },
            onFileSelected = { fileName, filePath ->
                currentFile = fileName

                viewModel.getFileContent(filePath)

                drawerOpen = false
            }
        )

        if(showTerminal){
            terminalScreen(viewModel, {showTerminal = !showTerminal})
        }
    }
}