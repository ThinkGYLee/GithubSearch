package com.gyleedev.data.remote

import com.gyleedev.data.preference.TokenPreference
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TokenInterceptor @Inject constructor(
    private val tokenPreference: TokenPreference,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = tokenPreference.getString()
        val builder = chain.request().newBuilder()

        if (accessToken != "") {
            builder.addHeader("Authorization", "token $accessToken")
        }
        return chain.proceed(builder.build())
    }
}
