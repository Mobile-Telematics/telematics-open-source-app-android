package com.telematics.core.common.di

import javax.inject.Qualifier

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class SharedPrefs

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DeprecatedSharedPrefs
