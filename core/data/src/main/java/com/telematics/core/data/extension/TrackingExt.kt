package com.telematics.core.data.extension

import com.telematicssdk.tracking.services.main.elm.ElmDevice
import com.telematicssdk.tracking.services.main.elm.managers.ElmLinkingError
import com.telematicssdk.tracking.services.main.elm.managers.ElmLinkingListener
import com.telematicssdk.tracking.services.main.elm.managers.VehicleElmManager
import com.telematics.core.model.tracking.ElmManagerLinkingResult
import com.telematics.core.network.mappers.toElmDevice
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun VehicleElmManager.awaitLinkingListener(): ElmManagerLinkingResult =
    suspendCoroutine { continuation ->

        registerLinkingListener(object : ElmLinkingListener {
            override fun onLinkingComplete(vehicleToken: String, elmMAC: String) {
                ElmManagerLinkingResult(
                    isLinkingComplete = true,
                    vehicleToken,
                    elmMAC,
                    isScanningComplete = false,
                    null,
                    null
                ).apply {
                    continuation.resume(this)
                }
            }

            override fun onLinkingFailed(error: ElmLinkingError) {
                ElmManagerLinkingResult(
                    isLinkingComplete = false,
                    null,
                    null,
                    isScanningComplete = false,
                    null,
                    error.name
                ).apply {
                    continuation.resume(this)
                }
            }

            override fun onScanningComplete(foundDevices: List<ElmDevice>) {
                ElmManagerLinkingResult(
                    isLinkingComplete = false,
                    null,
                    null,
                    isScanningComplete = true,
                    foundDevices.map { it.toElmDevice() },
                    null
                ).apply {
                    continuation.resume(this)
                }
            }

            override fun onScanningFailed(error: ElmLinkingError) {
                ElmManagerLinkingResult(
                    isLinkingComplete = false,
                    null,
                    null,
                    isScanningComplete = false,
                    null,
                    error.name
                ).apply {
                    continuation.resume(this)
                }
            }
        })
    }