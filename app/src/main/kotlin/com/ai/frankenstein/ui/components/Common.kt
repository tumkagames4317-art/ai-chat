package com.ai.frankenstein.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ai.frankenstein.ui.theme.ForestTheme
import com.ai.frankenstein.ui.theme.accentLime
import com.ai.frankenstein.ui.theme.bgPanel
import com.ai.frankenstein.ui.theme.bgPanelRaised
import com.ai.frankenstein.ui.theme.borderMoss
import com.ai.frankenstein.ui.theme.textPrimary
import com.ai.frankenstein.ui.theme.textSecondary

// Loading indicator
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = accentLime
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = color,
            strokeWidth = 2.dp
        )
    }
}

// Empty state
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String? = null,
    action: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textSecondary,
            modifier = Modifier.size(64.dp)
        )
        
        if (title.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = textPrimary
            )
        }
        
        if (description != null && description.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = textSecondary
            )
        }
        
        if (action != null) {
            Spacer(modifier = Modifier.height(16.dp))
            action()
        }
    }
}

// Info card
@Composable
fun InfoCard(
    title: String,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgPanelRaised)
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = textPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        content()
    }
}

// Action button
@Composable
fun ActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onClick() }
            .background(if (enabled) bgPanelRaised else bgPanel)
            .padding(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = if (enabled) textPrimary else textSecondary
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (enabled) textPrimary else textSecondary
        )
    }
}

// Status indicator
@Composable
fun StatusIndicator(
    status: String,
    color: Color = accentLime,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(50))
                .background(color)
        )
        
        Spacer(modifier = Modifier.width(6.dp))
        
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

// Divider with text
@Composable
fun DividerWithText(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Spacer(
            modifier = Modifier
                .height(1.dp)
                .weight(1f)
                .background(borderMoss)
        )
        
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textSecondary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(
            modifier = Modifier
                .height(1.dp)
                .weight(1f)
                .background(borderMoss)
        )
    }
}

// Header
@Composable
fun SectionHeader(
    title: String,
    action: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = textPrimary
        )
        
        if (action != null) {
            action()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommonComponentsPreview() {
    ForestTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LoadingIndicator()
            
            EmptyState(
                icon = androidx.compose.material.icons.Icons.Default.Info,
                title = "No items",
                description = "There are no items to display"
            )
            
            InfoCard(title = "Information") {
                Text("This is some information")
            }
            
            ActionButton(
                icon = androidx.compose.material.icons.Icons.Default.Add,
                text = "Add Item",
                onClick = {}
            )
            
            StatusIndicator(status = "Connected", color = accentLime)
            
            DividerWithText(text = "OR")
            
            SectionHeader(
                title = "Section Title",
                action = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Add,
                            contentDescription = "Add"
                        )
                    }
                }
            )
        }
    }
}
