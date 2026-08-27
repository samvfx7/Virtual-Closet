package com.example.data.model

data class FashionPersonalityResult(
    val archetype: String, // "Minimalist Chic", "Avant-Garde Curator", "Classic Aristocrat", "Boho Visionary", "Urban Edge"
    val summary: String,
    val colorPalette: List<String>,
    val signatureSilhouettes: List<String>,
    val stapleItems: List<String>,
    val stylingMotto: String
)

data class StyleBadge(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val isUnlocked: Boolean,
    val progress: Float
)

data class PackingListItem(
    val dayNumber: Int,
    val activity: String,
    val outfitTitle: String,
    val items: List<String>,
    val notes: String
)

data class PackingPlan(
    val destination: String,
    val days: Int,
    val season: String,
    val tripType: String,
    val dailyOutfits: List<PackingListItem>,
    val essentialAccessories: List<String>
)
