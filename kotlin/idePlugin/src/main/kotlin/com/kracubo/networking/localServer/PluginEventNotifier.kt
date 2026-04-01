package com.kracubo.networking.localServer

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.kracubo.events.localServer.ActiveProjectClosedListener
import com.kracubo.events.localServer.ActiveProjectClosedTopics
import project.OnProjectClosed

@Suppress("UNUSED")
@Service(Service.Level.APP)
class PluginEventNotifier : Disposable {

    init {
        ApplicationManager.getApplication().messageBus.connect(this)
            .subscribe(ActiveProjectClosedTopics.ACTIVE_PROJECT_CLOSED,
                object : ActiveProjectClosedListener {
                    override suspend fun onActiveProjectClosed() {
                        LocalWebSocketServer.getInstance().sendEventPacket(OnProjectClosed())
                    }
            })
    }

    override fun dispose() {}
}