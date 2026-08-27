package com.example.ui.analytics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.WardrobeGapAnalysis
import com.example.data.model.ClothingItem
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
fun WardrobeAnalyticsScreen(
    items: List<ClothingItem>,
    gapAnalysis: WardrobeGapAnalysis?,
    isAnalyzingGaps: Boolean,
    onAnalyzeGaps: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalPieces = items.size
    val totalWardrobeValue = items.sumOf { it.estimatedValue }
    val totalWears = items.sumOf { it.wearCount }
    val avgCostPerWear = if (totalWears > 0) totalWardrobeValue / totalWears else 0.0

    // Sustainability score (0 - 100 based on wear distribution and utilization)
    val utilizedCount = items.count { it.wearCount > 3 }
    val targetSustainabilityScore = if (totalPieces > 0) ((utilizedCount.toFloat() / totalPieces) * 100).toInt().coerceIn(35, 98) else 50
    
    val animatedSustainability by animateFloatAsState(
        targetValue = targetSustainabilityScore / 100f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "sustainability_anim"
    )

    val mostWorn = items.sortedByDescending { it.wearCount }.take(3)
    val leastWorn = items.sortedBy { it.wearCount }.take(3)

    val categoryDistribution = items.groupBy { it.category }
    var highlightedCategory by remember { mutableStateOf<String?>(null) }

    val categoryColors = listOf(
        ChampagneGold,
        RoseGold,
        AmberGlow,
        SuccessEmerald,
        ChampagneGoldLight,
        Color(0xFF818CF8),
        Color(0xFF38BDF8)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // High-level Stats Header Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: Total Value
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "ESTIMATED VALUE",
                    value = "$${totalWardrobeValue.toInt()}",
                    subtitle = "$totalPieces Curated Items",
                    icon = Icons.Default.MonetizationOn,
                    tint = ChampagneGold
                )

                // Card 2: Cost Per Wear
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "AVG COST / WEAR",
                    value = String.format("$%.2f", avgCostPerWear),
                    subtitle = "$totalWears Total Wears",
                    icon = Icons.Default.TrendingUp,
                    tint = SuccessEmerald
                )
            }
        }

        // Sustainability & Utilization Meter
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorderWhite10, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.Eco, contentDescription = "Eco", tint = SuccessEmerald, modifier = Modifier.size(20.dp))
                            Text(text = "Sustainability & Utilization", color = PlatinumText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(text = "${(animatedSustainability * 100).toInt()}/100", color = SuccessEmerald, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { animatedSustainability },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = SuccessEmerald,
                        trackColor = DarkNavyElevated
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "High re-wear frequency reduces closet carbon footprint by 42%. $utilizedCount of $totalPieces pieces in active weekly rotation.",
                        color = MutedText,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Interactive Animated Capsule Donut Chart & Category Breakdown
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorderWhite10, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CAPSULE ARCHITECTURE",
                            color = ChampagneGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${categoryDistribution.keys.size} Categories",
                            color = MutedText,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Donut Chart + Central Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Animated Donut Canvas
                        val chartAnim = remember { Animatable(0f) }
                        LaunchedEffect(items) {
                            chartAnim.animateTo(
                                targetValue = 1f,
                                animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
                            )
                        }

                        Box(
                            modifier = Modifier.size(130.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(120.dp)) {
                                val strokeWidth = 20.dp.toPx()
                                val radius = (size.minDimension - strokeWidth) / 2
                                val topLeft = Offset((size.width - radius * 2) / 2, (size.height - radius * 2) / 2)
                                val arcSize = Size(radius * 2, radius * 2)

                                var startAngle = -90f
                                val total = totalPieces.coerceAtLeast(1)

                                categoryDistribution.entries.forEachIndexed { index, (cat, list) ->
                                    val sweep = (list.size.toFloat() / total) * 360f * chartAnim.value
                                    val sliceColor = categoryColors.getOrElse(index) { ChampagneGold }
                                    val isHighlighted = highlightedCategory == cat

                                    drawArc(
                                        color = if (highlightedCategory == null || isHighlighted) sliceColor else sliceColor.copy(alpha = 0.3f),
                                        startAngle = startAngle,
                                        sweepAngle = sweep.coerceAtLeast(2f),
                                        useCenter = false,
                                        topLeft = topLeft,
                                        size = arcSize,
                                        style = Stroke(
                                            width = if (isHighlighted) strokeWidth * 1.25f else strokeWidth,
                                            cap = StrokeCap.Round
                                        )
                                    )
                                    startAngle += sweep
                                }
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$totalPieces",
                                    color = PlatinumText,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "PIECES",
                                    color = ChampagneGold,
                                    fontSize = 8.sp,
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Compact Interactive Category Legend Pills
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(start = 12.dp)
                        ) {
                            categoryDistribution.entries.take(5).forEachIndexed { index, (cat, list) ->
                                val color = categoryColors.getOrElse(index) { ChampagneGold }
                                val ratio = if (totalPieces > 0) (list.size.toFloat() / totalPieces * 100).toInt() else 0
                                val isSelected = highlightedCategory == cat

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) color.copy(alpha = 0.2f) else Color.Transparent)
                                        .clickable {
                                            highlightedCategory = if (isSelected) null else cat
                                        }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "$cat: $ratio%",
                                        color = if (isSelected) color else PlatinumText,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = CardBorderWhite5, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Detailed Progress Bars per category
                    categoryDistribution.entries.toList().forEachIndexed { index, entry ->
                        val category = entry.key
                        val list = entry.value
                        val ratio = if (totalPieces > 0) list.size.toFloat() / totalPieces else 0f
                        val color = categoryColors.getOrElse(index) { ChampagneGold }

                        CategoryDistributionRow(
                            category = category,
                            itemCount = list.size,
                            ratio = ratio,
                            color = color
                        )
                    }
                }
            }
        }

        // Most & Least Worn Items
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Most Worn Column
                Card(
                    modifier = Modifier.weight(1f).border(1.dp, CardBorderWhite10, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "🔥 Most Worn Staples", color = AmberGlow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        mostWorn.forEach { item ->
                            Text(text = "• ${item.name}", color = PlatinumText, fontSize = 11.sp, maxLines = 1)
                            Text(text = "   ${item.wearCount} wears ($${String.format("%.1f", item.estimatedValue / item.wearCount.coerceAtLeast(1))}/wear)", color = MutedText, fontSize = 9.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }

                // Under-utilized Column
                Card(
                    modifier = Modifier.weight(1f).border(1.dp, CardBorderWhite10, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "💎 Untapped Hidden Gems", color = RoseGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        leastWorn.forEach { item ->
                            Text(text = "• ${item.name}", color = PlatinumText, fontSize = 11.sp, maxLines = 1)
                            Text(text = "   ${item.wearCount} wears (Needs styling)", color = MutedText, fontSize = 9.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }

        // AI Wardrobe Gaps & Shopping Recommendations Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorderWhite10, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI Gaps", tint = ChampagneGold, modifier = Modifier.size(20.dp))
                            Text(text = "Gemini Wardrobe Gap Analysis", color = PlatinumText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        gapAnalysis?.let {
                            Text(text = "${it.readinessScore}% Versatility", color = ChampagneGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (gapAnalysis == null) {
                        Text(
                            text = "Discover high-leverage capsule pieces to purchase next that will unlock 10+ new outfit combinations from your existing clothes.",
                            color = MutedText,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isAnalyzingGaps) Brush.horizontalGradient(listOf(ChampagneGold.copy(alpha = 0.5f), ChampagneGoldLight.copy(alpha = 0.5f)))
                                    else Brush.horizontalGradient(listOf(ChampagneGold, ChampagneGoldLight))
                                )
                                .clickable(enabled = !isAnalyzingGaps) { onAnalyzeGaps() }
                                .testTag("analyze_wardrobe_gaps_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isAnalyzingGaps) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(color = ObsidianBg, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Analyzing Wardrobe Gaps...", color = ObsidianBg, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Lightbulb, contentDescription = "Analyze", tint = Color.Black, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Analyze Missing Capsule Pieces", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    } else {
                        Text(text = gapAnalysis.summary, color = PlatinumText, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        gapAnalysis.recommendations.forEach { rec ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkNavyElevated)
                                    .border(1.dp, CardBorderWhite5, RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "✨ ${rec.itemName}", color = ChampagneGoldLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(text = rec.category, color = RoseGold, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Text(text = rec.reason, color = MutedText, fontSize = 11.sp)
                                    Text(text = "Palette: ${rec.suggestedColors.joinToString(", ")}", color = PlatinumText, fontSize = 10.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = onAnalyzeGaps,
                            modifier = Modifier.fillMaxWidth().height(40.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkNavyElevated),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Re-Analyze Gaps", color = ChampagneGold, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryDistributionRow(
    category: String,
    itemCount: Int,
    ratio: Float,
    color: Color
) {
    val animRatio by animateFloatAsState(
        targetValue = ratio,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "cat_ratio_$category"
    )

    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = category, color = PlatinumText, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Text(text = "$itemCount pcs (${(ratio * 100).toInt()}%)", color = MutedText, fontSize = 10.sp)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { animRatio },
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = DarkNavyElevated
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.border(1.dp, CardBorderWhite10, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = MutedText, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                Icon(imageVector = icon, contentDescription = title, tint = tint, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, color = PlatinumText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = tint, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}
