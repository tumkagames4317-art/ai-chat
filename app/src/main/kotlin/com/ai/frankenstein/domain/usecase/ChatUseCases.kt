package com.ai.frankenstein.domain.usecase

import android.util.Log
import com.ai.frankenstein.core.Constants
import com.ai.frankenstein.data.repository.ApiKeyRepository
import com.ai.frankenstein.data.repository.ChatRepository
import com.ai.frankenstein.data.repository.ModelRepository
import com.ai.frankenstein.domain.model.Chat
import com.ai.frankenstein.domain.model.Message
import com.ai.frankenstein.domain.model.TokenUsage
import com.ai.frankenstein.domain.model.Model
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.Date

class ChatUseCases(
    private val chatRepository: ChatRepository,
    private val apiKeyRepository: ApiKeyRepository,
    private val modelRepository: ModelRepository
) {
    
    companion object {
        private const val TAG = "ChatUseCases"
    }
    
    // Get all chats
    fun getAllChats(): Flow<List<Chat>> = chatRepository.getAllChats()
    
    // Get chat by ID
    fun getChatById(id: Long): Flow<Chat?> = chatRepository.getChatById(id)
    
    // Create new chat
    suspend fun createNewChat(modelId: String, provider: String, title: String = "New Chat"): Result<Chat> = withContext(Dispatchers.IO) {
        return@withContext try {
            val model = modelRepository.getModelById(modelId).firstOrNull() ?: 
                Model.getDefaultModels().firstOrNull { it.id == modelId } ?: 
                throw Exception("Model not found")
            
            val chat = Chat(
                title = title,
                model = model.id,
                provider = model.provider
            )
            
            val chatId = chatRepository.createChat(chat).getOrThrow()
            val createdChat = chat.copy(id = chatId)
            
            // Add system message
            val systemMessage = Message(
                chatId = chatId,
                role = Message.MessageRole.SYSTEM,
                content = "You are a helpful AI assistant. Be concise and helpful.",
                model = model.id,
                provider = model.provider
            )
            chatRepository.addMessage(systemMessage)
            
            Result.success(createdChat)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create new chat", e)
            Result.failure(e)
        }
    }
    
    // Send message
    suspend fun sendMessage(
        chatId: Long,
        content: String,
        modelId: String? = null
    ): Result<Flow<Message>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val chat = chatRepository.getChatById(chatId).firstOrNull() ?: 
                return@withContext Result.failure(Exception("Chat not found"))
            
            val model = modelId?.let { modelRepository.getModelById(it).firstOrNull() } ?:
                modelRepository.getModelById(chat.model).firstOrNull() ?:
                throw Exception("Model not found")
            
            val userMessage = Message(
                chatId = chatId,
                role = Message.MessageRole.USER,
                content = content,
                model = model.id,
                provider = model.provider,
                tokenCount = countTokens(content)
            )
            
            val messageId = chatRepository.addMessage(userMessage).getOrThrow()
            val userMessageWithId = userMessage.copy(id = messageId)
            
            // Get API key for provider
            val apiKey = apiKeyRepository.getActiveApiKey(model.provider).getOrNull()
            
            if (apiKey == null) {
                return@withContext Result.failure(Exception("No API key for ${model.provider}"))
            }
            
            // Generate assistant response
            val responseFlow = generateResponse(chatId, userMessageWithId, model, apiKey)
            
            Result.success(responseFlow)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Generate response from AI
    private fun generateResponse(
        chatId: Long,
        userMessage: Message,
        model: Model,
        apiKey: com.ai.frankenstein.domain.model.ApiKey
    ): Flow<Message> = flow {
        try {
            // Get chat context
            val messages = chatRepository.getMessagesByChat(chatId).firstOrNull() ?: emptyList()
            
            // Prepare context for API
            val contextMessages = messages.takeLast(Constants.MAX_CONTEXT_TOKENS / 20) // Simple token estimation
                .filter { it.role != Message.MessageRole.SYSTEM || it == messages.firstOrNull() }
                .map { message ->
                    mapOf(
                        "role" to message.role.name.lowercase(),
                        "content" to message.content
                    )
                }
            
            // Add user message
            val requestMessages = contextMessages.toMutableList()
            requestMessages.add(
                mapOf(
                    "role" to "user",
                    "content" to userMessage.content
                )
            )
            
            // Calculate token count for context
            val contextTokens = contextMessages.sumOf { it["content"]?.toString()?.length ?: 0 } / 4
            val userTokens = userMessage.content.length / 4
            
            // Create assistant message placeholder
            val assistantMessage = Message(
                chatId = chatId,
                role = Message.MessageRole.ASSISTANT,
                content = "",
                model = model.id,
                provider = model.provider,
                parentMessageId = userMessage.id
            )
            
            val messageId = chatRepository.addMessage(assistantMessage).getOrThrow()
            val assistantMessageWithId = assistantMessage.copy(id = messageId)
            
            emit(assistantMessageWithId)
            
            // Simulate streaming response (in a real app, this would be actual API calls)
            val responseText = when (model.provider) {
                "openai" -> generateOpenAIResponse(userMessage.content, model, requestMessages)
                "anthropic" -> generateAnthropicResponse(userMessage.content, model, requestMessages)
                "mistral" -> generateMistralResponse(userMessage.content, model, requestMessages)
                "google" -> generateGoogleResponse(userMessage.content, model, requestMessages)
                else -> "I'm an AI assistant. How can I help you with: ${userMessage.content}?"
            }
            
            // Update assistant message with full response
            val totalTokens = countTokens(responseText)
            val cost = (userTokens + totalTokens) * model.inputCost + totalTokens * model.outputCost
            
            val finalMessage = assistantMessageWithId.copy(
                content = responseText,
                tokenCount = totalTokens,
                cost = cost
            )
            chatRepository.updateMessage(finalMessage)
            
            // Record token usage
            val tokenUsage = TokenUsage(
                model = model.id,
                provider = model.provider,
                inputTokens = (contextTokens + userTokens).toLong(),
                outputTokens = totalTokens.toLong(),
                totalTokens = (contextTokens + userTokens + totalTokens).toLong(),
                cost = cost,
                chatId = chatId,
                messageId = messageId
            )
            chatRepository.recordTokenUsage(tokenUsage)
            apiKeyRepository.updateApiKeyUsage(model.provider, totalTokens.toLong(), cost)
            
            emit(finalMessage)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate response", e)
            throw e
        }
    }.flowOn(Dispatchers.IO)
    
    // Simulate OpenAI response
    private suspend fun generateOpenAIResponse(
        prompt: String,
        model: Model,
        messages: List<Map<String, String>>
    ): String {
        // This is a simulation - in a real app, you'd call the OpenAI API
        return when {
            prompt.lowercase().contains("hello") || prompt.lowercase().contains("hi") ->
                "Hello! I'm ${model.name}, an AI assistant created by OpenAI. How can I help you today?"
            prompt.lowercase().contains("code") || prompt.lowercase().contains("program") ->
                "Here's a simple Python code example:\n\n```python\ndef hello_world():\n    print('Hello, World!')\n\nhello_world()\n```\n\nWould you like me to explain or modify this code?"
            prompt.lowercase().contains("summarize") ->
                "Summary: This is a simulated response from ${model.name}. In a real implementation, I would summarize the provided text."
            else -> "I'm ${model.name}, an AI model by OpenAI. I received your message: \"$prompt\" and I'm here to help!"
        }
    }
    
    // Simulate Anthropic response
    private suspend fun generateAnthropicResponse(
        prompt: String,
        model: Model,
        messages: List<Map<String, String>>
    ): String {
        return when {
            prompt.lowercase().contains("hello") || prompt.lowercase().contains("hi") ->
                "Hi there! I'm Claude, an AI assistant by Anthropic. I'm here to be helpful, harmless, and honest. How can I assist you?"
            prompt.lowercase().contains("explain") ->
                "Let me explain this concept in a clear and helpful way..."
            else -> "I'm Claude ${model.name.drop(7)}, an AI by Anthropic. I understand you're asking about: $prompt"
        }
    }
    
    // Simulate Mistral response
    private suspend fun generateMistralResponse(
        prompt: String,
        model: Model,
        messages: List<Map<String, String>>
    ): String {
        return when {
            prompt.lowercase().contains("hello") || prompt.lowercase().contains("hi") ->
                "Bonjour! I'm ${model.name}, a powerful open-source AI model by Mistral AI. How may I assist you?"
            prompt.lowercase().contains("french") ->
                "Je suis un modèle de Mistral AI. Je peux vous aider en français!"
            else -> "I'm ${model.name} from Mistral AI. I've received your query: $prompt"
        }
    }
    
    // Simulate Google response
    private suspend fun generateGoogleResponse(
        prompt: String,
        model: Model,
        messages: List<Map<String, String>>
    ): String {
        return when {
            prompt.lowercase().contains("hello") || prompt.lowercase().contains("hi") ->
                "Hello! I'm ${model.name}, a multimodal AI model by Google. I can help with text, images, and more!"
            else -> "I'm ${model.name} from Google. I understand your request: $prompt"
        }
    }
    
    // Count tokens (simplified - actual token counting would use a tokenizer library)
    private fun countTokens(text: String): Int {
        return text.length / 4 // Very rough estimate
    }
    
    // Delete chat
    suspend fun deleteChat(chat: Chat): Result<Unit> = chatRepository.deleteChat(chat)
    
    // Delete chat by ID
    suspend fun deleteChatById(id: Long): Result<Unit> = chatRepository.deleteChatById(id)
    
    // Toggle favorite
    suspend fun toggleFavoriteChat(chat: Chat): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val updatedChat = chat.copy(isFavorite = !chat.isFavorite)
            chatRepository.updateChat(updatedChat)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Toggle pin
    suspend fun togglePinChat(chat: Chat): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val updatedChat = chat.copy(isPinned = !chat.isPinned)
            chatRepository.updateChat(updatedChat)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Rename chat
    suspend fun renameChat(chat: Chat, newTitle: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val updatedChat = chat.copy(title = newTitle)
            chatRepository.updateChat(updatedChat)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get messages
    fun getMessagesByChat(chatId: Long): Flow<List<Message>> = chatRepository.getMessagesByChat(chatId)
    
    // Toggle favorite message
    suspend fun toggleFavoriteMessage(message: Message): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val updatedMessage = message.copy(isFavorite = !message.isFavorite)
            chatRepository.updateMessage(updatedMessage)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Toggle pin message
    suspend fun togglePinMessage(message: Message): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val updatedMessage = message.copy(isPinned = !message.isPinned)
            chatRepository.updateMessage(updatedMessage)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Search chats
    fun searchChats(query: String): Flow<List<Chat>> = chatRepository.searchChats(query)
    
    // Clear all chats
    suspend fun clearAllChats(): Result<Unit> = chatRepository.clearAllChats()
    
    // Export chat
    suspend fun exportChat(chatId: Long): Result<String> = chatRepository.exportChat(chatId)
    
    // Import chat
    suspend fun importChat(json: String): Result<Long> = chatRepository.importChat(json)
}
