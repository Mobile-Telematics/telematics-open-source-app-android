package com.telematics.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.net.HttpURLConnection
import javax.inject.Inject

class UnsupportedVersionErrorInterceptor @Inject constructor(
    private val onUnsupportedVersionError: () -> Unit
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.code == HttpURLConnection.HTTP_GONE) {

            onUnsupportedVersionError()
        }

        return response
    }
}
