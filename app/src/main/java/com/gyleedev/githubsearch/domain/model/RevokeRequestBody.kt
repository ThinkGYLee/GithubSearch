package com.gyleedev.githubsearch.domain.model

data class RevokeRequestBody(
    // data이름 때문에 언더바 사용해야함
    val access_token: String,
)
