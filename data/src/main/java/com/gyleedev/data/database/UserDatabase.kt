package com.gyleedev.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gyleedev.data.database.dao.AccessTimeDao
import com.gyleedev.data.database.dao.ReposDao
import com.gyleedev.data.database.dao.UserDao
import com.gyleedev.data.database.entity.AccessTimeEntity
import com.gyleedev.data.database.entity.ReposEntity
import com.gyleedev.data.database.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ReposEntity::class,
        AccessTimeEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(DbTypeConverters::class)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun reposDao(): ReposDao
    abstract fun accessTimeDao(): AccessTimeDao
}
