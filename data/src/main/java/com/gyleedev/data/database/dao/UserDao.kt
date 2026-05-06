package com.gyleedev.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.gyleedev.data.database.entity.UserEntity
import com.gyleedev.githubsearch.domain.model.FilterStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getUsers(): PagingSource<Int, UserEntity>

    @Query("SELECT * FROM user WHERE favorite = true ")
    fun getUsersAll(): PagingSource<Int, UserEntity>

    @Query("SELECT * FROM user WHERE favorite = true AND repos >0")
    fun getUsersRepo(): PagingSource<Int, UserEntity>

    @Query("SELECT * FROM user WHERE favorite = true AND repos =0")
    fun getUsersNonRepo(): PagingSource<Int, UserEntity>

    @Query("SELECT * FROM user WHERE user_id = :id  COLLATE NOCASE")
    suspend fun getUser(id: String): UserEntity?

    @Query("SELECT * FROM user WHERE user_id = :userId COLLATE NOCASE")
    fun getUserByGithubId(userId: String): Flow<UserEntity?>

    @Insert
    suspend fun insertUser(user: UserEntity): Long

    @Upsert
    suspend fun upsertUser(user: UserEntity)

    fun getUsers(status: FilterStatus): PagingSource<Int, UserEntity> = when (status) {
        FilterStatus.ALL -> getUsersAll()
        FilterStatus.REPO -> getUsersRepo()
        FilterStatus.NOREPO -> getUsersNonRepo()
    }

    @Query("DELETE FROM user")
    suspend fun resetUser()
}
