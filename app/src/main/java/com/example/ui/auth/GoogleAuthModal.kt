package com.example.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.example.auth.UserProfile
import com.example.ui.theme.CardBorderGold
import com.example.ui.theme.CardBorderWhite10
import com.example.ui.theme.CardBorderWhite5
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneGoldLight
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.MutedText
import com.example.ui.theme.MutedTextLight
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.PlatinumText
import com.example.ui.theme.SuccessEmerald

@Composable
fun GoogleLogoIcon(modifier: Modifier = Modifier.size(20.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        // Google "G" 4-color stylized rendering
        val blue = Color(0xFF4285F4)
        val red = Color(0xFFEA4335)
        val yellow = Color(0xFFFBBC05)
        val green = Color(0xFF34A853)

        // Draw outer circular arc segments
        val strokeW = w * 0.22f
        val center = Offset(w / 2, h / 2)
        val radius = (w - strokeW) / 2

        // Top arc (Red)
        drawArc(
            color = red,
            startAngle = 200f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(strokeW / 2, strokeW / 2),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeW)
        )
        // Bottom Right arc (Green)
        drawArc(
            color = green,
            startAngle = 340f,
            sweepAngle = 100f,
            useCenter = false,
            topLeft = Offset(strokeW / 2, strokeW / 2),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeW)
        )
        // Bottom Left arc (Yellow)
        drawArc(
            color = yellow,
            startAngle = 80f,
            sweepAngle = 120f,
            useCenter = false,
            topLeft = Offset(strokeW / 2, strokeW / 2),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeW)
        )
        // Right bar (Blue)
        drawRect(
            color = blue,
            topLeft = Offset(center.x, center.y - strokeW / 2),
            size = androidx.compose.ui.geometry.Size(radius + strokeW / 2, strokeW)
        )
    }
}

@Composable
fun GoogleAuthModal(
    user: UserProfile?,
    isLoading: Boolean,
    itemCount: Int,
    onSignInWithGoogle: () -> Unit,
    onSignOut: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorderWhite10, RoundedCornerShape(24.dp))
                .testTag("google_auth_modal"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GoogleLogoIcon(modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (user != null) "GOOGLE CLOUD ATELIER" else "GOOGLE SIGN IN",
                            color = ChampagneGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(DarkNavyElevated)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MutedText,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (user != null) {
                    // Logged In Account Overview
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.sweepGradient(
                                    listOf(
                                        ChampagneGold,
                                        Color(0xFF4285F4),
                                        Color(0xFF34A853),
                                        Color(0xFFFBBC05),
                                        Color(0xFFEA4335),
                                        ChampagneGold
                                    )
                                )
                            )
                            .padding(3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!user.photoUrl.isNullOrEmpty()) {
                            Image(
                                painter = rememberAsyncImagePainter(model = user.photoUrl),
                                contentDescription = "Profile Photo",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(CircleShape)
                                    .background(DarkNavyCard),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.name.take(1).uppercase(),
                                    color = ChampagneGold,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = user.name,
                        color = PlatinumText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = user.email,
                        color = MutedTextLight,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tier Pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(ChampagneGold.copy(alpha = 0.15f))
                            .border(1.dp, ChampagneGold.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "VIP",
                            tint = ChampagneGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = user.tier,
                            color = ChampagneGoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Divider(color = CardBorderWhite5, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Google Sync Live Stats
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkNavyElevated)
                            .border(1.dp, CardBorderWhite5, RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CloudDone, contentDescription = "Sync", tint = SuccessEmerald, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Cloud Wardrobe Sync", color = PlatinumText, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                            Text(text = "Active", color = SuccessEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Devices, contentDescription = "Devices", tint = ChampagneGold, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Encrypted Archive", color = PlatinumText, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                            Text(text = "$itemCount Pieces", color = ChampagneGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Security, contentDescription = "Security", tint = Color(0xFF4285F4), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Google Identity Protection", color = PlatinumText, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                            Icon(Icons.Default.CheckCircle, contentDescription = "Protected", tint = SuccessEmerald, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Sign Out Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkNavyElevated)
                            .border(1.dp, CardBorderWhite10, RoundedCornerShape(14.dp))
                            .clickable { onSignOut() }
                            .padding(horizontal = 16.dp)
                            .testTag("google_sign_out_button"),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Logout, contentDescription = "Sign Out", tint = MutedText, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Sign Out from Google", color = MutedTextLight, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }

                } else {
                    // Sign In Screen
                    Text(
                        text = "Synchronize Your Haute Wardrobe",
                        color = PlatinumText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Sign in with Google to backup your 3D digital closet, enable cross-device synchronization, and preserve your personalized AI Stylist memories.",
                        color = MutedTextLight,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Benefits List
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkNavyElevated)
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BenefitRow("Instant Cloud Backup for all 3D items & looks")
                        BenefitRow("Gemini Fashion Stylist contextual memory")
                        BenefitRow("Multi-device access & offline resilience")
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Google Official Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White)
                            .border(1.dp, CardBorderGold, RoundedCornerShape(14.dp))
                            .clickable(enabled = !isLoading) { onSignInWithGoogle() }
                            .testTag("google_sign_in_action_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF4285F4), strokeWidth = 2.dp)
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                GoogleLogoIcon(modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Continue with Google",
                                    color = Color(0xFF1F1F1F),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Fast & secure Google Credential Manager authentication",
                        color = MutedText,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun BenefitRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = ChampagneGold,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = PlatinumText,
            fontSize = 11.sp
        )
    }
}
