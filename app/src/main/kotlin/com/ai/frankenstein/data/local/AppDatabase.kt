package com.ai.frankenstein.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ai.frankenstein.domain.model.ApiKey
import com.ai.frankenstein.domain.model.Chat
import com.ai.frankenstein.domain.model.Message
import com.ai.frankenstein.domain.model.TokenUsage

@Database(
    entities = [Chat::class, Message::class, ApiKey::class, TokenUsage::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun apiKeyDao(): ApiKeyDao
    abstract fun tokenUsageDao(): TokenUsageDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ai_frankenstein_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
        
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
