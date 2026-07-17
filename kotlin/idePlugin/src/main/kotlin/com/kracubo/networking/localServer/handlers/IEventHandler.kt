package com.kracubo.networking.localServer.handlers

import core.Event
import core.Response
import kotlin.reflect.KClass

@Suppress("UNUSED")
interface IEventHandler<T : Event> {
    val eventClass: KClass<T>

    fun handle(event: T) : Response?
}