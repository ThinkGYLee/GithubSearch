package com.gyleedev.githubsearch.data.remote

import com.gyleedev.githubsearch.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Base64
import javax.inject.Inject

class RevokeInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val clientId = BuildConfig.CLIENT_ID
        val clientSecret = BuildConfig.CLIENT_SECRET
        val encodedBasic: String = Base64.getEncoder().encodeToString(("$clientId:$clientSecret").toByteArray())
        val builder = chain.request().newBuilder()

        builder.addHeader("Authorization", "Basic $encodedBasic")
        return chain.proceed(builder.build())
    }
}
