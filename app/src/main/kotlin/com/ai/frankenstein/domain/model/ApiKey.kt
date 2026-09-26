package com.ai.frankenstein.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ai.frankenstein.data.local.Converters
import java.util.Date

@Entity(tableName = "api_keys")
@TypeConverters(Converters::class)
data class ApiKey(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val provider: String,
    val key: String,
    val isActive: Boolean = true,
    val createdAt: Date = Date(),
    val lastUsedAt: Date? = null,
    val usageCount: Int = 0,
    val tokenUsage: Long = 0,
    val costUsage: Double = 0.0,
    val metadata: Map<String, String> = emptyMap()
) {
    companion object {
        val SUPPORTED_PROVIDERS = listOf(
            "openai",
            "anthropic",
            "mistral",
            "google",
            "xai",
            "deepseek",
            "ollama",
            "custom"
        )
    }
}
