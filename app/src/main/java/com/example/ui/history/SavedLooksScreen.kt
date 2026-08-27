package com.example.ui.history

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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClothingItem
import com.example.data.model.SavedOutfit
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CardBorderGold
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneGoldLight
import com.example.ui.theme.CoralRed
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.MutedText
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.PlatinumText
import com.example.ui.theme.RoseGold
import com.example.ui.theme.SuccessEmerald
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.example.ui.theme.CardBorderWhite10
import com.example.ui.theme.CardBorderWhite5
import com.example.ui.theme.ObsidianSurface

@Composable
fun SavedLooksScreen(
    savedOutfits: List<SavedOutfit>,
    allItems: List<ClothingItem>,
    onDeleteOutfit: (SavedOutfit) -> Unit,
    onRateWear: (Long, Boolean) -> Unit,
    onShareOutfit: (SavedOutfit) -> Unit,
    modifier: Modifier = Modifier
) {
    val itemMap = allItems.associateBy { it.id }
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SAVED LOOKS & WEAR LOG",
                        color = ChampagneGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${savedOutfits.size} Curated Ensembles in Lookbook",
                        color = MutedText,
                        fontSize = 11.sp
                    )
                }
            }
        }

        if (savedOutfits.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                        .border(1.dp, CardBorderWhite10, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.Bookmark, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "No Saved Ensembles", color = PlatinumText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Save looks from the Gemini Outfit Generator or create outfits on the Atelier Mannequin.",
                            color = MutedText,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        items(savedOutfits) { outfit ->
            val outfitItems = outfit.getItemIdList().mapNotNull { itemMap[it] }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorderWhite10, RoundedCornerShape(18.dp))
                    .testTag("saved_outfit_card_${outfit.id}"),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = outfit.name, color = PlatinumText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${outfit.occasion.uppercase()} • ${dateFormat.format(Date(outfit.createdAt))}",
                                color = ChampagneGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        IconButton(
                            onClick = { onDeleteOutfit(outfit) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = CoralRed.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Items Row
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(outfitItems) { item ->
                            val color = try { Color(android.graphics.Color.parseColor(item.colorHex)) } catch (e: Exception) { Color(0xFF334155) }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DarkNavyElevated)
                                    .border(1.dp, CardBorderWhite5, RoundedCornerShape(10.dp))
                                    .padding(8.dp)
                                    .width(100.dp)
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().height(36.dp).clip(RoundedCornerShape(6.dp)).background(color)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = item.name, color = PlatinumText, fontSize = 10.sp, maxLines = 1, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (outfit.colorTheoryReason.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Style Note: " + outfit.colorTheoryReason, color = MutedText, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Wear History Rating & Share
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = "Did you love this look?", color = MutedText, fontSize = 11.sp)
                            IconButton(
                                onClick = { onRateWear(outfit.id, true) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ThumbUp,
                                    contentDescription = "Like",
                                    tint = if (outfit.userLiked == true) SuccessEmerald else MutedText,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(
                                onClick = { onRateWear(outfit.id, false) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ThumbDown,
                                    contentDescription = "Dislike",
                                    tint = if (outfit.userLiked == false) CoralRed else MutedText,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = { onShareOutfit(outfit) },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(DarkNavyElevated)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = ChampagneGold, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
