package com.gyleedev.data.database

import androidx.room.TypeConverter
import java.time.Instant

object DbTypeConverters {

    @TypeConverter
    @JvmStatic
    fun toInstant(value: String?): Instant? = value?.let {
        Instant.parse(value)
    }

    @TypeConverter
    @JvmStatic
    fun fromInstant(value: Instant): String = value.toString()
}
