package com.telematics.core.network.interceptor

import android.util.Log
import com.telematics.core.network.errors.ApiError
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import javax.inject.Inject

class ErrorInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        val rawBody = response.body
        val bodyString = rawBody.string() // non-null в OkHttp 5.x

        val code = response.code
        Log.d(
            "ErrorInterceptor",
            "code: $code url: ${request.url.toUri()} msg: ${response.message}"
        )

        try {
            if (bodyString.isNotEmpty()) {
                val json = JSONObject(bodyString)
                if (json.has("Status")) {
                    val status = json.getInt("Status")
                    Log.d("ErrorInterceptor", "apiResponse.status: $status")
                    if (status != 200) {
                        throw ApiError(status)
                    }
                }
            }
        } catch (e: Exception) {
            throw ApiError(code)
        }

        val newBody = bodyString.toResponseBody(rawBody.contentType())

        return response.newBuilder()
            .body(newBody)
            .build()
    }
}

