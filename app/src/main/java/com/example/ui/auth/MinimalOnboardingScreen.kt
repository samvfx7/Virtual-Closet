package com.example.ui.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CraftCorners
import com.example.ui.components.LuxuryPrimaryButton
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonBlueSoftGlow
import com.example.ui.theme.PureBlack
import com.example.ui.theme.SurfaceLevel1
import com.example.ui.theme.SurfaceLevel2
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

data class OnboardingStep(
    val stepNumber: String,
    val title: String,
    val description: String,
    val tag: String
)

@Composable
fun MinimalOnboardingScreen(
    userName: String,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        OnboardingStep(
            stepNumber = "01 / 03",
            title = "3D DIGITAL CLOSET",
            description = "Explore your wardrobe in an interactive spatial 3D chamber. Inspect garments on rods and shelves with pure black contrast.",
            tag = "SPATIAL LUXURY"
        ),
        OnboardingStep(
            stepNumber = "02 / 03",
            title = "GEMINI AI STYLIST",
            description = "Select your occasion, weather, and mood. Receive 3 instant curated looks with concise color theory logic in seconds.",
            tag = "INTELLIGENT CURATION"
        ),
        OnboardingStep(
            stepNumber = "03 / 03",
            title = "SAVED LOOKBOOK",
            description = "Bookmark top combinations for effortless daily styling. Zero clutter, pure focus on great style.",
            tag = "ESSENTIAL WARDROBE"
        )
    )

    var currentStepIndex by remember { mutableIntStateOf(0) }
    val step = steps[currentStepIndex]

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PureBlack)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(NeonBlueSoftGlow, Color.Transparent),
                        center = Offset(size.width * 0.8f, size.height * 0.3f),
                        radius = 200.dp.toPx()
                    ),
                    radius = 200.dp.toPx(),
                    center = Offset(size.width * 0.8f, size.height * 0.3f)
                )
            }
            .padding(horizontal = 28.dp, vertical = 40.dp)
            .testTag("onboarding_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CURATOR // ${userName.uppercase()}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.6.sp,
                color = NeonBlue
            )

            TextButton(
                onClick = onFinish,
                modifier = Modifier.testTag("onboarding_skip_button")
            ) {
                Text(
                    text = "SKIP",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = TextMuted
                )
            }
        }

        // Center Content
        AnimatedContent(
            targetState = step,
            transitionSpec = {
                fadeIn(animationSpec = tween(220)) togetherWith
                        fadeOut(animationSpec = tween(180))
            },
            modifier = Modifier.align(Alignment.Center),
            label = "onboarding_step_transition"
        ) { current ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = current.stepNumber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = NeonBlue
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = current.title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.3).sp,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = current.description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 22.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .clip(CraftCorners.Chip)
                        .background(SurfaceLevel2)
                        .border(1.dp, BorderSubtle, CraftCorners.Chip)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = current.tag,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.4.sp,
                        color = NeonBlue
                    )
                }
            }
        }

        // Bottom Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            // Step indicator lines
            Row(
                modifier = Modifier.padding(bottom = 28.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                steps.indices.forEach { index ->
                    val isActive = index == currentStepIndex
                    Box(
                        modifier = Modifier
                            .size(width = if (isActive) 32.dp else 10.dp, height = 3.dp)
                            .clip(CraftCorners.Pill)
                            .background(if (isActive) NeonBlue else SurfaceLevel2)
                    )
                }
            }

            // Action Button
            val isLast = currentStepIndex == steps.lastIndex
            LuxuryPrimaryButton(
                text = if (isLast) "ENTER CLOSET" else "CONTINUE",
                icon = if (isLast) Icons.Default.Check else Icons.Default.ArrowForward,
                onClick = {
                    if (isLast) {
                        onFinish()
                    } else {
                        currentStepIndex++
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                testTag = "onboarding_action_button"
            )
        }
    }
}
