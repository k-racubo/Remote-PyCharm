package com.kracubo.networking.localServer.handlers.commands

import com.google.auto.service.AutoService
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.components.service
import com.kracubo.core.project.ProjectStructureProvider
import com.kracubo.networking.localServer.handlers.ICommandHandler
import core.Response
import project.open.OpenProjectCommand
import project.open.ProjectFileTreeResponse

@Suppress("UNUSED")
@AutoService(ICommandHandler::class)
class OpenProjectCommandHandler : ICommandHandler<OpenProjectCommand> {
    override val commandClass = OpenProjectCommand::class

    override suspend fun handle(command: OpenProjectCommand): Response? {
        projectManager.openProject(command.projectName, command.projectPath)

        return projectManager.runWithProject(
            action = { project ->
                val tree = runReadAction { project.service<ProjectStructureProvider>().buildTree() }

                ProjectFileTreeResponse(
                    requestId = command.requestId,
                    success = true,
                    fileTree = tree
                )
            }
        )
    }

}