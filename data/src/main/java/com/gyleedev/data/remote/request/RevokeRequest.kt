package com.gyleedev.data.remote.request

import com.google.gson.annotations.SerializedName

data class RevokeRequest(
    @SerializedName("access_token")
    val accessToken: String,
)
