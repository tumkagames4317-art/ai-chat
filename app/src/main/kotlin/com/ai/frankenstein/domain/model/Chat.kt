package com.ai.frankenstein.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ai.frankenstein.data.local.Converters
import java.util.Date

@Entity(tableName = "chats")
@TypeConverters(Converters::class)
data class Chat(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val model: String,
    val provider: String,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isFavorite: Boolean = false,
    val isPinned: Boolean = false,
    val tags: List<String> = emptyList(),
    val metadata: Map<String, String> = emptyMap(),
    val context: List<Message> = emptyList()
) {
    val lastMessagePreview: String
        get() = context.lastOrNull()?.content?.take(50) ?: ""
}
