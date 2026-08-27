package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ClothingItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothingItemDao {
    @Query("SELECT * FROM clothing_items ORDER BY createdAt DESC")
    fun getAllItems(): Flow<List<ClothingItem>>

    @Query("SELECT * FROM clothing_items WHERE id = :id")
    suspend fun getItemById(id: Long): ClothingItem?

    @Query("SELECT * FROM clothing_items WHERE category = :category ORDER BY createdAt DESC")
    fun getItemsByCategory(category: String): Flow<List<ClothingItem>>

    @Query("SELECT * FROM clothing_items WHERE favorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteItems(): Flow<List<ClothingItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ClothingItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ClothingItem>)

    @Update
    suspend fun updateItem(item: ClothingItem)

    @Delete
    suspend fun deleteItem(item: ClothingItem)

    @Query("DELETE FROM clothing_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    @Query("UPDATE clothing_items SET wearCount = wearCount + 1, lastWornTimestamp = :timestamp WHERE id = :id")
    suspend fun recordWear(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM clothing_items")
    suspend fun getItemCount(): Int
}
