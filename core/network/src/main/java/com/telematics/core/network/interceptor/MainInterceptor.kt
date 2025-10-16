package com.telematics.core.network.interceptor

import android.content.Context
import com.telematics.core.common.di.VersionHeader
import com.telematics.core.datastore.PreferenceStorage
import com.telematics.core.model.SessionData
import com.telematics.core.network.api.RefreshApi
import com.telematics.core.network.model.refresh_token.RefreshRequest
import com.telematics.core.network.model.rest.ApiResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class MainInterceptor @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val preferenceStorage: PreferenceStorage,
    private val refreshApi: RefreshApi,
    @param:VersionHeader private val version: String,
) : Interceptor, Authenticator {
    private val monitor = Any()
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        runBlocking {
            val accessToken = preferenceStorage.accessToken
            request = request.newBuilder()
                .addHeader(AUTHORIZATION_HEADER_NAME, accessToken.buildAuthorizationHeader())
                .header("Content-Type", "application/json")
                .header("User-Agent", context.packageName)
                .addHeader("ClientPlatform", "ANDROID")
                .addHeader("ClientVersion", version)
                .build()
        }
        return chain.proceed(request)
    }

    override fun authenticate(route: Route?, response: Response): Request? {

        if (preferenceStorage.accessToken.isEmpty() || preferenceStorage.refreshToken.isEmpty()) {
            return null
        }

        if (responseCount(response) >= MAX_AUTHENTICATE_TRIES) {
            return null // If we've failed 3 times, give up.
        }

        synchronized(monitor) {
            return runBlocking {
                val request = response.request
                val sessionData = SessionData(
                    accessToken = preferenceStorage.accessToken,
                    refreshToken = preferenceStorage.refreshToken
                )

                val accessToken = preferenceStorage.accessToken

                if (response.request.header(AUTHORIZATION_HEADER_NAME) != accessToken.buildAuthorizationHeader()) {
                    request.newBuilder().removeHeader(AUTHORIZATION_HEADER_NAME)
                        .addHeader(
                            AUTHORIZATION_HEADER_NAME,
                            accessToken.buildAuthorizationHeader()
                        )
                        .header("User-Agent", context.packageName)
                        .build()
                } else {
                    try {
                        refreshApi.refreshToken(
                            RefreshRequest(
                                sessionData.accessToken,
                                sessionData.refreshToken
                            )
                        ).run {
                            when (status) {
                                in 200..299 -> {
                                    result?.toSessionData()?.let { newSession ->
                                        preferenceStorage.accessToken = newSession.accessToken
                                        preferenceStorage.refreshToken = newSession.refreshToken
                                        request
                                            .newBuilder()
                                            .removeHeader(AUTHORIZATION_HEADER_NAME)
                                            .addHeader(
                                                AUTHORIZATION_HEADER_NAME,
                                                newSession.accessToken.buildAuthorizationHeader()
                                            )
                                            .header("User-Agent", context.packageName)
                                            .build()

                                    }
                                }

                                else -> {
                                    null
                                }
                            }
                        }
                    } catch (e: Exception) {
                        null
                    }
                }
            }
        }
    }

    private fun String.buildAuthorizationHeader() = "Bearer $this".trim()
    private fun responseCount(response: Response?): Int {
        var responseCurrent = response
        var result = 0
        while (responseCurrent != null) {
            responseCurrent = responseCurrent.priorResponse
            result++
        }
        return result
    }

    companion object {
        private const val MAX_AUTHENTICATE_TRIES = 3
        private const val AUTHORIZATION_HEADER_NAME = "Authorization"
    }

    private fun ApiResult?.toSessionData(): SessionData {
        return SessionData(
            this?.accessToken?.token ?: "",
            this?.refreshToken ?: "",
        )
    }
}