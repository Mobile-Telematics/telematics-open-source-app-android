package com.telematics.core.network.util

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import javax.inject.Inject
import kotlin.let

@Suppress("DEPRECATION")
open class HasNetworkConnection @Inject constructor(
    private val connectivityManager: ConnectivityManager?,
) {
    open operator fun invoke(): Boolean {
        return connectivityManager?.isCurrentlyConnected() ?: false
    }

    @Suppress("DEPRECATION")
    private fun ConnectivityManager.isCurrentlyConnected() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
            activeNetwork
                ?.let(::getNetworkCapabilities)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

        else -> activeNetworkInfo?.isConnected
    } ?: false
}