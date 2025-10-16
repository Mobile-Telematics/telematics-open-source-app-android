package com.telematics.core.datastore

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun SharedPreferences.stringEncrypted(
    context: Context,
    defaultValue: String = "",
    key: (KProperty<*>) -> String = KProperty<*>::name,
    encryption: com.telematics.core.encryption.Encryption
): ReadWriteProperty<Any, String> =
    object : ReadWriteProperty<Any, String> {
        override fun getValue(
            thisRef: Any,
            property: KProperty<*>,
        ) = encryption.performRSADecryption(context, getString(key(property), defaultValue))
            ?: defaultValue

        override fun setValue(
            thisRef: Any,
            property: KProperty<*>,
            value: String,
        ) = edit().putString(key(property), encryption.performRSAEncryption(context, value)).apply()
    }

fun SharedPreferences.string(
    defaultValue: String = "",
    key: (KProperty<*>) -> String = KProperty<*>::name,
): ReadWriteProperty<Any, String> =
    object : ReadWriteProperty<Any, String> {
        override fun getValue(
            thisRef: Any,
            property: KProperty<*>,
        ) = getString(key(property), defaultValue) ?: defaultValue

        override fun setValue(
            thisRef: Any,
            property: KProperty<*>,
            value: String,
        ) = edit().putString(key(property), value).apply()
    }

fun SharedPreferences.stringNullable(
    defaultValue: String? = null,
    key: (KProperty<*>) -> String? = KProperty<*>::name,
): ReadWriteProperty<Any, String?> =
    object : ReadWriteProperty<Any, String?> {
        override fun getValue(
            thisRef: Any,
            property: KProperty<*>,
        ) = getString(key(property), defaultValue) ?: defaultValue

        override fun setValue(
            thisRef: Any,
            property: KProperty<*>,
            value: String?,
        ) = edit().putString(key(property), value).apply()
    }

fun SharedPreferences.boolean(
    defaultValue: Boolean = false,
    key: (KProperty<*>) -> String = KProperty<*>::name,
): ReadWriteProperty<Any, Boolean> =
    object : ReadWriteProperty<Any, Boolean> {
        override fun getValue(
            thisRef: Any,
            property: KProperty<*>,
        ) = getBoolean(key(property), defaultValue)

        override fun setValue(
            thisRef: Any,
            property: KProperty<*>,
            value: Boolean,
        ) = edit().putBoolean(key(property), value).apply()
    }

fun SharedPreferences.double(
    defaultValue: Double = Double.NaN,
    key: (KProperty<*>) -> String = KProperty<*>::name,
): ReadWriteProperty<Any, Double> =
    object : ReadWriteProperty<Any, Double> {
        override fun getValue(
            thisRef: Any,
            property: KProperty<*>,
        ) = getDouble(key(property), defaultValue)

        override fun setValue(
            thisRef: Any,
            property: KProperty<*>,
            value: Double,
        ) = edit().putDouble(key(property), value).apply()
    }

fun SharedPreferences.long(
    defaultValue: Long = 0L,
    key: (KProperty<*>) -> String = KProperty<*>::name,
): ReadWriteProperty<Any, Long> =
    object : ReadWriteProperty<Any, Long> {
        override fun getValue(
            thisRef: Any,
            property: KProperty<*>,
        ) = getLong(key(property), defaultValue)

        override fun setValue(
            thisRef: Any,
            property: KProperty<*>,
            value: Long,
        ) = edit().putLong(key(property), value).apply()
    }

fun SharedPreferences.int(
    defaultValue: Int = 0,
    key: (KProperty<*>) -> String = KProperty<*>::name,
): ReadWriteProperty<Any, Int> =
    object : ReadWriteProperty<Any, Int> {
        override fun getValue(
            thisRef: Any,
            property: KProperty<*>,
        ) = getInt(key(property), defaultValue)

        override fun setValue(
            thisRef: Any,
            property: KProperty<*>,
            value: Int,
        ) = edit().putInt(key(property), value).apply()
    }

fun SharedPreferences.Editor.putDouble(key: String, double: Double) =
    putLong(key, java.lang.Double.doubleToRawLongBits(double))

fun SharedPreferences.getDouble(key: String, default: Double) =
    java.lang.Double.longBitsToDouble(getLong(key, java.lang.Double.doubleToRawLongBits(default)))