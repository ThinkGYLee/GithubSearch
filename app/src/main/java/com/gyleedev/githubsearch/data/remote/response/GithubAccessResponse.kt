package com.gyleedev.githubsearch.data.remote.response

import com.google.gson.annotations.SerializedName

data class GithubAccessResponse(
    @SerializedName("access_token") val accessToken: String
)