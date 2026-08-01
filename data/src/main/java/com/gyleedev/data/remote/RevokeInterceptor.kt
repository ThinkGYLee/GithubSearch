package com.gyleedev.data.remote

import com.gyleedev.data.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class RevokeInterceptor
    @Inject
    constructor() : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val encodedBasic =
                okhttp3.Credentials.basic(
                    BuildConfig.CLIENT_ID,
                    BuildConfig.CLIENT_SECRET,
                )
            val builder = chain.request().newBuilder()

            builder.addHeader("Accept", "application/vnd.github+json")
            builder.addHeader("Authorization", encodedBasic)
            return chain.proceed(builder.build())
        }
    }
