package com.ai.frankenstein.data.repository

import com.ai.frankenstein.domain.model.Model
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ModelRepository {
    
    private val models = mutableListOf<Model>()
    private val customModels = mutableListOf<Model>()
    
    init {
        // Load default models
        models.addAll(Model.getDefaultModels())
    }
    
    // Get all models
    fun getAllModels(): Flow<List<Model>> = flow {
        emit(models.sortedBy { it.sortOrder })
    }
    
    // Get models by provider
    fun getModelsByProvider(provider: String): Flow<List<Model>> = flow {
        emit(models.filter { it.provider == provider }.sortedBy { it.sortOrder })
    }
    
    // Get models by category
    fun getModelsByCategory(category: String): Flow<List<Model>> = flow {
        emit(models.filter { it.category == category }.sortedBy { it.sortOrder })
    }
    
    // Get model by ID
    fun getModelById(id: String): Flow<Model?> = flow {
        emit(models.find { it.id == id } ?: customModels.find { it.id == id })
    }
    
    // Get default model
    fun getDefaultModel(): Flow<Model> = flow {
        emit(models.find { it.id == "gpt-3.5-turbo" } ?: models.first())
    }
    
    // Search models
    fun searchModels(query: String): Flow<List<Model>> = flow {
        val allModels = models + customModels
        emit(allModels.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.description.contains(query, ignoreCase = true) ||
            it.provider.contains(query, ignoreCase = true)
        }.sortedBy { it.sortOrder })
    }
    
    // Add custom model
    suspend fun addCustomModel(model: Model): Result<Unit> {
        return try {
            customModels.add(model)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Update custom model
    suspend fun updateCustomModel(model: Model): Result<Unit> {
        return try {
            val index = customModels.indexOfFirst { it.id == model.id }
            if (index >= 0) {
                customModels[index] = model
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Delete custom model
    suspend fun deleteCustomModel(id: String): Result<Unit> {
        return try {
            customModels.removeIf { it.id == id }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get fallback models for a provider
    fun getFallbackModels(provider: String, excludeModelId: String? = null): List<Model> {
        return models.filter { 
            it.provider == provider && 
            it.id != excludeModelId &&
            it.isActive
        }.sortedBy { it.sortOrder }
    }
    
    // Get best model for a task (simple version - just return first available)
    fun getBestModelForTask(taskType: String, availableProviders: List<String>): Model? {
        val taskModels = when (taskType.lowercase()) {
            "text", "chat", "conversation" -> models.filter { 
                it.capabilities.contains("text") && availableProviders.contains(it.provider)
            }
            "vision", "image" -> models.filter { 
                it.capabilities.contains("vision") && availableProviders.contains(it.provider)
            }
            "reasoning", "complex" -> models.filter { 
                it.capabilities.contains("reasoning") && availableProviders.contains(it.provider)
            }
            else -> models.filter { availableProviders.contains(it.provider) }
        }
        
        return taskModels.sortedBy { it.sortOrder }.firstOrNull()
    }
}
