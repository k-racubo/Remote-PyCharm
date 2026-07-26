package com.kracubo.core.file

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager

@Service(Service.Level.PROJECT)
class FileManager(private val project: Project) {

    fun getFileContent(relativeFilePath: String): List<String> {
        val vfs = VirtualFileManager.getInstance()
        val fileUrl = "file://${project.basePath}/$relativeFilePath"

        val virtualFile = vfs.findFileByUrl(fileUrl)
            ?: return emptyList()

        return runCatching {
            virtualFile.inputStream.bufferedReader().readLines()
        }.getOrElse { emptyList() }
    }

    private fun getVirtualFile(relativePath: String): VirtualFile? {
        val basePath = project.basePath ?: return null
        val fileUrl = "file://$basePath/$relativePath"
        return VirtualFileManager.getInstance().findFileByUrl(fileUrl)
    }

    private fun getProjectRoot(): VirtualFile? {
        val basePath = project.basePath ?: return null
        return VirtualFileManager.getInstance().findFileByUrl("file://$basePath")
    }

    private fun createDirectories(root: VirtualFile, relativePath: String): VirtualFile? {
        var current = root
        for (part in relativePath.split("/")) {
            val child = current.findChild(part)
            current = if (child == null) {
                current.createChildDirectory(this, part)
            } else if (child.isDirectory) {
                child
            } else {
                return null
            }
        }
        return current
    }

    fun createDirectory(relativePath: String): Boolean {
        return runCatching {
            val root = getProjectRoot() ?: return false
            createDirectories(root, relativePath)
            true
        }.getOrElse { false }
    }

    fun renameFile(relativePath: String, newName: String) : Boolean {
        return runCatching {
            val file = getVirtualFile(relativePath) ?: return false
            file.rename(this, newName)
            true
        }.getOrElse { false }
    }

    fun removeFile(relativePath: String) : Boolean {
        return runCatching {
            val file = getVirtualFile(relativePath) ?: return false
            file.delete(this)
            true
        }.getOrElse { false }
    }

    fun createNewFile(relativePath: String, content: String = "") : Boolean {
        return runCatching {
            val root = getProjectRoot() ?: return false
            val pathParts = relativePath.split("/")
            val fileName = pathParts.last()
            val dirPath = pathParts.dropLast(1).joinToString("/")

            val targetDir = if (dirPath.isNotEmpty()) {
                createDirectories(root, dirPath)
            } else {
                root
            } ?: return false

            val virtualFile = targetDir.createChildData(this, fileName)
            if (content.isNotEmpty()) {
                virtualFile.setBinaryContent(content.toByteArray())
            }
            true
        }.getOrElse { false }
    }
}