package com.gyleedev.githubsearch.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.gyleedev.githubsearch.data.database.entity.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM user ORDER BY id DESC")
    fun allSelect(): PagingSource<Int, UserEntity>

    @Query("SELECT * FROM user ORDER BY id ASC")
    fun getAll(): List<UserEntity>

    @Query("SELECT * FROM user LIMIT 10 OFFSET (:page-1)*10")
    fun getUsers(page: Int): List<UserEntity>

    @Query("SELECT * FROM user WHERE user_id = :id  COLLATE NOCASE")
    fun getUser(id: String): UserEntity

    @Query("SELECT * FROM user WHERE id = :id")
    fun getUserById(id: Long): UserEntity

    @Query("SELECT * FROM user WHERE user_id = :userId COLLATE NOCASE")
    fun getUserByGithubId(userId: String): UserEntity

    @Insert
    fun insertUser(user: UserEntity): Long

    @Query("DELETE FROM user WHERE user_id = :id  COLLATE NOCASE")
    fun deleteUser(id: String)

    @Insert
    fun insert(user: List<UserEntity>)

    @Query("DELETE FROM user")
    fun deleteAll()

    @Query("SELECT * FROM user WHERE favorite = :favorite")
    fun getFavoriteUsers(favorite: Boolean): List<UserEntity>
    @Update
    fun updateUser(user: UserEntity): Int
}