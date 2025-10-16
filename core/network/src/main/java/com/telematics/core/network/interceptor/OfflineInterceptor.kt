package com.telematics.core.network.interceptor

import com.telematics.core.common.OfflineException
import com.telematics.core.network.util.HasNetworkConnection
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class OfflineInterceptor @Inject constructor(
    private val hasNetworkConnection: HasNetworkConnection,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!hasNetworkConnection()) throw OfflineException()

        val request: Request = chain.request()
        val response = chain.proceed(request)
        return response
    }
}
