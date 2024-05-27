package com.gyleedev.githubsearch.domain.model

sealed interface DetailFeed {
    data class UserProfile(
        val userModel: UserModel,
    ) : DetailFeed

    data class UserDetail(
        val userModel: UserModel,
    ) : DetailFeed

    data class RepoDetail(
        val repositoryModel: RepositoryModel,
    ) : DetailFeed

    data object RepoTitle : DetailFeed

    data object RepoNoItem : DetailFeed
}
