package com.example.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClothingItem
import com.example.ui.auth.MinimalOnboardingScreen
import com.example.ui.auth.WelcomeAuthScreen
import com.example.ui.components.CraftCorners
import com.example.ui.components.LuxuryPrimaryButton
import com.example.ui.components.SleekPulseIndicator
import com.example.ui.components.SleekShimmerBar
import com.example.ui.components.bounceClick
import com.example.ui.looks.SavedLooksScreen
import com.example.ui.scene3d.ThreeJsClosetView
import com.example.ui.stylist.AiStylistScreen
import com.example.ui.theme.BorderNeonSubtle
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.BorderSubtleLight
import com.example.ui.theme.CoralRed
import com.example.ui.theme.DangerBg
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonBlueGlow
import com.example.ui.theme.NeonBlueSoftGlow
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceGlass
import com.example.ui.theme.SurfaceLevel1
import com.example.ui.theme.SurfaceLevel2
import com.example.ui.theme.SurfaceLevel3
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.viewmodel.ClosetViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class BrutalistTab(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val testTag: String,
    val index: Int
) {
    object Closet3D : BrutalistTab("3D CLOSET", Icons.Default.ViewInAr, "tab_3d_closet", 0)
    object Stylist : BrutalistTab("AI STYLIST", Icons.Default.AutoAwesome, "tab_ai_stylist", 1)
    object Looks : BrutalistTab("SAVED LOOKS", Icons.Default.Bookmark, "tab_saved_looks", 2)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: ClosetViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val currentUser by viewModel.currentUser.collectAsState()
    val hasCompletedOnboarding by viewModel.hasCompletedOnboarding.collectAsState()
    val isAuthLoading by viewModel.isAuthLoading.collectAsState()

    val allItems by viewModel.allItems.collectAsState()
    val savedOutfits by viewModel.savedOutfits.collectAsState()
    val selected3DCategory by viewModel.selected3DCategory.collectAsState()
    val isAnalyzingImage by viewModel.isAnalyzingImage.collectAsState()
    val isGeneratingOutfits by viewModel.isGeneratingOutfits.collectAsState()
    val generatedOutfits by viewModel.generatedOutfits.collectAsState()
    val lastUploadedItemName by viewModel.lastUploadedItemName.collectAsState()

    var currentTab by remember { mutableStateOf<BrutalistTab>(BrutalistTab.Closet3D) }
    var showUploadModal by remember { mutableStateOf(false) }
    var showUserMenu by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    // Auto-dismiss toast
    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            delay(3500)
            toastMessage = null
        }
    }

    // Photo picker launchers
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                        decoder.isMutableRequired = true
                    }
                }
                viewModel.uploadClothingImage(bitmap) { itemName ->
                    toastMessage = "Added $itemName to wardrobe"
                }
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            viewModel.uploadClothingImage(it) { itemName ->
                toastMessage = "Added $itemName to wardrobe"
            }
        }
    }

    // --- STEP 1: AUTH CHECK (FIRST LAUNCH) ---
    if (currentUser == null) {
        WelcomeAuthScreen(
            isLoading = isAuthLoading,
            onSignInWithGoogle = { name ->
                viewModel.signInWithGoogle(name)
            },
            onContinueAsGuest = { name ->
                viewModel.continueAsGuest(name)
            }
        )
        return
    }

    // --- STEP 2: MINIMAL ONBOARDING (FIRST LAUNCH ONLY) ---
    if (!hasCompletedOnboarding) {
        MinimalOnboardingScreen(
            userName = currentUser?.name ?: "Curator",
            onFinish = {
                viewModel.completeOnboarding()
            }
        )
        return
    }

    // --- STEP 3: CORE INTERFACE (3 MINIMAL TABS) ---
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = PureBlack,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Cyan Accent Indicator Dot
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(NeonBlue)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "VIRTUAL",
                                color = TextSecondary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Light,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "CLOSET",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }

                        // Piece Counter Pill
                        Box(
                            modifier = Modifier
                                .clip(CraftCorners.Chip)
                                .background(SurfaceLevel2)
                                .border(1.dp, BorderSubtle, CraftCorners.Chip)
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${allItems.size}",
                                color = NeonBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                actions = {
                    // Refined User Pill (Linear Style)
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clip(CraftCorners.Pill)
                            .background(SurfaceLevel2)
                            .border(1.dp, BorderSubtle, CraftCorners.Pill)
                            .bounceClick(scaleDown = 0.94f) { showUserMenu = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("user_profile_chip"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (currentUser?.isGoogleAuthenticated == true) NeonBlue else TextMuted)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = (currentUser?.name ?: "CURATOR").uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                color = TextPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PureBlack
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PureBlack)
        ) {
            // Tab Content Switcher with smooth Fade + Scale/Slide
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220, easing = FastOutSlowInEasing)) togetherWith
                            fadeOut(animationSpec = tween(180, easing = FastOutSlowInEasing))
                },
                label = "luxury_tab_transition"
            ) { tab ->
                when (tab) {
                    is BrutalistTab.Closet3D -> {
                        ThreeJsClosetView(
                            items = allItems,
                            selectedCategory = selected3DCategory,
                            onCategorySelected = { viewModel.setSelected3DCategory(it) },
                            onUploadClick = { showUploadModal = true },
                            onGenerateOutfitClick = { currentTab = BrutalistTab.Stylist },
                            onDeleteItem = { viewModel.deleteItem(it) }
                        )
                    }
                    is BrutalistTab.Stylist -> {
                        AiStylistScreen(
                            isGenerating = isGeneratingOutfits,
                            generatedOutfits = generatedOutfits,
                            onGenerate = { occ, weather, mood ->
                                viewModel.generateOutfits(occ, weather, mood)
                            },
                            onSaveOutfit = { outfit ->
                                viewModel.saveGeneratedOutfit(outfit)
                                toastMessage = "Saved look '${outfit.title}'"
                            }
                        )
                    }
                    is BrutalistTab.Looks -> {
                        SavedLooksScreen(
                            savedOutfits = savedOutfits,
                            allItems = allItems,
                            onDeleteOutfit = { viewModel.deleteSavedOutfit(it) },
                            onNavigateToStylist = { currentTab = BrutalistTab.Stylist }
                        )
                    }
                }
            }

            // Upload Loading Banner with Sleek Shimmer Bar
            if (isAnalyzingImage) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .background(SurfaceLevel2)
                        .border(1.dp, BorderNeonSubtle)
                ) {
                    Column {
                        SleekShimmerBar()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            SleekPulseIndicator(modifier = Modifier.size(12.dp), color = NeonBlue)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "GEMINI ANALYZING GARMENT PROFILE...",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            // Floating Toast Notification Pill
            AnimatedVisibility(
                visible = toastMessage != null,
                enter = fadeIn() + slideInVertically { -it },
                exit = fadeOut() + slideOutVertically { -it },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                toastMessage?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CraftCorners.Card)
                            .background(SurfaceLevel2)
                            .border(1.dp, BorderNeonSubtle, CraftCorners.Card)
                            .padding(horizontal = 18.dp, vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(NeonBlueSoftGlow),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = NeonBlue,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = msg.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    color = TextPrimary
                                )
                            }
                            IconButton(
                                onClick = { toastMessage = null },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // ==========================================
            // FLOATING GLASSMORPHIC BOTTOM DOCK
            // ==========================================
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            // Soft ambient drop glow below dock
                            drawRoundRect(
                                brush = Brush.radialGradient(
                                    colors = listOf(NeonBlueSoftGlow, Color.Transparent),
                                    center = Offset(size.width / 2, size.height / 2),
                                    radius = size.width * 0.55f
                                ),
                                size = size
                            )
                        }
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceGlass)
                        .border(1.dp, BorderSubtleLight, RoundedCornerShape(20.dp))
                        .padding(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val tabs = listOf(
                            BrutalistTab.Closet3D,
                            BrutalistTab.Stylist,
                            BrutalistTab.Looks
                        )

                        tabs.forEach { tab ->
                            val isSelected = currentTab == tab
                            val bgAnimColor by animateColorAsState(
                                targetValue = if (isSelected) NeonBlue else Color.Transparent,
                                animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                                label = "tab_bg"
                            )
                            val iconColor by animateColorAsState(
                                targetValue = if (isSelected) PureBlack else TextMuted,
                                animationSpec = tween(durationMillis = 200),
                                label = "tab_icon"
                            )
                            val textColor by animateColorAsState(
                                targetValue = if (isSelected) PureBlack else TextSecondary,
                                animationSpec = tween(durationMillis = 200),
                                label = "tab_text"
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(bgAnimColor)
                                    .bounceClick(scaleDown = 0.94f) { currentTab = tab }
                                    .testTag(tab.testTag),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.title,
                                        tint = iconColor,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = tab.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                        letterSpacing = 0.6.sp,
                                        color = textColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal 1: Image Upload Dialog
    if (showUploadModal) {
        AlertDialog(
            onDismissRequest = { showUploadModal = false },
            containerColor = SurfaceLevel2,
            shape = CraftCorners.Modal,
            title = {
                Text(
                    text = "ADD TO WARDROBE",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Upload a photo. Gemini AI analyzes color palette, silhouette, and style parameters directly into your 3D closet.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Button: Take Photo
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CraftCorners.Button)
                            .background(SurfaceLevel3)
                            .border(1.dp, BorderSubtle, CraftCorners.Button)
                            .bounceClick(scaleDown = 0.96f) {
                                showUploadModal = false
                                cameraLauncher.launch(null)
                            }
                            .padding(vertical = 13.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = NeonBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("TAKE PHOTO", fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = TextPrimary)
                        }
                    }

                    // Button: Choose from Gallery
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CraftCorners.Button)
                            .background(SurfaceLevel3)
                            .border(1.dp, BorderSubtle, CraftCorners.Button)
                            .bounceClick(scaleDown = 0.96f) {
                                showUploadModal = false
                                galleryLauncher.launch("image/*")
                            }
                            .padding(vertical = 13.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = NeonBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("CHOOSE FROM GALLERY", fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = TextPrimary)
                        }
                    }

                    // Option to populate sample wardrobe
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CraftCorners.Button)
                            .background(SurfaceLevel3)
                            .border(1.dp, BorderSubtle, CraftCorners.Button)
                            .bounceClick(scaleDown = 0.96f) {
                                showUploadModal = false
                                viewModel.loadSampleData()
                                toastMessage = "Loaded sample wardrobe items"
                            }
                            .padding(vertical = 13.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = NeonBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("LOAD SAMPLE COLLECTION", fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = TextPrimary)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showUploadModal = false }) {
                    Text("CLOSE", color = NeonBlue, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        )
    }

    // Modal 2: User Profile & Session Dialog
    if (showUserMenu) {
        AlertDialog(
            onDismissRequest = { showUserMenu = false },
            containerColor = SurfaceLevel2,
            shape = CraftCorners.Modal,
            title = {
                Text(
                    text = "CURATOR PROFILE",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "AUTHENTICATED IDENTITY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = TextMuted
                    )
                    Text(
                        text = currentUser?.name ?: "Curator",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (currentUser?.isGoogleAuthenticated == true) "Google Authenticated • ${currentUser?.email}" else "Guest Session (Local Storage)",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(CraftCorners.Button)
                        .background(DangerBg)
                        .border(1.dp, CoralRed.copy(alpha = 0.3f), CraftCorners.Button)
                        .bounceClick(scaleDown = 0.94f) {
                            showUserMenu = false
                            viewModel.signOutGoogle()
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Logout, contentDescription = null, tint = CoralRed, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("SIGN OUT", color = CoralRed, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showUserMenu = false }) {
                    Text("CANCEL", color = TextSecondary, fontSize = 11.sp)
                }
            }
        )
    }
}
