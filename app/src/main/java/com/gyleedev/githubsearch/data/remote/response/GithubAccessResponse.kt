package com.gyleedev.githubsearch.data.remote.response

import com.google.gson.annotations.SerializedName
import com.gyleedev.githubsearch.domain.model.RepositoryModel
import retrofit2.http.Field
import java.io.Serial

data class GithubAccessResponse(
    @SerializedName("client_id") val clientId: String,
    @SerializedName("client_secret") val clientSecret: String,
    @SerializedName("code") val code: String
)
/*
fun GithubAccessResponse.toModel(id: String): RepositoryModel {
    return RepositoryModel(
        name = name,
        userGithubId = id,
        description = description,
        language = language,
        stargazer = stargazer,
        favorite = false
    )
}*/
