package com.ai.frankenstein.data.local

import android.content.Context
import androidx.datastore.core.DataStore
nimport androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ai.frankenstein.core.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.PREFS_NAME)

class PreferenceManager(private val context: Context) {
    
    companion object {
        // Theme
        val THEME_KEY = stringPreferencesKey(Constants.KEY_THEME)
        
        // Language
        val LANGUAGE_KEY = stringPreferencesKey(Constants.KEY_LANGUAGE)
        
        // First launch
        val FIRST_LAUNCH_KEY = booleanPreferencesKey(Constants.KEY_FIRST_LAUNCH)
        
        // Biometric
        val BIOMETRIC_ENABLED_KEY = booleanPreferencesKey(Constants.KEY_BIOMETRIC_ENABLED)
        
        // Security
        val MASTER_PASSWORD_KEY = stringPreferencesKey(Constants.KEY_MASTER_PASSWORD)
        
        // Settings
        val DEFAULT_MODEL_KEY = stringPreferencesKey("default_model")
        val STREAMING_ENABLED_KEY = booleanPreferencesKey("streaming_enabled")
        val SOUND_ENABLED_KEY = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED_KEY = booleanPreferencesKey("vibration_enabled")
        val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
        
        // Appearance
        val FONT_SIZE_KEY = intPreferencesKey("font_size")
        val ANIMATION_ENABLED_KEY = booleanPreferencesKey("animation_enabled")
    }
    
    // Theme
    val theme: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME_KEY] ?: "system"
    }
    
    suspend fun setTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }
    
    // Language
    val language: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY] ?: "en"
    }
    
    suspend fun setLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }
    
    // First launch
    val isFirstLaunch: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[FIRST_LAUNCH_KEY] ?: true
    }
    
    suspend fun setFirstLaunch(isFirst: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[FIRST_LAUNCH_KEY] = isFirst
        }
    }
    
    // Biometric
    val isBiometricEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[BIOMETRIC_ENABLED_KEY] ?: false
    }
    
    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BIOMETRIC_ENABLED_KEY] = enabled
        }
    }
    
    // Master password hash
    val masterPasswordHash: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[MASTER_PASSWORD_KEY]
    }
    
    suspend fun setMasterPasswordHash(hash: String?) {
        context.dataStore.edit { preferences ->
            if (hash != null) {
                preferences[MASTER_PASSWORD_KEY] = hash
            } else {
                preferences.remove(MASTER_PASSWORD_KEY)
            }
        }
    }
    
    // Default model
    val defaultModel: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[DEFAULT_MODEL_KEY] ?: Constants.DEFAULT_MODEL
    }
    
    suspend fun setDefaultModel(modelId: String) {
        context.dataStore.edit { preferences ->
            preferences[DEFAULT_MODEL_KEY] = modelId
        }
    }
    
    // Streaming
    val isStreamingEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[STREAMING_ENABLED_KEY] ?: true
    }
    
    suspend fun setStreamingEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[STREAMING_ENABLED_KEY] = enabled
        }
    }
    
    // Sound
    val isSoundEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SOUND_ENABLED_KEY] ?: true
    }
    
    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SOUND_ENABLED_KEY] = enabled
        }
    }
    
    // Vibration
    val isVibrationEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[VIBRATION_ENABLED_KEY] ?: true
    }
    
    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[VIBRATION_ENABLED_KEY] = enabled
        }
    }
    
    // Notifications
    val isNotificationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFICATIONS_ENABLED_KEY] ?: true
    }
    
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED_KEY] = enabled
        }
    }
    
    // Font size
    val fontSize: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[FONT_SIZE_KEY] ?: 16
    }
    
    suspend fun setFontSize(size: Int) {
        context.dataStore.edit { preferences ->
            preferences[FONT_SIZE_KEY] = size
        }
    }
    
    // Animation
    val isAnimationEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ANIMATION_ENABLED_KEY] ?: true
    }
    
    suspend fun setAnimationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ANIMATION_ENABLED_KEY] = enabled
        }
    }
    
    // Clear all preferences
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
