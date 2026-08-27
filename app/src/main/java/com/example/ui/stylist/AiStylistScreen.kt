package com.example.ui.stylist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GeneratedOutfit
import com.example.ui.components.CraftCorners
import com.example.ui.components.LuxuryElevatedCard
import com.example.ui.components.LuxuryPrimaryButton
import com.example.ui.components.LuxuryScreenHeader
import com.example.ui.components.LuxurySectionLabel
import com.example.ui.components.LuxurySelectorChip
import com.example.ui.components.bounceClick
import com.example.ui.theme.BorderNeonSubtle
import com.example.ui.theme.BorderSubtle
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiStylistScreen(
    isGenerating: Boolean,
    generatedOutfits: List<GeneratedOutfit>,
    onGenerate: (occasion: String, weather: String, mood: String) -> Unit,
    onSaveOutfit: (GeneratedOutfit) -> Unit,
    modifier: Modifier = Modifier
) {
    val occasions = listOf("Work", "Casual", "Date", "Party", "Gym", "Travel")
    val weathers = listOf("Sunny", "Breezy", "Crisp", "Cold")
    val moods = listOf("Minimalist", "Bold", "Classic", "Romantic")

    var selectedOccasion by remember { mutableStateOf("Work") }
    var selectedWeather by remember { mutableStateOf("Sunny") }
    var selectedMood by remember { mutableStateOf("Minimalist") }
    var savedOutfitIds by remember { mutableStateOf(setOf<String>()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(horizontal = 24.dp)
            .testTag("ai_stylist_screen"),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            // Statement Screen Title (Light + Bold mixed-weight)
            LuxuryScreenHeader(
                lightWord = "AI",
                boldWord = "STYLIST",
                subtitle = "Select contextual parameters to curate 3 high-performance looks."
            )
        }

        // Section 1: Occasion
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LuxurySectionLabel(text = "OCCASION")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    occasions.forEach { occ ->
                        val isSelected = selectedOccasion == occ
                        LuxurySelectorChip(
                            label = occ,
                            selected = isSelected,
                            onClick = { selectedOccasion = occ }
                        )
                    }
                }
            }
        }

        // Section 2: Weather
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LuxurySectionLabel(text = "WEATHER")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    weathers.forEach { w ->
                        val isSelected = selectedWeather == w
                        LuxurySelectorChip(
                            label = w,
                            selected = isSelected,
                            onClick = { selectedWeather = w }
                        )
                    }
                }
            }
        }

        // Section 3: Mood
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LuxurySectionLabel(text = "MOOD")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    moods.forEach { m ->
                        val isSelected = selectedMood == m
                        LuxurySelectorChip(
                            label = m,
                            selected = isSelected,
                            onClick = { selectedMood = m }
                        )
                    }
                }
            }
        }

        // Action: Generate Outfit Button with Soft Outer Glow & Spring Press
        item {
            Spacer(modifier = Modifier.height(4.dp))
            LuxuryPrimaryButton(
                text = "GENERATE OUTFIT",
                icon = Icons.Default.AutoAwesome,
                onClick = {
                    onGenerate(selectedOccasion, selectedWeather, selectedMood)
                },
                isLoading = isGenerating,
                loadingText = "CURATING WITH GEMINI...",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                testTag = "generate_outfit_button"
            )
        }

        // Generated Results Header
        if (generatedOutfits.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "CURATED LOOKS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.6.sp,
                            color = TextPrimary
                        )
                        Box(
                            modifier = Modifier
                                .clip(CraftCorners.Chip)
                                .background(SurfaceLevel2)
                                .border(1.dp, BorderSubtle, CraftCorners.Chip)
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${generatedOutfits.size}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonBlue
                            )
                        }
                    }

                    Text(
                        text = "$selectedOccasion • $selectedWeather".uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.2.sp,
                        color = TextMuted
                    )
                }
            }

            // Results Stack: Staggered animated outfit cards
            itemsIndexed(generatedOutfits) { index, outfit ->
                val isSaved = savedOutfitIds.contains(outfit.id)
                ElevatedOutfitCard(
                    outfit = outfit,
                    isSaved = isSaved,
                    index = index,
                    onSave = {
                        savedOutfitIds = savedOutfitIds + outfit.id
                        onSaveOutfit(outfit)
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(110.dp)) // Generous bottom breathing room for floating dock
        }
    }
}

@Composable
fun ElevatedOutfitCard(
    outfit: GeneratedOutfit,
    isSaved: Boolean,
    index: Int,
    onSave: () -> Unit
) {
    var saveFlash by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(300, delayMillis = index * 60)) +
                slideInVertically(
                    animationSpec = tween(300, delayMillis = index * 60, easing = FastOutSlowInEasing),
                    initialOffsetY = { 40 }
                )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CraftCorners.Card)
                .background(SurfaceLevel2)
                .border(
                    1.dp,
                    if (isSaved) BorderNeonSubtle else BorderSubtle,
                    CraftCorners.Card
                )
                .drawBehind {
                    if (isSaved) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(NeonBlueSoftGlow, Color.Transparent),
                                center = Offset(size.width * 0.9f, 30.dp.toPx()),
                                radius = 120.dp.toPx()
                            ),
                            radius = 120.dp.toPx(),
                            center = Offset(size.width * 0.9f, 30.dp.toPx())
                        )
                    }
                }
                .padding(20.dp)
                .testTag("outfit_card_${outfit.title}")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top Row: Title + Save Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = outfit.title.uppercase(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Minimal Action Button
                    Box(
                        modifier = Modifier
                            .clip(CraftCorners.Button)
                            .background(if (isSaved) SurfaceLevel3 else NeonBlue)
                            .border(
                                1.dp,
                                if (isSaved) BorderNeonSubtle else Color.Transparent,
                                CraftCorners.Button
                            )
                            .bounceClick(scaleDown = 0.94f) {
                                if (!isSaved) {
                                    saveFlash = true
                                    onSave()
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("save_outfit_button_${outfit.title}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Default.Check else Icons.Default.BookmarkBorder,
                                contentDescription = "Save Look",
                                modifier = Modifier.size(13.dp),
                                tint = if (isSaved) NeonBlue else PureBlack
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (isSaved) "SAVED" else "SAVE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.8.sp,
                                color = if (isSaved) NeonBlue else PureBlack
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pieces Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(NeonBlue, CraftCorners.Pill)
                    )
                    Text(
                        text = "PIECES INCLUDED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.4.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Garment List with precise swatches & soft typography
                outfit.items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val parsedColor = try {
                            Color(android.graphics.Color.parseColor(item.colorHex))
                        } catch (e: Exception) {
                            TextTertiary
                        }

                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(parsedColor)
                                .border(1.dp, BorderSubtle, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = item.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "— ${item.category}",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // "WHY IT WORKS" High-craft Callout
                val explanation = if (outfit.whyItWorks.isNotBlank()) outfit.whyItWorks else outfit.colorTheoryReason
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CraftCorners.Button)
                        .background(SurfaceLevel1)
                        .border(1.dp, BorderSubtle, CraftCorners.Button)
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "STYLING LOGIC",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.4.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = explanation,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 18.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}
