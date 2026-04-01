package com.kracubo.networking.localServer.handlers

import com.google.auto.service.AutoService
import core.Response
import project.list.GetProjectsList
import project.list.ProjectsListResponse

@Suppress("UNUSED")
@AutoService(ICommandHandler::class)
class GetProjectsListHandler : ICommandHandler<GetProjectsList> {
    override val commandClass = GetProjectsList::class

    override suspend fun handle(command: GetProjectsList): Response {
        return ProjectsListResponse(
            command.requestId,
            true,
            projectManager.getProjects(),
            projectManager.getCurrentProjectInfo()
        )
    }
}