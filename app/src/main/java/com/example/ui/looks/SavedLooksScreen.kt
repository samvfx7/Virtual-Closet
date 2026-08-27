package com.example.ui.looks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClothingItem
import com.example.data.model.SavedOutfit
import com.example.ui.components.CraftCorners
import com.example.ui.components.LuxuryElevatedCard
import com.example.ui.components.LuxuryPrimaryButton
import com.example.ui.components.LuxuryScreenHeader
import com.example.ui.components.bounceClick
import com.example.ui.theme.BorderNeonSubtle
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CoralRed
import com.example.ui.theme.DangerBg
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SavedLooksScreen(
    savedOutfits: List<SavedOutfit>,
    allItems: List<ClothingItem>,
    onDeleteOutfit: (SavedOutfit) -> Unit,
    onNavigateToStylist: () -> Unit,
    modifier: Modifier = Modifier
) {
    val itemsById = remember(allItems) { allItems.associateBy { it.id } }
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    var selectedOutfitForDetail by remember { mutableStateOf<SavedOutfit?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PureBlack)
            .testTag("saved_looks_screen")
    ) {
        if (savedOutfits.isEmpty()) {
            // Intentional Refined Empty State
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(NeonBlueSoftGlow, Color.Transparent),
                                center = Offset(size.width / 2, size.height * 0.45f),
                                radius = 180.dp.toPx()
                            ),
                            radius = 180.dp.toPx(),
                            center = Offset(size.width / 2, size.height * 0.45f)
                        )
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CraftCorners.Card)
                        .background(SurfaceLevel2)
                        .border(1.dp, BorderSubtle, CraftCorners.Card),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = NeonBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "NO SAVED LOOKS",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.6.sp,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Generate and bookmark your favorite combinations using the AI Stylist.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 24.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                LuxuryPrimaryButton(
                    text = "OPEN AI STYLIST",
                    icon = Icons.Default.AutoAwesome,
                    onClick = onNavigateToStylist,
                    modifier = Modifier.width(220.dp),
                    testTag = "empty_state_generate_button"
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    LuxuryScreenHeader(
                        lightWord = "SAVED",
                        boldWord = "LOOKS",
                        counter = "${savedOutfits.size}",
                        subtitle = "Your bookmarked wardrobe combinations and wear history."
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                itemsIndexed(savedOutfits) { index, outfit ->
                    val outfitItemIds = remember(outfit.itemIdsJson) {
                        outfit.itemIdsJson.split(",").mapNotNull { it.trim().toLongOrNull() }
                    }
                    val includedItems = outfitItemIds.mapNotNull { itemsById[it] }

                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(250, delayMillis = index * 40)) +
                                slideInVertically(
                                    animationSpec = tween(250, delayMillis = index * 40, easing = FastOutSlowInEasing),
                                    initialOffsetY = { 30 }
                                )
                    ) {
                        LuxuryElevatedCard(
                            onClick = { selectedOutfitForDetail = outfit },
                            testTag = "saved_outfit_card_${outfit.name}"
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = outfit.name.uppercase(),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.6.sp,
                                        color = TextPrimary,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CraftCorners.Chip)
                                            .background(SurfaceLevel3)
                                            .border(1.dp, BorderSubtle, CraftCorners.Chip)
                                            .bounceClick(scaleDown = 0.9f) {
                                                onDeleteOutfit(outfit)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = TextMuted,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Items preview with soft neon indicators
                                includedItems.forEach { item ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(5.dp)
                                                .clip(CircleShape)
                                                .background(NeonBlue)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = item.name,
                                            fontSize = 12.sp,
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

                                Spacer(modifier = Modifier.height(14.dp))

                                // Footer: Date & Wear Count Badge
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = dateFormat.format(Date(outfit.createdAt)).uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 1.sp,
                                        color = TextTertiary
                                    )

                                    Box(
                                        modifier = Modifier
                                            .clip(CraftCorners.Chip)
                                            .background(SurfaceLevel3)
                                            .border(1.dp, BorderSubtle, CraftCorners.Chip)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "${outfit.timesWorn} WEARS",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.8.sp,
                                            color = NeonBlue
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(110.dp))
                }
            }
        }

        // Outfit detail bottom sheet / modal with 24dp corners
        AnimatedVisibility(
            visible = selectedOutfitForDetail != null,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            selectedOutfitForDetail?.let { detail ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(SurfaceLevel2)
                        .border(
                            1.dp,
                            BorderNeonSubtle,
                            RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        // Modal drag indicator line
                        Box(
                            modifier = Modifier
                                .size(width = 36.dp, height = 3.dp)
                                .clip(CraftCorners.Pill)
                                .background(BorderSubtle)
                                .align(Alignment.CenterHorizontally)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = detail.name.uppercase(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.8.sp,
                                color = TextPrimary
                            )

                            IconButton(
                                onClick = { selectedOutfitForDetail = null },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        if (detail.colorTheoryReason.isNotBlank()) {
                            Spacer(modifier = Modifier.height(14.dp))
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
                                        text = "STYLING NOTES",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.4.sp,
                                        color = TextMuted
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = detail.colorTheoryReason,
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(CraftCorners.Button)
                                    .background(DangerBg)
                                    .border(1.dp, CoralRed.copy(alpha = 0.3f), CraftCorners.Button)
                                    .bounceClick(scaleDown = 0.94f) {
                                        onDeleteOutfit(detail)
                                        selectedOutfitForDetail = null
                                    }
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = CoralRed,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "DELETE LOOK",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp,
                                        color = CoralRed
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
