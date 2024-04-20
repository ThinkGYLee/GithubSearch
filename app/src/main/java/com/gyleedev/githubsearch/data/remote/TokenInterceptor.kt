package com.gyleedev.githubsearch.data.remote

import com.gyleedev.githubsearch.GithubSearchApplication
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TokenInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val accessToken = GithubSearchApplication.prefs.getString(defValue = "")
        val builder = chain.request().newBuilder()

        builder.addHeader("Authorization", "token $accessToken")
        return chain.proceed(builder.build())
    }
}