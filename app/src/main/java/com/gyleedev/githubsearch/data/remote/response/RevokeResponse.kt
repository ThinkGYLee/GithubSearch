package com.gyleedev.githubsearch.data.remote.response

import com.google.gson.annotations.SerializedName

data class RevokeResponse(
    @SerializedName("Status") val status: Int
)
