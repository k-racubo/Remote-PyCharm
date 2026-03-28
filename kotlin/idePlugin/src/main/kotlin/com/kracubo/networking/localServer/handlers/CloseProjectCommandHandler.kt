package com.kracubo.networking.localServer.handlers

import com.google.auto.service.AutoService
import core.Response
import project.close.CloseProjectCommand

@Suppress("UNUSED")
@AutoService(ICommandHandler::class)
class CloseProjectCommandHandler : ICommandHandler<CloseProjectCommand> {
    override val commandClass = CloseProjectCommand::class

    override suspend fun handle(command: CloseProjectCommand): Response? {
        projectManager.closeProject()
        return null
    }
}