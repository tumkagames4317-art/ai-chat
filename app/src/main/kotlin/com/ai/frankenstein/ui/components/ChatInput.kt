package com.ai.frankenstein.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ai.frankenstein.ui.theme.ForestTheme
import com.ai.frankenstein.ui.theme.accentLime
import com.ai.frankenstein.ui.theme.accentOlive
import com.ai.frankenstein.ui.theme.bgPanel
import com.ai.frankenstein.ui.theme.borderMoss
import com.ai.frankenstein.ui.theme.textPrimary
import com.ai.frankenstein.ui.theme.textSecondary

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onAttach: () -> Unit = {},
    onVoice: () -> Unit = {},
    isEnabled: Boolean = true,
    isLoading: Boolean = false,
    placeholder: String = "Type your message...",
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(bgPanel)
            .padding(8.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(bgPanel, RoundedCornerShape(24.dp))
                .border(1.dp, borderMoss, RoundedCornerShape(24.dp))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Attach button
            IconButton(
                onClick = onAttach,
                enabled = isEnabled && !isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "Attach file",
                    tint = textSecondary
                )
            }
            
            Spacer(modifier = Modifier.width(4.dp))
            
            // Text input
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    enabled = isEnabled && !isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (value.isNotBlank()) {
                                onSend()
                                keyboardController?.hide()
                            }
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onKeyEvent {
                            if (it.key == Key.Enter && value.isNotBlank()) {
                                onSend()
                                keyboardController?.hide()
                                true
                            } else {
                                false
                            }
                        }
                )
                
                // Placeholder
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textSecondary.copy(alpha = 0.5f),
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(4.dp))
            
            // Voice button
            IconButton(
                onClick = onVoice,
                enabled = isEnabled && !isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice input",
                    tint = textSecondary
                )
            }
            
            Spacer(modifier = Modifier.width(4.dp))
            
            // Send button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (value.isNotBlank() && isEnabled && !isLoading) accentLime else borderMoss)
                    .clickable(enabled = value.isNotBlank() && isEnabled && !isLoading) {
                        onSend()
                        keyboardController?.hide()
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    // Loading indicator
                    Box(
                        modifier = Modifier.size(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Simple dot animation
                        Row {
                            repeat(3) { index ->
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(
                                            color = textPrimary,
                                            shape = RoundedCornerShape(50)
                                        )
                                        .padding(start = if (index > 0) 2.dp else 0.dp)
                                )
                            }
                        }
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (value.isNotBlank() && isEnabled) bgPanel else textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatInputPreview() {
    ForestTheme {
        var text by remember { mutableStateOf("") }
        ChatInput(
            value = text,
            onValueChange = { text = it },
            onSend = { text = "" },
            placeholder = "Type your message..."
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatInputWithTextPreview() {
    ForestTheme {
        var text by remember { mutableStateOf("Hello, how are you?") }
        ChatInput(
            value = text,
            onValueChange = { text = it },
            onSend = { text = "" },
            placeholder = "Type your message..."
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatInputLoadingPreview() {
    ForestTheme {
        var text by remember { mutableStateOf("Waiting...") }
        ChatInput(
            value = text,
            onValueChange = { text = it },
            onSend = {},
            isLoading = true
        )
    }
}
