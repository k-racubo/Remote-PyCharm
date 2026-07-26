package com.kracubo.networking.localServer

import com.kracubo.controlPanel.logger.Logger
import com.kracubo.controlPanel.logger.MessageType
import com.kracubo.controlPanel.logger.SenderType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.NetworkInterface
import javax.jmdns.JmDNS
import javax.jmdns.ServiceInfo

object UdpListener {
    private const val SERVICE_TYPE = "_remotepycharm._tcp.local."
    private const val SERVICE_NAME = "RemotePyCharm local server"

    private val jmdnsInstances = mutableListOf<JmDNS>()

    private val mDNSScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun createMdnsService(serverPort: Int, version: String) {
        mDNSScope.launch {
            try {
                val interfaces = withContext(Dispatchers.IO) {
                    NetworkInterface.getNetworkInterfaces().asSequence()
                        .filter { it.isUp && !it.isLoopback }
                        .toList()
                }

                interfaces.flatMap { it.inetAddresses.asSequence() }.map { address ->
                    async {
                        val jmdns = JmDNS.create(address, SERVICE_NAME)

                        val serviceInfo = ServiceInfo.create(
                            SERVICE_TYPE,
                            SERVICE_NAME,
                            serverPort,
                            0,
                            0,
                            true,
                            mapOf("version" to version)
                        )

                        jmdns?.registerService(serviceInfo)

                        synchronized(jmdnsInstances) { jmdnsInstances.add(jmdns) }
                    }
                }.awaitAll()

                Logger.log("Server published in mDNS ($SERVICE_TYPE on all available interfaces)",
                    SenderType.LOCAL_SERVER)

            } catch (_: Exception) {
                Logger.log("mDNS publishing error ($SERVICE_TYPE on port: $serverPort)", SenderType.LOCAL_SERVER,
                    MessageType.ERROR)
            }
        }
    }

    fun stop() {
        mDNSScope.launch {
            val copy = synchronized(jmdnsInstances) {
                val list = jmdnsInstances.toList()
                jmdnsInstances.clear()
                list
            }

            if (copy.isEmpty()) return@launch

            val count = copy.size

            copy.map { instance ->
                async {
                    try {
                        instance.unregisterAllServices()
                        instance.close()
                    } catch (e: Exception) {
                        Logger.log("Error stop mdns service: $e", SenderType.LOCAL_SERVER, MessageType.WARNING)
                    }
                }
            }.awaitAll()

            Logger.log("All JmDNS instances ($count) stopped", SenderType.LOCAL_SERVER)
        }
    }
}