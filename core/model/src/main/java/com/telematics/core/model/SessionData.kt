package com.telematics.core.model

interface IAvailableSession

data class SessionData(
    val accessToken: String,
    val refreshToken: String,
) : IAvailableSession {
    fun isEmpty(): Boolean = accessToken.isBlank()
}