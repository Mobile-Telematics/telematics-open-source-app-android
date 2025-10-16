package com.telematics.core.model

enum class UserTypeByEmail(val code: Int) {
    NEW_USER(0),
    REGULAR_USER(1),
    NO_USER(2),
    UNDEFINED(-1)
}