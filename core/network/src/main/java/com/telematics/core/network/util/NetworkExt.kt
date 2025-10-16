package com.telematics.core.network.util

import com.google.gson.Gson
import com.telematics.core.common.NetworkException
import com.telematics.core.network.model.ResponseError
import retrofit2.Response

private fun <T> Response<T>.getFailureResult(): Result<T> =
    try {
        val message = errorBody()?.let {
            Gson().fromJson(
                it.string(),
                ResponseError::class.java
            )?.message
        }
        Result.failure(getRemoteAccessException(code(), message))
    } catch (e: Exception) {
        Result.failure(NetworkException.UnknownException(e.message))
    }

private fun <T> Response<T>.getFailureEmptyResult(): Result<Unit> =
    try {
        val message = errorBody()?.let {
            Gson().fromJson(
                it.string(),
                ResponseError::class.java
            )?.message
        }
        Result.failure(getRemoteAccessException(code(), message))
    } catch (e: Exception) {
        Result.failure(NetworkException.UnknownException(e.message))
    }

fun <T> Response<T>.createResult(): Result<T> {
    val result = body()
    return if (isSuccessful) {
        result?.let { Result.success(it) } ?: Result.failure(NetworkException.UnknownException())
    } else {
        getFailureResult()
    }
}

fun <T> Response<T>.createEmptyResult(): Result<Unit> {
    return if (isSuccessful) {
        Result.success(Unit)
    } else {
        getFailureEmptyResult()
    }
}


fun getRemoteAccessException(errorCode: Int, message: String? = null): NetworkException =
    when (errorCode) {
        400 -> NetworkException.BadRequestException(message ?: "Bad Request")
        401 -> NetworkException.UnauthorizedException(message ?: "Unauthorized")
        403 -> NetworkException.ForbiddenException(message ?: "Forbidden")
        404 -> NetworkException.NotFoundException(message ?: "Not Found")
        410 -> NetworkException.BlockedException(message ?: "Blocked")
        409 -> NetworkException.ConflictException(message ?: "Conflict")
        500 -> NetworkException.ServerErrorException(message ?: "Server error")
        else -> NetworkException.UnknownException(message ?: "Unknown error")
    }