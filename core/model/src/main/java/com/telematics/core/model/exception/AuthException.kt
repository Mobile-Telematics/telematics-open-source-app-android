package com.telematics.core.model.exception

import java.io.IOException

class AuthException(val errorCode: AuthErrorCode) : IOException()