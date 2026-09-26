package com.ai.frankenstein.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ai.frankenstein.core.AIFrankensteinApp
import com.ai.frankenstein.domain.model.Chat
import com.ai.frankenstein.domain.model.Model
import com.ai.frankenstein.ui.components.ModelSelector
import com.ai.frankenstein.ui.theme.ForestTheme
import com.ai.frankenstein.ui.theme.accentEmerald
import com.ai.frankenstein.ui.theme.accentLime
import com.ai.frankenstein.ui.theme.accentOlive
import com.ai.frankenstein.ui.theme.bgDeep
import com.ai.frankenstein.ui.theme.bgPanel
import com.ai.frankenstein.ui.theme.bgPanelRaised
import com.ai.frankenstein.ui.theme.borderMoss
import com.ai.frankenstein.ui.theme.textPrimary
import com.ai.frankenstein.ui.theme.textSecondary
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    app: AIFrankensteinApp
) {
    val chatUseCases = app.chatUseCases
    val modelUseCases = app.modelUseCases
    
    // State
    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedModel by remember { mutableStateOf<Model?>(null) }
    var showModelSelector by remember { mutableStateOf(false) }
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    
    // Get chats
    val chats = chatUseCases.getAllChats().collectAsState(initial = emptyList())
    
    // Get models
    val models = modelUseCases.getAllModels().collectAsState(initial = emptyList())
    
    // Get default model
    LaunchedEffect(Unit) {
        selectedModel = models.value.firstOrNull()
    }
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.background(bgPanel)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header
                    Text(
                        text = "AI Frankenstein",
                        style = MaterialTheme.typography.titleLarge,
                        color = accentLime,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Divider(color = borderMoss, modifier = Modifier.padding(vertical = 8.dp))
                    
                    // New chat button
                    NavigationDrawerItem(
                        label = { Text("New Chat", color = textPrimary) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "New Chat",
                                tint = accentLime
                            )
                        },
                        selected = false,
                        onClick = {
                            // Create new chat and navigate
                            selectedModel?.let { model ->
                                chatUseCases.createNewChat(
                                    modelId = model.id,
                                    provider = model.provider
                                ).onSuccess { chat ->
                                    navController.navigate("chat/${chat.id}")
                                }
                            }
                            drawerState.close()
                        },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    
                    Divider(color = borderMoss, modifier = Modifier.padding(vertical = 8.dp))
                    
                    // Navigation items
                    listOf(
                        "Chats" to Icons.Default.Menu,
                        "Models" to Icons.Default.Settings,
                        "Settings" to Icons.Default.Settings,
                        "API Keys" to Icons.Default.MoreVert
                    ).forEach { (label, icon) ->
                        NavigationDrawerItem(
                            label = { Text(label, color = textPrimary) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = textSecondary
                                )
                            },
                            selected = false,
                            onClick = {
                                when (label) {
                                    "Settings" -> navController.navigate("settings")
                                    "API Keys" -> navController.navigate("apiKeys")
                                }
                                drawerState.close()
                            },
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgDeep)
        ) {
            // Top app bar
            TopAppBar(
                title = {
                    if (showSearch) {
                        // Search field
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = textSecondary,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            
                            Text(
                                text = searchQuery,
                                style = MaterialTheme.typography.bodyMedium,
                                color = textPrimary
                            )
                        }
                    } else {
                        Text(
                            text = "AI Frankenstein",
                            style = MaterialTheme.typography.titleLarge,
                            color = textPrimary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { drawerState.open() }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = textPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showSearch = !showSearch }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = textPrimary
                        )
                    }
                    
                    IconButton(onClick = { showModelSelector = !showModelSelector }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Select Model",
                            tint = textPrimary
                        )
                    }
                }
            )
            
            // Model selector
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
            
            // Chats list
            if (chats.value.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = textSecondary,
                        modifier = Modifier.size(64.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "No chats yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = textPrimary
                    )
                    
                    Text(
                        text = "Start a new conversation",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textSecondary
                    )
                }
            } else {
                LazyColumn(
                    state = rememberLazyListState(),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Pinned chats
                    val pinnedChats = remember(chats.value) {
                        chats.value.filter { it.isPinned }.sortedByDescending { it.updatedAt }
                    }
                    
                    val regularChats = remember(chats.value) {
                        chats.value.filter { !it.isPinned }.sortedByDescending { it.updatedAt }
                    }
                    
                    if (pinnedChats.isNotEmpty()) {
                        item {
                            Text(
                                text = "Pinned",
                                style = MaterialTheme.typography.labelSmall,
                                color = textSecondary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        
                        items(pinnedChats) { chat ->
                            ChatItem(
                                chat = chat,
                                onClick = { navController.navigate("chat/${chat.id}") },
                                onFavorite = {
                                    chatUseCases.toggleFavoriteChat(chat)
                                },
                                onPin = {
                                    chatUseCases.togglePinChat(chat)
                                }
                            )
                        }
                    }
                    
                    if (pinnedChats.isNotEmpty() && regularChats.isNotEmpty()) {
                        item {
                            Divider(
                                color = borderMoss,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                    
                    // Regular chats
                    if (regularChats.isNotEmpty()) {
                        item {
                            Text(
                                text = "Recent",
                                style = MaterialTheme.typography.labelSmall,
                                color = textSecondary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        
                        items(regularChats) { chat ->
                            ChatItem(
                                chat = chat,
                                onClick = { navController.navigate("chat/${chat.id}") },
                                onFavorite = {
                                    chatUseCases.toggleFavoriteChat(chat)
                                },
                                onPin = {
                                    chatUseCases.togglePinChat(chat)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatItem(
    chat: Chat,
    onClick: () -> Unit,
    onFavorite: () -> Unit,
    onPin: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = chat.title,
                style = MaterialTheme.typography.bodyMedium,
                color = textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            if (chat.lastMessagePreview.isNotBlank()) {
                Text(
                    text = chat.lastMessagePreview,
                    style = MaterialTheme.typography.bodySmall,
                    color = textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onPin() },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (chat.isPinned) Icons.Default.PushPin else Icons.Default.MoreVert,
                    contentDescription = "Pin",
                    tint = if (chat.isPinned) accentLime else textSecondary
                )
            }
            
            IconButton(
                onClick = { onFavorite() },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (chat.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (chat.isFavorite) accentLime else textSecondary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ForestTheme {
        val mockApp = remember {
            object : AIFrankensteinApp() {
                override fun onCreate() {
                    // Mock
                }
            }
        }
        
        HomeScreen(
            navController = rememberNavController(),
            app = mockApp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatItemPreview() {
    ForestTheme {
        val chat = remember {
            Chat(
                id = 1,
                title = "Test Chat",
                model = "gpt-3.5-turbo",
                provider = "openai",
                createdAt = Date(),
                updatedAt = Date()
            )
        }
        
        ChatItem(
            chat = chat,
            onClick = {},
            onFavorite = {},
            onPin = {}
        )
    }
}
