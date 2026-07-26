package com.kracubo.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.kracubo.app.ui.theme.AppTheme
import androidx.navigation.compose.composable
import com.kracubo.app.ui.screens.codeeditor.editor.CodeEditorScreen
import com.kracubo.app.ui.screens.codeeditor.projectsslist.ProjectsList


class CodeEditorActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        setContent {
            val context = LocalContext.current
            val navController = rememberNavController()

            BackHandler(
                enabled = navController.currentDestination?.route != "ListProjects"
            ) {

            }

            AppTheme {
                Surface(Modifier.fillMaxSize()) {
                    NavHost(navController = navController, startDestination = "ListProjects") {
                        composable("ListProjects"){
                            ProjectsList(
                                { navController.navigate("CodeEditor") },
                                {
                                    val intent = Intent(context, MainActivity::class.java).apply {
                                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                                Intent.FLAG_ACTIVITY_SINGLE_TOP
                                    }
                                    context.startActivity(intent)

                                    (context as? Activity)?.finish()
                                })
                        }
                        composable("CodeEditor") {
                            CodeEditorScreen(
                                onNavigateToMainMenu = {
                                    Toast.makeText(context, "Project was closed in plugin", Toast.LENGTH_SHORT).show()

                                    val intent = Intent(context, MainActivity::class.java).apply {
                                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                                Intent.FLAG_ACTIVITY_SINGLE_TOP
                                    }
                                    context.startActivity(intent)

                                    (context as? Activity)?.finish()
                                },
                                navigateToProjectsListScreen = {
                                    navController.navigate("ListProjects") {
                                        popUpTo("CodeEditor") {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}