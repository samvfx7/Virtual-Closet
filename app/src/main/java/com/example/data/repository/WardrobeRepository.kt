package com.example.data.repository

import com.example.data.SampleWardrobeData
import com.example.data.db.ClothingItemDao
import com.example.data.db.SavedOutfitDao
import com.example.data.model.ClothingItem
import com.example.data.model.SavedOutfit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WardrobeRepository(
    private val clothingDao: ClothingItemDao,
    private val outfitDao: SavedOutfitDao
) {
    val allItems: Flow<List<ClothingItem>> = clothingDao.getAllItems()
    val savedOutfits: Flow<List<SavedOutfit>> = outfitDao.getAllSavedOutfits()
    val favoriteItems: Flow<List<ClothingItem>> = clothingDao.getFavoriteItems()

    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        val count = clothingDao.getItemCount()
        if (count == 0) {
            clothingDao.insertItems(SampleWardrobeData.getInitialItems())
        }
    }

    suspend fun insertItem(item: ClothingItem): Long = withContext(Dispatchers.IO) {
        clothingDao.insertItem(item)
    }

    suspend fun updateItem(item: ClothingItem) = withContext(Dispatchers.IO) {
        clothingDao.updateItem(item)
    }

    suspend fun deleteItemById(id: Long) = withContext(Dispatchers.IO) {
        clothingDao.deleteItemById(id)
    }

    suspend fun recordWear(id: Long) = withContext(Dispatchers.IO) {
        clothingDao.recordWear(id)
    }

    suspend fun insertOutfit(outfit: SavedOutfit): Long = withContext(Dispatchers.IO) {
        outfitDao.insertOutfit(outfit)
    }

    suspend fun deleteOutfitById(id: Long) = withContext(Dispatchers.IO) {
        outfitDao.deleteOutfitById(id)
    }

    suspend fun recordWearLog(id: Long, timestamp: Long, liked: Boolean) = withContext(Dispatchers.IO) {
        outfitDao.recordWearLog(id, timestamp, liked)
    }

    suspend fun findSimilarItem(name: String, category: String, colorHex: String, existingItems: List<ClothingItem>): ClothingItem? {
        return existingItems.firstOrNull { existing ->
            existing.category.equals(category, ignoreCase = true) &&
                    (existing.name.contains(name, ignoreCase = true) || name.contains(existing.name, ignoreCase = true) ||
                            existing.colorHex.equals(colorHex, ignoreCase = true))
        }
    }
}
