package com.gyleedev.githubsearch.domain.model

import com.google.gson.annotations.SerializedName

data class RevokeRequestBody(
    // data이름 때문에 언더바 사용해야함
    @SerializedName("access_token")
    val accessToken: String
)
