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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
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
import androidx.compose.ui.unit.dp
import com.ai.frankenstein.domain.model.Model
import com.ai.frankenstein.ui.theme.ForestTheme
import com.ai.frankenstein.ui.theme.accentEmerald
import com.ai.frankenstein.ui.theme.accentLime
import com.ai.frankenstein.ui.theme.accentOlive
import com.ai.frankenstein.ui.theme.bgPanel
import com.ai.frankenstein.ui.theme.bgPanelRaised
import com.ai.frankenstein.ui.theme.borderMoss
import com.ai.frankenstein.ui.theme.textPrimary
import com.ai.frankenstein.ui.theme.textSecondary

@Composable
fun ModelSelector(
    selectedModel: Model?,
    models: List<Model>,
    onModelSelected: (Model) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredModels = if (searchQuery.isBlank()) {
        models
    } else {
        models.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.provider.contains(searchQuery, ignoreCase = true) ||
            it.description.contains(searchQuery, ignoreCase = true)
        }
    }
    
    Column(modifier = modifier) {
        // Selected model button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(bgPanelRaised)
                .clickable { isExpanded = !isExpanded },
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                if (selectedModel != null) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedModel.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = textPrimary
                        )
                        Text(
                            text = selectedModel.provider,
                            style = MaterialTheme.typography.labelSmall,
                            color = textSecondary
                        )
                    }
                } else {
                    Text(
                        text = "Select a model",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textSecondary
                    )
                }
                
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = if (isExpanded) "Close" else "Open",
                    tint = textSecondary
                )
            }
        }
        
        // Dropdown with models
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 2 }),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .background(bgPanel, RoundedCornerShape(12.dp))
                    .border(1.dp, borderMoss, RoundedCornerShape(12.dp))
            ) {
                // Search field
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = textSecondary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = "Search models...",
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (searchQuery.isNotBlank()) {
                        IconButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = textSecondary
                            )
                        }
                    }
                }
                
                Divider(color = borderMoss, modifier = Modifier.padding(horizontal = 8.dp))
                
                // Models list
                if (filteredModels.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No models found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = textSecondary
                        )
                    }
                } else {
                    LazyColumn(
                        state = rememberLazyListState(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        items(filteredModels) { model ->
                            ModelItem(
                                model = model,
                                isSelected = selectedModel?.id == model.id,
                                onClick = {
                                    onModelSelected(model)
                                    isExpanded = false
                                    searchQuery = ""
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
fun ModelItem(
    model: Model,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = model.name,
                style = MaterialTheme.typography.bodyMedium,
                color = textPrimary
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = model.provider,
                    style = MaterialTheme.typography.labelSmall,
                    color = when (model.provider) {
                        "openai" -> Color(0xFF00FF88)
                        "anthropic" -> Color(0xFFAA00FF)
                        "mistral" -> Color(0xFFFF5500)
                        "google" -> Color(0xFF0088FF)
                        "ollama" -> Color(0xFFFF00FF)
                        else -> textSecondary
                    }
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "${model.contextWindow} context",
                    style = MaterialTheme.typography.labelSmall,
                    color = textSecondary
                )
            }
        }
        
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = accentLime
            )
        }
    }
}

@Composable
fun ModelChip(
    model: Model,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) accentLime else bgPanelRaised)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = model.name,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) bgPanel else textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ModelSelectorPreview() {
    ForestTheme {
        val models = remember {
            listOf(
                Model(
                    id = "gpt-4o",
                    name = "GPT-4o",
                    provider = "openai",
                    description = "Latest OpenAI model",
                    maxTokens = 16384,
                    contextWindow = 16384
                ),
                Model(
                    id = "claude-3-5-sonnet",
                    name = "Claude 3.5 Sonnet",
                    provider = "anthropic",
                    description = "Fast and intelligent",
                    maxTokens = 8192,
                    contextWindow = 8192
                ),
                Model(
                    id = "mistral-large",
                    name = "Mistral Large",
                    provider = "mistral",
                    description = "Powerful open-source",
                    maxTokens = 32768,
                    contextWindow = 32768
                )
            )
        }
        
        ModelSelector(
            selectedModel = models[0],
            models = models,
            onModelSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ModelChipPreview() {
    ForestTheme {
        val model = remember {
            Model(
                id = "gpt-4o",
                name = "GPT-4o",
                provider = "openai",
                description = "Latest OpenAI model"
            )
        }
        
        Row(modifier = Modifier.padding(16.dp)) {
            ModelChip(
                model = model,
                isSelected = true,
                onClick = {}
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            ModelChip(
                model = model,
                isSelected = false,
                onClick = {}
            )
        }
    }
}
