package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ClothingItem
import com.example.data.model.SavedOutfit

@Database(
    entities = [ClothingItem::class, SavedOutfit::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clothingItemDao(): ClothingItemDao
    abstract fun savedOutfitDao(): SavedOutfitDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "virtual_closet_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
