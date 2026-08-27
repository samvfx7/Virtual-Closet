package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiFashionAi
import com.example.ai.StealLookResult
import com.example.ai.WardrobeGapAnalysis
import com.example.auth.GoogleAuthManager
import com.example.auth.UserProfile
import com.example.data.db.AppDatabase
import com.example.data.model.ClothingItem
import com.example.data.model.GeneratedOutfit
import com.example.data.model.PackingPlan
import com.example.data.model.SavedOutfit
import com.example.data.repository.WardrobeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClosetViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = WardrobeRepository(
        clothingDao = database.clothingItemDao(),
        outfitDao = database.savedOutfitDao()
    )
    private val authManager = GoogleAuthManager(application)

    val currentUser: StateFlow<UserProfile?> = authManager.currentUser
    val hasCompletedOnboarding: StateFlow<Boolean> = authManager.hasCompletedOnboarding
    val isAuthLoading: StateFlow<Boolean> = authManager.isLoading
    val authError: StateFlow<String?> = authManager.errorMessage

    private val _lastUploadedItemName = MutableStateFlow<String?>(null)
    val lastUploadedItemName: StateFlow<String?> = _lastUploadedItemName.asStateFlow()

    val allItems: StateFlow<List<ClothingItem>> = repository.allItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val savedOutfits: StateFlow<List<SavedOutfit>> = repository.savedOutfits.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _selected3DCategory = MutableStateFlow("ALL")
    val selected3DCategory: StateFlow<String> = _selected3DCategory.asStateFlow()

    private val _is3DModeThreeJs = MutableStateFlow(true)
    val is3DModeThreeJs: StateFlow<Boolean> = _is3DModeThreeJs.asStateFlow()

    private val _isAnalyzingImage = MutableStateFlow(false)
    val isAnalyzingImage: StateFlow<Boolean> = _isAnalyzingImage.asStateFlow()

    private val _isGeneratingOutfits = MutableStateFlow(false)
    val isGeneratingOutfits: StateFlow<Boolean> = _isGeneratingOutfits.asStateFlow()

    private val _isAnalyzingGaps = MutableStateFlow(false)
    val isAnalyzingGaps: StateFlow<Boolean> = _isAnalyzingGaps.asStateFlow()

    private val _isProcessingStealLook = MutableStateFlow(false)
    val isProcessingStealLook: StateFlow<Boolean> = _isProcessingStealLook.asStateFlow()

    private val _isGeneratingPacking = MutableStateFlow(false)
    val isGeneratingPacking: StateFlow<Boolean> = _isGeneratingPacking.asStateFlow()

    private val _generatedOutfits = MutableStateFlow<List<GeneratedOutfit>>(emptyList())
    val generatedOutfits: StateFlow<List<GeneratedOutfit>> = _generatedOutfits.asStateFlow()

    private val _gapAnalysis = MutableStateFlow<WardrobeGapAnalysis?>(null)
    val gapAnalysis: StateFlow<WardrobeGapAnalysis?> = _gapAnalysis.asStateFlow()

    private val _stealLookResult = MutableStateFlow<StealLookResult?>(null)
    val stealLookResult: StateFlow<StealLookResult?> = _stealLookResult.asStateFlow()

    private val _packingPlan = MutableStateFlow<PackingPlan?>(null)
    val packingPlan: StateFlow<PackingPlan?> = _packingPlan.asStateFlow()

    private val _selectedMannequinItems = MutableStateFlow<List<ClothingItem>>(emptyList())
    val selectedMannequinItems: StateFlow<List<ClothingItem>> = _selectedMannequinItems.asStateFlow()

    init {
        // New users start with an empty wardrobe by default as requested.
    }

    fun loadSampleData() {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun setSelected3DCategory(category: String) {
        _selected3DCategory.value = category
    }

    fun toggle3DMode() {
        _is3DModeThreeJs.value = !_is3DModeThreeJs.value
    }

    fun toggleMannequinItem(item: ClothingItem) {
        val current = _selectedMannequinItems.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.category == item.category }
        if (existingIndex != -1) {
            if (current[existingIndex].id == item.id) {
                current.removeAt(existingIndex)
            } else {
                current[existingIndex] = item
            }
        } else {
            current.add(item)
        }
        _selectedMannequinItems.value = current
    }

    fun clearMannequin() {
        _selectedMannequinItems.value = emptyList()
    }

    fun uploadClothingImage(bitmap: Bitmap, onComplete: ((String) -> Unit)? = null) {
        viewModelScope.launch {
            _isAnalyzingImage.value = true
            try {
                val analysis = GeminiFashionAi.analyzeClothingImage(bitmap)
                val newItem = ClothingItem(
                    name = analysis.name,
                    category = analysis.category,
                    colorHex = analysis.colorHex,
                    primaryColorName = analysis.primaryColorName,
                    pattern = analysis.pattern,
                    styleType = analysis.styleType,
                    fit = analysis.fit,
                    season = analysis.season,
                    materialType = analysis.materialType,
                    occasionTags = analysis.occasionTags,
                    brand = analysis.brand,
                    condition = analysis.condition,
                    description = analysis.description,
                    section = analysis.section,
                    estimatedValue = analysis.estimatedValue
                )
                repository.insertItem(newItem)
                _lastUploadedItemName.value = analysis.name
                onComplete?.invoke(analysis.name)
            } catch (e: Exception) {
                // handle error
            } finally {
                _isAnalyzingImage.value = false
            }
        }
    }

    fun clearUploadStatus() {
        _lastUploadedItemName.value = null
    }

    fun continueAsGuest(name: String) {
        authManager.continueAsGuest(name)
    }

    fun completeOnboarding() {
        authManager.completeOnboarding()
    }

    fun updateItem(item: ClothingItem) {
        viewModelScope.launch {
            repository.updateItem(item)
        }
    }

    fun deleteItem(item: ClothingItem) {
        viewModelScope.launch {
            repository.deleteItemById(item.id)
            _selectedMannequinItems.value = _selectedMannequinItems.value.filter { it.id != item.id }
        }
    }

    fun recordWear(itemId: Long) {
        viewModelScope.launch {
            repository.recordWear(itemId)
        }
    }

    fun generateOutfits(occasion: String, weather: String, mood: String) {
        viewModelScope.launch {
            _isGeneratingOutfits.value = true
            try {
                val items = allItems.value
                val results = GeminiFashionAi.generateOutfits(items, occasion, weather, mood)
                _generatedOutfits.value = results
            } catch (e: Exception) {
                // handle
            } finally {
                _isGeneratingOutfits.value = false
            }
        }
    }

    fun saveGeneratedOutfit(outfit: GeneratedOutfit) {
        viewModelScope.launch {
            val itemIds = outfit.items.map { it.id }.joinToString(",")
            val savedOutfit = SavedOutfit(
                name = outfit.title,
                itemIdsJson = itemIds,
                occasion = outfit.occasion,
                weather = outfit.weather,
                aestheticMood = outfit.aestheticMood,
                rating = outfit.rating,
                colorTheoryReason = outfit.colorTheoryReason,
                styleBalanceNotes = outfit.styleBalanceNotes,
                stylingTips = outfit.stylingTips,
                missingItemsSuggestions = outfit.missingItemsSuggestions
            )
            repository.insertOutfit(savedOutfit)
        }
    }

    fun wearGeneratedOutfit(outfit: GeneratedOutfit) {
        viewModelScope.launch {
            outfit.items.forEach { item ->
                repository.recordWear(item.id)
            }
            saveGeneratedOutfit(outfit)
        }
    }

    fun deleteSavedOutfit(outfit: SavedOutfit) {
        viewModelScope.launch {
            repository.deleteOutfitById(outfit.id)
        }
    }

    fun rateSavedOutfit(outfitId: Long, liked: Boolean) {
        viewModelScope.launch {
            repository.recordWearLog(outfitId, System.currentTimeMillis(), liked)
        }
    }

    fun analyzeWardrobeGaps() {
        viewModelScope.launch {
            _isAnalyzingGaps.value = true
            try {
                val gaps = GeminiFashionAi.analyzeWardrobeGaps(allItems.value)
                _gapAnalysis.value = gaps
            } catch (e: Exception) {
                // handle
            } finally {
                _isAnalyzingGaps.value = false
            }
        }
    }

    fun stealMyLook(bitmap: Bitmap) {
        viewModelScope.launch {
            _isProcessingStealLook.value = true
            try {
                val result = GeminiFashionAi.matchInspirationLook(bitmap, allItems.value)
                _stealLookResult.value = result
            } catch (e: Exception) {
                // handle
            } finally {
                _isProcessingStealLook.value = false
            }
        }
    }

    fun generatePackingPlan(destination: String, days: Int, season: String, tripType: String) {
        viewModelScope.launch {
            _isGeneratingPacking.value = true
            try {
                val plan = GeminiFashionAi.generatePackingPlan(destination, days, season, tripType, allItems.value)
                _packingPlan.value = plan
            } catch (e: Exception) {
                // handle
            } finally {
                _isGeneratingPacking.value = false
            }
        }
    }

    fun signInWithGoogle(webClientId: String? = null) {
        viewModelScope.launch {
            authManager.signInWithGoogle(webClientId)
        }
    }

    fun signInDemoGoogle(email: String = "sam77.dev@gmail.com", name: String = "Sam Curator") {
        authManager.signInWithDefaultGoogleAccount(email, name)
    }

    fun signOutGoogle() {
        authManager.signOut()
    }

    fun clearAuthError() {
        authManager.clearError()
    }
}
