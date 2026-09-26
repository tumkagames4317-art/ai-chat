package com.ai.frankenstein.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ai.frankenstein.core.AIFrankensteinApp
import com.ai.frankenstein.ui.screens.ApiKeysScreen
import com.ai.frankenstein.ui.screens.ChatScreen
import com.ai.frankenstein.ui.screens.HomeScreen
import com.ai.frankenstein.ui.screens.SettingsScreen
import com.ai.frankenstein.ui.screens.TerminalScreen
import com.ai.frankenstein.ui.theme.AIFrankensteinTheme
import com.ai.frankenstein.ui.theme.ForestTheme

class MainActivity : ComponentActivity() {
    
    private lateinit var app: AIFrankensteinApp
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        app = application as AIFrankensteinApp
        
        setContent {
            AIFrankensteinTheme {
                // ForestSwampTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        
                        NavHost(
                            navController = navController,
                            startDestination = "home"
                        ) {
                            composable("home") {
                                HomeScreen(
                                    navController = navController,
                                    app = app
                                )
                            }
                            
                            composable("chat/{chatId}") { backStackEntry ->
                                val chatId = backStackEntry.arguments?.getString("chatId")?.toLongOrNull()
                                ChatScreen(
                                    chatId = chatId,
                                    navController = navController,
                                    app = app
                                )
                            }
                            
                            composable("settings") {
                                SettingsScreen(
                                    navController = navController,
                                    app = app
                                )
                            }
                            
                            composable("apiKeys") {
                                ApiKeysScreen(
                                    navController = navController,
                                    app = app
                                )
                            }
                            
                            composable("terminal") {
                                TerminalScreen(
                                    navController = navController,
                                    app = app
                                )
                            }
                        }
                    }
                // }
            }
        }
    }
}
