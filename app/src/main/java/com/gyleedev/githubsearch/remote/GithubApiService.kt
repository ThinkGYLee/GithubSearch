package com.gyleedev.githubsearch.remote

import com.gyleedev.githubsearch.remote.response.RepoResponse
import com.gyleedev.githubsearch.remote.response.UserResponse
import retrofit2.http.GET
import retrofit2.http.Path


interface GithubApiService {

    @GET("users/{user}")
    suspend fun getUser(@Path("user") user: String): UserResponse

    @GET("users/{user}/repos")
    suspend fun getRepos(@Path("user") user: String): List<RepoResponse>
}
