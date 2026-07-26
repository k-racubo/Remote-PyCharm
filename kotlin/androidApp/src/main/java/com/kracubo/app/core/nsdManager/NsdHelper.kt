package com.kracubo.app.core.nsdManager

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import android.os.ext.SdkExtensions
import android.util.Log
import androidx.annotation.RequiresExtension
import java.util.concurrent.atomic.AtomicBoolean

class NsdHelper(val context: Context) {
    private val nsdManager: NsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager

    // flag for old resolveService()
    private val isResolving = AtomicBoolean(false)

    private var isDiscoveryActive = AtomicBoolean(false)

    private val serviceInfoCallback = if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) ||
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) &&
            SdkExtensions.getExtensionVersion(Build.VERSION_CODES.TIRAMISU) >= 7) {
        object : NsdManager.ServiceInfoCallback {
            override fun onServiceInfoCallbackRegistrationFailed(errorCode: Int) {
                Log.e("NsdHelper", "Callback registration failed: $errorCode")
            }

            override fun onServiceUpdated(serviceInfo: NsdServiceInfo) {
                handleResolvedService(serviceInfo)
            }

            override fun onServiceLost() {
                Log.i("NsdHelper", "Service is no longer available")
            }

            override fun onServiceInfoCallbackUnregistered() {
                Log.i("NsdHelper", "Callback unregistered")
            }
        }
    } else null

    private val discoveryListener = object : NsdManager.DiscoveryListener {
        override fun onDiscoveryStarted(regType: String?) {
            isDiscoveryActive.set(true)
            Log.i("NsdHelper", "Discovery started")
        }

        @RequiresExtension(extension = Build.VERSION_CODES.TIRAMISU, version = 7)
        override fun onServiceFound(serviceInfo: NsdServiceInfo?) {
            Log.i("NsdHelper", "Service found: ${serviceInfo?.serviceName}")

            if (!isResolving.compareAndSet(false,true)) {
                Log.w("NsdHelper", "Resolve already in progress, skipping...")
                return
            }

            serviceInfo?.let { it ->
                if (serviceInfoCallback != null) {
                    nsdManager.registerServiceInfoCallback(it, { it.run() }, serviceInfoCallback)
                } else {
                    // depricated cause old (but usable for old android versions)
                    nsdManager.resolveService(it, object : NsdManager.ResolveListener {
                        override fun onServiceResolved(resolvedInfo: NsdServiceInfo) {
                            isResolving.set(false)
                            handleResolvedService(resolvedInfo)
                        }

                        override fun onResolveFailed(si: NsdServiceInfo, errorCode: Int) {
                            isResolving.set(false)
                            listener?.onError()
                        }
                    })
                }
            }
        }

        override fun onServiceLost(serviceInfo: NsdServiceInfo?) {
            Log.e("NsdHelper", "Service lost: ${serviceInfo?.serviceName}")
        }

        override fun onDiscoveryStopped(serviceType: String?) {
            isDiscoveryActive.set(false)
            Log.i("NsdHelper", "Discovery stopped")
        }

        override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
            isDiscoveryActive.set(false)
            Log.e("NsdHelper", "Start discovery failed: $errorCode")
            listener?.onError()
        }

        override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {
            Log.e("NsdHelper", "Stop discovery failed: $errorCode")
            listener?.onError()
        }
    }

    interface DiscoveryListener {
        fun onServiceFound(ip: String, port: Int)
        fun onError()
    }

    var listener: DiscoveryListener? = null

    fun discoverService() {
        stopDiscovery()

        nsdManager.discoverServices("_remotepycharm._tcp", NsdManager.PROTOCOL_DNS_SD,
            discoveryListener)
    }

    fun stopDiscovery() {
        if (!isDiscoveryActive.get()) return

        try {
            nsdManager.stopServiceDiscovery(discoveryListener)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && serviceInfoCallback != null) {
                nsdManager.unregisterServiceInfoCallback(serviceInfoCallback)
            }

            Log.i("NsdHelper", "Discovery and callbacks stopped")
        } catch (e: Exception) {
            Log.e("NsdHelper", "Stop error: ${e.message}")
        } finally {
            isResolving.set(false)
            isDiscoveryActive.set(false)
        }
    }

    fun handleResolvedService(serviceInfo: NsdServiceInfo) {
        val host = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            serviceInfo.hostAddresses.firstOrNull()?.hostAddress
        } else {
            @Suppress("DEPRECATION")
            serviceInfo.host?.hostAddress
        }

        val port = serviceInfo.port

        context.getSharedPreferences("local_search_prefs", Context.MODE_PRIVATE).edit().apply {
            putString("LOCAL_SEARCH_IP", host)
            putInt("LOCAL_SEARCH_PORT", port)
            apply()
        }

        Log.i("NsdHelper", "Resolved IP: $host, Port: $port")

        listener?.onServiceFound(host!!, port)
    }
}