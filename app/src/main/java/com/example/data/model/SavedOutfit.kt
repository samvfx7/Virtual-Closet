package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_outfits")
data class SavedOutfit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val itemIdsJson: String, // comma-separated or JSON list of item IDs
    val occasion: String = "casual",
    val weather: String = "mild",
    val aestheticMood: String = "chic",
    val rating: Int = 9, // 1 to 10
    val colorTheoryReason: String = "",
    val styleBalanceNotes: String = "",
    val stylingTips: String = "",
    val missingItemsSuggestions: String = "",
    val dateWorn: Long? = null,
    val userLiked: Boolean? = null,
    val timesWorn: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getItemIdList(): List<Long> {
        if (itemIdsJson.isBlank()) return emptyList()
        return itemIdsJson.split(",")
            .mapNotNull { it.trim().toLongOrNull() }
    }
}

data class GeneratedOutfit(
    val id: String,
    val title: String,
    val items: List<ClothingItem>,
    val occasion: String,
    val weather: String,
    val aestheticMood: String,
    val rating: Int,
    val colorTheoryReason: String,
    val styleBalanceNotes: String,
    val stylingTips: String,
    val missingItemsSuggestions: String,
    val whyItWorks: String
)
