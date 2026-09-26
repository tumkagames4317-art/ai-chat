package com.ai.frankenstein.data.repository

import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.ai.frankenstein.core.Constants
import com.ai.frankenstein.data.local.PreferenceManager
import com.ai.frankenstein.domain.model.ApiKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.Date

class ApiKeyRepository(private val preferenceManager: PreferenceManager) {
    
    companion object {
        private const val TAG = "ApiKeyRepository"
        private const val ENCRYPTED_PREFS_NAME = Constants.PREFS_ENCRYPTED
        private const val KEY_API_KEYS = "encrypted_api_keys"
    }
    
    // In-memory cache for active keys
    private val activeKeysCache = mutableMapOf<String, ApiKey>()
    
    init {
        // Initialize encrypted storage
        try {
            val masterKey = MasterKey.Builder(context = preferenceManager.context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            
            val encryptedSharedPrefs = EncryptedSharedPreferences.create(
                preferenceManager.context,
                ENCRYPTED_PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            
            // Store reference
            encryptedPrefs = encryptedSharedPrefs
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize encrypted storage", e)
        }
    }
    
    private var encryptedPrefs: EncryptedSharedPreferences? = null
    
    // Store API key securely
    suspend fun saveApiKey(apiKey: ApiKey): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val prefs = encryptedPrefs ?: throw IllegalStateException("Encrypted storage not initialized")
            
            val key = "api_key_${apiKey.id}_${apiKey.provider}"
            prefs.edit()
                .putString(key, apiKey.key)
                .apply()
            
            // Update cache if active
            if (apiKey.isActive) {
                activeKeysCache[apiKey.provider] = apiKey
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save API key", e)
            Result.failure(e)
        }
    }
    
    // Get API key by ID
    suspend fun getApiKeyById(id: Long): Result<ApiKey?> = withContext(Dispatchers.IO) {
        return@withContext try {
            // For now, return from cache or create a placeholder
            // In a full implementation, we'd retrieve from encrypted storage
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get active API key for a provider
    suspend fun getActiveApiKey(provider: String): Result<ApiKey?> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Check cache first
            activeKeysCache[provider]?.let {
                return@withContext Result.success(it)
            }
            
            // Try to get from encrypted storage
            val prefs = encryptedPrefs ?: throw IllegalStateException("Encrypted storage not initialized")
            
            // This is a simplified version - in production, you'd need to track which keys belong to which providers
            for (key in prefs.all.keys) {
                if (key.startsWith("api_key_") && key.contains("_$provider")) {
                    val apiKeyValue = prefs.getString(key, null) ?: continue
                    // Create a temporary ApiKey object (you'd need to store metadata separately)
                    val apiKey = ApiKey(
                        id = key.hashCode().toLong(),
                        name = "$provider Key",
                        provider = provider,
                        key = apiKeyValue,
                        isActive = true,
                        createdAt = Date()
                    )
                    activeKeysCache[provider] = apiKey
                    return@withContext Result.success(apiKey)
                }
            }
            
            Result.success(null)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get API key for $provider", e)
            Result.failure(e)
        }
    }
    
    // Get all API keys (metadata only, not the actual keys for security)
    fun getAllApiKeysMetadata(): Flow<List<ApiKeyMetadata>> = flow {
        // In a full implementation, this would retrieve from a database
        // For now, emit empty list
        emit(emptyList())
    }.flowOn(Dispatchers.IO)
    
    // Delete API key
    suspend fun deleteApiKey(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val prefs = encryptedPrefs ?: throw IllegalStateException("Encrypted storage not initialized")
            
            // Remove from cache
            activeKeysCache.values.find { it.id == id }?.let { key ->
                activeKeysCache.remove(key.provider)
            }
            
            // Remove from storage
            prefs.edit()
                .remove("api_key_$id")
                .apply()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Update API key usage
    suspend fun updateApiKeyUsage(provider: String, tokens: Long, cost: Double): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            activeKeysCache[provider]?.let { key ->
                val updatedKey = key.copy(
                    tokenUsage = key.tokenUsage + tokens,
                    costUsage = key.costUsage + cost,
                    lastUsedAt = Date(),
                    usageCount = key.usageCount + 1
                )
                activeKeysCache[provider] = updatedKey
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Set active key for provider
    suspend fun setActiveKeyForProvider(provider: String, keyId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            // In a full implementation, update in database
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Validate API key
    suspend fun validateApiKey(provider: String, key: String): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            // In a full implementation, make a test API call
            // For now, just check if key is not empty
            Result.success(key.isNotBlank())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Temporary in-memory storage for demo purposes
    private val demoApiKeys = mutableListOf<ApiKey>()
    
    // For demo: add a key
    suspend fun addDemoKey(apiKey: ApiKey) {
        demoApiKeys.add(apiKey)
        if (apiKey.isActive) {
            activeKeysCache[apiKey.provider] = apiKey
        }
    }
    
    // For demo: get all demo keys
    fun getDemoKeys(): List<ApiKey> = demoApiKeys
    
    data class ApiKeyMetadata(
        val id: Long,
        val name: String,
        val provider: String,
        val isActive: Boolean,
        val createdAt: Date
    )
}
