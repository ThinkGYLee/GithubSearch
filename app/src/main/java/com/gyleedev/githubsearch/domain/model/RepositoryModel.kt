package com.gyleedev.githubsearch.domain.model

data class RepositoryModel(
    val name: String?,
    val description: String?,
    val language: String?,
    val stargazer: Int
)
