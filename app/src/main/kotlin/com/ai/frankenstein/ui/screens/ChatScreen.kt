package com.ai.frankenstein.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nested.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ai.frankenstein.core.AIFrankensteinApp
import com.ai.frankenstein.domain.model.Chat
import com.ai.frankenstein.domain.model.Message
import com.ai.frankenstein.domain.model.Model
import com.ai.frankenstein.ui.components.ChatBubble
import com.ai.frankenstein.ui.components.ChatInput
import com.ai.frankenstein.ui.components.ModelSelector
import com.ai.frankenstein.ui.components.StreamingIndicator
import com.ai.frankenstein.ui.components.ThinkingIndicator
import com.ai.frankenstein.ui.theme.ForestTheme
import com.ai.frankenstein.ui.theme.accentLime
import com.ai.frankenstein.ui.theme.bgDeep
import com.ai.frankenstein.ui.theme.textPrimary
import com.ai.frankenstein.ui.theme.textSecondary
import kotlinx.coroutines.flow.Flow
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatId: Long? = null,
    navController: NavController,
    app: AIFrankensteinApp
) {
    val context = LocalContext.current
    val chatUseCases = app.chatUseCases
    val modelUseCases = app.modelUseCases
    
    // State
    var selectedModel by remember { mutableStateOf<Model?>(null) }
    var messageText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isStreaming by remember { mutableStateOf(false) }
    var showModelSelector by remember { mutableStateOf(false) }
    
    // Get models
    val models = modelUseCases.getAllModels().collectAsState(initial = emptyList())
    
    // Get or create chat
    LaunchedEffect(chatId) {
        if (chatId == null) {
            // Create new chat
            selectedModel = models.value.firstOrNull()
            selectedModel?.let { model ->
                val result = chatUseCases.createNewChat(
                    modelId = model.id,
                    provider = model.provider,
                    title = "New Chat"
                )
                result.onSuccess { chat ->
                    // Navigate to new chat
                }
            }
        } else {
            // Load existing chat
            val chat = chatUseCases.getChatById(chatId).collectAsState(initial = null)
            chat.value?.let { c ->
                selectedModel = models.value.find { it.id == c.model }
            }
        }
    }
    
    // Get messages for current chat
    val messages = chatId?.let { id ->
        chatUseCases.getMessagesByChat(id).collectAsState(initial = emptyList())
    } ?: remember { mutableStateOf(emptyList<Message>()) }
    
    // Get current chat
    val chat = chatId?.let { id ->
        chatUseCases.getChatById(id).collectAsState(initial = null)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDeep)
    ) {
        // Top app bar
        TopAppBar(
            title = {
                Text(
                    text = chat?.value?.title ?: "New Chat",
                    style = MaterialTheme.typography.titleLarge,
                    color = textPrimary
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = textPrimary
                    )
                }
            },
            actions = {
                IconButton(onClick = { showModelSelector = !showModelSelector }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = textPrimary
                    )
                }
                
                IconButton(onClick = { /* Regenerate */ }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Regenerate",
                        tint = textPrimary
                    )
                }
                
                IconButton(onClick = { /* More options */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = textPrimary
                    )
                }
            }
        )
        
        // Model selector (if shown)
        if (showModelSelector) {
            ModelSelector(
                selectedModel = selectedModel,
                models = models.value,
                onModelSelected = { model ->
                    selectedModel = model
                    showModelSelector = false
                },
                modifier = Modifier.padding(8.dp)
            )
        }
        
        // Messages list
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (messages.value.isEmpty() && !isLoading) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = textSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Start a conversation",
                        style = MaterialTheme.typography.titleMedium,
                        color = textPrimary
                    )
                    
                    Text(
                        text = "Select a model and type your message",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textSecondary
                    )
                }
            } else {
                val listState = rememberLazyListState()
                
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(navController)
                ) {
                    items(messages.value) { message ->
                        ChatBubble(
                            message = message,
                            isStreaming = isStreaming && message.id == messages.value.lastOrNull()?.id,
                            onCopy = {
                                // Copy to clipboard
                            },
                            onFavorite = {
                                // Toggle favorite
                            },
                            onPin = {
                                // Toggle pin
                            },
                            onMore = {
                                // Show more options
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    
                    // Loading indicator
                    if (isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                ThinkingIndicator(message = "AI is thinking...")
                            }
                        }
                    }
                }
            }
            
            // Streaming indicator at bottom
            if (isStreaming) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    StreamingIndicator()
                }
            }
        }
        
        // Input area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Selected model info
            selectedModel?.let { model ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                ) {
                    Text(
                        text = "Model: ${model.name}",
                        style = MaterialTheme.typography.labelSmall,
                        color = textSecondary
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Text(
                        text = "${model.provider}",
                        style = MaterialTheme.typography.labelSmall,
                        color = when (model.provider) {
                            "openai" -> Color(0xFF00FF88)
                            "anthropic" -> Color(0xFFAA00FF)
                            "mistral" -> Color(0xFFFF5500)
                            "google" -> Color(0xFF0088FF)
                            else -> textSecondary
                        }
                    )
                }
            }
            
            ChatInput(
                value = messageText,
                onValueChange = { messageText = it },
                onSend = {
                    if (messageText.isNotBlank() && selectedModel != null) {
                        isLoading = true
                        isStreaming = true
                        
                        // Send message
                        val chatIdToUse = chatId ?: 0 // In a real app, use the actual chat ID
                        chatUseCases.sendMessage(
                            chatId = chatIdToUse,
                            content = messageText,
                            modelId = selectedModel?.id
                        ).onSuccess { flow ->
                            // In a real app, collect the flow to get streaming updates
                            messageText = ""
                        }.onFailure { e ->
                            isLoading = false
                            isStreaming = false
                        }
                    }
                },
                isLoading = isLoading,
                placeholder = "Type your message..."
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ForestTheme {
        val mockApp = remember {
            object : AIFrankensteinApp() {
                override fun onCreate() {
                    // Mock
                }
            }
        }
        
        ChatScreen(
            chatId = null,
            navController = rememberNavController(),
            app = mockApp
        )
    }
}
