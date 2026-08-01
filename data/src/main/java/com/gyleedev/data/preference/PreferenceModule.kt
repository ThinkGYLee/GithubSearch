package com.gyleedev.data.preference

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferenceModule {
    @Binds
    @Singleton
    abstract fun bindTokenPreference(tokenPreferenceImpl: TokenPreferenceImpl): TokenPreference
}
