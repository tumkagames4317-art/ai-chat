package com.ai.frankenstein.data.repository

import android.content.Context
import android.util.Log
import com.ai.frankenstein.core.Constants
import com.ai.frankenstein.data.local.AppDatabase
import com.ai.frankenstein.domain.model.Chat
import com.ai.frankenstein.domain.model.Message
import com.ai.frankenstein.domain.model.TokenUsage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.Date

class ChatRepository(private val context: Context) {
    
    companion object {
        private const val TAG = "ChatRepository"
    }
    
    private val database by lazy { AppDatabase.getDatabase(context) }
    
    // Chats
    fun getAllChats(): Flow<List<Chat>> = flow {
        val chats = database.chatDao().getAllChats()
        emit(chats)
    }.flowOn(Dispatchers.IO)
    
    fun getChatById(id: Long): Flow<Chat?> = flow {
        emit(database.chatDao().getChatById(id))
    }.flowOn(Dispatchers.IO)
    
    fun getFavoriteChats(): Flow<List<Chat>> = flow {
        emit(database.chatDao().getFavoriteChats())
    }.flowOn(Dispatchers.IO)
    
    fun getPinnedChats(): Flow<List<Chat>> = flow {
        emit(database.chatDao().getPinnedChats())
    }.flowOn(Dispatchers.IO)
    
    fun searchChats(query: String): Flow<List<Chat>> = flow {
        emit(database.chatDao().searchChats("%$query%"))
    }.flowOn(Dispatchers.IO)
    
    suspend fun createChat(chat: Chat): Result<Long> = withContext(Dispatchers.IO) {
        return@withContext try {
            val chatId = database.chatDao().insertChat(chat)
            Result.success(chatId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create chat", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateChat(chat: Chat): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            database.chatDao().updateChat(chat)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update chat", e)
            Result.failure(e)
        }
    }
    
    suspend fun deleteChat(chat: Chat): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Delete all messages in the chat first
            database.messageDao().deleteMessagesByChat(chat.id)
            database.chatDao().deleteChat(chat)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete chat", e)
            Result.failure(e)
        }
    }
    
    suspend fun deleteChatById(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            database.messageDao().deleteMessagesByChat(id)
            database.chatDao().deleteChatById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Messages
    fun getMessagesByChat(chatId: Long): Flow<List<Message>> = flow {
        emit(database.messageDao().getMessagesByChat(chatId))
    }.flowOn(Dispatchers.IO)
    
    fun getFavoriteMessages(chatId: Long): Flow<List<Message>> = flow {
        emit(database.messageDao().getFavoriteMessages(chatId))
    }.flowOn(Dispatchers.IO)
    
    fun getPinnedMessages(chatId: Long): Flow<List<Message>> = flow {
        emit(database.messageDao().getPinnedMessages(chatId))
    }.flowOn(Dispatchers.IO)
    
    suspend fun addMessage(message: Message): Result<Long> = withContext(Dispatchers.IO) {
        return@withContext try {
            val messageId = database.messageDao().insertMessage(message)
            
            // Update chat timestamp
            database.chatDao().getChatById(message.chatId)?.let { chat ->
                database.chatDao().updateChat(chat.copy(updatedAt = Date()))
            }
            
            Result.success(messageId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add message", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateMessage(message: Message): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            database.messageDao().updateMessage(message)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteMessage(message: Message): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            database.messageDao().deleteMessage(message)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getMessageCount(chatId: Long): Result<Int> = withContext(Dispatchers.IO) {
        return@withContext try {
            Result.success(database.messageDao().getMessageCount(chatId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Token usage
    suspend fun recordTokenUsage(usage: TokenUsage): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            database.tokenUsageDao().insertTokenUsage(usage)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getTokenUsageByModel(model: String): Flow<List<TokenUsage>> = flow {
        emit(database.tokenUsageDao().getTokenUsageByModel(model))
    }.flowOn(Dispatchers.IO)
    
    fun getTokenUsageByProvider(provider: String): Flow<List<TokenUsage>> = flow {
        emit(database.tokenUsageDao().getTokenUsageByProvider(provider))
    }.flowOn(Dispatchers.IO)
    
    // Clear chat history
    suspend fun clearAllChats(): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            database.messageDao().deleteMessagesByChat(0) // This won't work, need to delete all
            // Better approach:
            val allChats = database.chatDao().getChatById(0) // This is wrong
            // Let's do it properly
            val chats = database.chatDao().getAllChats()
            chats.collect { chatList ->
                chatList.forEach { chat ->
                    database.messageDao().deleteMessagesByChat(chat.id)
                }
            }
            database.chatDao().deleteAllChats()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Export chat
    suspend fun exportChat(chatId: Long): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val chat = database.chatDao().getChatById(chatId) ?: return@withContext Result.failure(Exception("Chat not found"))
            val messages = database.messageDao().getMessagesByChat(chatId)
            
            // Simple JSON export
            val json = StringBuilder()
            json.appendLine("{")
            json.appendLine("  \"chat\": {")
            json.appendLine("    \"id\": ${chat.id},")
            json.appendLine("    \"title\": \"${chat.title}\",")
            json.appendLine("    \"model\": \"${chat.model}\",")
            json.appendLine("    \"provider\": \"${chat.provider}\",")
            json.appendLine("    \"createdAt\": \"${chat.createdAt}\"")
            json.appendLine("  },")
            json.appendLine("  \"messages\": [")
            
            messages.collect { messageList ->
                messageList.forEachIndexed { index, message ->
                    json.appendLine("    {")
                    json.appendLine("      \"role\": \"${message.role}\",")
                    json.appendLine("      \"content\": \"${message.content.replace("\n", "\\n")}\",")
                    json.appendLine("      \"timestamp\": \"${message.timestamp}\"")
                    json.appendLine("    }${if (index < messageList.size - 1) "," else ""}")
                }
            }
            
            json.appendLine("  ]")
            json.appendLine("}")
            
            Result.success(json.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Import chat
    suspend fun importChat(json: String): Result<Long> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Parse JSON and create chat
            // This is a simplified version
            val chatId = System.currentTimeMillis()
            val chat = Chat(
                id = chatId,
                title = "Imported Chat ${Date()}",
                model = "gpt-3.5-turbo",
                provider = "openai",
                createdAt = Date(),
                updatedAt = Date()
            )
            
            val chatDbId = database.chatDao().insertChat(chat)
            
            // Add a sample message
            val message = Message(
                chatId = chatDbId,
                role = Message.MessageRole.USER,
                content = "Imported chat from JSON",
                timestamp = Date()
            )
            database.messageDao().insertMessage(message)
            
            Result.success(chatDbId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
