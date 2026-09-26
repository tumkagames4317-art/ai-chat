package com.ai.frankenstein.core

import android.app.Application
import com.ai.frankenstein.data.local.PreferenceManager
import com.ai.frankenstein.data.repository.ApiKeyRepository
import com.ai.frankenstein.data.repository.ChatRepository
import com.ai.frankenstein.data.repository.ModelRepository
import com.ai.frankenstein.domain.usecase.ChatUseCases
import com.ai.frankenstein.domain.usecase.ModelUseCases
import com.ai.frankenstein.domain.usecase.SettingsUseCases

class AIFrankensteinApp : Application() {
    
    val preferenceManager: PreferenceManager by lazy {
        PreferenceManager(this)
    }
    
    val apiKeyRepository: ApiKeyRepository by lazy {
        ApiKeyRepository(preferenceManager)
    }
    
    val modelRepository: ModelRepository by lazy {
        ModelRepository()
    }
    
    val chatRepository: ChatRepository by lazy {
        ChatRepository(this)
    }
    
    val chatUseCases: ChatUseCases by lazy {
        ChatUseCases(chatRepository, apiKeyRepository, modelRepository)
    }
    
    val modelUseCases: ModelUseCases by lazy {
        ModelUseCases(modelRepository, apiKeyRepository)
    }
    
    val settingsUseCases: SettingsUseCases by lazy {
        SettingsUseCases(preferenceManager)
    }
    
    override fun onCreate() {
        super.onCreate()
        // Initialize app
    }
}
