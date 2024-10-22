package com.aidarn.gitcook.data

import androidx.room.TypeConverter
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

object ComplexTypeConverters {
    @TypeConverter
    @JvmStatic
    fun toDuration(value: Long?): Duration? {
        return value?.toDuration(DurationUnit.MINUTES)
    }

    @TypeConverter
    @JvmStatic
    fun fromDuration(value: Duration?): Long? {
        return value?.inWholeMinutes
    }

    @TypeConverter
    @JvmStatic
    fun toInstant(value: CharSequence): Instant {
        return Instant.parse(value)
    }

    @TypeConverter
    @JvmStatic
    fun fromInstant(value: Instant): CharSequence {
        return value.toString()
    }

    @TypeConverter
    @JvmStatic
    fun toAny(value: String): Any {
        return try {
            value.toInt()
        } catch (_: NumberFormatException) {
            value
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromAny(value: Any): String {
        return value.toString()
    }
}
