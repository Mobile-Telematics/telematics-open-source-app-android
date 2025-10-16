package com.telematics.core.network.interceptor

import android.content.Context
import com.telematics.core.network.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class UserProviderValuesInterceptor @Inject constructor(
    @param:ApplicationContext private val context: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("X-API-KEY", BuildConfig.X_API_KEY)
            .header("Content-Type", "application/json")
            .header("User-Agent", context.packageName)
            .build()
        return chain.proceed(request)
    }
}