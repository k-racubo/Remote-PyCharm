package com.kracubo.app.core.viewmodels.codeditor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.kracubo.app.core.networking.handlers.Handler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import project.list.ProjectInfo

class ProjectsListViewModel : BaseViewModel() {

    private val colors = listOf(
        Color(0xFF4CAF50),
        Color(0xFF26A69A),
        Color(0xFFBA7867),
        Color(0xFFD061A2),
        Color(0xFF758DD9),
        Color(0xFF758DD9),
    )

    var projects by mutableStateOf(emptyList<Project>())

    init {
        Handler.setCurrentViewmodel(this)

        viewModelScope.launch {
            delay(100)
            Handler.getProjectsList()
        }
    }

    private fun createInitials(name: String) : String { return name.first().toString() + name.last().toString() }

    private fun pickRandColor() : Color { return colors.random() }

    fun updateProjectList(projects: List<ProjectInfo>?) {
        this.projects = projects?.map { info ->
            Project(
                initials = createInitials(info.projectName),
                name = info.projectName,
                path = info.projectPath,
                color = pickRandColor()
            )
        } ?: emptyList()
    }

    fun closeCurrentProject() { viewModelScope.launch { Handler.closeCurrentProject() } }

    fun openProject(projectName: String, projectPath: String) {
        viewModelScope.launch {
            Handler.openProject(projectName, projectPath)
        }
    }

    override fun onCleared() { Handler.clearCurrentViewmodel() }
}