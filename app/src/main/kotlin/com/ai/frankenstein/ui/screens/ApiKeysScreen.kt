package com.ai.frankenstein.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ai.frankenstein.core.AIFrankensteinApp
import com.ai.frankenstein.domain.model.ApiKey
import com.ai.frankenstein.domain.model.Model
import com.ai.frankenstein.ui.theme.ForestTheme
import com.ai.frankenstein.ui.theme.accentEmerald
import com.ai.frankenstein.ui.theme.accentLime
import com.ai.frankenstein.ui.theme.accentOlive
import com.ai.frankenstein.ui.theme.bgDeep
import com.ai.frankenstein.ui.theme.bgPanel
import com.ai.frankenstein.ui.theme.bgPanelRaised
import com.ai.frankenstein.ui.theme.borderMoss
import com.ai.frankenstein.ui.theme.stateError
import com.ai.frankenstein.ui.theme.stateSuccess
import com.ai.frankenstein.ui.theme.textPrimary
import com.ai.frankenstein.ui.theme.textSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeysScreen(
    navController: NavController,
    app: AIFrankensteinApp
) {
    val modelUseCases = app.modelUseCases
    
    // State
    var apiKeys by remember { mutableStateOf(listOf<ApiKey>()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedKey by remember { mutableStateOf<ApiKey?>(null) }
    var keyName by remember { mutableStateOf("") }
    var keyValue by remember { mutableStateOf("") }
    var selectedProvider by remember { mutableStateOf("openai") }
    var showKey by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<String?>(null) }
    
    // Get models
    val models = modelUseCases.getAllModels().collectAsState(initial = emptyList())
    
    // Load API keys (demo)
    LaunchedEffect(Unit) {
        apiKeys = modelUseCases.getAllApiKeys()
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
                    text = "API Keys",
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
                OutlinedButton(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = accentLime,
                        containerColor = Color.Transparent
                    ),
                    border = null
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = accentLime
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text("Add Key", color = accentLime)
                }
            }
        )
        
        // Content
        if (apiKeys.isEmpty()) {
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
                    text = "No API keys",
                    style = MaterialTheme.typography.titleMedium,
                    color = textPrimary
                )
                
                Text(
                    text = "Add your first API key to start using AI models",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textSecondary,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentLime,
                        contentColor = bgDeep
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = bgDeep
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text("Add API Key")
                }
            }
        } else {
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier.fillMaxSize()
            ) {
                items(apiKeys) { key ->
                    ApiKeyItem(
                        key = key,
                        onEdit = {
                            selectedKey = key
                            keyName = key.name
                            keyValue = key.key
                            selectedProvider = key.provider
                            showKey = false
                            showAddDialog = true
                        },
                        onDelete = {
                            selectedKey = key
                            showDeleteDialog = true
                        },
                        onTest = {
                            // Test the API key
                            modelUseCases.testApiKey(key.provider, key.key)
                                .onSuccess { isValid ->
                                    testResult = if (isValid) "Connection successful!" else "Connection failed"
                                }
                                .onFailure {
                                    testResult = "Error: ${it.message}"
                                }
                        },
                        onToggleVisibility = {
                            // Toggle key visibility
                        }
                    )
                    
                    Divider(color = borderMoss, modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
    
    // Add/Edit API Key Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = if (selectedKey == null) "Add API Key" else "Edit API Key",
                    style = MaterialTheme.typography.titleLarge,
                    color = textPrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp)
                ) {
                    // Provider selector
                    Text(
                        text = "Provider",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textPrimary
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(bgPanelRaised)
                            .clickable { 
                                // Show provider selector
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = getProviderDisplayName(selectedProvider),
                            style = MaterialTheme.typography.bodyMedium,
                            color = textPrimary
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Select",
                            tint = textSecondary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Key name
                    Text(
                        text = "Key Name",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textPrimary
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    BasicTextField(
                        value = keyName,
                        onValueChange = { keyName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(bgPanelRaised)
                            .padding(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // API Key
                    Text(
                        text = "API Key",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textPrimary
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    BasicTextField(
                        value = keyValue,
                        onValueChange = { keyValue = it },
                        visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(bgPanelRaised)
                            .padding(12.dp)
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    ) {
                        IconButton(
                            onClick = { showKey = !showKey }
                        ) {
                            Icon(
                                imageVector = if (showKey) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (showKey) "Hide" else "Show",
                                tint = textSecondary
                            )
                        }
                        
                        if (keyValue.isNotBlank()) {
                            IconButton(
                                onClick = { 
                                    // Copy to clipboard
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = textSecondary
                                )
                            }
                        }
                    }
                    
                    // Test result
                    testResult?.let { result ->
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (result.contains("success")) stateSuccess.copy(alpha = 0.2f) else stateError.copy(alpha = 0.2f))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = result,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (result.contains("success")) stateSuccess else stateError
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedKey == null) {
                            // Add new key
                            val newKey = ApiKey(
                                name = keyName.ifBlank { "${getProviderDisplayName(selectedProvider)} Key" },
                                provider = selectedProvider,
                                key = keyValue,
                                isActive = true
                            )
                            modelUseCases.addDemoApiKey(newKey)
                            apiKeys = apiKeys + newKey
                        } else {
                            // Update existing key
                            val updatedKey = selectedKey!!.copy(
                                name = keyName.ifBlank { "${getProviderDisplayName(selectedProvider)} Key" },
                                provider = selectedProvider,
                                key = keyValue
                            )
                            // In a real app, update in database
                        }
                        
                        showAddDialog = false
                        selectedKey = null
                        keyName = ""
                        keyValue = ""
                        testResult = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentLime,
                        contentColor = bgDeep
                    ),
                    enabled = keyValue.isNotBlank()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddDialog = false
                        selectedKey = null
                        keyName = ""
                        keyValue = ""
                        testResult = null
                    }
                ) {
                    Text("Cancel", color = textSecondary)
                }
            }
        )
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog && selectedKey != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Delete API Key",
                    style = MaterialTheme.typography.titleLarge,
                    color = textPrimary
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete this API key? You won't be able to use ${selectedKey?.name} until you add it again.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Delete the key
                        apiKeys = apiKeys.filter { it.id != selectedKey?.id }
                        showDeleteDialog = false
                        selectedKey = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = stateError,
                        contentColor = bgPanel
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel", color = textSecondary)
                }
            }
        )
    }
}

@Composable
fun ApiKeyItem(
    key: ApiKey,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTest: () -> Unit,
    onToggleVisibility: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Provider icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(getProviderColor(key.provider)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = key.provider.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = bgPanel
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = key.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textPrimary
                )
                
                Text(
                    text = "${getProviderDisplayName(key.provider)} • ${key.usageCount} uses",
                    style = MaterialTheme.typography.bodySmall,
                    color = textSecondary
                )
            }
            
            // Action buttons
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onTest
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Test",
                        tint = textSecondary
                    )
                }
                
                IconButton(
                    onClick = onEdit
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = textSecondary
                    )
                }
                
                IconButton(
                    onClick = onDelete
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = stateError
                    )
                }
            }
        }
        
        // Token usage
        if (key.tokenUsage > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${key.tokenUsage.formatNumber()} tokens",
                    style = MaterialTheme.typography.labelSmall,
                    color = textSecondary
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "${key.costUsage.formatCurrency()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = textSecondary
                )
            }
        }
    }
}

@Composable
fun ProviderSelector(
    selectedProvider: String,
    onProviderSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val providers = Model.SUPPORTED_PROVIDERS
    
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgPanelRaised)
            .padding(16.dp)
    ) {
        Text(
            text = "Select Provider",
            style = MaterialTheme.typography.titleMedium,
            color = textPrimary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        providers.forEach { provider ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onProviderSelected(provider) }
                    .padding(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(getProviderColor(provider)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = provider.take(1).uppercase(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = bgPanel
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = getProviderDisplayName(provider),
                    style = MaterialTheme.typography.bodyMedium,
                    color = textPrimary
                )
                
                if (selectedProvider == provider) {
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = accentLime
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel", color = textSecondary)
            }
        }
    }
}

@Composable
fun getProviderDisplayName(provider: String): String {
    return when (provider) {
        "openai" -> "OpenAI"
        "anthropic" -> "Anthropic"
        "mistral" -> "Mistral"
        "google" -> "Google"
        "xai" -> "xAI"
        "deepseek" -> "DeepSeek"
        "ollama" -> "Ollama"
        else -> provider.capitalizeFirstLetter()
    }
}

@Composable
fun getProviderColor(provider: String): Color {
    return when (provider) {
        "openai" -> Color(0xFF00FF88)
        "anthropic" -> Color(0xFFAA00FF)
        "mistral" -> Color(0xFFFF5500)
        "google" -> Color(0xFF0088FF)
        "xai" -> Color(0xFFFF00FF)
        "deepseek" -> Color(0xFF00FFFF)
        "ollama" -> Color(0xFFFF00AA)
        else -> accentOlive
    }
}

@Preview(showBackground = true)
@Composable
fun ApiKeysScreenPreview() {
    ForestTheme {
        val mockApp = remember {
            object : AIFrankensteinApp() {
                override fun onCreate() {
                    // Mock
                }
            }
        }
        
        ApiKeysScreen(
            navController = rememberNavController(),
            app = mockApp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ApiKeyItemPreview() {
    ForestTheme {
        val key = remember {
            ApiKey(
                id = 1,
                name = "My OpenAI Key",
                provider = "openai",
                key = "sk-1234567890",
                isActive = true,
                tokenUsage = 1000,
                costUsage = 5.50
            )
        }
        
        ApiKeyItem(
            key = key,
            onEdit = {},
            onDelete = {},
            onTest = {},
            onToggleVisibility = {}
        )
    }
}
