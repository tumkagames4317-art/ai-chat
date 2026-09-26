package com.ai.frankenstein.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ai.frankenstein.domain.model.TokenUsage
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TokenUsageDao {
    @Query("SELECT * FROM token_usage ORDER BY date DESC")
    fun getAllTokenUsage(): Flow<List<TokenUsage>>
    
    @Query("SELECT * FROM token_usage WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getTokenUsageByDateRange(startDate: Date, endDate: Date): Flow<List<TokenUsage>>
    
    @Query("SELECT * FROM token_usage WHERE model = :model ORDER BY date DESC")
    fun getTokenUsageByModel(model: String): Flow<List<TokenUsage>>
    
    @Query("SELECT * FROM token_usage WHERE provider = :provider ORDER BY date DESC")
    fun getTokenUsageByProvider(provider: String): Flow<List<TokenUsage>>
    
    @Query("SELECT SUM(inputTokens) FROM token_usage WHERE date >= :startDate")
    suspend fun getTotalInputTokens(startDate: Date): Long?
    
    @Query("SELECT SUM(outputTokens) FROM token_usage WHERE date >= :startDate")
    suspend fun getTotalOutputTokens(startDate: Date): Long?
    
    @Query("SELECT SUM(cost) FROM token_usage WHERE date >= :startDate")
    suspend fun getTotalCost(startDate: Date): Double?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTokenUsage(usage: TokenUsage)
    
    @Query("DELETE FROM token_usage WHERE id = :id")
    suspend fun deleteTokenUsage(id: Long)
    
    @Query("DELETE FROM token_usage")
    suspend fun deleteAllTokenUsage()
}
