package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SavedOutfit
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedOutfitDao {
    @Query("SELECT * FROM saved_outfits ORDER BY createdAt DESC")
    fun getAllSavedOutfits(): Flow<List<SavedOutfit>>

    @Query("SELECT * FROM saved_outfits WHERE id = :id")
    suspend fun getOutfitById(id: Long): SavedOutfit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfit(outfit: SavedOutfit): Long

    @Update
    suspend fun updateOutfit(outfit: SavedOutfit)

    @Delete
    suspend fun deleteOutfit(outfit: SavedOutfit)

    @Query("DELETE FROM saved_outfits WHERE id = :id")
    suspend fun deleteOutfitById(id: Long)

    @Query("UPDATE saved_outfits SET dateWorn = :timestamp, userLiked = :liked WHERE id = :id")
    suspend fun recordWearLog(id: Long, timestamp: Long, liked: Boolean)
}
