package com.ai.frankenstein.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ai.frankenstein.ui.theme.ForestTheme
import com.ai.frankenstein.ui.theme.accentLime
import com.ai.frankenstein.ui.theme.accentLimeDim
import com.ai.frankenstein.ui.theme.bgPanel
import com.ai.frankenstein.ui.theme.textSecondary
import kotlinx.coroutines.launch

@Composable
fun ThinkingIndicator(
    message: String = "AI is thinking...",
    modifier: Modifier = Modifier,
    dotCount: Int = 3
) {
    val infiniteTransition = rememberInfiniteTransition(label = "thinking")
    
    // Animate each dot with a delay
    val dots = List(dotCount) { index ->
        val delay = index * 150
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 1200
                    0.5f at 0 + delay
                    1.2f at 300 + delay
                    0.5f at 600 + delay
                    0.5f at 1200 + delay
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "dotScale$index"
        )
        
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 1200
                    0.3f at 0 + delay
                    1f at 300 + delay
                    0.3f at 600 + delay
                    0.3f at 1200 + delay
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "dotAlpha$index"
        )
        
        scale to alpha
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = textSecondary
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            dots.forEach { (scale, alpha) ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(accentLime.copy(alpha = alpha))
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@Composable
fun ParticleThinkingIndicator(
    modifier: Modifier = Modifier,
    particleCount: Int = 3
) {
    val infiniteTransition = rememberInfiniteTransition(label = "particleThinking")
    
    Box(
        modifier = modifier.size(48.dp),
        contentAlignment = Alignment.Center
    ) {
        // Draw orbit paths
        Canvas(modifier = Modifier.matchParentSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 3
            
            // Draw faint orbits
            repeat(particleCount) { index ->
                drawCircle(
                    color = accentLimeDim.copy(alpha = 0.2f),
                    radius = radius * (1 + index * 0.3f),
                    center = center
                )
            }
        }
        
        // Animate particles
        repeat(particleCount) { index ->
            val angle by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1600 + index * 200,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                label = "particle$index"
            )
            
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 800,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "particleScale$index"
            )
            
            val glowAlpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1600,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "particleGlow$index"
            )
            
            val particleColor = accentLime.copy(alpha = glowAlpha)
            
            Box(
                modifier = Modifier
                    .size((8 * scale).dp)
                    .clip(RoundedCornerShape(50))
                    .background(particleColor)
            )
        }
    }
}

@Composable
fun StreamingIndicator(
    modifier: Modifier = Modifier,
    isStreaming: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "streaming")
    
    if (isStreaming) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            repeat(3) { index ->
                val offset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 10f,
                    animationSpec = infiniteRepeatable(
                        animation = keyframes {
                            durationMillis = 600
                            0f at 0
                            10f at 150 + index * 100
                            0f at 300 + index * 100
                            0f at 600
                        },
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "streamDot$index"
                )
                
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = keyframes {
                            durationMillis = 600
                            0.3f at 0
                            1f at 150 + index * 100
                            0.3f at 300 + index * 100
                            0.3f at 600
                        },
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "streamAlpha$index"
                )
                
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(accentLime.copy(alpha = alpha))
                )
                
                if (index < 2) {
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ThinkingIndicatorPreview() {
    ForestTheme {
        ThinkingIndicator(
            message = "AI is thinking...",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ParticleThinkingIndicatorPreview() {
    ForestTheme {
        ParticleThinkingIndicator(
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StreamingIndicatorPreview() {
    ForestTheme {
        StreamingIndicator(
            modifier = Modifier.padding(16.dp),
            isStreaming = true
        )
    }
}
