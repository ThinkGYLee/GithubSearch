package com.gyleedev.githubsearch.data.remote

import com.gyleedev.githubsearch.util.PreferenceUtil
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TokenInterceptor @Inject constructor(
    private val preferenceUtil: PreferenceUtil,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = preferenceUtil.getString(defValue = "")
        val builder = chain.request().newBuilder()

        builder.addHeader("Authorization", "token $accessToken")
        return chain.proceed(builder.build())
    }
}
