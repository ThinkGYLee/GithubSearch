package com.gyleedev.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.gyleedev.githubsearch.domain.model.UserModel

@Entity(
    tableName = "user",
    indices = [Index(value = ["github_id"], unique = true)],
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "github_id")
    val githubId: String,
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
    val repoCount: Int,
    @ColumnInfo(name = "createdDate")
    val createdDate: String?,
    @ColumnInfo(name = "updatedDate")
    val updatedDate: String?,
    @ColumnInfo(name = "reposAddress")
    val reposAddress: String,
    @ColumnInfo(name = "blogUrl")
    val blogUrl: String?,
    @ColumnInfo(name = "favorite")
    val favorite: Boolean,
)

fun UserModel.toEntity(): UserEntity =
    UserEntity(
        id = id,
        githubId = login,
        name = name,
        followers = followers,
        following = following,
        company = company,
        avatar = avatar,
        email = email,
        bio = bio,
        repoCount = repoCount,
        createdDate = createdDate,
        updatedDate = updatedDate,
        reposAddress = reposAddress,
        blogUrl = blogUrl,
        favorite = favorite,
    )

fun UserEntity.toModel(): UserModel =
    UserModel(
        id = id,
        login = githubId,
        name = name,
        followers = followers,
        following = following,
        company = company,
        avatar = avatar,
        email = email,
        bio = bio,
        repoCount = repoCount,
        createdDate = createdDate,
        updatedDate = updatedDate,
        reposAddress = reposAddress,
        blogUrl = blogUrl,
        favorite = favorite,
    )
