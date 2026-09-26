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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ai.frankenstein.core.AIFrankensteinApp
import com.ai.frankenstein.core.terminal.TerminalSession
import com.ai.frankenstein.ui.theme.ForestTheme
import com.ai.frankenstein.ui.theme.accentLime
import com.ai.frankenstein.ui.theme.accentOlive
import com.ai.frankenstein.ui.theme.bgDeep
import com.ai.frankenstein.ui.theme.bgPanel
import com.ai.frankenstein.ui.theme.bgPanelRaised
import com.ai.frankenstein.ui.theme.borderMoss
import com.ai.frankenstein.ui.theme.stateError
import com.ai.frankenstein.ui.theme.stateSuccess
import com.ai.frankenstein.ui.theme.stateWarning
import com.ai.frankenstein.ui.theme.textPrimary
import com.ai.frankenstein.ui.theme.textSecondary
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen(
    navController: NavController,
    app: AIFrankensteinApp
) {
    // State
    val terminalSession = remember { TerminalSession() }
    var commandText by remember { mutableStateOf("") }
    var outputLines by remember { mutableStateOf(listOf<String>("Welcome to AI Frankenstein Terminal\n\nType 'help' for available commands.")) }
    var isRunning by remember { mutableStateOf(false) }
    var currentDirectory by remember { mutableStateOf("/") }
    
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // Collect terminal output
    LaunchedEffect(Unit) {
        terminalSession.outputFlow.collectLatest { newOutput ->
            outputLines = outputLines + newOutput
        }
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
                    text = "Terminal",
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
                IconButton(
                    onClick = {
                        outputLines = listOf("Terminal cleared\n")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = textSecondary
                    )
                }
            }
        )
        
        // Terminal output
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(bgPanel)
                .padding(12.dp)
        ) {
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier.fillMaxSize()
            ) {
                items(outputLines) { line ->
                    TerminalLine(
                        line = line,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
        
        // Warning banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(stateWarning.copy(alpha = 0.2f))
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Warning",
                    tint = stateWarning
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Some commands may be dangerous. Be careful!",
                    style = MaterialTheme.typography.bodySmall,
                    color = stateWarning
                )
            }
        }
        
        // Input row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(bgPanelRaised)
                .padding(8.dp)
        ) {
            // Prompt
            Text(
                text = "$currentDirectory \$ ",
                style = MaterialTheme.typography.bodyMedium,
                color = accentLime
            )
            
            // Command input
            BasicTextField(
                value = commandText,
                onValueChange = { commandText = it },
                enabled = !isRunning,
                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false
                ),
                keyboardActions = androidx.compose.ui.text.input.KeyboardActions(
                    onSend = {
                        if (commandText.isNotBlank()) {
                            executeCommand(commandText)
                            commandText = ""
                            keyboardController?.hide()
                        }
                    }
                ),
                modifier = Modifier
                    .weight(1f)
                    .onKeyEvent {
                        if (it.key == Key.Enter && commandText.isNotBlank()) {
                            executeCommand(commandText)
                            commandText = ""
                            keyboardController?.hide()
                            true
                        } else {
                            false
                        }
                    }
            )
            
            // Send button
            IconButton(
                onClick = {
                    if (commandText.isNotBlank()) {
                        executeCommand(commandText)
                        commandText = ""
                        keyboardController?.hide()
                    }
                },
                enabled = commandText.isNotBlank() && !isRunning
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Execute",
                    tint = if (commandText.isNotBlank() && !isRunning) accentLime else textSecondary
                )
            }
        }
    }
    
    // Execute command
    fun executeCommand(command: String) {
        isRunning = true
        
        // Add command to output
        terminalSession.execute(command)
            .onSuccess {
                isRunning = false
            }
            .onFailure {
                isRunning = false
                outputLines = outputLines + "Error: ${it.message}\n"
            }
    }
}

@Composable
fun TerminalLine(
    line: String,
    modifier: Modifier = Modifier
) {
    val color = when {
        line.startsWith("Error") || line.startsWith("error") -> stateError
        line.startsWith("Warning") || line.startsWith("warning") -> stateWarning
        line.startsWith("Success") || line.startsWith("success") -> stateSuccess
        line.startsWith("\$") -> accentLime
        line.isBlank() -> Color.Transparent
        else -> textPrimary
    }
    
    Text(
        text = line,
        style = MaterialTheme.typography.bodySmall,
        color = color,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun TerminalScreenPreview() {
    ForestTheme {
        val mockApp = remember {
            object : AIFrankensteinApp() {
                override fun onCreate() {
                    // Mock
                }
            }
        }
        
        TerminalScreen(
            navController = rememberNavController(),
            app = mockApp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TerminalLinePreview() {
    ForestTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            TerminalLine(line = "\$ ls -la")
            TerminalLine(line = "drwxr-xr-x 1 user user 4096 Jan 1 12:00 .")
            TerminalLine(line = "Error: File not found")
            TerminalLine(line = "Warning: This may take a while")
            TerminalLine(line = "Success: Command executed")
        }
    }
}
