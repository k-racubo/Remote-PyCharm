package com.kracubo.events.localServer

import com.intellij.util.messages.Topic

interface ActiveProjectClosedListener {
    suspend fun onActiveProjectClosed()
}

object ActiveProjectClosedTopics {
    val ACTIVE_PROJECT_CLOSED = Topic.create(
        "ActiveProjectClosed",
        ActiveProjectClosedListener::class.java
    )
}