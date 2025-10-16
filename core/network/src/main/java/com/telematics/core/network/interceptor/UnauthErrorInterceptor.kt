package com.telematics.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.net.HttpURLConnection
import javax.inject.Inject

class UnauthErrorInterceptor @Inject constructor(
    private val onUnauthError: () -> Unit
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
            onUnauthError()
        }

        return response
    }
}
