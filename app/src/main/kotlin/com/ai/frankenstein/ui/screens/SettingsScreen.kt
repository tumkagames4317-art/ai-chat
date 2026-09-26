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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ai.frankenstein.core.AIFrankensteinApp
import com.ai.frankenstein.ui.theme.ForestTheme
import com.ai.frankenstein.ui.theme.accentLime
import com.ai.frankenstein.ui.theme.accentOlive
import com.ai.frankenstein.ui.theme.bgDeep
import com.ai.frankenstein.ui.theme.bgPanel
import com.ai.frankenstein.ui.theme.bgPanelRaised
import com.ai.frankenstein.ui.theme.borderMoss
import com.ai.frankenstein.ui.theme.textPrimary
import com.ai.frankenstein.ui.theme.textSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    app: AIFrankensteinApp
) {
    val settingsUseCases = app.settingsUseCases
    
    // State
    var theme by remember { mutableStateOf("system") }
    var language by remember { mutableStateOf("en") }
    var isBiometricEnabled by remember { mutableStateOf(false) }
    var isSoundEnabled by remember { mutableStateOf(true) }
    var isVibrationEnabled by remember { mutableStateOf(true) }
    var isStreamingEnabled by remember { mutableStateOf(true) }
    var fontSize by remember { mutableStateOf(16) }
    var isAnimationEnabled by remember { mutableStateOf(true) }
    
    // Load settings
    LaunchedEffect(Unit) {
        theme = settingsUseCases.theme.collectAsState(initial = "system").value
        language = settingsUseCases.language.collectAsState(initial = "en").value
        isBiometricEnabled = settingsUseCases.isBiometricEnabled.collectAsState(initial = false).value
        isSoundEnabled = settingsUseCases.isSoundEnabled.collectAsState(initial = true).value
        isVibrationEnabled = settingsUseCases.isVibrationEnabled.collectAsState(initial = true).value
        isStreamingEnabled = settingsUseCases.isStreamingEnabled.collectAsState(initial = true).value
        fontSize = settingsUseCases.fontSize.collectAsState(initial = 16).value
        isAnimationEnabled = settingsUseCases.isAnimationEnabled.collectAsState(initial = true).value
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
                    text = "Settings",
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
            }
        )
        
        // Settings content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Appearance section
            SettingsSection(title = "Appearance") {
                // Theme
                SettingsItem(
                    title = "Theme",
                    description = "Change app theme",
                    icon = Icons.Default.Palette,
                    trailing = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (theme) {
                                    "system" -> "System"
                                    "light" -> "Light"
                                    "dark" -> "Dark"
                                    else -> "System"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = textSecondary
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Change",
                                tint = textSecondary
                            )
                        }
                    },
                    onClick = {
                        // Show theme selector
                    }
                )
                
                Divider(color = borderMoss, modifier = Modifier.padding(vertical = 8.dp))
                
                // Animation
                SettingsSwitch(
                    title = "Animations",
                    description = "Enable smooth animations",
                    icon = Icons.Default.Palette,
                    isChecked = isAnimationEnabled,
                    onCheckedChange = { checked ->
                        isAnimationEnabled = checked
                        // Save setting
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Behavior section
            SettingsSection(title = "Behavior") {
                // Streaming
                SettingsSwitch(
                    title = "Streaming responses",
                    description = "Receive AI responses as they're generated",
                    icon = Icons.Default.Speaker,
                    isChecked = isStreamingEnabled,
                    onCheckedChange = { checked ->
                        isStreamingEnabled = checked
                        // Save setting
                    }
                )
                
                Divider(color = borderMoss, modifier = Modifier.padding(vertical = 8.dp))
                
                // Sound
                SettingsSwitch(
                    title = "Sounds",
                    description = "Enable notification sounds",
                    icon = Icons.Default.Speaker,
                    isChecked = isSoundEnabled,
                    onCheckedChange = { checked ->
                        isSoundEnabled = checked
                        // Save setting
                    }
                )
                
                Divider(color = borderMoss, modifier = Modifier.padding(vertical = 8.dp))
                
                // Vibration
                SettingsSwitch(
                    title = "Vibrations",
                    description = "Enable haptic feedback",
                    icon = Icons.Default.Vibration,
                    isChecked = isVibrationEnabled,
                    onCheckedChange = { checked ->
                        isVibrationEnabled = checked
                        // Save setting
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Security section
            SettingsSection(title = "Security") {
                // Biometric lock
                SettingsSwitch(
                    title = "Biometric Lock",
                    description = "Use fingerprint or face ID to unlock",
                    icon = Icons.Default.Lock,
                    isChecked = isBiometricEnabled,
                    onCheckedChange = { checked ->
                        isBiometricEnabled = checked
                        // Save setting
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Notifications section
            SettingsSection(title = "Notifications") {
                SettingsSwitch(
                    title = "Notifications",
                    description = "Enable app notifications",
                    icon = Icons.Default.Notifications,
                    isChecked = true, // Default for preview
                    onCheckedChange = { checked ->
                        // Save setting
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // About section
            SettingsSection(title = "About") {
                SettingsItem(
                    title = "Version",
                    description = "1.0.0",
                    icon = Icons.Default.Palette,
                    onClick = {}
                )
                
                Divider(color = borderMoss, modifier = Modifier.padding(vertical = 8.dp))
                
                SettingsItem(
                    title = "Licenses",
                    description = "Open source licenses",
                    icon = Icons.Default.Palette,
                    onClick = {
                        navController.navigate("licenses")
                    }
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = textPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        content()
    }
}

@Composable
fun SettingsItem(
    title: String,
    description: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    trailing: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = accentLime,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = textPrimary
            )
            
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = textSecondary
                )
            }
        }
        
        if (trailing != null) {
            trailing()
        } else {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Next",
                tint = textSecondary
            )
        }
    }
}

@Composable
fun SettingsSwitch(
    title: String,
    description: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = accentLime,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = textPrimary
            )
            
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = textSecondary
                )
            }
        }
        
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            thumbContent = {
                if (isChecked) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "On",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        )
    }
}

@Composable
fun ThemeSelector(
    selectedTheme: String,
    onThemeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val themes = listOf(
        "system" to "System",
        "light" to "Light",
        "dark" to "Dark"
    )
    
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgPanelRaised)
            .padding(16.dp)
    ) {
        Text(
            text = "Select Theme",
            style = MaterialTheme.typography.titleMedium,
            color = textPrimary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        themes.forEach { (value, label) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onThemeSelected(value) }
                    .padding(12.dp)
            ) {
                RadioButton(
                    selected = selectedTheme == value,
                    onClick = { onThemeSelected(value) }
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textPrimary
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                val icon = when (value) {
                    "light" -> Icons.Default.LightMode
                    "dark" -> Icons.Default.DarkMode
                    else -> Icons.Default.Palette
                }
                
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = textSecondary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.bodyMedium,
                color = textSecondary,
                modifier = Modifier
                    .clickable { onDismiss() }
                    .padding(8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    ForestTheme {
        val mockApp = remember {
            object : AIFrankensteinApp() {
                override fun onCreate() {
                    // Mock
                }
            }
        }
        
        SettingsScreen(
            navController = rememberNavController(),
            app = mockApp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsItemPreview() {
    ForestTheme {
        SettingsItem(
            title = "Theme",
            description = "Change app theme",
            icon = Icons.Default.Palette,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsSwitchPreview() {
    ForestTheme {
        var checked by remember { mutableStateOf(true) }
        
        SettingsSwitch(
            title = "Animations",
            description = "Enable smooth animations",
            icon = Icons.Default.Palette,
            isChecked = checked,
            onCheckedChange = { checked = it }
        )
    }
}
