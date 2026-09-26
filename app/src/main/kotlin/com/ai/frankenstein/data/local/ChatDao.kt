package com.ai.frankenstein.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ai.frankenstein.domain.model.Chat
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats ORDER BY updatedAt DESC")
    fun getAllChats(): Flow<List<Chat>>
    
    @Query("SELECT * FROM chats WHERE id = :id LIMIT 1")
    suspend fun getChatById(id: Long): Chat?
    
    @Query("SELECT * FROM chats WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavoriteChats(): Flow<List<Chat>>
    
    @Query("SELECT * FROM chats WHERE isPinned = 1 ORDER BY updatedAt DESC")
    fun getPinnedChats(): Flow<List<Chat>>
    
    @Query("SELECT * FROM chats WHERE title LIKE :query OR model LIKE :query ORDER BY updatedAt DESC")
    fun searchChats(query: String): Flow<List<Chat>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: Chat): Long
    
    @Update
    suspend fun updateChat(chat: Chat)
    
    @Delete
    suspend fun deleteChat(chat: Chat)
    
    @Query("DELETE FROM chats WHERE id = :id")
    suspend fun deleteChatById(id: Long)
    
    @Query("DELETE FROM chats")
    suspend fun deleteAllChats()
}
