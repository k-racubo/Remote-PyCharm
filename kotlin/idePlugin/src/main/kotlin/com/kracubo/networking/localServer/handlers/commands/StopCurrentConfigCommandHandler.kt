package com.kracubo.networking.localServer.handlers.commands

import com.google.auto.service.AutoService
import com.intellij.openapi.components.service
import com.kracubo.core.project.ProjectRunner
import com.kracubo.networking.localServer.handlers.ICommandHandler
import core.Response
import project.run.StopCurrentConfigCommand

@Suppress("UNUSED")
@AutoService(ICommandHandler::class)
class StopCurrentConfigCommandHandler : ICommandHandler<StopCurrentConfigCommand> {
    override val commandClass = StopCurrentConfigCommand::class

    override suspend fun handle(command: StopCurrentConfigCommand): Response? {
        projectManager.runWithProject(action = { project -> project.service<ProjectRunner>().stopCurrentConfig() })
        return null
    }
}