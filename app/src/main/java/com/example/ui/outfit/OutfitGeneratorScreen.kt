package com.example.ui.outfit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClothingItem
import com.example.data.model.GeneratedOutfit
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CardBorderGold
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneGoldLight
import com.example.ui.theme.ChampagneGoldDark
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.MutedText
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.PlatinumText
import com.example.ui.theme.RoseGold
import com.example.ui.theme.SuccessEmerald

import com.example.ui.theme.CardBorderWhite10
import com.example.ui.theme.CardBorderWhite5
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.ObsidianSurface

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OutfitGeneratorScreen(
    wardrobeItems: List<ClothingItem>,
    generatedOutfits: List<GeneratedOutfit>,
    isGenerating: Boolean,
    onGenerate: (occasion: String, weather: String, mood: String) -> Unit,
    onSaveOutfit: (GeneratedOutfit) -> Unit,
    onWearOutfit: (GeneratedOutfit) -> Unit,
    onInspectItem: (ClothingItem) -> Unit,
    onShareOutfit: (GeneratedOutfit) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedOccasion by remember { mutableStateOf("work") }
    var selectedWeather by remember { mutableStateOf("Sunny 75°F (Warm)") }
    var selectedMood by remember { mutableStateOf("Chic Minimalist") }
    var previewOutfitForMannequin by remember { mutableStateOf<GeneratedOutfit?>(null) }

    val occasions = listOf("work", "casual", "date", "party", "gym", "formal", "weekend", "travel")
    val weathers = listOf(
        "Sunny 75°F (Warm)",
        "Breezy 62°F (Mild)",
        "Crisp 45°F (Layered)",
        "Rainy 52°F (Storm Coat)",
        "Summer Heat 88°F"
    )
    val moods = listOf(
        "Chic Minimalist",
        "Bold & Statement",
        "Classic Aristocrat",
        "Romantic Fluid",
        "Urban Edgy",
        "Boho Visionary",
        "Preppy Heritage"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header & AI Generator Control Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorderGold, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(ChampagneGold.copy(alpha = 0.3f), ChampagneGold.copy(alpha = 0.1f))
                                    )
                                )
                                .border(1.dp, ChampagneGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Gemini AI",
                                tint = ChampagneGold,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Gemini Couture Outfit Engine",
                                color = PlatinumText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.2).sp
                            )
                            Text(
                                text = "Color theory, climate layering & silhouette balance",
                                color = MutedText,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 1. Occasion Selector
                    Text(
                        text = "1. TARGET OCCASION",
                        color = ChampagneGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        occasions.forEach { occ ->
                            val isSelected = selectedOccasion == occ
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(if (isSelected) ChampagneGold else DarkNavyElevated)
                                    .border(1.dp, if (isSelected) ChampagneGoldLight else CardBorderWhite10, RoundedCornerShape(50))
                                    .clickable { selectedOccasion = occ }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = occ.replaceFirstChar { it.uppercase() },
                                    color = if (isSelected) Color.Black else PlatinumText,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Weather Condition
                    Text(
                        text = "2. WEATHER & LAYERING",
                        color = ChampagneGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(weathers) { w ->
                            val isSelected = selectedWeather == w
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(if (isSelected) RoseGold else DarkNavyElevated)
                                    .border(1.dp, if (isSelected) RoseGold else CardBorderWhite10, RoundedCornerShape(50))
                                    .clickable { selectedWeather = w }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = w,
                                    color = if (isSelected) Color.Black else PlatinumText,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. Aesthetic Mood
                    Text(
                        text = "3. AESTHETIC VIBE / MOOD",
                        color = ChampagneGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(moods) { m ->
                            val isSelected = selectedMood == m
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(if (isSelected) ChampagneGold else DarkNavyElevated)
                                    .border(1.dp, if (isSelected) ChampagneGoldLight else CardBorderWhite10, RoundedCornerShape(50))
                                    .clickable { selectedMood = m }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = m,
                                    color = if (isSelected) Color.Black else PlatinumText,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Generate / Remix Action Button with Gold Gradient
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isGenerating) {
                                    Brush.horizontalGradient(listOf(ChampagneGold.copy(alpha = 0.5f), ChampagneGoldDark.copy(alpha = 0.5f)))
                                } else {
                                    Brush.horizontalGradient(listOf(ChampagneGold, ChampagneGoldDark))
                                }
                            )
                            .clickable(enabled = !isGenerating) {
                                onGenerate(selectedOccasion, selectedWeather, selectedMood)
                            }
                            .testTag("generate_outfit_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isGenerating) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = ObsidianBg,
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Styling with Gemini AI...",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Generate",
                                    tint = Color.Black,
                                    modifier = Modifier.size(19.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (generatedOutfits.isEmpty()) "Generate Curated Ensembles" else "Remix & Regenerate Looks",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Optional Live Mannequin Preview if an outfit is selected
        if (previewOutfitForMannequin != null) {
            item {
                previewOutfitForMannequin?.let { outfit ->
                    MannequinVisualizer(
                        selectedItems = outfit.items,
                        onRemoveItem = {},
                        onSaveOutfit = { onSaveOutfit(outfit) }
                    )
                }
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GENERATED COMBINATIONS",
                    color = ChampagneGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${generatedOutfits.size} Suggested Looks",
                    color = MutedText,
                    fontSize = 12.sp
                )
            }
        }

        // Empty State or List of Outfits
        if (generatedOutfits.isEmpty() && !isGenerating) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .border(1.dp, CardBorderGold.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkNavyCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No Outfits Generated Yet",
                            color = PlatinumText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap 'Generate Curated Ensembles' above to have Gemini AI analyze your wardrobe and create personalized looks.",
                            color = MutedText,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        // Render Generated Outfits
        items(generatedOutfits) { outfit ->
            OutfitSuggestionCard(
                outfit = outfit,
                onSave = { onSaveOutfit(outfit) },
                onWear = { onWearOutfit(outfit) },
                onInspectItem = onInspectItem,
                onPreviewOnMannequin = { previewOutfitForMannequin = outfit },
                onShare = { onShareOutfit(outfit) }
            )
        }
    }
}

@Composable
fun OutfitSuggestionCard(
    outfit: GeneratedOutfit,
    onSave: () -> Unit,
    onWear: () -> Unit,
    onInspectItem: (ClothingItem) -> Unit,
    onPreviewOnMannequin: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorderWhite10, RoundedCornerShape(18.dp))
            .testTag("outfit_suggestion_card_${outfit.id}"),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title & Score Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = outfit.title,
                        color = PlatinumText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.2).sp
                    )
                    Text(
                        text = "${outfit.occasion.uppercase()} • ${outfit.aestheticMood}",
                        color = ChampagneGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                }

                // AI Harmony Score Badge (Signature Style Score)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(ChampagneGold.copy(alpha = 0.15f))
                        .border(1.dp, CardBorderGold, RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = ChampagneGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${outfit.rating} / 10",
                            color = ChampagneGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Visual Items Row in Outfit
            Text(
                text = "ENSEMBLE PIECES",
                color = ChampagneGold,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(outfit.items) { item ->
                    val color = try { Color(android.graphics.Color.parseColor(item.colorHex)) } catch (e: Exception) { Color(0xFF334155) }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkNavyElevated)
                            .border(1.dp, CardBorderWhite10, RoundedCornerShape(12.dp))
                            .clickable { onInspectItem(item) }
                            .padding(8.dp)
                            .width(115.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(color),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.category,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.name,
                                color = PlatinumText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                            Text(
                                text = item.primaryColorName,
                                color = MutedText,
                                fontSize = 9.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // AI Color Theory & Style Notes
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkNavyElevated)
                    .border(1.dp, CardBorderWhite5, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Color Theory",
                            tint = ChampagneGold,
                            modifier = Modifier.size(16.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = outfit.colorTheoryReason,
                            color = PlatinumText,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }

                    if (outfit.stylingTips.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Styling Tips",
                                tint = RoseGold,
                                modifier = Modifier.size(16.dp).padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Styling Tips: " + outfit.stylingTips,
                                color = MutedText,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    if (outfit.missingItemsSuggestions.isNotEmpty()) {
                        Text(
                            text = "💡 Missing Accents: " + outfit.missingItemsSuggestions,
                            color = ChampagneGoldLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Wear Today Button
                Button(
                    onClick = onWear,
                    modifier = Modifier.weight(1f).height(40.dp).testTag("wear_outfit_button_${outfit.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessEmerald.copy(alpha = 0.2f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SuccessEmerald.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Wear", tint = SuccessEmerald, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Wear Today", color = SuccessEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Save to Favorites with Gold Gradient
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.horizontalGradient(listOf(ChampagneGold, ChampagneGoldDark)))
                        .clickable { onSave() }
                        .testTag("save_outfit_button_${outfit.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Bookmark, contentDescription = "Save", tint = Color.Black, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Save Look", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Mannequin Try-On Preview
                IconButton(
                    onClick = onPreviewOnMannequin,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkNavyElevated)
                        .border(1.dp, CardBorderWhite10, RoundedCornerShape(12.dp))
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Mannequin", tint = ChampagneGold, modifier = Modifier.size(18.dp))
                }

                // Share Button
                IconButton(
                    onClick = onShare,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkNavyElevated)
                        .border(1.dp, CardBorderWhite10, RoundedCornerShape(12.dp))
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = PlatinumText, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
