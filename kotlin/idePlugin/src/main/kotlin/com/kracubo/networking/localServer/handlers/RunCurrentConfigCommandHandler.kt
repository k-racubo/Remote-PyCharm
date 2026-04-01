package com.kracubo.networking.localServer.handlers

import com.google.auto.service.AutoService
import com.intellij.openapi.components.service
import com.kracubo.core.project.ProjectRunner
import core.Response
import project.run.ResultOfRunResponse
import project.run.RunCurrentConfigCommand

@Suppress("UNUSED")
@AutoService(ICommandHandler::class)
class RunCurrentConfigCommandHandler : ICommandHandler<RunCurrentConfigCommand> {
    override val commandClass = RunCurrentConfigCommand::class

    override suspend fun handle(command: RunCurrentConfigCommand): Response? {
        return projectManager.runWithProject(
            action = {project ->
                val result = project.service<ProjectRunner>().runCurrentConfigAsync()

                ResultOfRunResponse(
                    command.requestId,
                    true,
                    result
                )
            })
    }

}