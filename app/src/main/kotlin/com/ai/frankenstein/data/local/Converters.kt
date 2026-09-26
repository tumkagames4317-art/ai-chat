package com.ai.frankenstein.data.local

import androidx.room.TypeConverter
import com.ai.frankenstein.domain.model.Message
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromMessageRole(value: String?): Message.MessageRole? {
        return value?.let {
            try {
                Message.MessageRole.valueOf(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    @TypeConverter
    fun messageRoleToString(role: Message.MessageRole?): String? {
        return role?.name
    }

    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        return value?.split(",")?.map { it.trim() } ?: emptyList()
    }

    @TypeConverter
    fun stringListToString(list: List<String>?): String {
        return list?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun fromStringMap(value: String?): Map<String, String> {
        return value?.let {
            try {
                val entries = it.split(";")
                entries.associate { entry ->
                    val (key, v) = entry.split("=", limit = 2)
                    key to v
                }
            } catch (e: Exception) {
                emptyMap()
            }
        } ?: emptyMap()
    }

    @TypeConverter
    fun stringMapToString(map: Map<String, String>?): String {
        return map?.entries?.joinToString(";") { "${it.key}=${it.value}" } ?: ""
    }
}
