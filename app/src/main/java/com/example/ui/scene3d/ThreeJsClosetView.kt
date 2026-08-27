package com.example.ui.scene3d

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.ClothingItem
import com.example.ui.components.CraftCorners
import com.example.ui.components.LuxuryPrimaryButton
import com.example.ui.components.LuxurySelectorChip
import com.example.ui.components.bounceClick
import com.example.ui.theme.BorderNeonSubtle
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.BorderSubtleLight
import com.example.ui.theme.CoralRed
import com.example.ui.theme.DangerBg
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonBlueGlow
import com.example.ui.theme.NeonBlueSoftGlow
import com.example.ui.theme.PureBlack
import com.example.ui.theme.SurfaceLevel1
import com.example.ui.theme.SurfaceLevel2
import com.example.ui.theme.SurfaceLevel3
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import org.json.JSONArray
import org.json.JSONObject

class ClosetJsInterface(private val onItemSelected: (Long) -> Unit) {
    @JavascriptInterface
    fun onClothingItemSelected(itemId: Long) {
        onItemSelected(itemId)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ThreeJsClosetView(
    items: List<ClothingItem>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onUploadClick: () -> Unit,
    onGenerateOutfitClick: () -> Unit,
    onDeleteItem: (ClothingItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var selectedItemForDetail by remember { mutableStateOf<ClothingItem?>(null) }
    val categories = listOf("ALL", "TOPS", "BOTTOMS", "DRESSES", "OUTERWEAR", "SHOES", "ACCESSORIES")
    val mainHandler = remember { android.os.Handler(android.os.Looper.getMainLooper()) }

    fun sendItemsToWebView(view: WebView, clothingList: List<ClothingItem>) {
        val jsonArray = JSONArray()
        clothingList.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("name", item.name)
                put("category", item.category)
                put("colorHex", item.colorHex)
                put("primaryColorName", item.primaryColorName)
                put("pattern", item.pattern)
                put("styleType", item.styleType)
                put("brand", item.brand)
                put("section", item.section)
            }
            jsonArray.put(obj)
        }
        val jsonString = jsonArray.toString()
        view.evaluateJavascript("if (window.loadWardrobeData) { window.loadWardrobeData($jsonString); }", null)
    }

    // Send updated wardrobe data whenever items change
    LaunchedEffect(items, webViewRef) {
        webViewRef?.let { webView ->
            sendItemsToWebView(webView, items)
        }
    }

    // Filter updates
    LaunchedEffect(selectedCategory, webViewRef) {
        webViewRef?.evaluateJavascript("if (window.setCategoryFilter) { window.setCategoryFilter('$selectedCategory'); }", null)
    }

    Box(modifier = modifier.fillMaxSize().background(PureBlack)) {
        // 3D Three.js WebView Scene
        AndroidView(
            modifier = Modifier.fillMaxSize().testTag("three_js_closet_scene"),
            factory = { context ->
                WebView(context).apply {
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        allowFileAccess = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        cacheMode = WebSettings.LOAD_DEFAULT
                    }
                    webChromeClient = WebChromeClient()
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            view?.let { sendItemsToWebView(it, items) }
                        }
                    }
                    addJavascriptInterface(ClosetJsInterface { id ->
                        mainHandler.post {
                            items.find { it.id == id }?.let {
                                selectedItemForDetail = it
                            }
                        }
                    }, "AndroidBridge")

                    loadUrl("file:///android_asset/three_closet.html")
                    webViewRef = this
                }
            },
            update = {
                webViewRef = it
            }
        )

        // Empty Wardrobe Visual Card Overlay
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp)
                    .clip(CraftCorners.Card)
                    .background(SurfaceLevel2.copy(alpha = 0.95f))
                    .border(1.dp, BorderSubtle, CraftCorners.Card)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CraftCorners.Card)
                            .background(SurfaceLevel3)
                            .border(1.dp, BorderNeonSubtle, CraftCorners.Card),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = NeonBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "YOUR CLOSET IS EMPTY",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.4.sp,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Upload garments using the + UPLOAD button at the top to populate your 3D digital closet.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Box(
                        modifier = Modifier
                            .clip(CraftCorners.Button)
                            .background(NeonBlue)
                            .bounceClick(scaleDown = 0.94f) { onUploadClick() }
                            .padding(horizontal = 20.dp, vertical = 11.dp)
                            .testTag("empty_wardrobe_upload_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Upload",
                                tint = PureBlack,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "UPLOAD CLOTHES",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = PureBlack
                            )
                        }
                    }
                }
            }
        }

        // Top Controls: Category Filter Chips + Action Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Filter Pills
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        LuxurySelectorChip(
                            label = cat,
                            selected = isSelected,
                            onClick = { onCategorySelected(cat) }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Upload Button [UPLOAD] with micro-interaction
                Box(
                    modifier = Modifier
                        .clip(CraftCorners.Button)
                        .background(NeonBlue)
                        .bounceClick(scaleDown = 0.94f) { onUploadClick() }
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                        .testTag("upload_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Upload",
                            tint = PureBlack,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "UPLOAD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = PureBlack
                        )
                    }
                }
            }
        }

        // Bottom-Right Floating [GENERATE OUTFIT] Action Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 96.dp, end = 20.dp)
        ) {
            LuxuryPrimaryButton(
                text = "GENERATE OUTFIT",
                icon = Icons.Default.AutoAwesome,
                onClick = onGenerateOutfitClick,
                modifier = Modifier.height(48.dp),
                testTag = "generate_outfit_floating_button"
            )
        }

        // Reset camera icon on bottom left with physical surface styling
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 96.dp, start = 20.dp)
                .size(48.dp)
                .clip(CraftCorners.Button)
                .background(SurfaceLevel2)
                .border(1.dp, BorderSubtleLight, CraftCorners.Button)
                .bounceClick(scaleDown = 0.92f) {
                    webViewRef?.evaluateJavascript("if (window.resetCamera) { window.resetCamera(); }", null)
                }
                .testTag("reset_3d_camera_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Reset View",
                tint = NeonBlue,
                modifier = Modifier.size(18.dp)
            )
        }

        // Selected Item Minimal Detail Overlay (if tapped in 3D scene)
        AnimatedVisibility(
            visible = selectedItemForDetail != null,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 96.dp, start = 20.dp, end = 20.dp)
        ) {
            selectedItemForDetail?.let { item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CraftCorners.Card)
                        .background(SurfaceLevel2)
                        .border(1.dp, BorderNeonSubtle, CraftCorners.Card)
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.category.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.4.sp,
                                    color = NeonBlue
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = item.name.uppercase(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${item.primaryColorName} • ${item.styleType} • ${item.season}".uppercase(),
                                    fontSize = 10.sp,
                                    letterSpacing = 0.8.sp,
                                    color = TextSecondary
                                )
                            }

                            IconButton(
                                onClick = { selectedItemForDetail = null },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(CraftCorners.Button)
                                    .background(DangerBg)
                                    .border(1.dp, CoralRed.copy(alpha = 0.3f), CraftCorners.Button)
                                    .bounceClick(scaleDown = 0.94f) {
                                        onDeleteItem(item)
                                        selectedItemForDetail = null
                                    }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = CoralRed,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "DELETE",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp,
                                        color = CoralRed
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
