package com.kracubo.networking.localServer.handlers.commands

import com.google.auto.service.AutoService
import com.intellij.openapi.components.service
import com.kracubo.core.file.FileManager
import com.kracubo.networking.localServer.handlers.ICommandHandler
import core.ErrorResponse
import core.Response
import file.FileContentResponse
import file.GetFileContent

@Suppress("UNUSED")
@AutoService(ICommandHandler::class)
class GetFileContentHandler : ICommandHandler<GetFileContent> {
    override val commandClass = GetFileContent::class

    override suspend fun handle(command: GetFileContent): Response? {
        return projectManager.runWithProject(
            action = { project ->
                val content = project.service<FileManager>().getFileContent(command.filePath)

                if (content.isEmpty()) {
                    return@runWithProject ErrorResponse(
                        command.requestId,
                        false,
                        "GET_FILE_CONTENT_FAILED",
                        "VFS can't read or found file"
                    )
                }

                FileContentResponse(command.requestId, true, content)
            }
        )
    }
}