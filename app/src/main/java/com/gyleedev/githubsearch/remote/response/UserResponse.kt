package com.gyleedev.githubsearch.remote.response

import com.google.gson.annotations.SerializedName
import com.gyleedev.githubsearch.domain.model.UserModel

data class UserResponse(
    @SerializedName("name") val name: String,
    @SerializedName("login") val login: String,
    @SerializedName("followers") val followers: Int,
    @SerializedName("following") val following: Int,
    @SerializedName("avatar_url") val avatar: String,
    @SerializedName("company") val company: String,
    @SerializedName("email") val email: String,
    @SerializedName("bio") val bio: String,
    @SerializedName("public_repos") val repos: Int,
    @SerializedName("created_at") val createdDate: String,
    @SerializedName("updated_at") val updatedDate: String,
    @SerializedName("repos_url") val reposAddress: String,
    @SerializedName("blog") val blogUrl: String,
)

fun UserResponse.toModel(): UserModel {
    return UserModel(
        name = name,
        login = login,
        followers = followers,
        following = following,
        avatar = avatar,
        company = company,
        email = email,
        bio = bio,
        repos = repos,
        createdDate = createdDate,
        updatedDate = updatedDate,
        reposAddress = reposAddress,
        blogUrl = blogUrl
    )
}
