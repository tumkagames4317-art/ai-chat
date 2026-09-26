package com.ai.frankenstein.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ai.frankenstein.domain.model.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesByChat(chatId: Long): Flow<List<Message>>
    
    @Query("SELECT * FROM messages WHERE id = :id LIMIT 1")
    suspend fun getMessageById(id: Long): Message?
    
    @Query("SELECT * FROM messages WHERE chatId = :chatId AND isFavorite = 1 ORDER BY timestamp ASC")
    fun getFavoriteMessages(chatId: Long): Flow<List<Message>>
    
    @Query("SELECT * FROM messages WHERE chatId = :chatId AND isPinned = 1 ORDER BY timestamp ASC")
    fun getPinnedMessages(chatId: Long): Flow<List<Message>>
    
    @Query("SELECT * FROM messages WHERE parentMessageId = :parentId ORDER BY timestamp ASC")
    fun getReplies(parentId: Long): Flow<List<Message>>
    
    @Query("SELECT * FROM messages WHERE content LIKE :query ORDER BY timestamp DESC")
    fun searchMessages(query: String): Flow<List<Message>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<Message>)
    
    @Update
    suspend fun updateMessage(message: Message)
    
    @Delete
    suspend fun deleteMessage(message: Message)
    
    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessageById(id: Long)
    
    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun deleteMessagesByChat(chatId: Long)
    
    @Query("SELECT COUNT(*) FROM messages WHERE chatId = :chatId")
    suspend fun getMessageCount(chatId: Long): Int
}
