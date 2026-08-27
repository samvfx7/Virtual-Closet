package com.example.ui.studio

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardTravel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.StealLookResult
import com.example.data.model.ClothingItem
import com.example.data.model.FashionPersonalityResult
import com.example.data.model.PackingPlan
import com.example.data.model.StyleBadge
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CardBorderGold
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneGoldLight
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.MutedText
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.PlatinumText
import com.example.ui.theme.RoseGold
import com.example.ui.theme.SuccessEmerald

import com.example.ui.theme.CardBorderWhite10
import com.example.ui.theme.CardBorderWhite5
import com.example.ui.theme.ObsidianSurface

@Composable
fun StylistStudioScreen(
    wardrobeItems: List<ClothingItem>,
    stealLookResult: StealLookResult?,
    isProcessingStealLook: Boolean,
    onStealLook: (Bitmap) -> Unit,
    packingPlan: PackingPlan?,
    isGeneratingPacking: Boolean,
    onGeneratePacking: (destination: String, days: Int, season: String, tripType: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Steal Look", "Travel Capsule", "Style Persona", "Badges & Streaks")

    // Image Picker for Inspiration Photo
    val stealLookLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                }
                onStealLook(bitmap)
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
    ) {
        // Tab Row
        TabRow(
            selectedTabIndex = currentTab,
            containerColor = ObsidianSurface,
            contentColor = ChampagneGold,
            modifier = Modifier.fillMaxWidth().border(1.dp, CardBorderWhite10)
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = currentTab == index,
                    onClick = { currentTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (currentTab == index) ChampagneGold else PlatinumText
                        )
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (currentTab) {
                0 -> {
                    // TAB 0: STEAL MY LOOK
                    item {
                        StealLookSection(
                            stealLookResult = stealLookResult,
                            isProcessing = isProcessingStealLook,
                            onPickPhoto = { stealLookLauncher.launch("image/*") }
                        )
                    }
                }
                1 -> {
                    // TAB 1: TRAVEL PACKING ASSISTANT
                    item {
                        TravelPackingSection(
                            packingPlan = packingPlan,
                            isGenerating = isGeneratingPacking,
                            onGenerate = onGeneratePacking
                        )
                    }
                }
                2 -> {
                    // TAB 2: STYLE PERSONA & ARCHETYPE QUIZ
                    item {
                        StylePersonaSection()
                    }
                }
                3 -> {
                    // TAB 3: GAMIFICATION, STREAKS & BADGES
                    item {
                        BadgesAndStreaksSection(wardrobeCount = wardrobeItems.size)
                    }
                }
            }
        }
    }
}

@Composable
fun StealLookSection(
    stealLookResult: StealLookResult?,
    isProcessing: Boolean,
    onPickPhoto: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorderWhite10, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI", tint = ChampagneGold, modifier = Modifier.size(22.dp))
                Text(text = "Steal My Look: AI Matcher", color = PlatinumText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Upload a Pinterest, Instagram, or runway inspiration photo. Gemini AI analyzes the aesthetic and assembles the closest match from your actual wardrobe.",
                color = MutedText,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isProcessing) Brush.horizontalGradient(listOf(ChampagneGold.copy(alpha = 0.5f), ChampagneGoldLight.copy(alpha = 0.5f)))
                        else Brush.horizontalGradient(listOf(ChampagneGold, ChampagneGoldLight))
                    )
                    .clickable(enabled = !isProcessing) { onPickPhoto() }
                    .testTag("upload_inspiration_photo_button"),
                contentAlignment = Alignment.Center
            ) {
                if (isProcessing) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Matching Wardrobe Pieces...", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = "Upload", tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upload Inspiration Photo", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            stealLookResult?.let { result ->
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkNavyElevated)
                        .border(1.dp, CardBorderWhite10, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Style: ${result.detectedStyle}", color = ChampagneGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${result.similarityScore}% Match", color = SuccessEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Text(text = "Stylist Advice: ${result.stylingAdvice}", color = PlatinumText, fontSize = 11.sp)

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "MATCHING CLOSET PIECES:", color = ChampagneGold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(result.matchedItems) { item ->
                                val color = try { Color(android.graphics.Color.parseColor(item.colorHex)) } catch (e: Exception) { Color(0xFF334155) }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(DarkNavyCard)
                                        .border(1.dp, CardBorderWhite10, RoundedCornerShape(10.dp))
                                        .padding(8.dp)
                                        .width(110.dp)
                                ) {
                                    Column {
                                        Box(
                                            modifier = Modifier.fillMaxWidth().height(40.dp).clip(RoundedCornerShape(6.dp)).background(color)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = item.name, color = PlatinumText, fontSize = 10.sp, maxLines = 1, fontWeight = FontWeight.Bold)
                                        Text(text = item.category, color = ChampagneGold, fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TravelPackingSection(
    packingPlan: PackingPlan?,
    isGenerating: Boolean,
    onGenerate: (destination: String, days: Int, season: String, tripType: String) -> Unit
) {
    var destination by remember { mutableStateOf("Paris, France") }
    var tripDays by remember { mutableIntStateOf(5) }
    var tripSeason by remember { mutableStateOf("Spring") }
    var tripVibe by remember { mutableStateOf("Boutique / Leisure") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorderWhite10, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Default.FlightTakeoff, contentDescription = "Travel", tint = ChampagneGold, modifier = Modifier.size(22.dp))
                Text(text = "Travel Capsule Packing Engine", color = PlatinumText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Generate a versatile, wrinkle-resistant capsule wardrobe plan tailored for any destination, duration, and climate.",
                color = MutedText,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                label = { Text("Destination", color = MutedText) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ChampagneGold,
                    unfocusedBorderColor = CardBorderWhite10,
                    focusedTextColor = PlatinumText,
                    unfocusedTextColor = PlatinumText,
                    focusedContainerColor = DarkNavyElevated,
                    unfocusedContainerColor = DarkNavyElevated
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = tripSeason,
                    onValueChange = { tripSeason = it },
                    label = { Text("Season", color = MutedText) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = CardBorderWhite10,
                        focusedTextColor = PlatinumText,
                        unfocusedTextColor = PlatinumText,
                        focusedContainerColor = DarkNavyElevated,
                        unfocusedContainerColor = DarkNavyElevated
                    ),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = "$tripDays Days",
                    onValueChange = {},
                    label = { Text("Duration", color = MutedText) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = CardBorderWhite10,
                        focusedTextColor = PlatinumText,
                        unfocusedTextColor = PlatinumText,
                        focusedContainerColor = DarkNavyElevated,
                        unfocusedContainerColor = DarkNavyElevated
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isGenerating) Brush.horizontalGradient(listOf(ChampagneGold.copy(alpha = 0.5f), ChampagneGoldLight.copy(alpha = 0.5f)))
                        else Brush.horizontalGradient(listOf(ChampagneGold, ChampagneGoldLight))
                    )
                    .clickable(enabled = !isGenerating) { onGenerate(destination, tripDays, tripSeason, tripVibe) }
                    .testTag("generate_packing_plan_button"),
                contentAlignment = Alignment.Center
            ) {
                if (isGenerating) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Creating Packing Itinerary...", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text("Generate $tripDays-Day Travel Capsule", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            packingPlan?.let { plan ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "DAILY PACKING ITINERARY (${plan.destination})", color = ChampagneGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                plan.dailyOutfits.forEach { day ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkNavyElevated)
                            .border(1.dp, CardBorderWhite5, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Day ${day.dayNumber}: ${day.activity}", color = ChampagneGoldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Pieces: " + day.items.joinToString(" • "), color = PlatinumText, fontSize = 11.sp)
                            Text(text = day.notes, color = MutedText, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StylePersonaSection() {
    var quizStep by remember { mutableIntStateOf(0) }
    var selectedVibe by remember { mutableStateOf("Timeless Luxury & Neutral Tailoring") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorderWhite10, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Default.Psychology, contentDescription = "Persona", tint = ChampagneGold, modifier = Modifier.size(22.dp))
                Text(text = "Haute Couture Archetype Quiz", color = PlatinumText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Discover your distinctive personal fashion DNA and unlock bespoke styling rules.",
                color = MutedText,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Persona Result Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(DarkNavyElevated, ObsidianBg)
                        )
                    )
                    .border(1.dp, CardBorderWhite10, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "YOUR ARCHETYPE:",
                        color = ChampagneGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "✨ The Minimalist Aristocrat",
                        color = ChampagneGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "You gravitate towards immaculate tailoring, natural textures (silk, wool, cashmere), and a restrained tonal palette anchored in obsidian, cream, camel, and subtle gold accents.",
                        color = PlatinumText,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "STYLE MOTTO: \"Elegance is refusal. Let silhouette and texture speak.\"",
                        color = RoseGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "SIGNATURE PALETTE: Champagne Gold • Charcoal • Ivory Silk • Deep Bordeaux",
                        color = ChampagneGoldLight,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BadgesAndStreaksSection(wardrobeCount: Int) {
    val badges = listOf(
        StyleBadge("b1", "Capsule Master", "Curate 15+ versatile staples in 3D Atelier", "star", wardrobeCount >= 15, 1.0f),
        StyleBadge("b2", "Zero Waste Stylist", "Achieve 85%+ wardrobe utilization rate", "eco", true, 0.92f),
        StyleBadge("b3", "Haute Visionary", "Generate 10+ AI styled outfit combinations", "auto", true, 1.0f),
        StyleBadge("b4", "Globetrotter", "Create customized travel packing capsules", "flight", true, 0.75f)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorderWhite10, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = "Badges", tint = AmberGlow, modifier = Modifier.size(22.dp))
                    Text(text = "Style Badges & Daily Streak", color = PlatinumText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(AmberGlow.copy(alpha = 0.15f))
                        .border(1.dp, AmberGlow.copy(alpha = 0.5f), RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = "🔥 7-Day Streak", color = AmberGlow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            badges.forEach { badge ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkNavyElevated)
                        .border(1.dp, if (badge.isUnlocked) CardBorderGold else CardBorderWhite5, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (badge.isUnlocked) ChampagneGold.copy(alpha = 0.25f) else Color.DarkGray)
                                .border(1.dp, if (badge.isUnlocked) ChampagneGold else Color.Gray, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (badge.isUnlocked) Icons.Default.CheckCircle else Icons.Default.Star,
                                contentDescription = badge.title,
                                tint = if (badge.isUnlocked) ChampagneGold else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = badge.title, color = PlatinumText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = badge.description, color = MutedText, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
