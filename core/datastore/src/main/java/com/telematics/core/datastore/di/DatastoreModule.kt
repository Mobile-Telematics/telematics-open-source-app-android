package com.telematics.core.datastore.di

import android.content.Context
import android.content.SharedPreferences
import com.telematics.core.common.di.DeprecatedSharedPrefs
import com.telematics.core.common.di.SharedPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatastoreModule {
    @Singleton
    @Provides
    @SharedPrefs
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        val applicationLabel = context.applicationInfo.loadLabel(context.packageManager).toString()
        return context.getSharedPreferences(
            "${applicationLabel}_app_shared_prefs",
            Context.MODE_PRIVATE
        )
    }

    @Singleton
    @Provides
    @DeprecatedSharedPrefs
    fun provideDeprecatedSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(
            "auth_data",
            Context.MODE_PRIVATE
        )
    }

}