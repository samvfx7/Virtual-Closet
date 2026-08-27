package com.example.ui.outfit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClothingItem
import com.example.ui.theme.CardBorderGold
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneGoldLight
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.MutedText
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.PlatinumText
import com.example.ui.theme.RoseGold

@Composable
fun MannequinVisualizer(
    selectedItems: List<ClothingItem>,
    onRemoveItem: (ClothingItem) -> Unit,
    onSaveOutfit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isBackView by remember { mutableStateOf(false) }
    val flipRotation by animateFloatAsState(
        targetValue = if (isBackView) 180f else 0f,
        animationSpec = tween(600),
        label = "mannequin_flip"
    )

    val topItem = selectedItems.find { it.category == "TOPS" }
    val bottomItem = selectedItems.find { it.category == "BOTTOMS" }
    val dressItem = selectedItems.find { it.category == "DRESSES" }
    val outerItem = selectedItems.find { it.category == "OUTERWEAR" }
    val shoeItem = selectedItems.find { it.category == "SHOES" }
    val accessoryItem = selectedItems.find { it.category == "ACCESSORIES" }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, CardBorderGold, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar with Front/Back Flip Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isBackView) "Atelier Mannequin (Rear View)" else "Atelier Mannequin (Front View)",
                        color = ChampagneGold,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${selectedItems.size} Layered Elements",
                        color = MutedText,
                        fontSize = 12.sp
                    )
                }

                // Flip View Button
                IconButton(
                    onClick = { isBackView = !isBackView },
                    modifier = Modifier
                        .background(DarkNavyElevated, CircleShape)
                        .border(1.dp, CardBorderGold.copy(alpha = 0.5f), CircleShape)
                        .size(40.dp)
                        .testTag("flip_mannequin_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FlipCameraAndroid,
                        contentDescription = "Flip Perspective",
                        tint = ChampagneGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3D Mannequin Visual Canvas with Dynamic Layers
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF1B2433), ObsidianBg),
                            center = Offset(200f, 200f),
                            radius = 450f
                        )
                    )
                    .graphicsLayer {
                        rotationY = flipRotation
                        cameraDistance = 12f * density
                    },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val cx = w / 2

                    // 1. Mannequin Stand & Base Pole
                    val goldColor = ChampagneGold.copy(alpha = 0.85f)
                    drawLine(
                        color = goldColor,
                        start = Offset(cx, h * 0.15f),
                        end = Offset(cx, h * 0.92f),
                        strokeWidth = 4f
                    )
                    drawOval(
                        color = goldColor,
                        topLeft = Offset(cx - 35f, h * 0.9f),
                        size = Size(70f, 18f),
                        style = Stroke(width = 3f)
                    )

                    // 2. Sculpted Mannequin Form (Head, Shoulders, Waist)
                    val formColor = Color(0xFF2A364B)
                    // Head/Neck Finial
                    drawOval(
                        color = goldColor,
                        topLeft = Offset(cx - 18f, h * 0.08f),
                        size = Size(36f, 32f)
                    )

                    // Shoulders & Torso Silhouette
                    val torsoPath = Path().apply {
                        moveTo(cx - 55f, h * 0.22f)
                        lineTo(cx + 55f, h * 0.22f)
                        lineTo(cx + 42f, h * 0.45f)
                        lineTo(cx + 46f, h * 0.58f)
                        lineTo(cx - 46f, h * 0.58f)
                        lineTo(cx - 42f, h * 0.45f)
                        close()
                    }
                    drawPath(path = torsoPath, color = formColor)

                    // 3. Clothing Render Layers:
                    // A. Dress or Top + Bottom
                    if (dressItem != null) {
                        val dressColor = try { Color(android.graphics.Color.parseColor(dressItem.colorHex)) } catch (e: Exception) { ChampagneGold }
                        val dressPath = Path().apply {
                            moveTo(cx - 48f, h * 0.23f)
                            lineTo(cx + 48f, h * 0.23f)
                            lineTo(cx + 40f, h * 0.44f)
                            lineTo(cx + 65f, h * 0.74f)
                            lineTo(cx - 65f, h * 0.74f)
                            lineTo(cx - 40f, h * 0.44f)
                            close()
                        }
                        drawPath(path = dressPath, color = dressColor)
                        drawPath(path = dressPath, color = ChampagneGold.copy(alpha = 0.5f), style = Stroke(width = 2f))
                    } else {
                        // Top Item
                        if (topItem != null) {
                            val topColor = try { Color(android.graphics.Color.parseColor(topItem.colorHex)) } catch (e: Exception) { Color(0xFFE2E8F0) }
                            val topPath = Path().apply {
                                moveTo(cx - 52f, h * 0.22f)
                                lineTo(cx + 52f, h * 0.22f)
                                lineTo(cx + 40f, h * 0.46f)
                                lineTo(cx - 40f, h * 0.46f)
                                close()
                            }
                            drawPath(path = topPath, color = topColor)
                            drawPath(path = topPath, color = ChampagneGold.copy(alpha = 0.4f), style = Stroke(width = 2f))
                        }

                        // Bottom Item (Trousers / Skirt)
                        if (bottomItem != null) {
                            val bottomColor = try { Color(android.graphics.Color.parseColor(bottomItem.colorHex)) } catch (e: Exception) { Color(0xFF1E293B) }
                            val pantsLeft = Path().apply {
                                moveTo(cx - 40f, h * 0.46f)
                                lineTo(cx - 6f, h * 0.46f)
                                lineTo(cx - 10f, h * 0.78f)
                                lineTo(cx - 36f, h * 0.78f)
                                close()
                            }
                            val pantsRight = Path().apply {
                                moveTo(cx + 6f, h * 0.46f)
                                lineTo(cx + 40f, h * 0.46f)
                                lineTo(cx + 36f, h * 0.78f)
                                lineTo(cx + 10f, h * 0.78f)
                                close()
                            }
                            drawPath(path = pantsLeft, color = bottomColor)
                            drawPath(path = pantsRight, color = bottomColor)
                            drawPath(path = pantsLeft, color = ChampagneGold.copy(alpha = 0.4f), style = Stroke(width = 1.5f))
                            drawPath(path = pantsRight, color = ChampagneGold.copy(alpha = 0.4f), style = Stroke(width = 1.5f))
                        }
                    }

                    // Outerwear Layer (Coats / Jackets)
                    if (outerItem != null) {
                        val coatColor = try { Color(android.graphics.Color.parseColor(outerItem.colorHex)) } catch (e: Exception) { Color(0xFFB45309) }
                        // Left lapel & arm
                        val leftLapel = Path().apply {
                            moveTo(cx - 56f, h * 0.20f)
                            lineTo(cx - 20f, h * 0.22f)
                            lineTo(cx - 22f, h * 0.65f)
                            lineTo(cx - 64f, h * 0.65f)
                            close()
                        }
                        // Right lapel & arm
                        val rightLapel = Path().apply {
                            moveTo(cx + 20f, h * 0.22f)
                            lineTo(cx + 56f, h * 0.20f)
                            lineTo(cx + 64f, h * 0.65f)
                            lineTo(cx + 22f, h * 0.65f)
                            close()
                        }
                        drawPath(path = leftLapel, color = coatColor)
                        drawPath(path = rightLapel, color = coatColor)
                        drawPath(path = leftLapel, color = ChampagneGold.copy(alpha = 0.6f), style = Stroke(width = 2f))
                        drawPath(path = rightLapel, color = ChampagneGold.copy(alpha = 0.6f), style = Stroke(width = 2f))
                    }

                    // Shoes Layer
                    if (shoeItem != null) {
                        val shoeColor = try { Color(android.graphics.Color.parseColor(shoeItem.colorHex)) } catch (e: Exception) { Color(0xFF0F172A) }
                        drawRoundRect(
                            color = shoeColor,
                            topLeft = Offset(cx - 38f, h * 0.79f),
                            size = Size(26f, 16f),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                        drawRoundRect(
                            color = shoeColor,
                            topLeft = Offset(cx + 12f, h * 0.79f),
                            size = Size(26f, 16f),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                    }

                    // Accessories (Jewelry / Scarf / Bag)
                    if (accessoryItem != null) {
                        // Gold Chain / Scarf at Neck
                        drawArc(
                            color = ChampagneGold,
                            startAngle = 0f,
                            sweepAngle = 180f,
                            useCenter = false,
                            topLeft = Offset(cx - 20f, h * 0.20f),
                            size = Size(40f, 28f),
                            style = Stroke(width = 4f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Color Contrast & Harmony Swatch Bar
            if (selectedItems.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkNavyElevated, RoundedCornerShape(12.dp))
                        .border(1.dp, CardBorderGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Color Palette Contrast:",
                        color = MutedText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        selectedItems.forEach { item ->
                            val color = try { Color(android.graphics.Color.parseColor(item.colorHex)) } catch (e: Exception) { Color.Gray }
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(1.dp, ChampagneGold.copy(alpha = 0.6f), CircleShape)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Selected items chip row with remove action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    selectedItems.take(4).forEach { item ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkNavyElevated)
                                .border(1.dp, CardBorderGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .clickable { onRemoveItem(item) }
                                .padding(horizontal = 6.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.name.take(12),
                                color = PlatinumText,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Save Outfit Button
                Button(
                    onClick = onSaveOutfit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("save_curated_outfit_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Save Outfit",
                        tint = ObsidianBg,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save Curated Ensemble",
                        color = ObsidianBg,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            } else {
                Text(
                    text = "Select items from the 3D Closet or Wardrobe list to dress the mannequin.",
                    color = MutedText,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}
