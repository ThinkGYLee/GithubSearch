package com.gyleedev.data.remote

import android.content.Context
import com.gyleedev.data.BuildConfig
import com.gyleedev.data.PreferenceUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    private val apiUrl = "https://api.github.com"
    private val accessUrl = "https://github.com"

    // TODO 이동
    @Singleton
    @Provides
    fun providePreferenceUtil(@ApplicationContext context: Context): PreferenceUtil = PreferenceUtil(context)

    @Singleton
    @Provides
    @TypeApi
    fun provideApiOkHttpClient(preferenceUtil: PreferenceUtil): OkHttpClient = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        val tokenInterceptor = TokenInterceptor(preferenceUtil)
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
    fun provideApiRetrofit(@TypeApi okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(apiUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Singleton
    @Provides
    @TypeApi
    fun provideApiGithubApi(@TypeApi retrofit: Retrofit): GithubApiService =
        retrofit.create(GithubApiService::class.java)

    @Singleton
    @Provides
    @TypeAccess
    fun provideAccessOkHttpClient(): OkHttpClient = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    } else {
        OkHttpClient.Builder().build()
    }

    @Singleton
    @Provides
    @TypeAccess
    fun provideAccessRetrofit(@TypeAccess okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(accessUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Singleton
    @Provides
    @TypeAccess
    fun provideAccessGithubApi(@TypeAccess retrofit: Retrofit): AccessService =
        retrofit.create(AccessService::class.java)

    @Singleton
    @Provides
    @TypeRevoke
    fun provideRevokeOkHttpClient(): OkHttpClient {
        val revokeInterceptor = RevokeInterceptor()
        val interceptor = if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            OkHttpClient.Builder()
                .addNetworkInterceptor(loggingInterceptor)
        } else {
            OkHttpClient.Builder()
        }
        return interceptor.addInterceptor(revokeInterceptor).build()
    }

    @Singleton
    @Provides
    @TypeRevoke
    fun provideRevokeRetrofit(@TypeRevoke okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(apiUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Singleton
    @Provides
    @TypeRevoke
    fun provideRevokeGithubApi(@TypeRevoke retrofit: Retrofit): RevokeService =
        retrofit.create(RevokeService::class.java)
}
