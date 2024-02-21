package com.gyleedev.githubsearch.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gyleedev.githubsearch.domain.model.UserModel


@Entity(
    tableName = "user"
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "name")
    val name: String?,
    @ColumnInfo(name = "followers")
    val followers: Int,
    @ColumnInfo(name = "following")
    val following: Int,
    @ColumnInfo(name = "avatar")
    val avatar: String,
    @ColumnInfo(name = "company")
    val company: String?,
    @ColumnInfo(name = "email")
    val email: String?,
    @ColumnInfo(name = "bio")
    val bio: String?,
    @ColumnInfo(name = "repos")
    val repos: Int,
    @ColumnInfo(name = "createdDate")
    val createdDate: String?,
    @ColumnInfo(name = "updatedDate")
    val updatedDate: String?,
    @ColumnInfo(name = "reposAddress")
    val reposAddress: String,
    @ColumnInfo(name = "blogUrl")
    val blogUrl: String?,
    @ColumnInfo(name = "favorite")
    val favorite: Boolean
)


fun UserModel.toEntity(): UserEntity {
    return UserEntity(
        id = 0,
        userId = login,
        name = name,
        followers = followers,
        following = following,
        company = company,
        avatar = avatar,
        email = email,
        bio = bio,
        repos = repos,
        createdDate = createdDate,
        updatedDate = updatedDate,
        reposAddress = reposAddress,
        blogUrl = blogUrl,
        favorite = favorite
    )
}

fun UserEntity.toModel(): UserModel {
    return UserModel(
        login = userId,
        name = name,
        followers = followers,
        following = following,
        company = company,
        avatar = avatar,
        email = email,
        bio = bio,
        repos = repos,
        createdDate = createdDate,
        updatedDate = updatedDate,
        reposAddress = reposAddress,
        blogUrl = blogUrl,
        favorite = favorite
    )
}