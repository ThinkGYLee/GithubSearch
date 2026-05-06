package com.gyleedev.githubsearch.domain.model

import java.time.Instant

data class AccessTime(
    val id: Long,
    val githubId: String,
    val accessTime: Instant,
    val isRepoFetched: Boolean,
)
