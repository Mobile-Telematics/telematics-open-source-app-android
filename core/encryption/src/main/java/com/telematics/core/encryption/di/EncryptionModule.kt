package com.telematics.core.encryption.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface EncryptionModuleBinds {
    @Singleton
    @Binds
    fun bindEncryption(
        encryption: com.telematics.core.encryption.EncryptionImpl
    ): com.telematics.core.encryption.Encryption
}