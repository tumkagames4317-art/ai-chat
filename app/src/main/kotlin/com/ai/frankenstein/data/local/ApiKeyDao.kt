package com.ai.frankenstein.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ai.frankenstein.domain.model.ApiKey
import kotlinx.coroutines.flow.Flow

@Dao
interface ApiKeyDao {
    @Query("SELECT * FROM api_keys ORDER BY sortOrder ASC, name ASC")
    fun getAllApiKeys(): Flow<List<ApiKey>>
    
    @Query("SELECT * FROM api_keys WHERE provider = :provider AND isActive = 1 ORDER BY lastUsedAt DESC LIMIT 1")
    suspend fun getActiveKeyForProvider(provider: String): ApiKey?
    
    @Query("SELECT * FROM api_keys WHERE isActive = 1 ORDER BY lastUsedAt DESC")
    fun getActiveApiKeys(): Flow<List<ApiKey>>
    
    @Query("SELECT * FROM api_keys WHERE id = :id LIMIT 1")
    suspend fun getApiKeyById(id: Long): ApiKey?
    
    @Query("SELECT * FROM api_keys WHERE provider = :provider ORDER BY name ASC")
    fun getApiKeysByProvider(provider: String): Flow<List<ApiKey>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApiKey(apiKey: ApiKey): Long
    
    @Update
    suspend fun updateApiKey(apiKey: ApiKey)
    
    @Delete
    suspend fun deleteApiKey(apiKey: ApiKey)
    
    @Query("DELETE FROM api_keys WHERE id = :id")
    suspend fun deleteApiKeyById(id: Long)
    
    @Query("UPDATE api_keys SET isActive = :isActive WHERE id = :id")
    suspend fun setApiKeyActive(id: Long, isActive: Boolean)
    
    @Query("UPDATE api_keys SET tokenUsage = tokenUsage + :tokens, costUsage = costUsage + :cost, lastUsedAt = datetime('now'), usageCount = usageCount + 1 WHERE id = :id")
    suspend fun updateUsage(id: Long, tokens: Long, cost: Double)
}
