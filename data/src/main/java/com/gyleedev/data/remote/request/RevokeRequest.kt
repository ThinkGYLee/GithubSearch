package com.gyleedev.data.remote.request

import com.google.gson.annotations.SerializedName
import com.gyleedev.githubsearch.domain.model.RevokeRequestBody

data class RevokeRequest(
    @SerializedName("access_token")
    val accessToken: String,
)

fun RevokeRequestBody.toRequest(): RevokeRequest = RevokeRequest(
    accessToken = accessToken,
)
