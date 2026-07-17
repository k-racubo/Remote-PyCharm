package com.kracubo.networking.localServer.handlers

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import core.ApiJson
import core.Command
import core.ErrorResponse
import core.Event
import core.Response
import java.util.ServiceLoader
import kotlin.reflect.KClass

@Service(Service.Level.APP)
class Handler {

    companion object { fun getInstance() = service<Handler>() }

    private val handlers = mutableMapOf<KClass<out Command>, ICommandHandler<*>>()

    private val eventHandlers = mutableMapOf<KClass<out Event>, IEventHandler<*>>()

    init {
        ServiceLoader.load(ICommandHandler::class.java, this::class.java.classLoader).forEach { handler ->
            handlers[handler.commandClass] = handler
        }

        ServiceLoader.load(IEventHandler::class.java, this::class.java.classLoader).forEach { handler ->
            eventHandlers[handler.eventClass] = handler
        }
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun resolve(message: String): Response? {
        try {
            val command = ApiJson.instance.decodeFromString<Command>(message)
            val handler = handlers[command::class] as? ICommandHandler<Command>

            return handler?.handle(command)
        } catch (_: Exception) {}

        try {
            val event = ApiJson.instance.decodeFromString<Event>(message)
            val handler = eventHandlers[event::class] as? IEventHandler<Event>

            return handler?.handle(event)
        } catch (_: Exception) {
            // needs normal api for errors
            return ErrorResponse("", false, "", "")
        }
    }
}