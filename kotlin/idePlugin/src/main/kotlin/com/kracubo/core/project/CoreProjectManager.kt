package com.kracubo.core.project

import com.intellij.ide.RecentProjectsManager
import com.intellij.ide.RecentProjectsManagerBase
import com.intellij.ide.impl.OpenProjectTask
import com.intellij.ide.trustedProjects.TrustedProjects
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.EDT
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.project.ex.ProjectManagerEx
import com.kracubo.controlPanel.logger.Logger
import com.kracubo.controlPanel.logger.MessageType
import com.kracubo.controlPanel.logger.SenderType
import com.kracubo.events.localServer.ActiveProjectClosedTopics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import project.list.ProjectInfo
import java.nio.file.Paths


@Service(Service.Level.APP)
class CoreProjectManager : Disposable {

    private var closedByCommand: Boolean = false

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        ApplicationManager.getApplication().messageBus.connect(this)
            .subscribe(ProjectManager.TOPIC, object : ProjectManagerListener {
                override fun projectClosed(project: Project) {
                    if (project.basePath == activeProjectPath) {
                        if (!closedByCommand) {
                            Logger.log(
                                "Project: closed in IDE plugin",
                                SenderType.LOGGER, MessageType.WARNING
                            )

                            serviceScope.launch {
                                ApplicationManager.getApplication().messageBus
                                    .syncPublisher(ActiveProjectClosedTopics.ACTIVE_PROJECT_CLOSED)
                                    .onActiveProjectClosed()
                            }
                        }
                        activeProjectPath = null
                    }
                    closedByCommand = false
                }
            })
    }

    companion object { fun getInstance() = service<CoreProjectManager>() }

    private var activeProjectPath: String? = null

    fun getProjects() : List<ProjectInfo>? {
        val manager = RecentProjectsManager.getInstance() as? RecentProjectsManagerBase
        if (manager == null) {
            Logger.log("RecentProjectsManager is not RecentProjectsManagerBase instance",
                SenderType.LOGGER, MessageType.ERROR)

            return null
        }

        val projects = mutableListOf<ProjectInfo>()

        manager.getRecentPaths().forEach { path ->
            projects.add(
                ProjectInfo(
                    manager.getProjectName(path),
                    path)
            )
        }

        return projects
    }

    fun getCurrentProjectInfo() : ProjectInfo {
        val openProjects = ProjectManager.getInstance().openProjects

        return ProjectInfo(openProjects.first().name, openProjects.first().basePath.toString())
    }

    @Suppress("UnstableApiUsage")
    fun openProject(pName: String, pPath: String) {
        val path = Paths.get(pPath)

        val options = OpenProjectTask {
            runConfigurators = true
            forceOpenInNewFrame = true
            isNewProject = false
            projectName = null
            useDefaultProjectAsTemplate = false
            showWelcomeScreen = false
        }

        TrustedProjects.setProjectTrusted(path, true)

        val newProject = ProjectManagerEx.getInstanceEx().openProject(path, options)

        if (newProject == null) {
            Logger.log("Open project error: $pName", SenderType.LOCAL_SERVER, MessageType.ERROR)
            return
        }

        Logger.log("Project: $pName is opened", SenderType.LOCAL_SERVER)

        activeProjectPath = pPath
    }

    fun closeProject() {
        val project = getActiveProject() ?: return

        closedByCommand = true

        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.EDT) {
                project.let { ProjectManager.getInstance().closeAndDispose(it) }
            }
        }

        Logger.log("Project: closed in mobile app", SenderType.LOCAL_SERVER)
    }

    private fun getActiveProject(): Project? {
        val openProjects = ProjectManager.getInstance().openProjects
        return openProjects.find { it.basePath == activeProjectPath }
    }

    suspend fun <T> runWithProject(action: suspend (Project) -> T): T? {
        val project = getActiveProject()
        return if (project != null) {
            action(project)
        } else {
            Logger.log("Project not open but trying call services", SenderType.LOCAL_SERVER,
                MessageType.WARNING)

            return null
        }
    }

    override fun dispose() { activeProjectPath = null }
}