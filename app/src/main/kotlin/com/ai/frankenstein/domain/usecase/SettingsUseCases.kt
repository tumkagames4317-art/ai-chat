package com.ai.frankenstein.domain.usecase

import com.ai.frankenstein.data.local.PreferenceManager
import kotlinx.coroutines.flow.Flow

class SettingsUseCases(private val preferenceManager: PreferenceManager) {
    
    // Theme
    val theme: Flow<String> = preferenceManager.theme
    suspend fun setTheme(theme: String) = preferenceManager.setTheme(theme)
    
    // Language
    val language: Flow<String> = preferenceManager.language
    suspend fun setLanguage(language: String) = preferenceManager.setLanguage(language)
    
    // First launch
    val isFirstLaunch: Flow<Boolean> = preferenceManager.isFirstLaunch
    suspend fun setFirstLaunch(isFirst: Boolean) = preferenceManager.setFirstLaunch(isFirst)
    
    // Biometric
    val isBiometricEnabled: Flow<Boolean> = preferenceManager.isBiometricEnabled
    suspend fun setBiometricEnabled(enabled: Boolean) = preferenceManager.setBiometricEnabled(enabled)
    
    // Master password
    val masterPasswordHash: Flow<String?> = preferenceManager.masterPasswordHash
    suspend fun setMasterPasswordHash(hash: String?) = preferenceManager.setMasterPasswordHash(hash)
    
    // Default model
    val defaultModel: Flow<String> = preferenceManager.defaultModel
    suspend fun setDefaultModel(modelId: String) = preferenceManager.setDefaultModel(modelId)
    
    // Streaming
    val isStreamingEnabled: Flow<Boolean> = preferenceManager.isStreamingEnabled
    suspend fun setStreamingEnabled(enabled: Boolean) = preferenceManager.setStreamingEnabled(enabled)
    
    // Sound
    val isSoundEnabled: Flow<Boolean> = preferenceManager.isSoundEnabled
    suspend fun setSoundEnabled(enabled: Boolean) = preferenceManager.setSoundEnabled(enabled)
    
    // Vibration
    val isVibrationEnabled: Flow<Boolean> = preferenceManager.isVibrationEnabled
    suspend fun setVibrationEnabled(enabled: Boolean) = preferenceManager.setVibrationEnabled(enabled)
    
    // Notifications
    val isNotificationsEnabled: Flow<Boolean> = preferenceManager.isNotificationsEnabled
    suspend fun setNotificationsEnabled(enabled: Boolean) = preferenceManager.setNotificationsEnabled(enabled)
    
    // Font size
    val fontSize: Flow<Int> = preferenceManager.fontSize
    suspend fun setFontSize(size: Int) = preferenceManager.setFontSize(size)
    
    // Animation
    val isAnimationEnabled: Flow<Boolean> = preferenceManager.isAnimationEnabled
    suspend fun setAnimationEnabled(enabled: Boolean) = preferenceManager.setAnimationEnabled(enabled)
    
    // Clear all preferences
    suspend fun clearAllPreferences() = preferenceManager.clearAll()
}
