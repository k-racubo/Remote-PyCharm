package com.kracubo.app.core.viewmodels.codeditor

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.kracubo.app.core.networking.handlers.Handler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class CodeEditorViewModel : BaseViewModel() {
    var onProjectDownOnServer by mutableStateOf(false)

    private val _projectTree = MutableStateFlow<FileNode?>(null)
    val projectTree = _projectTree.asStateFlow()

    private val _terminalLines = MutableStateFlow<List<TerminalLine>>(emptyList())
    val terminalLines = _terminalLines.asStateFlow()

    private val _isProjectRunning = mutableStateOf(false)
    val isProjectRunning: State<Boolean> = _isProjectRunning

    private val _fileContent = MutableStateFlow<List<String>?>(null)
    val fileContent = _fileContent.asStateFlow()

    var searchText by mutableStateOf("")

    init {
        Handler.setCurrentViewmodel(this)
    }

    fun onProjectClosedOnServer() { onProjectDownOnServer = true }

    fun updateProjectTree(fileTree: JsonObject) {
        val projectName = fileTree.keys.firstOrNull() ?: "Project"
        val projectData = fileTree[projectName]?.jsonObject

        if (projectData != null) {
            val rootNode = parseFileTree(projectName, projectData, "")
            _projectTree.value = rootNode
        }
    }

    private fun parseFileTree(name: String, node: JsonObject, parentPath: String = ""): FileNode {
        val type = node["type"]?.jsonPrimitive?.content

        val currentPath = if (parentPath.isEmpty()) name else "$parentPath/$name"

        return if (type == "dir") {
            val children = node["children"]?.jsonObject?.mapValues { (childName, childValue) ->
                parseFileTree(childName, childValue.jsonObject, currentPath)
            } ?: emptyMap()

            FileNode(
                name = name,
                path = currentPath,
                type = FileType.FOLDER,
                children = children.values.toList()
            )
        } else {
            FileNode(
                name = name,
                path = currentPath,
                type = FileType.FILE,
                children = null
            )
        }
    }

    fun runProject() {
        viewModelScope.launch {
            _terminalLines.value = emptyList()
            Handler.runProject()
            _isProjectRunning.value = true
        }
    }

    fun stopProject() {
        viewModelScope.launch {
            Handler.stopProject()
            _isProjectRunning.value = false
        }
    }

    fun onRunResult(result: String) {
        _isProjectRunning.value = false

        result.lines().forEach { line ->
            if (line.isNotBlank()) {
                addTerminalLine(line, LineType.OUTPUT)
            }
        }

        addTerminalLine("", LineType.OUTPUT)
    }

    private fun addTerminalLine(text: String, type: LineType = LineType.OUTPUT) {
        _terminalLines.update { current ->
            current + TerminalLine(
                text = text,
                type = type,
                timestamp = System.currentTimeMillis()
            )
        }
    }

    fun onSearchTextChanged(newText: String){
        searchText = newText
    }
    fun cancelSearchByFile(){
        searchText = ""
    }
    fun getFileContent(filePath: String) { viewModelScope.launch { Handler.getFileContent(filePath) } }

    fun updateCurrentFileContent(content: List<String>) { _fileContent.value = content }

    data class TerminalLine(
        val text: String,
        val type: LineType,
        val timestamp: Long
    )

    enum class LineType {
        OUTPUT,
        INFO,
        ERROR,
        COMMAND
    }

    override fun onCleared() { Handler.clearCurrentViewmodel() }
}

data class FileNode(
    val name: String,
    val path: String,
    val type: FileType,
    val children: List<FileNode>? = null
)

enum class FileType {
    FILE,
    FOLDER
}