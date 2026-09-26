package com.ai.frankenstein.core.utils

import com.ai.frankenstein.domain.model.Message

/**
 * Token counter utility for estimating token usage
 * Note: This is a simplified implementation. For accurate token counting,
 * you should use the official tokenizer libraries from each provider.
 */
class TokenCounter {
    
    companion object {
        // Average token lengths for different languages
        private const val AVG_TOKEN_LENGTH_ENGLISH = 4.0
        private const val AVG_TOKEN_LENGTH_RUSSIAN = 5.0
        private const val AVG_TOKEN_LENGTH_CHINESE = 2.0
        private const val AVG_TOKEN_LENGTH_JAPANESE = 3.0
        
        // Token counts for special characters
        private const val NEWLINE_TOKENS = 1
        private const val SPACE_TOKENS = 1
        private const val PUNCTUATION_TOKENS = 1
    }
    
    /**
     * Count tokens in a text (simplified estimation)
     */
    fun countTokens(text: String, language: String = "english"): Int {
        if (text.isBlank()) return 0
        
        val avgLength = when (language.lowercase()) {
            "russian", "ru" -> AVG_TOKEN_LENGTH_RUSSIAN
            "chinese", "zh" -> AVG_TOKEN_LENGTH_CHINESE
            "japanese", "ja" -> AVG_TOKEN_LENGTH_JAPANESE
            else -> AVG_TOKEN_LENGTH_ENGLISH
        }
        
        // Simple estimation: text length / average token length
        var count = (text.length / avgLength).toInt()
        
        // Add tokens for newlines
        count += text.count { it == '\n' } * NEWLINE_TOKENS
        
        // Add tokens for spaces
        count += text.count { it == ' ' } / 2
        
        // Add tokens for punctuation
        count += text.count { it in ".!?,;:()[]{}\"" } / 2
        
        return count.coerceAtLeast(1)
    }
    
    /**
     * Count tokens in a message
     */
    fun countMessageTokens(message: Message): Int {
        return countTokens(message.content)
    }
    
    /**
     * Count tokens in a list of messages
     */
    fun countMessagesTokens(messages: List<Message>): Int {
        return messages.sumOf { countMessageTokens(it) }
    }
    
    /**
     * Estimate cost for a given number of tokens and model
     */
    fun estimateCost(
        inputTokens: Int,
        outputTokens: Int,
        inputCostPerToken: Double,
        outputCostPerToken: Double
    ): Double {
        return (inputTokens * inputCostPerToken) + (outputTokens * outputCostPerToken)
    }
    
    /**
     * Get token count for a specific model's context window
     */
    fun getContextWindowTokens(modelId: String): Int {
        // This would be model-specific in a real implementation
        return when (modelId) {
            "gpt-4o", "gpt-4o-mini" -> 16384
            "gpt-3.5-turbo", "gpt-3.5-turbo-16k" -> 16384
            "claude-3-5-sonnet", "claude-3-haiku", "claude-3-opus" -> 8192
            "mistral-large", "mistral-small", "mistral-tiny" -> 32768
            "gemini-1.5-pro", "gemini-1.5-flash" -> 32768
            else -> 4096
        }
    }
    
    /**
     * Check if context exceeds model's limit
     */
    fun isContextWithinLimit(messages: List<Message>, modelId: String, margin: Int = 256): Boolean {
        val totalTokens = countMessagesTokens(messages)
        val limit = getContextWindowTokens(modelId) - margin
        return totalTokens <= limit
    }
    
    /**
     * Truncate context to fit within model's limit
     */
    fun truncateContext(
        messages: List<Message>,
        modelId: String,
        margin: Int = 256
    ): List<Message> {
        val limit = getContextWindowTokens(modelId) - margin
        val totalTokens = countMessagesTokens(messages)
        
        if (totalTokens <= limit) {
            return messages
        }
        
        // Remove oldest messages until we fit
        val result = messages.toMutableList()
        var currentTokens = totalTokens
        
        while (result.size > 1 && currentTokens > limit) {
            val removed = result.removeAt(0)
            currentTokens -= countMessageTokens(removed)
        }
        
        return result
    }
}
