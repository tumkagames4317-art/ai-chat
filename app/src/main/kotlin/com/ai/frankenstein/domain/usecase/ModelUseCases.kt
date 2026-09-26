package com.ai.frankenstein.domain.usecase

import com.ai.frankenstein.data.repository.ApiKeyRepository
import com.ai.frankenstein.data.repository.ModelRepository
import com.ai.frankenstein.domain.model.ApiKey
import com.ai.frankenstein.domain.model.Model
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class ModelUseCases(
    private val modelRepository: ModelRepository,
    private val apiKeyRepository: ApiKeyRepository
) {
    
    // Get all models
    fun getAllModels(): Flow<List<Model>> = modelRepository.getAllModels()
    
    // Get models by provider
    fun getModelsByProvider(provider: String): Flow<List<Model>> = modelRepository.getModelsByProvider(provider)
    
    // Get models by category
    fun getModelsByCategory(category: String): Flow<List<Model>> = modelRepository.getModelsByCategory(category)
    
    // Get model by ID
    fun getModelById(id: String): Flow<Model?> = modelRepository.getModelById(id)
    
    // Get default model
    fun getDefaultModel(): Flow<Model> = modelRepository.getDefaultModel()
    
    // Search models
    fun searchModels(query: String): Flow<List<Model>> = modelRepository.searchModels(query)
    
    // Get available providers (those with API keys)
    fun getAvailableProviders(): Flow<List<String>> = flow {
        val allProviders = Model.SUPPORTED_PROVIDERS
        val providersWithKeys = mutableListOf<String>()
        
        // Check which providers have active keys
        // This is a simplified version - in a full implementation, you'd query the actual keys
        allProviders.forEach { provider ->
            // For demo purposes, assume all providers are available
            providersWithKeys.add(provider)
        }
        
        emit(providersWithKeys)
    }
    
    // Get available models (those with API keys for their provider)
    fun getAvailableModels(): Flow<List<Model>> = combine(
        modelRepository.getAllModels(),
        getAvailableProviders()
    ) { models, providers ->
        models.filter { model ->
            providers.contains(model.provider)
        }
    }
    
    // Get best model for task
    fun getBestModelForTask(taskType: String): Flow<Model?> = flow {
        val availableProviders = getAvailableProviders().firstOrNull() ?: emptyList()
        val model = modelRepository.getBestModelForTask(taskType, availableProviders)
        emit(model)
    }
    
    // Add custom model
    suspend fun addCustomModel(model: Model): Result<Unit> = modelRepository.addCustomModel(model)
    
    // Update custom model
    suspend fun updateCustomModel(model: Model): Result<Unit> = modelRepository.updateCustomModel(model)
    
    // Delete custom model
    suspend fun deleteCustomModel(id: String): Result<Unit> = modelRepository.deleteCustomModel(id)
    
    // Get fallback models
    fun getFallbackModels(provider: String, excludeModelId: String? = null): List<Model> {
        return modelRepository.getFallbackModels(provider, excludeModelId)
    }
    
    // Test API key
    suspend fun testApiKey(provider: String, key: String): Result<Boolean> {
        return apiKeyRepository.validateApiKey(provider, key)
    }
    
    // Save API key (for demo purposes)
    suspend fun saveApiKey(apiKey: ApiKey): Result<Unit> {
        return apiKeyRepository.saveApiKey(apiKey)
    }
    
    // Get active API key for provider
    suspend fun getActiveApiKey(provider: String): Result<ApiKey?> {
        return apiKeyRepository.getActiveApiKey(provider)
    }
    
    // Get all API keys (demo)
    fun getAllApiKeys(): List<ApiKey> {
        return apiKeyRepository.getDemoKeys()
    }
    
    // Add demo API key
    suspend fun addDemoApiKey(apiKey: ApiKey) {
        apiKeyRepository.addDemoKey(apiKey)
    }
}
