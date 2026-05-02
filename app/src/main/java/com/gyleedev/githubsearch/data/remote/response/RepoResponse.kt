package com.gyleedev.githubsearch.data.remote.response

import com.google.gson.annotations.SerializedName
import com.gyleedev.githubsearch.domain.model.RepositoryModel

data class RepoResponse(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("language") val language: String,
    @SerializedName("stargazers_count") val stargazer: Int,
)

fun RepoResponse.toModel(id: String): RepositoryModel {
    return RepositoryModel(
        name = name,
        userGithubId = id,
        description = description,
        language = language,
        stargazer = stargazer,
        favorite = false,
    )
}
