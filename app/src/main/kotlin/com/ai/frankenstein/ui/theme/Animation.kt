package com.ai.frankenstein.ui.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Custom easing curves for Forest Swamp theme
val easeOrganic: Easing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1.0f)
val easeGlide: Easing = CubicBezierEasing(0.22f, 1.0f, 0.36f, 1.0f)
val easeSettle: Easing = CubicBezierEasing(0.16f, 1.0f, 0.3f, 1.0f)

// Animation specs
object ForestAnimations {
    // Micro-interactions: 120-180ms
    val microInteraction = TweenSpec<Float>(
        durationMillis = 150,
        easing = easeGlide
    )
    
    // Card appearance: 240-320ms
    val cardAppearance = TweenSpec<Float>(
        durationMillis = 280,
        easing = easeOrganic
    )
    
    // Screen transitions: 380-450ms
    val screenTransition = TweenSpec<Float>(
        durationMillis = 400,
        easing = easeGlide
    )
    
    // Spring animations
    val springBouncy = Spring<Float>(
        dampingRatio = 0.4f,
        stiffness = 100f
    )
    
    val springStiff = Spring<Float>(
        dampingRatio = 0.8f,
        stiffness = 500f
    )
    
    // Stagger animation helper
    fun <T> stagger(
        delayMillis: Int = 45,
        spec: FiniteAnimationSpec<T>
    ): FiniteAnimationSpec<T> {
        return TweenSpec(
            durationMillis = spec.durationMillis,
            easing = spec.easing,
            delayMillis = delayMillis
        )
    }
}

// Custom modifiers for Forest Swamp animations

fun Modifier.fadeIn(
    durationMillis: Int = 240,
    delayMillis: Int = 0,
    easing: Easing = easeGlide
) = composed {
    val transition = updateTransition(targetState = true, label = "fadeIn")
    
    val alpha by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                tween(durationMillis = durationMillis, delayMillis = delayMillis, easing = easing)
            } else {
                snap()
            }
        },
        label = "alpha"
    ) { isVisible ->
        if (isVisible) 1f else 0f
    }
    
    this.graphicsLayer {
        alpha = alpha
    }
}

fun Modifier.slideUp(
    durationMillis: Int = 280,
    delayMillis: Int = 0,
    offsetY: Float = 10f,
    easing: Easing = easeGlide
) = composed {
    val transition = updateTransition(targetState = true, label = "slideUp")
    
    val offset by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                tween(durationMillis = durationMillis, delayMillis = delayMillis, easing = easing)
            } else {
                snap()
            }
        },
        label = "offset"
    ) { isVisible ->
        if (isVisible) 0f else offsetY
    }
    
    this.graphicsLayer {
        translationY = offset
    }
}

fun Modifier.scaleIn(
    durationMillis: Int = 240,
    delayMillis: Int = 0,
    initialScale: Float = 0.9f,
    easing: Easing = easeOrganic
) = composed {
    val transition = updateTransition(targetState = true, label = "scaleIn")
    
    val scale by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                tween(durationMillis = durationMillis, delayMillis = delayMillis, easing = easing)
            } else {
                snap()
            }
        },
        label = "scale"
    ) { isVisible ->
        if (isVisible) 1f else initialScale
    }
    
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

fun Modifier.pulse(
    isActive: Boolean = true,
    durationMillis: Int = 900,
    minScale: Float = 0.4f,
    maxScale: Float = 1f
) = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    if (isActive) {
        this.graphicsLayer {
            scaleY = scale
        }
    } else {
        this
    }
}

fun Modifier.shake(
    isActive: Boolean = true,
    durationMillis: Int = 200,
    cycles: Int = 2,
    amplitude: Float = 4f
) = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "shake")
    
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = amplitude,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis / cycles, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
            iterations = cycles
        ),
        label = "shakeOffset"
    )
    
    if (isActive) {
        this.graphicsLayer {
            translationX = offset
        }
    } else {
        this
    }
}

// Text streaming animation (for AI responses)
@Composable
fun rememberStreamingAnimation(
    isStreaming: Boolean,
    durationMillis: Int = 180
): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "streaming")
    
    return if (isStreaming) {
        val opacity by infiniteTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = durationMillis),
                repeatMode = RepeatMode.Reverse
            ),
            label = "streamingOpacity"
        )
        opacity
    } else {
        1f
    }
}

// Thinking indicator animation (3 particles)
@Composable
fun rememberThinkingAnimation(
    isThinking: Boolean
): List<Float> {
    val infiniteTransition = rememberInfiniteTransition(label = "thinking")
    
    if (!isThinking) {
        return List(3) { 0f }
    }
    
    val particle1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1600
                0f at 0
                1f at 400
                0f at 800
                0f at 1600
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "particle1"
    )
    
    val particle2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1600
                0f at 0
                0f at 200
                1f at 600
                0f at 1000
                0f at 1600
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "particle2"
    )
    
    val particle3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1600
                0f at 0
                0f at 400
                0f at 800
                1f at 1200
                0f at 1600
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "particle3"
    )
    
    return listOf(particle1, particle2, particle3)
}

// Progress animation for loading
@Composable
fun rememberProgressAnimation(
    isLoading: Boolean,
    durationMillis: Int = 1000
): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "progress")
    
    return if (isLoading) {
        val progress by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = durationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "progress"
        )
        progress
    } else {
        0f
    }
}

// Custom modifier for staggered list animations
fun Modifier.staggeredAppearance(
    index: Int,
    isVisible: Boolean = true,
    delayPerItem: Int = 45
) = composed {
    val transition = updateTransition(targetState = isVisible, label = "staggeredAppearance")
    
    val alpha by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                tween(
                    durationMillis = 260,
                    delayMillis = index * delayPerItem,
                    easing = easeGlide
                )
            } else {
                snap()
            }
        },
        label = "alpha"
    ) { visible ->
        if (visible) 1f else 0f
    }
    
    val offsetY by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                tween(
                    durationMillis = 260,
                    delayMillis = index * delayPerItem,
                    easing = easeGlide
                )
            } else {
                snap()
            }
        },
        label = "offsetY"
    ) { visible ->
        if (visible) 0f else 10f
    }
    
    val blur by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                tween(
                    durationMillis = 260,
                    delayMillis = index * delayPerItem,
                    easing = easeGlide
                )
            } else {
                snap()
            }
        },
        label = "blur"
    ) { visible ->
        if (visible) 0f else 4f
    }
    
    this.graphicsLayer {
        alpha = alpha
        translationY = offsetY
        // Note: blur is not directly supported in graphicsLayer for text
        // For actual blur effect, you'd need to use Modifier.blur() from compose foundation
    }
}

// Modifier for hover effect
fun Modifier.hoverEffect(
    isHovered: Boolean,
    durationMillis: Int = 150
) = composed {
    val transition = updateTransition(targetState = isHovered, label = "hoverEffect")
    
    val offsetY by transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = durationMillis, easing = easeGlide)
        },
        label = "offsetY"
    ) { hovered ->
        if (hovered) -2f else 0f
    }
    
    this.graphicsLayer {
        translationY = offsetY
    }
}
