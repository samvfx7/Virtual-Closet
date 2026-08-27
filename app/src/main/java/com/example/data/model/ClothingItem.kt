package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothing_items")
data class ClothingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // TOPS, BOTTOMS, DRESSES, OUTERWEAR, SHOES, ACCESSORIES
    val colorHex: String,
    val primaryColorName: String,
    val pattern: String, // solid, striped, floral, plaid, check, graphic, knit, textured
    val styleType: String, // casual, formal, sporty, boho, minimalist, vintage, street, chic
    val fit: String, // slim, regular, oversized, fitted
    val season: String, // spring, summer, fall, winter, all-season
    val materialType: String, // cotton, wool, silk, polyester, denim, linen, leather, cashmere
    val occasionTags: String, // comma-separated: work, casual, gym, party, date, formal, weekend, travel
    val brand: String = "",
    val condition: String = "good", // new, good, vintage
    val description: String = "",
    val imageUri: String = "", // URI or sample key
    val wearCount: Int = 0,
    val lastWornTimestamp: Long = 0L,
    val favorite: Boolean = false,
    val section: String = "ROD", // ROD, SHELF, DRAWER
    val estimatedValue: Double = 50.0,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getOccasionsList(): List<String> =
        occasionTags.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    fun matchesFilter(
        query: String = "",
        categoryFilter: String = "ALL",
        seasonFilter: String = "ALL",
        styleFilter: String = "ALL",
        occasionFilter: String = "ALL"
    ): Boolean {
        val matchesQuery = query.isEmpty() ||
                name.contains(query, ignoreCase = true) ||
                brand.contains(query, ignoreCase = true) ||
                description.contains(query, ignoreCase = true) ||
                primaryColorName.contains(query, ignoreCase = true) ||
                materialType.contains(query, ignoreCase = true)

        val matchesCategory = categoryFilter == "ALL" || category.equals(categoryFilter, ignoreCase = true)
        val matchesSeason = seasonFilter == "ALL" || season.equals(seasonFilter, ignoreCase = true) || season.equals("all-season", ignoreCase = true)
        val matchesStyle = styleFilter == "ALL" || styleType.equals(styleFilter, ignoreCase = true)
        val matchesOccasion = occasionFilter == "ALL" || occasionTags.contains(occasionFilter, ignoreCase = true)

        return matchesQuery && matchesCategory && matchesSeason && matchesStyle && matchesOccasion
    }
}
