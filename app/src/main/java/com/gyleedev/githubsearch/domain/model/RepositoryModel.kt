package com.gyleedev.githubsearch.domain.model

data class RepositoryModel(
    val name: String?,
    val userGithubId: String,
    val description: String?,
    val language: String?,
    val stargazer: Int,
    val favorite: Boolean,
)
