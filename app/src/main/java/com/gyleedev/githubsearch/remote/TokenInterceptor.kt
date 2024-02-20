package com.gyleedev.githubsearch.remote

import com.gyleedev.githubsearch.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = BuildConfig.GPGKEY
        val tokenKey = "token $token"
        val builder = chain.request().newBuilder()
        builder.addHeader("Authorization", tokenKey)
        return chain.proceed(builder.build())
    }
}