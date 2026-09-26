package com.ai.frankenstein.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ai.frankenstein.data.local.Converters
import java.util.Date

@Entity(tableName = "messages")
@TypeConverters(Converters::class)
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val chatId: Long,
    val role: MessageRole,
    val content: String,
    val model: String? = null,
    val provider: String? = null,
    val timestamp: Date = Date(),
    val tokenCount: Int = 0,
    val cost: Double = 0.0,
    val isFavorite: Boolean = false,
    val isPinned: Boolean = false,
    val metadata: Map<String, String> = emptyMap(),
    val parentMessageId: Long? = null
) {
    enum class MessageRole {
        USER, ASSISTANT, SYSTEM
    }
}
