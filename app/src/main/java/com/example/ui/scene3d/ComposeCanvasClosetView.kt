package com.example.ui.scene3d

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClothingItem
import com.example.ui.theme.CardBorderGold
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.PlatinumText
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun ComposeCanvasClosetView(
    items: List<ClothingItem>,
    selectedCategory: String,
    onItemSelected: (ClothingItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var panX by remember { mutableFloatStateOf(0f) }
    var tiltY by remember { mutableFloatStateOf(0f) }
    val textMeasurer = rememberTextMeasurer()

    val infiniteTransition = rememberInfiniteTransition(label = "closet_sway")
    val swayPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sway_phase"
    )

    val filteredItems = remember(items, selectedCategory) {
        if (selectedCategory == "ALL") items else items.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    panX += dragAmount.x * 0.8f
                    tiltY = (tiltY + dragAmount.y * 0.3f).coerceIn(-40f, 40f)
                }
            }
            .testTag("compose_canvas_closet")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(filteredItems, panX) {
                    detectTapGestures { tapOffset ->
                        // Detect tap on items in the hanging rack or shelf
                        val w = size.width
                        val h = size.height
                        val startX = w * 0.1f + panX
                        val cardWidth = 140f
                        val spacing = 165f

                        filteredItems.forEachIndexed { index, item ->
                            val itemX = startX + index * spacing
                            val itemY = h * 0.32f
                            if (tapOffset.x in itemX..(itemX + cardWidth) &&
                                tapOffset.y in (itemY - 40f)..(itemY + 280f)
                            ) {
                                onItemSelected(item)
                            }
                        }
                    }
                }
        ) {
            val w = size.width
            val h = size.height

            // 1. Perspective Background & Luxury Oak Panels
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF1E2838), Color(0xFF090C10)),
                    center = Offset(w * 0.5f, h * 0.4f),
                    radius = w * 0.85f
                ),
                size = size
            )

            // 2. 3D Closet Floor with Perspective Lines
            val floorTop = h * 0.72f
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0B0E14), Color(0xFF151C28))
                ),
                topLeft = Offset(0f, floorTop),
                size = Size(w, h - floorTop)
            )

            for (i in 0..10) {
                val xStart = (w / 10f) * i
                val xEnd = (xStart - w * 0.5f) * 1.5f + w * 0.5f
                drawLine(
                    color = ChampagneGold.copy(alpha = 0.15f),
                    start = Offset(xStart, floorTop),
                    end = Offset(xEnd, h),
                    strokeWidth = 1.5f
                )
            }

            // 3. Gold Brass Hanging Rod
            val rodY = h * 0.28f + tiltY * 0.2f
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFB48624), ChampagneGold, Color(0xFFF7E7A9), ChampagneGold, Color(0xFF8C6415))
                ),
                start = Offset(20f, rodY),
                end = Offset(w - 20f, rodY),
                strokeWidth = 12f
            )

            // Rod Wall Mounts
            drawCircle(color = ChampagneGold, radius = 18f, center = Offset(30f, rodY))
            drawCircle(color = ChampagneGold, radius = 18f, center = Offset(w - 30f, rodY))

            // 4. Render Hanging / Displayed Clothes
            val cardWidth = 135f
            val cardHeight = 220f
            val spacing = 160f
            val startX = w * 0.15f + panX

            filteredItems.forEachIndexed { index, item ->
                val posX = startX + index * spacing
                val posY = rodY + 24f

                // Individual sway pendulum effect
                val swayAngle = sin(swayPhase + index * 0.9f) * 3.5f

                // Hanger Gold Hook
                drawLine(
                    color = ChampagneGold,
                    start = Offset(posX + cardWidth / 2, rodY),
                    end = Offset(posX + cardWidth / 2, posY),
                    strokeWidth = 4f
                )

                // Hanger Shoulder Triangle
                drawLine(
                    color = ChampagneGold,
                    start = Offset(posX + 15f, posY + 20f),
                    end = Offset(posX + cardWidth - 15f, posY + 20f),
                    strokeWidth = 4f
                )
                drawLine(
                    color = ChampagneGold,
                    start = Offset(posX + 15f, posY + 20f),
                    end = Offset(posX + cardWidth / 2, posY),
                    strokeWidth = 3f
                )
                drawLine(
                    color = ChampagneGold,
                    start = Offset(posX + cardWidth - 15f, posY + 20f),
                    end = Offset(posX + cardWidth / 2, posY),
                    strokeWidth = 3f
                )

                rotate(
                    degrees = swayAngle,
                    pivot = Offset(posX + cardWidth / 2, rodY)
                ) {
                    // Clothing Card Base
                    val parsedColor = try {
                        Color(android.graphics.Color.parseColor(item.colorHex))
                    } catch (e: Exception) {
                        Color(0xFF334155)
                    }

                    // Shadow
                    drawRoundRect(
                        color = Color.Black.copy(alpha = 0.45f),
                        topLeft = Offset(posX + 4f, posY + 22f + 4f),
                        size = Size(cardWidth, cardHeight),
                        cornerRadius = CornerRadius(14f, 14f)
                    )

                    // Card Background
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(parsedColor, parsedColor.copy(alpha = 0.75f), Color(0xFF0F172A))
                        ),
                        topLeft = Offset(posX, posY + 22f),
                        size = Size(cardWidth, cardHeight),
                        cornerRadius = CornerRadius(14f, 14f)
                    )

                    // Gold Border
                    drawRoundRect(
                        color = ChampagneGold.copy(alpha = 0.6f),
                        topLeft = Offset(posX, posY + 22f),
                        size = Size(cardWidth, cardHeight),
                        cornerRadius = CornerRadius(14f, 14f),
                        style = Stroke(width = 2.5f)
                    )

                    // Pattern indicators
                    if (item.pattern == "striped") {
                        for (sy in 0..5) {
                            drawRect(
                                color = Color.White.copy(alpha = 0.2f),
                                topLeft = Offset(posX + 8f, posY + 40f + sy * 20f),
                                size = Size(cardWidth - 16f, 8f)
                            )
                        }
                    }

                    // Brand Label
                    if (item.brand.isNotEmpty()) {
                        drawText(
                            textMeasurer = textMeasurer,
                            text = item.brand.uppercase(),
                            topLeft = Offset(posX + 12f, posY + cardHeight - 55f),
                            style = TextStyle(
                                color = ChampagneGold,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    // Item Name
                    val shortName = if (item.name.length > 14) item.name.substring(0, 12) + ".." else item.name
                    drawText(
                        textMeasurer = textMeasurer,
                        text = shortName,
                        topLeft = Offset(posX + 12f, posY + cardHeight - 38f),
                        style = TextStyle(
                            color = PlatinumText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    // Category Pill
                    drawText(
                        textMeasurer = textMeasurer,
                        text = item.category,
                        topLeft = Offset(posX + 12f, posY + cardHeight - 20f),
                        style = TextStyle(
                            color = Color(0xFF94A3B8),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            // 5. Lower Velvet Shelf
            val shelfY = floorTop - 45f
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF231826), Color(0xFF130D15))
                ),
                topLeft = Offset(20f, shelfY),
                size = Size(w - 40f, 32f),
                cornerRadius = CornerRadius(8f, 8f)
            )
            drawRoundRect(
                color = ChampagneGold.copy(alpha = 0.5f),
                topLeft = Offset(20f, shelfY),
                size = Size(w - 40f, 32f),
                cornerRadius = CornerRadius(8f, 8f),
                style = Stroke(width = 1.5f)
            )
        }

        // Overlay hint
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
                .background(DarkNavyElevated.copy(alpha = 0.85f), RoundedCornerShape(16.dp))
                .border(1.dp, CardBorderGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = "✨ Drag to slide wardrobe • Tap piece to inspect",
                color = ChampagneGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
