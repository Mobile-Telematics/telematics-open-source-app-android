@file:Suppress("DEPRECATION")

package com.telematics.core.encryption

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.util.Calendar
import java.util.GregorianCalendar
import javax.crypto.Cipher
import javax.inject.Inject
import javax.inject.Singleton
import javax.security.auth.x500.X500Principal

@Suppress("SameParameterValue")
@Singleton
class EncryptionImpl @Inject constructor(
    private val logger: com.telematics.core.logger.Logger
) : Encryption {

    companion object {

        private const val MAIN_ALIAS = "MAIN_ALIAS"

        private const val TRANSFORMATION_ASYMMETRIC = "RSA/ECB/PKCS1Padding"

        private const val KEY_LENGTH = 2048
    }

    @Volatile
    private var mKeyPair: KeyPair? = null

    override fun initAndroidKeyStore(context: Context) {
        try {
            if (mKeyPair == null) {
                val keyStore = KeyStore.getInstance("AndroidKeyStore")
                keyStore.load(null)

                mKeyPair = if (!keyStore.containsAlias(MAIN_ALIAS)) {
                    createAndroidKeyStoreAsymmetricKey(context, MAIN_ALIAS)
                } else {
                    getAndroidKeyStoreAsymmetricKeyPair(keyStore, MAIN_ALIAS)
                }
            }
        } catch (e: Exception) {
            logger.e(e.stackTraceToString())
        }
    }

    private fun createAndroidKeyStoreAsymmetricKey(context: Context, alias: String): KeyPair {
        val generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore")

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            initGeneratorWithKeyPairGeneratorSpec(context, generator, alias)
        } else {
            initGeneratorWithKeyGenParameterSpec(generator, alias)
        }

        // Generates Key with given spec and saves it to the KeyStore
        return generator.generateKeyPair()
    }

    private fun initGeneratorWithKeyPairGeneratorSpec(
        context: Context,
        generator: KeyPairGenerator,
        alias: String
    ) {
        val startDate = GregorianCalendar()
        val endDate = GregorianCalendar()
        endDate.add(Calendar.YEAR, 25)

        val builder = KeyPairGeneratorSpec.Builder(context)
            .setAlias(alias)
            .setSerialNumber(BigInteger.ONE)
            .setSubject(X500Principal("CN=$alias CA Certificate"))
            .setStartDate(startDate.time)
            .setEndDate(endDate.time)
            .setKeySize(KEY_LENGTH)

        generator.initialize(builder.build())
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun initGeneratorWithKeyGenParameterSpec(generator: KeyPairGenerator, alias: String) {
        val startDate = GregorianCalendar()
        val endDate = GregorianCalendar()
        endDate.add(Calendar.YEAR, 25)

        val builder = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
            .setCertificateNotBefore(startDate.time)
            .setCertificateNotAfter(endDate.time)
            .setKeySize(KEY_LENGTH)

        generator.initialize(builder.build())
    }


    private fun getAndroidKeyStoreAsymmetricKeyPair(keyStore: KeyStore, alias: String): KeyPair? {
        val privateKey = keyStore.getKey(alias, null) as PrivateKey?
        val publicKey = keyStore.getCertificate(alias)?.publicKey

        return if (privateKey != null && publicKey != null) {
            KeyPair(publicKey, privateKey)
        } else {
            null
        }
    }

    override fun isEncryptionEnabled() = mKeyPair != null

    @Synchronized
    override fun performRSAEncryption(context: Context, data: String?): String? {
        return try {
            when (data) {
                null -> null
                "" -> ""
                else -> {
                    if (!isEncryptionEnabled()) initAndroidKeyStore(context)
                    mKeyPair?.public?.let { key ->
                        val cipher = Cipher.getInstance(TRANSFORMATION_ASYMMETRIC)
                        cipher.init(Cipher.ENCRYPT_MODE, key)

                        var limit = KEY_LENGTH / 8 - 11
                        var position = 0
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        val message = data.toByteArray()
                        while (position < message.size) {
                            if (message.size - position < limit) limit = message.size - position
                            val buffer = cipher.doFinal(message, position, limit)
                            byteArrayOutputStream.write(buffer)
                            position += limit
                        }

                        val bytes = byteArrayOutputStream.toByteArray()

                        Base64.encodeToString(bytes, Base64.DEFAULT)
                    }
                }
            }
        } catch (e: Exception) {
            ""
        }
    }

    @Synchronized
    override fun performRSADecryption(context: Context, data: String?): String? {
        return try {
            when (data) {
                null -> null
                "" -> ""
                else -> {
                    if (!isEncryptionEnabled()) initAndroidKeyStore(context)
                    mKeyPair?.private?.let { key ->
                        val cipher = Cipher.getInstance(TRANSFORMATION_ASYMMETRIC)
                        cipher.init(Cipher.DECRYPT_MODE, key)
                        val encryptedData = Base64.decode(data, Base64.DEFAULT)

                        var limit = KEY_LENGTH / 8
                        var position = 0
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        while (position < encryptedData.size) {
                            if (encryptedData.size - position < limit) limit =
                                encryptedData.size - position
                            val buffer = cipher.doFinal(encryptedData, position, limit)
                            byteArrayOutputStream.write(buffer)
                            position += limit
                        }

                        val decodedData = byteArrayOutputStream.toByteArray()
                        String(decodedData)
                    }
                }
            }
        } catch (e: Exception) {
            ""
        }
    }
}

interface Encryption {
    fun initAndroidKeyStore(context: Context)
    fun isEncryptionEnabled(): Boolean
    fun performRSAEncryption(context: Context, data: String?): String?
    fun performRSADecryption(context: Context, data: String?): String?
}
