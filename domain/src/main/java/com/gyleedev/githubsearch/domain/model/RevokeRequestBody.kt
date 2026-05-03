package com.gyleedev.githubsearch.domain.model

import com.google.gson.annotations.SerializedName

data class RevokeRequestBody(
    @SerializedName("access_token")
    val accessToken: String,
)
