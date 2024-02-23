package com.gyleedev.githubsearch.domain.model

import androidx.compose.runtime.Stable

@Stable
data class UserModel(
    val name: String?,
    val login: String,
    val followers: Int,
    val following: Int,
    val avatar: String,
    val company: String?,
    val email: String?,
    val bio: String?,
    val repos: Int,
    val createdDate: String?,
    val updatedDate: String?,
    val reposAddress: String,
    val blogUrl: String?,
    val favorite: Boolean
)
