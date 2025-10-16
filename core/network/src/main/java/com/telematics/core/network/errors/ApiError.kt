package com.telematics.core.network.errors

import java.io.IOException

data class ApiError(
    val errorCode: Int,
    val msg: String? = null,
    //val details: List<ErrorDetailsData>? = null
) : IOException(msg)