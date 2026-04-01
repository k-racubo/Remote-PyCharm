package com.kracubo.networking.localServer.handlers

import com.kracubo.core.project.CoreProjectManager
import core.Command
import core.Response
import kotlin.reflect.KClass

interface ICommandHandler<T : Command> {
    val commandClass: KClass<T>
    val projectManager: CoreProjectManager get() = CoreProjectManager.getInstance()
    suspend fun handle(command: T): Response?
}