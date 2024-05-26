package com.gyleedev.githubsearch.data.database

import androidx.room.TypeConverter
import java.time.Instant

object DbTypeConverters {

    @TypeConverter
    @JvmStatic
    fun toInstant(value: String?): Instant? {
        return value?.let {
            Instant.parse(value)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromInstant(value: Instant): String {
        return value.toString()
    }
}
