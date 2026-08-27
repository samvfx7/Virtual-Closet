package com.example.ui.closet

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClothingItem
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CardBorderGold
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneGoldLight
import com.example.ui.theme.CoralRed
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
import com.example.ui.theme.ObsidianSurface

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WardrobeManagerScreen(
    items: List<ClothingItem>,
    isAnalyzing: Boolean,
    onUploadImage: (Bitmap) -> Unit,
    onUpdateItem: (ClothingItem) -> Unit,
    onDeleteItem: (ClothingItem) -> Unit,
    onRecordWear: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }
    var selectedSeason by remember { mutableStateOf("ALL") }
    var selectedStyle by remember { mutableStateOf("ALL") }

    var inspectingItem by remember { mutableStateOf<ClothingItem?>(null) }
    var editingItem by remember { mutableStateOf<ClothingItem?>(null) }
    var duplicateWarningItem by remember { mutableStateOf<ClothingItem?>(null) }
    var showUploadModal by remember { mutableStateOf(false) }

    val categories = listOf("ALL", "TOPS", "BOTTOMS", "DRESSES", "OUTERWEAR", "SHOES", "ACCESSORIES")

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
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
                onUploadImage(bitmap)
                showUploadModal = false
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            onUploadImage(it)
            showUploadModal = false
        }
    }

    val filteredItems = items.filter {
        it.matchesFilter(
            query = searchQuery,
            categoryFilter = selectedCategory,
            seasonFilter = selectedSeason,
            styleFilter = selectedStyle
        )
    }

    Box(modifier = modifier.fillMaxSize().background(ObsidianBg)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Search Bar & Filter Controls
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by name, brand, material, color...", color = MutedText, fontSize = 13.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = ChampagneGold) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = MutedText)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = CardBorderWhite10,
                        focusedTextColor = PlatinumText,
                        unfocusedTextColor = PlatinumText,
                        focusedContainerColor = ObsidianSurface,
                        unfocusedContainerColor = ObsidianSurface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("wardrobe_search_input")
                )
            }

            // Category Tabs
            item {
                ScrollableTabRow(
                    selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
                    containerColor = ObsidianSurface,
                    contentColor = ChampagneGold,
                    edgePadding = 0.dp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, CardBorderWhite10, RoundedCornerShape(14.dp))
                ) {
                    categories.forEach { cat ->
                        Tab(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            text = {
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedCategory == cat) ChampagneGold else PlatinumText
                                )
                            }
                        )
                    }
                }
            }

            // Color Swatches Quick Filter Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "COLOR PALETTE VIEW",
                        color = ChampagneGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "${filteredItems.size} Pieces Shown",
                        color = MutedText,
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                val distinctColors = items.map { it.colorHex to it.primaryColorName }.distinctBy { it.first }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(distinctColors) { (hex, name) ->
                        val color = try { Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { Color.Gray }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { searchQuery = name }
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(1.dp, ChampagneGold.copy(alpha = 0.6f), CircleShape)
                            )
                            Text(
                                text = name.take(7),
                                color = MutedText,
                                fontSize = 9.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Recently Added Carousel
            item {
                Text(
                    text = "RECENTLY CURATED PIECES",
                    color = ChampagneGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(items.take(6)) { item ->
                        val color = try { Color(android.graphics.Color.parseColor(item.colorHex)) } catch (e: Exception) { Color(0xFF334155) }
                        Card(
                            modifier = Modifier
                                .width(140.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, CardBorderGold, RoundedCornerShape(14.dp))
                                .clickable { inspectingItem = item },
                            colors = CardDefaults.cardColors(containerColor = DarkNavyCard)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(65.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(color),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.category,
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = item.name,
                                    color = PlatinumText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = item.brand.ifEmpty { "Atelier" },
                                    color = ChampagneGold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            // Wardrobe Grid of Items
            item {
                Text(
                    text = "ALL WARDROBE INVENTORY",
                    color = ChampagneGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }

            // Render Items in Flow / List format
            items(filteredItems.chunked(2)) { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pair.forEach { item ->
                        Box(modifier = Modifier.weight(1f)) {
                            WardrobeItemCard(
                                item = item,
                                onClick = { inspectingItem = item },
                                onToggleFavorite = {
                                    onUpdateItem(item.copy(favorite = !item.favorite))
                                }
                            )
                        }
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // Bottom space for FAB
            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }

        // Add / Upload FAB
        FloatingActionButton(
            onClick = { showUploadModal = true },
            containerColor = ChampagneGold,
            contentColor = ObsidianBg,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_clothing_item_fab")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Clothing Item",
                modifier = Modifier.size(26.dp)
            )
        }

        // Upload Options Dialog Modal
        if (showUploadModal) {
            AlertDialog(
                onDismissRequest = { showUploadModal = false },
                containerColor = ObsidianSurface,
                title = {
                    Text(
                        text = "Add to Virtual Wardrobe",
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Upload a photo to let Gemini AI automatically detect color hex, pattern, style, material, and occasion tags.",
                            color = MutedText,
                            fontSize = 12.sp
                        )

                        // Camera Button
                        Button(
                            onClick = { cameraLauncher.launch(null) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Camera", tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Take Photo with Camera", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        // Gallery Button
                        Button(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkNavyElevated),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Image, contentDescription = "Gallery", tint = ChampagneGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Choose from Gallery", color = PlatinumText, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showUploadModal = false }) {
                        Text("Cancel", color = ChampagneGold)
                    }
                }
            )
        }

        // Item Detail Inspection Dialog
        inspectingItem?.let { item ->
            ItemDetailDialog(
                item = item,
                onDismiss = { inspectingItem = null },
                onEdit = {
                    inspectingItem = null
                    editingItem = item
                },
                onDelete = {
                    onDeleteItem(item)
                    inspectingItem = null
                },
                onRecordWear = {
                    onRecordWear(item.id)
                    inspectingItem = item.copy(wearCount = item.wearCount + 1, lastWornTimestamp = System.currentTimeMillis())
                }
            )
        }

        // Item Edit Dialog
        editingItem?.let { item ->
            ItemEditDialog(
                item = item,
                onDismiss = { editingItem = null },
                onSave = { updated ->
                    onUpdateItem(updated)
                    editingItem = null
                }
            )
        }

        // AI Analyzing Overlay Loader
        if (isAnalyzing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ObsidianBg.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .padding(32.dp)
                        .border(1.dp, CardBorderGold, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = ChampagneGold,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = "Gemini Vision Analyzing...",
                            color = PlatinumText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Detecting color palette, silhouette, material weave, and occasion profile.",
                            color = MutedText,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WardrobeItemCard(
    item: ClothingItem,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val parsedColor = try {
        Color(android.graphics.Color.parseColor(item.colorHex))
    } catch (e: Exception) {
        Color(0xFF334155)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CardBorderWhite10, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("wardrobe_item_card_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Visual Color / Texture Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(parsedColor),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (item.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (item.favorite) RoseGold else Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(ObsidianBg.copy(alpha = 0.75f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.category,
                        color = ChampagneGold,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.name,
                color = PlatinumText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Text(
                text = "${item.primaryColorName} • ${item.materialType}",
                color = MutedText,
                fontSize = 10.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (item.brand.isNotEmpty()) item.brand else "Atelier",
                    color = ChampagneGoldLight,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Worn ${item.wearCount}x",
                    color = MutedText,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
fun ItemDetailDialog(
    item: ClothingItem,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onRecordWear: () -> Unit
) {
    val color = try { Color(android.graphics.Color.parseColor(item.colorHex)) } catch (e: Exception) { Color.Gray }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianSurface,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = item.name, color = PlatinumText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(1.dp, ChampagneGold, CircleShape)
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = item.description, color = PlatinumText, fontSize = 12.sp)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Category: ${item.category}", color = ChampagneGold, fontSize = 11.sp)
                    Text(text = "Season: ${item.season}", color = ChampagneGold, fontSize = 11.sp)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Material: ${item.materialType}", color = MutedText, fontSize = 11.sp)
                    Text(text = "Fit: ${item.fit}", color = MutedText, fontSize = 11.sp)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Brand: ${item.brand.ifEmpty { "N/A" }}", color = MutedText, fontSize = 11.sp)
                    Text(text = "Est. Value: $${item.estimatedValue}", color = SuccessEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Text(
                    text = "Occasions: ${item.occasionTags}",
                    color = RoseGold,
                    fontSize = 11.sp
                )

                Text(
                    text = "Wear Tracker: Worn ${item.wearCount} times",
                    color = ChampagneGoldLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Record Wear Action
                Button(
                    onClick = onRecordWear,
                    modifier = Modifier.fillMaxWidth().height(38.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessEmerald),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("✓ Log Wear Today (+1 Sustainability)", fontSize = 11.sp, color = Color.White)
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onEdit) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = ChampagneGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", color = ChampagneGold)
                }
                TextButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = CoralRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete", color = CoralRed)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = PlatinumText)
            }
        }
    )
}

@Composable
fun ItemEditDialog(
    item: ClothingItem,
    onDismiss: () -> Unit,
    onSave: (ClothingItem) -> Unit
) {
    var name by remember { mutableStateOf(item.name) }
    var brand by remember { mutableStateOf(item.brand) }
    var category by remember { mutableStateOf(item.category) }
    var primaryColorName by remember { mutableStateOf(item.primaryColorName) }
    var materialType by remember { mutableStateOf(item.materialType) }
    var occasionTags by remember { mutableStateOf(item.occasionTags) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianSurface,
        title = { Text("Edit Clothing Item", color = ChampagneGold, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name", color = MutedText) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = PlatinumText, unfocusedTextColor = PlatinumText)
                )
                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Brand", color = MutedText) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = PlatinumText, unfocusedTextColor = PlatinumText)
                )
                OutlinedTextField(
                    value = primaryColorName,
                    onValueChange = { primaryColorName = it },
                    label = { Text("Color Name", color = MutedText) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = PlatinumText, unfocusedTextColor = PlatinumText)
                )
                OutlinedTextField(
                    value = materialType,
                    onValueChange = { materialType = it },
                    label = { Text("Material", color = MutedText) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = PlatinumText, unfocusedTextColor = PlatinumText)
                )
                OutlinedTextField(
                    value = occasionTags,
                    onValueChange = { occasionTags = it },
                    label = { Text("Occasion Tags", color = MutedText) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = PlatinumText, unfocusedTextColor = PlatinumText)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        item.copy(
                            name = name,
                            brand = brand,
                            primaryColorName = primaryColorName,
                            materialType = materialType,
                            occasionTags = occasionTags
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold)
            ) {
                Text("Save Changes", color = ObsidianBg, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = MutedText) }
        }
    )
}
