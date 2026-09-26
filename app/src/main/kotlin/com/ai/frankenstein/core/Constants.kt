package com.ai.frankenstein.core

object Constants {
    // App
    const val APP_NAME = "AI Frankenstein"
    const val APP_VERSION = "1.0.0"
    
    // Preferences
    const val PREFS_NAME = "AI_FRANKENSTEIN_PREFS"
    const val PREFS_ENCRYPTED = "AI_FRANKENSTEIN_ENCRYPTED_PREFS"
    const val KEY_THEME = "theme_preference"
    const val KEY_LANGUAGE = "language_preference"
    const val KEY_FIRST_LAUNCH = "first_launch"
    const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    const val KEY_MASTER_PASSWORD = "master_password_hash"
    
    // Database
    const val DATABASE_NAME = "ai_frankenstein_db"
    const val DATABASE_VERSION = 1
    
    // API
    const val API_TIMEOUT = 60L // seconds
    const val STREAM_CHUNK_SIZE = 1024
    
    // Models
    const val DEFAULT_MODEL = "gpt-3.5-turbo"
    const val FALLBACK_MODEL = "mistral-tiny"
    
    // Token limits
    const val MAX_CONTEXT_TOKENS = 4096
    const val MAX_RESPONSE_TOKENS = 2048
    const val TOKEN_MARGIN = 256
    
    // File types
    val SUPPORTED_FILE_TYPES = listOf(
        "txt", "pdf", "docx", "xlsx", "pptx", "csv", "json", "epub",
        "png", "jpg", "jpeg", "gif", "webp", "svg"
    )
    
    // Terminal
    const val TERMINAL_MAX_OUTPUT_LINES = 1000
    const val TERMINAL_TIMEOUT = 10000L // ms
    
    // Animation durations
    const val ANIMATION_SHORT = 120L
    const val ANIMATION_MEDIUM = 240L
    const val ANIMATION_LONG = 380L
    const val ANIMATION_XLONG = 450L
}
