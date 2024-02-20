package com.gyleedev.githubsearch.domain.model

import com.gyleedev.githubusersearch.domain.model.RepositoryModel
import com.gyleedev.githubusersearch.domain.model.UserModel

sealed interface DetailFeed {
    data class UserProfile(
        val userModel: UserModel
    ) : DetailFeed

    data class UserDetail(
        val userModel: UserModel
    ) : DetailFeed

    data class RepoDetail(
        val repositoryModel: RepositoryModel
    ) : DetailFeed

    data object RepoTitle : DetailFeed

    data object RepoNoItem : DetailFeed
}