package com.gyleedev.githubsearch.core.testing

import com.gyleedev.githubsearch.domain.model.UserModel

fun createDummyUser(login: String) = UserModel(
    id = 1L,
    name = "Test Name",
    login = login,
    followers = 10,
    following = 20,
    avatar = "",
    company = null,
    email = null,
    bio = null,
    repoCount = 5,
    createdDate = null,
    updatedDate = null,
    reposAddress = "",
    blogUrl = null,
    favorite = false,
)
