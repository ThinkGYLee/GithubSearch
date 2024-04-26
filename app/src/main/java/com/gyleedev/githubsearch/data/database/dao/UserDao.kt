package com.gyleedev.githubsearch.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.gyleedev.githubsearch.data.database.entity.UserEntity
import com.gyleedev.githubsearch.domain.model.FilterStatus

@Dao
interface UserDao {

    @Query("SELECT * FROM user LIMIT 10 OFFSET (:page-1)*10")
    fun getUsers(page: Int): List<UserEntity>

    @Query("SELECT * FROM user WHERE favorite = true ")
    fun getUsersAll(): PagingSource<Int, UserEntity>

    @Query("SELECT * FROM user WHERE favorite = true AND repos >0")
    fun getUsersRepo(): PagingSource<Int, UserEntity>

    @Query("SELECT * FROM user WHERE favorite = true AND repos =0")
    fun getUsersNonRepo(): PagingSource<Int, UserEntity>

    @Query("SELECT * FROM user WHERE favorite = :favorite LIMIT 10 OFFSET (:page-1)*10")
    fun getFavorite(page: Int, favorite: Boolean = true): List<UserEntity>

    @Query("SELECT * FROM user WHERE user_id = :id  COLLATE NOCASE")
    fun getUser(id: String): UserEntity

    @Query("SELECT * FROM user WHERE user_id = :userId COLLATE NOCASE")
    fun getUserByGithubId(userId: String): UserEntity

    @Insert
    fun insertUser(user: UserEntity): Long

    @Update
    fun updateUser(user: UserEntity)

    fun getUsers(status: FilterStatus): PagingSource<Int, UserEntity> {
        return when (status) {
            FilterStatus.ALL -> getUsersAll()
            FilterStatus.REPO -> getUsersRepo()
            FilterStatus.NOREPO -> getUsersNonRepo()
        }
    }
}