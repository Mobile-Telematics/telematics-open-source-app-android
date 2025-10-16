package com.telematics.core.common

import java.io.IOException

class OfflineException(message: String? = null) : IOException(message)

sealed class NetworkException(message: String?) : Exception(message) {
    class BadRequestException(message: String?) : NetworkException(message)
    class UnauthorizedException(message: String?) : NetworkException(message)
    class ForbiddenException(message: String?) : NetworkException(message)
    class NotFoundException(message: String?) : NetworkException(message)
    class BlockedException(message: String?) : NetworkException(message)
    class ConflictException(message: String?) : NetworkException(message)
    class UnknownException(message: String? = null) : NetworkException(message)
    class ServerErrorException(message: String?) : NetworkException(message)
    data object NoNetwork : NetworkException("Check your network connection") {
        private fun readResolve(): Any = NoNetwork
    }
}