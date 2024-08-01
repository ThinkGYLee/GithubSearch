package com.gyleedev.githubsearch.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.gyleedev.githubsearch.domain.model.RepositoryModel

// 코스 정보
@Entity(
    tableName = "repos",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("user_entity_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]

)
data class ReposEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "user_entity_id")
    val userEntityId: Long,
    @ColumnInfo(name = "user_github_id")
    val userGithubId: String,
    @ColumnInfo(name = "name")
    val name: String?,
    @ColumnInfo(name = "description")
    val description: String?,
    @ColumnInfo(name = "language")
    val language: String?,
    @ColumnInfo(name = "stargazer")
    val stargazer: Int,
    @ColumnInfo(name = "favorite")
    val favorite: Boolean
)

fun ReposEntity.toModel(): RepositoryModel {
    return RepositoryModel(
        name = name,
        userGithubId = userGithubId,
        description = description,
        language = language,
        stargazer = stargazer,
        favorite = favorite
    )
}

fun RepositoryModel.toEntity(userEntityId: Long): ReposEntity {
    return ReposEntity(
        id = 0,
        userEntityId = userEntityId,
        userGithubId = userGithubId,
        description = description,
        name = name,
        stargazer = stargazer,
        language = language,
        favorite = favorite
    )
}
