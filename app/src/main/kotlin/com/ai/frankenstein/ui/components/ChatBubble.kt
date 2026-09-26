package com.ai.frankenstein.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PushPin
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ai.frankenstein.domain.model.Message
import com.ai.frankenstein.ui.theme.ForestTheme
import com.ai.frankenstein.ui.theme.accentEmerald
import com.ai.frankenstein.ui.theme.accentLime
import com.ai.frankenstein.ui.theme.accentOlive
import com.ai.frankenstein.ui.theme.bgPanel
import com.ai.frankenstein.ui.theme.bgPanelRaised
import com.ai.frankenstein.ui.theme.borderMoss
import com.ai.frankenstein.ui.theme.textPrimary
import com.ai.frankenstein.ui.theme.textSecondary
import java.util.Date

@Composable
fun ChatBubble(
    message: Message,
    isStreaming: Boolean = false,
    onCopy: () -> Unit = {},
    onFavorite: () -> Unit = {},
    onPin: () -> Unit = {},
    onMore: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isUser = message.role == Message.MessageRole.USER
    val isAssistant = message.role == Message.MessageRole.ASSISTANT
    val isSystem = message.role == Message.MessageRole.SYSTEM
    
    val bubbleColor = when {
        isUser -> accentEmerald
        isSystem -> bgPanelRaised
        else -> bgPanel
    }
    
    val textColor = when {
        isUser || isSystem -> textPrimary
        else -> textPrimary
    }
    
    val alignment = when {
        isUser -> Alignment.End
        else -> Alignment.Start
    }
    
    val shape = RoundedCornerShape(
        topStart = if (isUser) 16.dp else 4.dp,
        topEnd = if (isUser) 4.dp else 16.dp,
        bottomStart = 16.dp,
        bottomEnd = 16.dp
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Column(
            modifier = Modifier
                .clip(shape)
                .background(bubbleColor)
                .padding(12.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            // Header with model info
            if (!isUser && message.model != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Text(
                        text = message.model ?: "AI",
                        style = MaterialTheme.typography.labelSmall,
                        color = accentLime
                    )
                    if (message.provider != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "@${message.provider}",
                            style = MaterialTheme.typography.labelSmall,
                            color = textSecondary
                        )
                    }
                }
            }
            
            // Message content
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 2 })
            ) {
                SelectionContainer {
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = if (isStreaming) 100 else Int.MAX_VALUE
                    )
                }
            }
            
            // Streaming indicator
            if (isStreaming) {
                Spacer(modifier = Modifier.size(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    // Animated dots
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    color = if (isStreaming) accentLime else Color.Transparent,
                                    shape = RoundedCornerShape(50)
                                )
                                .padding(start = if (index > 0) 4.dp else 0.dp)
                        )
                    }
                }
            }
            
            // Footer with timestamp and actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
            ) {
                if (!isUser) {
                    // Token count
                    if (message.tokenCount > 0) {
                        Text(
                            text = "${message.tokenCount} tokens",
                            style = MaterialTheme.typography.labelSmall,
                            color = textSecondary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                }
                
                // Timestamp
                Text(
                    text = message.timestamp.formatTime(),
                    style = MaterialTheme.typography.labelSmall,
                    color = textSecondary
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Action buttons
                Row {
                    if (!isUser) {
                        IconButton(
                            onClick = { onCopy() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    IconButton(
                        onClick = { onFavorite() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (message.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (message.isFavorite) accentLime else textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = { onPin() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (message.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "Pin",
                            tint = if (message.isPinned) accentLime else textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = { onMore() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SelectionContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // In a real implementation, this would handle text selection
    Box(modifier = modifier) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun ChatBubblePreview() {
    ForestTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ChatBubble(
                message = Message(
                    id = 1,
                    chatId = 1,
                    role = Message.MessageRole.USER,
                    content = "Hello, how are you today?",
                    model = "gpt-3.5-turbo",
                    provider = "openai",
                    timestamp = Date(),
                    tokenCount = 10
                )
            )
            
            ChatBubble(
                message = Message(
                    id = 2,
                    chatId = 1,
                    role = Message.MessageRole.ASSISTANT,
                    content = "I'm doing well, thank you for asking! How can I help you today?",
                    model = "gpt-3.5-turbo",
                    provider = "openai",
                    timestamp = Date(),
                    tokenCount = 15
                ),
                isStreaming = true
            )
        }
    }
}
