package com.gyleedev.githubsearch.data.remote

import com.gyleedev.githubsearch.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class TypeAccess

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class TypeApi

    @Provides
    fun provideBaseApiUrl() = "https://api.github.com"

    @Provides
    fun provideBaseAccessUrl() = "https://github.com"

    @Singleton
    @Provides
    @TypeApi
    fun provideApiOkHttpClient(): OkHttpClient = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        val tokenInterceptor = TokenInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor)
            .addNetworkInterceptor(loggingInterceptor)
            .build()
    } else {
        OkHttpClient.Builder().build()
    }

    @Singleton
    @Provides
    @TypeApi
    fun provideApiRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(provideBaseApiUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    @TypeApi
    fun provideApiGithubApi(retrofit: Retrofit): GithubApiService {
        return retrofit.create(GithubApiService::class.java)
    }

    @Singleton
    @Provides
    @TypeAccess
    fun provideAccessOkHttpClient(): OkHttpClient = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        val tokenInterceptor = TokenInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor)
            .addNetworkInterceptor(loggingInterceptor)
            .build()
    } else {
        OkHttpClient.Builder().build()
    }

    @Singleton
    @Provides
    @TypeAccess
    fun provideAccessRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(provideBaseApiUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    @TypeAccess
    fun provideAccessGithubApi(retrofit: Retrofit): GithubApiService {
        return retrofit.create(GithubApiService::class.java)
    }

}