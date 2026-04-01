package com.kracubo.networking.localServer

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.extensions.PluginId
import com.kracubo.controlPanel.logger.Logger
import com.kracubo.controlPanel.logger.MessageType
import com.kracubo.controlPanel.logger.SenderType
import com.kracubo.core.project.CoreProjectManager
import com.kracubo.events.localServer.ServerDownTopics
import com.kracubo.events.localServer.UnexpectedServerDown
import com.kracubo.extensions.prettyJson
import com.kracubo.networking.localServer.handlers.Handler
import core.ApiJson
import core.Event
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.cio.CIO
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.host
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonObject
import project.WelcomePacket
import kotlin.time.Duration.Companion.seconds

@Service(Service.Level.APP)
class LocalWebSocketServer : Disposable {

    companion object { fun getInstance() = service<LocalWebSocketServer>() }

    var port: Int? = null
    private var server: EmbeddedServer<*, *>? = null

    private var currentSession: WebSocketSession? = null

    var isServerStarted: Boolean = false

    private val handler by lazy { Handler.getInstance() }

    private val version: String by lazy {
        val pluginId = PluginId.getId("com.kracubo.remotepycharm")
        PluginManagerCore.getPlugin(pluginId)?.version ?: "1.0.0"
    }

    fun start(port: Int): Boolean {
        return try {
            server = embeddedServer(CIO, port = port) {
                monitor.subscribe(ApplicationStopped) {
                    if (isServerStarted) {
                        ApplicationManager.getApplication().messageBus
                            .syncPublisher(ServerDownTopics.UNEXPECTED_SERVER_DOWN)
                            .onUnexpectedServerDown(UnexpectedServerDown(port = this@LocalWebSocketServer.port!!))

                        UdpListener.stop()

                        isServerStarted = false
                        this@LocalWebSocketServer.port = null
                        server = null
                    }
                }

                install(WebSockets) {
                    pingPeriod = 15.seconds
                    timeout = 30.seconds
                }

                install(ContentNegotiation) { json() }

                routing {
                    webSocket("/ws") { handleConnection(this, call.request.host()) }

                    get("/crash") {
                        call.respond("server down")
                        UdpListener.stop()
                        server?.stop(0, 0)
                    }
                }
            }

            server?.start(wait = false)

            this.port = port
            isServerStarted = true

            UdpListener.createMdnsService(port, version)

            true
        } catch (e: Exception) {
            println(e)
            false
        }
    }

    fun stop() {
        isServerStarted = false
        server?.stop(1000, 5000)

        port = null
        server = null
        currentSession = null

        UdpListener.stop()

        Logger.log("Local server stopped", senderType = SenderType.LOCAL_SERVER)
    }

    private suspend fun handleConnection(session: WebSocketSession, hostAddress: String) {

        if (currentSession != null && currentSession?.isActive == true) {
            session.close(
                CloseReason(
                    CloseReason.Codes.TRY_AGAIN_LATER,
                    "Only one connection allowed"))

            Logger.log("host: $hostAddress trying connect to server but socket busy another connection",
                SenderType.LOCAL_SERVER, MessageType.WARNING)
            return
        }

        currentSession = session

        Logger.log("New connection: $hostAddress", SenderType.LOCAL_SERVER)

       sendEventPacket(WelcomePacket(version = version))

        try {
            for (frame in session.incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()

                    Logger.log(
                        "Packet received from $hostAddress:\n${text.prettyJson()}",
                        SenderType.LOCAL_SERVER
                    )

                    val jsonElement = try {
                        ApiJson.instance.parseToJsonElement(text)
                    } catch (e: Exception) {
                        Logger.log(
                            "Invalid JSON: ${e.message}", SenderType.LOCAL_SERVER,
                            MessageType.WARNING
                        )
                        null
                    }

                    if (jsonElement == null) { continue }

                    if (jsonElement !is JsonObject) {
                        Logger.log(
                            "Expected JSON object, got ${jsonElement::class.simpleName}",
                            SenderType.LOCAL_SERVER, MessageType.WARNING
                        )
                        continue
                    }

                    session.launch {
                        val response = handler.resolve(text) ?: return@launch

                        val responseString = ApiJson.instance.encodeToString(response)

                        val sendMutex = Mutex()

                        sendMutex.withLock { session.send(responseString) }

                        Logger.log(
                            "Packet sent to $hostAddress:\n${responseString.prettyJson()}",
                            SenderType.LOCAL_SERVER
                        )
                    }
                }
            }
        } finally {
            currentSession = null
            CoreProjectManager.getInstance().closeProject()
            Logger.log("Connection closed: $hostAddress", SenderType.LOCAL_SERVER)
        }
    }

    suspend fun sendEventPacket(packet: Event) : Boolean? {
        return if (currentSession != null && currentSession?.isActive == true) {
            val message = ApiJson.instance.encodeToString<Event>(packet)
            currentSession?.send(message)

            Logger.log(
                "Packet sent: \n${message.prettyJson()}",
                SenderType.LOCAL_SERVER
            )

            true
        } else false
    }

    override fun dispose() {
        UdpListener.stop()
        stop()
    }
}