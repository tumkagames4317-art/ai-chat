package com.ai.frankenstein.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "models")
data class Model(
    @PrimaryKey val id: String,
    val name: String,
    val provider: String,
    val description: String = "",
    val maxTokens: Int = 4096,
    val contextWindow: Int = 4096,
    val inputCost: Double = 0.0,
    val outputCost: Double = 0.0,
    val isCustom: Boolean = false,
    val isActive: Boolean = true,
    val category: String = "text",
    val capabilities: List<String> = emptyList(),
    val endpoint: String? = null,
    val sortOrder: Int = 0
) {
    companion object {
        fun getDefaultModels(): List<Model> {
            return listOf(
                // OpenAI
                Model(
                    id = "gpt-4o",
                    name = "GPT-4o",
                    provider = "openai",
                    description = "Latest OpenAI model with vision and reasoning",
                    maxTokens = 16384,
                    contextWindow = 16384,
                    inputCost = 0.005,
                    outputCost = 0.015,
                    category = "text",
                    capabilities = listOf("text", "vision", "reasoning", "streaming"),
                    sortOrder = 1
                ),
                Model(
                    id = "gpt-4o-mini",
                    name = "GPT-4o Mini",
                    provider = "openai",
                    description = "Faster, cheaper version of GPT-4o",
                    maxTokens = 16384,
                    contextWindow = 16384,
                    inputCost = 0.0015,
                    outputCost = 0.006,
                    category = "text",
                    capabilities = listOf("text", "vision", "streaming"),
                    sortOrder = 2
                ),
                Model(
                    id = "gpt-3.5-turbo",
                    name = "GPT-3.5 Turbo",
                    provider = "openai",
                    description = "Fast and cost-effective model",
                    maxTokens = 16384,
                    contextWindow = 16384,
                    inputCost = 0.0005,
                    outputCost = 0.0015,
                    category = "text",
                    capabilities = listOf("text", "streaming"),
                    sortOrder = 3
                ),
                Model(
                    id = "gpt-3.5-turbo-16k",
                    name = "GPT-3.5 Turbo 16K",
                    provider = "openai",
                    description = "Extended context version",
                    maxTokens = 16384,
                    contextWindow = 16384,
                    inputCost = 0.001,
                    outputCost = 0.002,
                    category = "text",
                    capabilities = listOf("text", "streaming"),
                    sortOrder = 4
                ),
                
                // Anthropic
                Model(
                    id = "claude-3-5-sonnet",
                    name = "Claude 3.5 Sonnet",
                    provider = "anthropic",
                    description = "Fast and intelligent model",
                    maxTokens = 8192,
                    contextWindow = 8192,
                    inputCost = 0.003,
                    outputCost = 0.015,
                    category = "text",
                    capabilities = listOf("text", "vision", "streaming"),
                    sortOrder = 5
                ),
                Model(
                    id = "claude-3-haiku",
                    name = "Claude 3 Haiku",
                    provider = "anthropic",
                    description = "Fastest and cheapest Claude model",
                    maxTokens = 8192,
                    contextWindow = 8192,
                    inputCost = 0.00025,
                    outputCost = 0.00125,
                    category = "text",
                    capabilities = listOf("text", "vision", "streaming"),
                    sortOrder = 6
                ),
                Model(
                    id = "claude-3-opus",
                    name = "Claude 3 Opus",
                    provider = "anthropic",
                    description = "Most powerful Claude model",
                    maxTokens = 8192,
                    contextWindow = 8192,
                    inputCost = 0.015,
                    outputCost = 0.075,
                    category = "text",
                    capabilities = listOf("text", "vision", "reasoning", "streaming"),
                    sortOrder = 7
                ),
                
                // Mistral
                Model(
                    id = "mistral-large",
                    name = "Mistral Large",
                    provider = "mistral",
                    description = "Powerful open-source model",
                    maxTokens = 32768,
                    contextWindow = 32768,
                    inputCost = 0.0025,
                    outputCost = 0.0025,
                    category = "text",
                    capabilities = listOf("text", "streaming"),
                    sortOrder = 8
                ),
                Model(
                    id = "mistral-small",
                    name = "Mistral Small",
                    provider = "mistral",
                    description = "Fast and efficient model",
                    maxTokens = 32768,
                    contextWindow = 32768,
                    inputCost = 0.0005,
                    outputCost = 0.0005,
                    category = "text",
                    capabilities = listOf("text", "streaming"),
                    sortOrder = 9
                ),
                Model(
                    id = "mistral-tiny",
                    name = "Mistral Tiny",
                    provider = "mistral",
                    description = "Lightweight model",
                    maxTokens = 32768,
                    contextWindow = 32768,
                    inputCost = 0.0001,
                    outputCost = 0.0001,
                    category = "text",
                    capabilities = listOf("text", "streaming"),
                    sortOrder = 10
                ),
                
                // Google
                Model(
                    id = "gemini-1.5-pro",
                    name = "Gemini 1.5 Pro",
                    provider = "google",
                    description = "Google's latest AI model",
                    maxTokens = 32768,
                    contextWindow = 32768,
                    inputCost = 0.0025,
                    outputCost = 0.0075,
                    category = "text",
                    capabilities = listOf("text", "vision", "streaming"),
                    sortOrder = 11
                ),
                Model(
                    id = "gemini-1.5-flash",
                    name = "Gemini 1.5 Flash",
                    provider = "google",
                    description = "Faster version of Gemini",
                    maxTokens = 32768,
                    contextWindow = 32768,
                    inputCost = 0.000375,
                    outputCost = 0.001125,
                    category = "text",
                    capabilities = listOf("text", "vision", "streaming"),
                    sortOrder = 12
                ),
                
                // Local / Ollama
                Model(
                    id = "llama3.2",
                    name = "Llama 3.2",
                    provider = "ollama",
                    description = "Local Llama model",
                    maxTokens = 8192,
                    contextWindow = 8192,
                    inputCost = 0.0,
                    outputCost = 0.0,
                    category = "text",
                    capabilities = listOf("text", "streaming"),
                    endpoint = "http://localhost:11434",
                    sortOrder = 20
                ),
                Model(
                    id = "mistral-local",
                    name = "Mistral (Local)",
                    provider = "ollama",
                    description = "Local Mistral model",
                    maxTokens = 32768,
                    contextWindow = 32768,
                    inputCost = 0.0,
                    outputCost = 0.0,
                    category = "text",
                    capabilities = listOf("text", "streaming"),
                    endpoint = "http://localhost:11434",
                    sortOrder = 21
                )
            )
        }
    }
}
