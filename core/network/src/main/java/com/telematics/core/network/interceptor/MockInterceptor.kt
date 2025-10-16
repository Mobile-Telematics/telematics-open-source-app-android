package com.telematics.core.network.interceptor

import com.google.gson.Gson
import com.telematics.core.network.model.ResponseError
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.io.IOException


class MockInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        val encodedPath = chain.request().url.encodedPath

        var code = 200
        var bodyJson = ""

        when (encodedPath) {
            "/example/mobileapp/v1/dashboard" -> {

                code = 410
                bodyJson = Gson().toJson(
                    ResponseError(
                        message = ""
                    )
                )
            }
        }

        val response = if (bodyJson.isEmpty()) {
            chain.proceed(request)
        } else {
            Response.Builder()
                .code(code)
                .message("Mock response")
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .body(bodyJson.toResponseBody("application/json".toMediaType()))
                .addHeader("content-type", "application/json")
                .build()
        }

        return response
    }
}

fun bodyToString(request: RequestBody?): String? {
    return try {
        val buffer = Buffer()
        if (request != null) request.writeTo(buffer) else return ""
        buffer.readUtf8()
    } catch (e: IOException) {
        null
    }
}