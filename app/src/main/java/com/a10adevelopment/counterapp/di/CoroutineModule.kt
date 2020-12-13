package com.a10adevelopment.counterapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object CoroutineModule {
    @Provides
    @Singleton
    fun provideAppCoroutine(): CoroutineScope {
        return CoroutineScope(SupervisorJob())
    }
}