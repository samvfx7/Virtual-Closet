package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderNeonActive
import com.example.ui.theme.BorderNeonSubtle
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.BorderSubtleLight
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonBlueGlow
import com.example.ui.theme.NeonBlueSoftGlow
import com.example.ui.theme.PureBlack
import com.example.ui.theme.SurfaceLevel1
import com.example.ui.theme.SurfaceLevel2
import com.example.ui.theme.SurfaceLevel3
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

// ==========================================
// CRAFT DESIGN TOKENS
// ==========================================
object CraftCorners {
    val Chip = RoundedCornerShape(8.dp)
    val Button = RoundedCornerShape(12.dp)
    val Card = RoundedCornerShape(16.dp)
    val Modal = RoundedCornerShape(24.dp)
    val Pill = RoundedCornerShape(999.dp)
}

// Micro-interaction: Springy scale down to 0.97f on tap and spring back on release
fun Modifier.bounceClick(
    scaleDown: Float = 0.96f,
    onClick: () -> Unit
): Modifier = this.pointerInput(Unit) {
    while (true) {
        awaitPointerEventScope {
            awaitFirstDown(requireUnconsumed = false)
            val upOrCancel = waitForUpOrCancellation()
            if (upOrCancel != null) {
                onClick()
            }
        }
    }
}

@Composable
fun Modifier.pressScale(
    targetScale: Float = 0.97f
): Modifier {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) targetScale else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 400f),
        label = "press_scale"
    )
    return this
        .scale(scale)
        .pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    awaitFirstDown(requireUnconsumed = false)
                    isPressed = true
                    val up = waitForUpOrCancellation()
                    isPressed = false
                }
            }
        }
}

// ==========================================
// LUXURY SECTION HEADER
// ==========================================
@Composable
fun LuxuryScreenHeader(
    lightWord: String,
    boldWord: String,
    subtitle: String? = null,
    counter: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                // Subtle radial glow behind the header for depth
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(NeonBlueSoftGlow, Color.Transparent),
                        center = Offset(size.width * 0.15f, size.height * 0.5f),
                        radius = 160.dp.toPx()
                    ),
                    radius = 160.dp.toPx(),
                    center = Offset(size.width * 0.15f, size.height * 0.5f)
                )
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = lightWord,
                fontSize = 28.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = (-0.5).sp,
                color = TextSecondary
            )
            Text(
                text = boldWord,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                color = TextPrimary
            )
            if (counter != null) {
                Box(
                    modifier = Modifier
                        .clip(CraftCorners.Chip)
                        .background(SurfaceLevel2)
                        .border(1.dp, BorderSubtle, CraftCorners.Chip)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = counter,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = NeonBlue
                    )
                }
            }
        }

        if (subtitle != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 18.sp,
                color = TextSecondary
            )
        }
    }
}

// Section Label (e.g. OCCASION, WEATHER, MOOD)
@Composable
fun LuxurySectionLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(4.dp)
                .background(NeonBlue, CraftCorners.Pill)
        )
        Text(
            text = text.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.6.sp,
            color = TextMuted
        )
    }
}

// ==========================================
// LUXURY PRIMARY CTA BUTTON (With Glow & Spring Press)
// ==========================================
@Composable
fun LuxuryPrimaryButton(
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    loadingText: String = "PROCESSING...",
    testTag: String = "primary_button"
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 400f),
        label = "btn_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .drawBehind {
                // Soft neon ambient glow behind button
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(NeonBlueGlow, Color.Transparent),
                        center = Offset(size.width / 2, size.height / 2),
                        radius = size.width * 0.65f
                    ),
                    radius = size.width * 0.65f,
                    center = Offset(size.width / 2, size.height / 2)
                )
            }
            .clip(CraftCorners.Button)
            .background(NeonBlue)
            .pointerInput(isLoading) {
                if (!isLoading) {
                    awaitPointerEventScope {
                        while (true) {
                            awaitFirstDown(requireUnconsumed = false)
                            isPressed = true
                            val up = waitForUpOrCancellation()
                            isPressed = false
                            if (up != null) {
                                onClick()
                            }
                        }
                    }
                }
            }
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                SleekPulseIndicator(modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = loadingText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = PureBlack
                )
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PureBlack,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = PureBlack
                )
            }
        }
    }
}

// ==========================================
// LUXURY SELECTOR CHIP (Occasion, Weather, Mood, Category)
// Smooth animated transition (180ms ease)
// ==========================================
@Composable
fun LuxurySelectorChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 500f),
        label = "chip_scale"
    )

    val bgColor by animateColorAsState(
        targetValue = if (selected) NeonBlue else SurfaceLevel2,
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "chip_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) NeonBlue else BorderSubtleLight,
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "chip_border"
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) PureBlack else TextPrimary,
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "chip_text"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(CraftCorners.Chip)
            .background(bgColor)
            .border(1.dp, borderColor, CraftCorners.Chip)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitFirstDown(requireUnconsumed = false)
                        isPressed = true
                        val up = waitForUpOrCancellation()
                        isPressed = false
                        if (up != null) {
                            onClick()
                        }
                    }
                }
            }
            .padding(horizontal = 14.dp, vertical = 9.dp)
            .let { if (testTag.isNotBlank()) it.testTag(testTag) else it },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Black else FontWeight.Medium,
            letterSpacing = 0.8.sp,
            color = textColor
        )
    }
}

// ==========================================
// SLEEK NEON SHIMMER / PULSE LOADING INDICATOR
// ==========================================
@Composable
fun SleekPulseIndicator(
    modifier: Modifier = Modifier,
    color: Color = PureBlack
) {
    val transition = rememberInfiniteTransition(label = "pulse_transition")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = modifier
            .clip(CraftCorners.Pill)
            .background(color.copy(alpha = alpha))
    )
}

@Composable
fun SleekShimmerBar(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "shimmer_transition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            SurfaceLevel1,
            NeonBlueSoftGlow,
            SurfaceLevel2,
            NeonBlueSoftGlow,
            SurfaceLevel1
        ),
        start = Offset(translateAnim - 400f, 0f),
        end = Offset(translateAnim, 0f)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(brush)
    )
}

// ==========================================
// ELEVATED CARD SURFACE
// ==========================================
@Composable
fun LuxuryElevatedCard(
    modifier: Modifier = Modifier,
    shape: Shape = CraftCorners.Card,
    onClick: (() -> Unit)? = null,
    testTag: String? = null,
    content: @Composable () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.985f else 1f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 500f),
        label = "card_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .background(SurfaceLevel2)
            .border(1.dp, BorderSubtle, shape)
            .let { m ->
                if (onClick != null) {
                    m.pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                awaitFirstDown(requireUnconsumed = false)
                                isPressed = true
                                val up = waitForUpOrCancellation()
                                isPressed = false
                                if (up != null) {
                                    onClick()
                                }
                            }
                        }
                    }
                } else m
            }
            .let { if (testTag != null) it.testTag(testTag) else it }
            .padding(18.dp)
    ) {
        content()
    }
}
