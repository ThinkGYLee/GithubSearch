package com.gyleedev.data.remote

import com.gyleedev.data.remote.response.RepoResponse
import com.gyleedev.data.remote.response.UserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubApiService {
    @GET("users/{user}")
    suspend fun getUser(
        @Path("user") user: String,
    ): Response<UserResponse>

    @GET("users/{user}/repos")
    suspend fun getRepos(
        @Path("user") user: String,
    ): List<RepoResponse>
}
