package com.example.ui.auth

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CraftCorners
import com.example.ui.components.LuxuryPrimaryButton
import com.example.ui.components.bounceClick
import com.example.ui.theme.BorderNeonSubtle
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.BorderSubtleLight
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonBlueGlow
import com.example.ui.theme.NeonBlueSoftGlow
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceLevel1
import com.example.ui.theme.SurfaceLevel2
import com.example.ui.theme.SurfaceLevel3
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun WelcomeAuthScreen(
    isLoading: Boolean,
    onSignInWithGoogle: (name: String) -> Unit,
    onContinueAsGuest: (name: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var nameInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PureBlack)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(NeonBlueSoftGlow, Color.Transparent),
                        center = Offset(size.width * 0.2f, size.height * 0.25f),
                        radius = 240.dp.toPx()
                    ),
                    radius = 240.dp.toPx(),
                    center = Offset(size.width * 0.2f, size.height * 0.25f)
                )
            }
            .padding(horizontal = 28.dp, vertical = 40.dp)
            .testTag("welcome_auth_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.Start
        ) {
            // Neon accent marker dot
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(NeonBlue)
                )
                Text(
                    text = "DIGITAL ATELIER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.8.sp,
                    color = NeonBlue
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Mixed-weight statement Brand Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "VIRTUAL",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = (-0.5).sp,
                    color = TextSecondary
                )
                Text(
                    text = "CLOSET",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Minimalist 3D digital wardrobe & high-performance outfit generator powered by Gemini AI.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 22.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Section Label
            Text(
                text = "YOUR NAME",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.6.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                placeholder = {
                    Text(
                        "Enter your name",
                        color = TextTertiary,
                        fontSize = 14.sp
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonBlue,
                    unfocusedBorderColor = BorderSubtleLight,
                    focusedContainerColor = SurfaceLevel2,
                    unfocusedContainerColor = SurfaceLevel2,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = NeonBlue
                ),
                shape = CraftCorners.Button,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_name_input")
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = NeonBlue,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(32.dp)
                    )
                }
            } else {
                // Option 1: Sign in with Google (Light Surface / High Craft)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CraftCorners.Button)
                        .background(TextPrimary)
                        .bounceClick(scaleDown = 0.96f) {
                            focusManager.clearFocus()
                            onSignInWithGoogle(nameInput)
                        }
                        .padding(vertical = 15.dp)
                        .testTag("google_signin_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SIGN IN WITH GOOGLE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = PureBlack
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Option 2: Continue as Guest
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CraftCorners.Button)
                        .background(SurfaceLevel1)
                        .border(1.dp, BorderSubtleLight, CraftCorners.Button)
                        .bounceClick(scaleDown = 0.96f) {
                            focusManager.clearFocus()
                            onContinueAsGuest(nameInput)
                        }
                        .padding(vertical = 15.dp)
                        .testTag("guest_continue_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "CONTINUE AS GUEST",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = NeonBlue,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }

        // Minimal footer
        Text(
            text = "DIGITAL CRAFT • ZERO BLOAT",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp,
            color = TextTertiary,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
