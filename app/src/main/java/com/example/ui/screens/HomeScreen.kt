package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.VideoItem
import com.example.ui.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToVideo: (Long) -> Unit,
    onNavigateToLive: () -> Unit,
    onNavigateToSearch: () -> Unit
) {
    val videos by viewModel.videos.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val unreadNotifsCount = notifications.size

    val recommendedVideos by viewModel.recommendedVideos.collectAsState()
    val recommendationReason by viewModel.recommendationReason.collectAsState()
    val isRecommendationLoading by viewModel.isRecommendationLoading.collectAsState()
    val userBehaviors by viewModel.userBehaviors.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Tech", "Music", "Gaming", "Comedy")

    // Filtered videos based on category and search text
    val filteredVideos = videos.filter {
        (selectedCategory == "All" || it.category.equals(selectedCategory, ignoreCase = true)) &&
        (searchQuery.isEmpty() || it.title.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
            .testTag("home_feed_list"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // App Header Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE50914)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FlowStream",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onNavigateToSearch,
                        modifier = Modifier.testTag("search_action_button")
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                    }

                    Box {
                        IconButton(onClick = { showNotificationsDialog = true }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                        }
                        if (unreadNotifsCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red)
                                    .align(Alignment.TopEnd)
                                    .padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = unreadNotifsCount.toString(),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (userProfile?.isPremium == true) {
                        Surface(
                            shadowElevation = 4.dp,
                            color = Color(0xFFFFD700),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "PREMIUM",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Live stream banner trigger
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { onNavigateToLive() }
                    .testTag("live_stream_card"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE50914)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("LIVE", color = Color(0xFFE50914), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("AstroTech Cyber Session", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("84 active viewers. Jump into live discussion and donate!", color = Color(0xFFFEE6E9), fontSize = 11.sp)
                        }
                    }
                    Icon(Icons.Default.ArrowForward, contentDescription = "Join Live", tint = Color.White)
                }
            }
        }

        // Search text-field visualizer
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF211F26))
                    .clickable { onNavigateToSearch() }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Search technology, synth music, shorts...", color = Color.Gray, fontSize = 14.sp)
                }
            }
        }

        // Category Rows Selectors
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        modifier = Modifier.clickable { selectedCategory = category },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) Color(0xFFE6E1E5) else Color(0xFF211F26),
                        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFF2D2A35))
                    ) {
                        Text(
                            text = category,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) Color(0xFF1C1B1F) else Color(0xFFE6E1E5),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Featured Hero Slider Block using /drawable/img_hero_banner.jpg
        item {
            Text(
                "Trending Worldwide",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(horizontal = 16.dp)
                    .clickable {
                        // Navigate to detail of second item if possible or first
                        val trendingVideo = videos.firstOrNull { it.category == "Music" }
                        if (trendingVideo != null) onNavigateToVideo(trendingVideo.id)
                    },
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner),
                        contentDescription = "Trending theme banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Dark fade tint
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                )
                            )
                    )

                    // Hero video titles
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFE50914), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("HOT TREND", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Music Studio", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Cyberpunk Coding Beat - Lofi Synth Chill Study Session",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // AI Recommendations smart banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("ai_recommendations_container"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF16151A)),
                border = BorderStroke(1.dp, Color(0xFFFF007F).copy(alpha = 0.4f)) // Neon accent line matching elegant dark theme
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI",
                                tint = Color(0xFFFFB74D),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI Recommended For You", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        // Refresh / Reset button actions
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isRecommendationLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color(0xFFFF007F),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                IconButton(
                                    onClick = { viewModel.refreshAIRecommendations() },
                                    modifier = Modifier.size(24.dp).testTag("refresh_ai_recs_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh Recommendation Model",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = { viewModel.clearAllBehaviors() },
                                    modifier = Modifier.size(24.dp).testTag("reset_recs_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteSweep,
                                        contentDescription = "Clear Interaction profile",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Text(
                        text = recommendationReason,
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    if (userBehaviors.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        // Subtitle indicating log counts
                        Text(
                            text = "Engine analyzing ${userBehaviors.size} interactive engagement points.",
                            color = Color(0xFFFF007F).copy(alpha = 0.8f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Horizontal scrolling list of personalized subcards
                    if (isRecommendationLoading && recommendedVideos.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Re-indexing feed context...", color = Color.Gray, fontSize = 11.sp)
                        }
                    } else if (recommendedVideos.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(recommendedVideos) { video ->
                                Card(
                                    modifier = Modifier
                                        .width(180.dp)
                                        .clickable { onNavigateToVideo(video.id) }
                                        .testTag("recommended_video_card_${video.id}"),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF211F26)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(80.dp)
                                        ) {
                                            val imageRes = if (video.thumbnailUri == "img_app_icon") {
                                                R.drawable.img_app_icon
                                            } else {
                                                R.drawable.img_hero_banner
                                            }
                                            Image(
                                                painter = painterResource(id = imageRes),
                                                contentDescription = video.title,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                                    .align(Alignment.BottomEnd)
                                                    .padding(2.dp)
                                            ) {
                                                Text(video.category, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Column(modifier = Modifier.padding(6.dp)) {
                                            Text(
                                                text = video.title,
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = video.authorName,
                                                color = Color.Gray,
                                                fontSize = 9.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Text("No suggestions context indexed. Play videos to initialize preferences.", color = Color.DarkGray, fontSize = 11.sp)
                    }
                }
            }
        }

        // Render main feed items
        if (filteredVideos.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.VideocamOff, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No matching videos found...", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }
        } else {
            items(filteredVideos) { video ->
                VideoFeedCard(
                    video = video,
                    onClick = { onNavigateToVideo(video.id) }
                )
            }
        }
    }

    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFFE50914))
                    Text("Inbox Alerts & Notices", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (notifications.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No alerts in your inbox right now.", color = Color.Gray, fontSize = 12.sp)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            items(notifications) { notif ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF211F26)),
                                    border = BorderStroke(1.dp, if (notif.isRead) Color.Transparent else Color(0xFFFF007F).copy(alpha = 0.3f))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = notif.title,
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            if (!notif.isRead) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(6.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(0xFFFF007F))
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = notif.description,
                                            color = Color.LightGray,
                                            fontSize = 11.sp,
                                            lineHeight = 14.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = notif.timestamp,
                                            color = Color.Gray,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearNotifications()
                        showNotificationsDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFE50914))
                ) {
                    Text("Clear All As Read", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotificationsDialog = false }) {
                    Text("Dismiss", color = Color.Gray, fontSize = 12.sp)
                }
            },
            containerColor = Color(0xFF16151A),
            textContentColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun VideoFeedCard(video: VideoItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() }
            .testTag("video_feed_card_${video.id}"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16151A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Thumbnail relative container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                // Determine resource or fallback
                val imageRes = if (video.thumbnailUri == "img_app_icon") {
                    R.drawable.img_app_icon
                } else {
                    R.drawable.img_hero_banner
                }
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = video.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Tags overlays
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(video.category, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    if (video.isExclusive) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFFD700), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("EXCLUSIVE", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }

                // Video Duration stamp imitation
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                ) {
                    Text("14:20", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Info rows
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Mock channel icons
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF2D2A35), Color(0xFF16151A))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = video.authorName.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = video.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(video.authorName, color = Color.Gray, fontSize = 11.sp)
                        Text("•", color = Color.DarkGray, fontSize = 11.sp)
                        Text("${formatViews(video.views)} views", color = Color.Gray, fontSize = 11.sp)
                        Text("•", color = Color.DarkGray, fontSize = 11.sp)
                        Text(video.uploadTime, color = Color.Gray, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

fun formatViews(views: Long): String {
    return when {
        views >= 1000000 -> "${"%.1f".format(views / 1000000.0)}M"
        views >= 1000 -> "${views / 1000}K"
        else -> views.toString()
    }
}
