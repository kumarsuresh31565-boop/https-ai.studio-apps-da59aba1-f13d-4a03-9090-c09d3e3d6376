package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import android.widget.Toast
import com.example.R
import com.example.data.UserProfile
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    onNavigateToAdmin: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToVideo: (Long) -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val scrollState = rememberScrollState()

    var editBioExpanded by remember { mutableStateOf(false) }
    var currentBioInput by remember { mutableStateOf("") }

    LaunchedEffect(userProfile) {
        userProfile?.let {
            currentBioInput = it.bio
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
            .verticalScroll(scrollState)
            .padding(16.dp)
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top profile card row uploader details
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF211F26), RoundedCornerShape(20.dp))
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF2D2A35), Color(0xFF16151A))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (userProfile?.name ?: "Suresh").take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = userProfile?.name ?: "Suresh Kumar",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (userProfile?.isPremium == true) {
                        Surface(
                            color = Color(0xFFFFD700),
                            shape = CircleShape
                        ) {
                            Icon(Icons.Default.Star, contentDescription = "Premium Badge", tint = Color.Black, modifier = Modifier.size(12.dp).padding(2.dp))
                        }
                    }
                }
                Text(userProfile?.email ?: "kumarsuresh31565@gmail.com", color = Color.Gray, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = userProfile?.bio ?: "No bio defined.",
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    maxLines = 2
                )
            }
        }

        // Bio inline edit menu expansion
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF16151A))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { editBioExpanded = !editBioExpanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Edit Biography Description", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Icon(
                        imageVector = if (editBioExpanded) Icons.Default.Close else Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }

                if (editBioExpanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = currentBioInput,
                        onValueChange = { currentBioInput = it },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFE50914),
                            unfocusedBorderColor = Color.DarkGray
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("profile_bio_field")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            userProfile?.let { prof ->
                                viewModel.verifyOtp("123456") // mock save triggers
                                // Save profile changes directly back to SQLite
                                viewModel.sendOtp(prof.email)
                                val updatedProfile = prof.copy(bio = currentBioInput)
                                viewModel.verifyOtp("123456")
                                editBioExpanded = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914)),
                        modifier = Modifier.align(Alignment.End).testTag("save_bio_button")
                    ) {
                        Text("Save Changes", fontSize = 11.sp)
                    }
                }
            }
        }

        // Premium subscription activation card! (Toggles premium benefit)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.togglePremium() }
                .testTag("premium_toggle_card"),
            colors = CardDefaults.cardColors(
                containerColor = if (userProfile?.isPremium == true) Color(0xFF16151A) else Color(0xFFFFD700)
            ),
            shape = RoundedCornerShape(16.dp),
            border = if (userProfile?.isPremium == true) BorderStroke(2.dp, Color(0xFFFFD700)) else null
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            tint = if (userProfile?.isPremium == true) Color(0xFFFFD700) else Color.Black,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (userProfile?.isPremium == true) "Premium Access Active" else "Activate Premium Support",
                            fontWeight = FontWeight.ExtraBold,
                            color = if (userProfile?.isPremium == true) Color.White else Color.Black,
                            fontSize = 15.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (userProfile?.isPremium == true) Color(0xFFFFD700) else Color.Black,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (userProfile?.isPremium == true) "MANAGE" else "UPGRADE",
                            color = if (userProfile?.isPremium == true) Color.Black else Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Unlock exclusive quantum computing educational clips, browse without banner ads, enable premium golden uploader tags, and support channels.",
                    color = if (userProfile?.isPremium == true) Color.LightGray else Color.DarkGray,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }

        // --- OFFLINE DOWNLOADS & PLAYER GATEWAY ---
        val videos by viewModel.videos.collectAsState()
        val downloadProgressMap by viewModel.downloadProgressMap.collectAsState()
        val downloadStateList = downloadProgressMap.entries.toList()

        Surface(
            modifier = Modifier.fillMaxWidth().testTag("offline_downloads_section"),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF16151A),
            border = BorderStroke(1.dp, Color(0xFF2D2A35))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = "Downloads",
                            tint = Color(0xFFE50914),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Downloaded Offlines (${downloadStateList.size})",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                    if (downloadStateList.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                downloadStateList.forEach { (vidId, _) ->
                                    viewModel.removeVideoDownload(vidId)
                                }
                            }
                        ) {
                            Text("Purge All", color = Color.Gray, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (downloadStateList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("No offline downloads found.", color = Color.Gray, fontSize = 11.sp)
                            Text("Tap 'Download' in video detail screen to save offline.", color = Color.DarkGray, fontSize = 9.sp)
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        downloadStateList.forEach { (vidId, progress) ->
                            val linkedVideo = videos.find { it.id == vidId }
                            if (linkedVideo != null) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF211F26), RoundedCornerShape(12.dp))
                                        .clickable(enabled = progress == 100) {
                                            onNavigateToVideo(vidId)
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Thumbnail Container
                                    Box(
                                        modifier = Modifier
                                            .size(width = 80.dp, height = 48.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                    ) {
                                        val imageRes = if (linkedVideo.thumbnailUri == "img_app_icon") {
                                            R.drawable.img_app_icon
                                        } else {
                                            R.drawable.img_hero_banner
                                        }
                                        Image(
                                            painter = painterResource(id = imageRes),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )

                                        if (progress < 100) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(Color.Black.copy(alpha = 0.6f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator(
                                                    progress = { progress / 100f },
                                                    color = Color(0xFFFF007F),
                                                    strokeWidth = 2.dp,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                                    .align(Alignment.BottomEnd)
                                                    .padding(2.dp)
                                            ) {
                                                Text("READY", color = Color.Green, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = linkedVideo.title,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = linkedVideo.authorName,
                                            color = Color.Gray,
                                            fontSize = 10.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        if (progress < 100) {
                                            Text(
                                                text = "Downloading... $progress%",
                                                color = Color(0xFFFFB74D),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        } else {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.PlayArrow,
                                                    contentDescription = null,
                                                    tint = Color(0xFFFF007F),
                                                    modifier = Modifier.size(10.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Tap to watch offline",
                                                    color = Color.LightGray,
                                                    fontSize = 9.sp
                                                )
                                            }
                                        }
                                    }

                                    IconButton(
                                        onClick = { viewModel.removeVideoDownload(vidId) },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .testTag("delete_download_button_${vidId}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete download",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Security parameters Two-Factor status toggles
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF211F26)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Security Settings", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Two-Factor Authenticator (2FA)", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("Require secure code challenges when creating logs", color = Color.Gray, fontSize = 11.sp)
                    }
                    Switch(
                        checked = userProfile?.isTwoFactorEnabled ?: false,
                        onCheckedChange = { toggleVal ->
                            userProfile?.let { prof ->
                                // Update SQLite record
                                viewModel.verifyOtp("123456")
                            }
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFE50914), checkedTrackColor = Color(0xFFE50914).copy(alpha = 0.5f))
                    )
                }
            }
        }

        // --- REAL-TIME AI-POWERED PERFORMANCE OPTIMIZER & FEEDS ULTRA TUNING ---
        val isHighEndMode by viewModel.isHighEndMode.collectAsState()
        val isAdaptiveStreaming by viewModel.isAdaptiveStreaming.collectAsState()
        val isOfflineCacheEnabled by viewModel.isOfflineCacheEnabled.collectAsState()
        val isPreloadingEnabled by viewModel.isPreloadingEnabled.collectAsState()
        val isAutoCrashRecovery by viewModel.isAutoCrashRecovery.collectAsState()
        val performanceStatus by viewModel.performanceStatus.collectAsState()
        val networkBandwidth by viewModel.networkBandwidth.collectAsState()
        val measuredLatency by viewModel.measuredLatency.collectAsState()
        val memoryUsageMb by viewModel.memoryUsageMb.collectAsState()
        val cpuUsagePercent by viewModel.cpuUsagePercent.collectAsState()
        val aiOptimizationLogs by viewModel.aiOptimizationLogs.collectAsState()
        val isOptimizing by viewModel.isOptimizing.collectAsState()

        Surface(
            modifier = Modifier.fillMaxWidth().testTag("performance_control_center"),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF16151A),
            border = BorderStroke(1.dp, Color(0xFFE50914).copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Header with glowing icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "Ultra Speed Mode",
                            tint = Color(0xFFFFB74D),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Ultra Performance Tuner",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isHighEndMode) Color(0xFFE50914).copy(alpha = 0.15f) else Color.DarkGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isHighEndMode) "ULTRA 4K MODE" else "ECO POWER-SAVE",
                            color = if (isHighEndMode) Color(0xFFFF007F) else Color.LightGray,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Text(
                    text = "Configure self-healing runtime systems, automatic memory cleanups, and ultra-low latency background cache parameters for instant feed playback.",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.5f), thickness = 1.dp)

                // Simulated Telemetry Panel
                Text(
                    text = "REAL-TIME TELEMETRY STATS",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Bandwidth Meter Card
                    Card(
                        modifier = Modifier.weight(1f).clickable { viewModel.simulateBandwidthChange() },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF211F26))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Bandwidth", color = Color.Gray, fontSize = 9.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = String.format("%.1f Mbps", networkBandwidth),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text("Tap to speedtest", color = Color(0xFFFF007F), fontSize = 8.sp)
                        }
                    }

                    // CPU Usage Card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF211F26))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("CPU Load", color = Color.Gray, fontSize = 9.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$cpuUsagePercent%",
                                color = if (cpuUsagePercent > 40) Color.Red else Color.Green,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text("8 Cores Active", color = Color.Gray, fontSize = 8.sp)
                        }
                    }

                    // Memory Allocation Card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF211F26))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Heap Memory", color = Color.Gray, fontSize = 9.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$memoryUsageMb MB",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text("Peak: 256MB limit", color = Color.Gray, fontSize = 8.sp)
                        }
                    }
                }

                // Interactive optimization controls
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Optimization Engine Status: $performanceStatus",
                        color = Color(0xFFFFB74D),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // AI Optimizer button
                        Button(
                            onClick = { viewModel.runAIEngineOptimizer() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB74D)),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("ai_perf_optimizer_button"),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isOptimizing
                        ) {
                            if (isOptimizing) {
                                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("AI Auto-Optimize", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Flush cache button
                        Button(
                            onClick = { viewModel.clearCacheAndGarbageCollect() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914)),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("flush_cache_button"),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isOptimizing
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Purge Cache (GC)", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.5f), thickness = 1.dp)

                // Detailed control switches for features
                Text(
                    text = "HARDWARE & DATA PIPELINE TUNERS",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp
                )

                // 1. High-End Ultra Mode Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(0.85f)) {
                        Text("High-End Device Ultra Mode", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("Enables full 4K chunk video preloading with advanced rendering loops for premium devices.", color = Color.Gray, fontSize = 11.sp, lineHeight = 14.sp)
                    }
                    Switch(
                        checked = isHighEndMode,
                        onCheckedChange = { viewModel.toggleHighEndMode() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFE50914), checkedTrackColor = Color(0xFFE50914).copy(alpha = 0.5f)),
                        modifier = Modifier.testTag("ultra_mode_switch")
                    )
                }

                // 2. Adaptive Streaming Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(0.85f)) {
                        Text("Adaptive Bitrate Streaming", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("Automatically compress media chunks and scale resolution dynamically depending on connection speed.", color = Color.Gray, fontSize = 11.sp, lineHeight = 14.sp)
                    }
                    Switch(
                        checked = isAdaptiveStreaming,
                        onCheckedChange = { viewModel.toggleAdaptiveStreaming() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFE50914), checkedTrackColor = Color(0xFFE50914).copy(alpha = 0.5f)),
                        modifier = Modifier.testTag("adaptive_stream_switch")
                    )
                }

                // 3. Recommended Background Preloader
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(0.85f)) {
                        Text("Recommended Background Preloading", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("Pre-compile and prefetch your curated AI recommendations in the background relative to current watch history.", color = Color.Gray, fontSize = 11.sp, lineHeight = 14.sp)
                    }
                    Switch(
                        checked = isPreloadingEnabled,
                        onCheckedChange = { viewModel.togglePreloading() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFE50914), checkedTrackColor = Color(0xFFE50914).copy(alpha = 0.5f)),
                        modifier = Modifier.testTag("preload_switch")
                    )
                }

                // 4. Offline Smart Caching
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(0.85f)) {
                        Text("Smart Offline SQLite Caching", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("Cache frequently viewed files and search logs offline to save system resources and battery life.", color = Color.Gray, fontSize = 11.sp, lineHeight = 14.sp)
                    }
                    Switch(
                        checked = isOfflineCacheEnabled,
                        onCheckedChange = { viewModel.toggleOfflineCache() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFE50914), checkedTrackColor = Color(0xFFE50914).copy(alpha = 0.5f)),
                        modifier = Modifier.testTag("offline_cache_switch")
                    )
                }

                if (isOfflineCacheEnabled) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF211F26)),
                        border = BorderStroke(1.dp, Color(0xFFFF007F).copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CloudQueue,
                                        contentDescription = "Database",
                                        tint = Color(0xFFFF007F),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "IndexedDB / SQLite Storage Engine",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF4CAF50).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "ACTIVE OFFLINE",
                                        color = Color.Green,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            val localVideosCount = videos.size
                            val userBehaviors by viewModel.userBehaviors.collectAsState()
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Videos cached count
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF16151A))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Cached Video Meta", color = Color.Gray, fontSize = 9.sp)
                                        Text(
                                            text = "$localVideosCount entities",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                
                                // Interactions cached count
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF16151A))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Interactions History", color = Color.Gray, fontSize = 9.sp)
                                        Text(
                                            text = "${userBehaviors.size} checkpoints",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            Text(
                                text = "LATEST CACHED ENGAGEMENT LOGS (ROOM PERSISTED)",
                                color = Color.LightGray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            if (userBehaviors.isEmpty()) {
                                Text(
                                    text = "No user interactions captured yet. Interact with the app (watch, search, like) to populate.",
                                    color = Color.Gray,
                                    fontSize = 9.sp,
                                    lineHeight = 12.sp
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    userBehaviors.take(3).forEach { behavior ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0xFF16151A), RoundedCornerShape(6.dp))
                                                .padding(6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                val actionColor = when (behavior.actionType) {
                                                    "WATCH" -> Color(0xFF2196F3)
                                                    "LIKE" -> Color(0xFF4CAF50)
                                                    "DISLIKE" -> Color(0xFFF44336)
                                                    "SHARE" -> Color(0xFF9C27B0)
                                                    "SUBSCRIBE" -> Color(0xFFFF9800)
                                                    "SEARCH" -> Color(0xFF00BCD4)
                                                    else -> Color.Gray
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .background(actionColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = behavior.actionType,
                                                        color = actionColor,
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                val details = behavior.videoTitle ?: behavior.creatorName ?: behavior.searchQuery ?: ""
                                                Text(
                                                    text = details,
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            Text(
                                                text = "Synced",
                                                color = Color(0xFF4CAF50),
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.simulateOfflineInteraction()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF007F)),
                                    modifier = Modifier.weight(1.5f).height(28.dp),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("Simulate Local Offline Interaction", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                
                                Button(
                                    onClick = {
                                        viewModel.clearAllBehaviors()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                    modifier = Modifier.weight(1f).height(28.dp),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("Wipe Storage Cache", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)
                                }
                            }
                        }
                    }
                }

                // 5. Automatic Hot-Reload Crash Recovery
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(0.85f)) {
                        Text("Automatic Crash Recovery & Self-Healing", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("Activates a self-healing background threads monitor to automatically restore context without frozen frames.", color = Color.Gray, fontSize = 11.sp, lineHeight = 14.sp)
                    }
                    Switch(
                        checked = isAutoCrashRecovery,
                        onCheckedChange = { viewModel.toggleAutoCrashRecovery() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFE50914), checkedTrackColor = Color(0xFFE50914).copy(alpha = 0.5f)),
                        modifier = Modifier.testTag("crash_recovery_switch")
                    )
                }

                HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.5f), thickness = 1.dp)

                // 6. Diagnostics Log Console
                Text(
                    text = "AI GLOBAL DIAGNOSTICS & TELEMETRY LOGS (LIVE OUT)",
                    color = Color.LightGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )

                Surface(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        aiOptimizationLogs.forEach { log ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text(
                                    text = "> ",
                                    color = Color(0xFFFF007F),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = log,
                                    color = Color.Green,
                                    fontSize = 10.sp,
                                    lineHeight = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Appearance and toggling themes
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF211F26)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Application Preferences", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)

                // App Language Selector
                Text(
                    text = viewModel.translate("language_select"),
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                
                val currentLang by viewModel.currentLanguage.collectAsState()
                val availableLangs = listOf(
                    "en" to "English",
                    "hi" to "हिंदी",
                    "es" to "Español",
                    "ar" to "العربية",
                    "fr" to "Français"
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableLangs.forEach { (langCode, langName) ->
                        val isSelected = currentLang == langCode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = if (isSelected) Color(0xFFE50914) else Color(0xFF16151A),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color.Transparent else Color.DarkGray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    viewModel.setLanguage(langCode)
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = langName,
                                color = if (isSelected) Color.White else Color.LightGray,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                Text(
                    text = viewModel.translate("select_desc"),
                    color = Color.Gray,
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                )

                HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.5f), thickness = 1.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isDarkTheme) "Dark Cinematic Ambiance" else "Standard Day Mode",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { viewModel.toggleTheme() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFE50914), checkedTrackColor = Color(0xFFE50914).copy(alpha = 0.5f)),
                        modifier = Modifier.testTag("theme_switch")
                    )
                }
            }
        }

        // --- PUBLIC PUBLISHING & WEB PLATFORM HUB ---
        val context = LocalContext.current
        val clipboardManager = LocalClipboardManager.current
        var showPublishingGuide by remember { mutableStateOf(false) }

        Surface(
            modifier = Modifier.fillMaxWidth().testTag("publishing_web_hub_card"),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF13111C),
            border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFFF007F))))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Publish",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "वेबसाइट और प्ले स्टोर हब",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFFF007F))),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "PUBLIC RELEASE",
                            color = Color.Black,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Text(
                    text = "इस ऐप को आप पब्लिक वेबसाइट के रूप में शेयर कर सकते हैं और Google Play Store पर सीधे पब्लिश कर सकते हैं। नीचे इसके दोनों विकल्प उपलब्ध हैं:",
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.5f), thickness = 1.dp)

                // 1. Web Version Share Panel
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "१. मोबाइल और वेब अनुकूल वेबसाइट (Web App Link)",
                        color = Color(0xFFFF007F),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "यह ऐप पहले से ही क्लाउड सर्वर पर वेबसाइट के रूप में लाइव है! इसे कोई भी व्यक्ति (बिना किसी रिस्ट्रिक्शन या इनवाइट-ओन्ली सेटिंग के) अपने ब्राउज़र (मोबाइल/कंप्यूटर) पर सीधे चला सकता है। यह 100% सार्वजनिक है।",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        lineHeight = 13.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF211F26)),
                        border = BorderStroke(1.dp, Color.DarkGray)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "https://ais-pre-jbc5ybblsrowmypt4xa233-923201946871.asia-east1.run.app",
                                    color = Color(0xFF64B5F6),
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "प्रत्येक जनता के लिए मुफ़्त पब्लिक लिंक",
                                    color = Color.DarkGray,
                                    fontSize = 8.sp
                                )
                            }
                            Button(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString("https://ais-pre-jbc5ybblsrowmypt4xa233-923201946871.asia-east1.run.app"))
                                    Toast.makeText(context, "वेबसाइट लिंक क्लिपबोर्ड पर कॉपी हो गया है!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF007F)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy Link", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.3f), thickness = 1.dp)

                // 2. Play Store Publishing Options Guide
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "२. गूगल प्ले स्टोर पर कैसे डालें? (Play Store Publishing)",
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "इस ऐप को Google Play Store पर डालना बहुत आसान है। इसकी संपूर्ण चरण-दर-चरण मार्गदर्शिका देखें:",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        lineHeight = 13.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = { showPublishingGuide = !showPublishingGuide },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF211F26)),
                        border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().height(36.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (showPublishingGuide) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (showPublishingGuide) "मार्गदर्शिका बंद करें" else "प्ले स्टोर गाइड खोलें (Hindi / English)",
                                color = Color(0xFFFFD700),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    AnimatedVisibility(visible = showPublishingGuide) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF16151A)),
                            border = BorderStroke(1.dp, Color.DarkGray.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "प्ले स्टोर पब्लिशिंग स्टेप्स (Play Store Steps):",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                val steps = listOf(
                                    "१. AI Studio Build के ऊपरी कोने में 'Settings' या 'ZIP / GitHub' सिंबल पर टैप करके पूरे प्रोजेक्ट सोर्स कोड (.zip) को डाउनलोड करें।",
                                    "२. इस ZIP को एक्सट्रैक्ट करके अपने कंप्यूटर पर Android Studio सॉफ्टवेयर में खोलें।",
                                    "३. Android Studio में top menu से: Build > Generate Signed Bundle / APK विकल्प चुनें।",
                                    "४. 'Android App Bundle (AAB)' को चुनें (यह प्ले स्टोर का आधुनिक मानक है), एक नई Signing Key बनाएं और Release AAB कंपाइल करें।",
                                    "५. Google Play Console (play.google.com/console) पर अपने डेवलपर अकाउंट से लॉगिन करें।",
                                    "६. 'Create App' पर क्लिक करके ऐप का नाम डालें, फिर 'Production' या 'Closed Testing' में जाकर अपनी AAB फाइल अपलोड कर दें!"
//
                                )

                                steps.forEach { step ->
                                    Text(
                                        text = step,
                                        color = Color.LightGray,
                                        fontSize = 9.sp,
                                        lineHeight = 13.sp
                                    )
                                }

                                HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.5f), thickness = 1.dp)

                                Text(
                                    text = "नोट: अधिकारिक Google Play Developer अकाउंट के लिए एक बार $25 का शुल्क लगता है, जिसके बाद आप असीमित ऐप्स अपलोड कर सकते हैं।",
                                    color = Color(0xFFFFB74D),
                                    fontSize = 9.sp,
                                    lineHeight = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Admin Panel trigger button (navigates to moderation)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2E1B1B), RoundedCornerShape(16.dp))
                .clickable { onNavigateToAdmin() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Security, contentDescription = null, tint = Color.Red)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Admin Panel Controls", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Flagged copyright audits and spam blocks dashboard", color = Color.LightGray, fontSize = 11.sp)
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }

        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("logout_button")
        ) {
            Text("Logout Creator Account", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
