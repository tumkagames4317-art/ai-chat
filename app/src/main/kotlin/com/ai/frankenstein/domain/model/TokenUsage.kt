package com.ai.frankenstein.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ai.frankenstein.data.local.Converters
import java.util.Date

@Entity(tableName = "token_usage")
@TypeConverters(Converters::class)
data class TokenUsage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val model: String,
    val provider: String,
    val inputTokens: Long = 0,
    val outputTokens: Long = 0,
    val totalTokens: Long = 0,
    val cost: Double = 0.0,
    val date: Date = Date(),
    val chatId: Long? = null,
    val messageId: Long? = null
)
