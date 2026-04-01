package com.kracubo.networking.localServer.handlers

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.kracubo.events.localServer.ActiveProjectClosedListener
import com.kracubo.events.localServer.ActiveProjectClosedTopics
import com.kracubo.networking.localServer.LocalWebSocketServer
import core.ApiJson
import core.Command
import core.Response
import project.OnProjectClosed
import java.util.ServiceLoader
import kotlin.reflect.KClass

@Service(Service.Level.APP)
class Handler : Disposable {

    private val handlers = mutableMapOf<KClass<out Command>, ICommandHandler<*>>()

    init {
        ServiceLoader.load(ICommandHandler::class.java, this::class.java.classLoader).forEach { handler ->
            handlers[handler.commandClass] = handler
        }

        ApplicationManager.getApplication().messageBus.connect(this)
            .subscribe(ActiveProjectClosedTopics.ACTIVE_PROJECT_CLOSED,
                object : ActiveProjectClosedListener {
                    override suspend fun onActiveProjectClosed() {
                        sendOnClosedProjectEvent()
                    }
                })
    }

    companion object { fun getInstance() = service<Handler>() }

    @Suppress("UNCHECKED_CAST")
    suspend fun resolve(message: String): Response? {
        val command = ApiJson.instance.decodeFromString<Command>(message)
        val handler = handlers[command::class] as? ICommandHandler<Command>

        return handler?.handle(command)
    }

    suspend fun sendOnClosedProjectEvent() {
        LocalWebSocketServer.getInstance().sendEventPacket(OnProjectClosed())
    }

    override fun dispose() {}
}