package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.example.ui.screens.*
import com.example.ui.viewmodel.MainViewModel

sealed class Tab(val title: String, val testTag: String) {
    object Home : Tab("Home", "tab_home")
    object Shorts : Tab("Shorts", "tab_shorts")
    object Upload : Tab("Create", "tab_upload")
    object Studio : Tab("Studio", "tab_studio")
    object Profile : Tab("Profile", "tab_profile")
}

@Composable
fun AppNavigation(
    viewModel: MainViewModel
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    var currentTab by remember { mutableStateOf<Tab>(Tab.Home) }

    // Immersive Overlay Screens State
    var activeVideoIdForDetail by remember { mutableStateOf<Long?>(null) }
    var isLiveSessionActive by remember { mutableStateOf(false) }
    var isAdminPanelActive by remember { mutableStateOf(false) }
    var isSearchingActive by remember { mutableStateOf(false) }

    if (!isLoggedIn) {
        AuthScreen(
            viewModel = viewModel,
            onLoginSuccess = {
                // Done
            }
        )
    } else {
        // Logged-in Core App Scaffold
        Scaffold(
            bottomBar = {
                // Custom Modern M3 NavigationBar in Elegant Dark Style
                NavigationBar(
                    containerColor = Color(0xFF0F0F0F),
                    contentColor = Color(0xFFE6E1E5),
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("app_bottom_nav_bar")
                ) {
                    val items = listOf(Tab.Home, Tab.Shorts, Tab.Upload, Tab.Studio, Tab.Profile)
                    items.forEach { item ->
                        val isSelected = currentTab == item
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = item },
                            icon = {
                                when (item) {
                                    Tab.Home -> Icon(
                                        imageVector = if (isSelected) Icons.Filled.Home else Icons.Outlined.Home,
                                        contentDescription = "Home"
                                    )
                                    Tab.Shorts -> Icon(
                                        imageVector = if (isSelected) Icons.Filled.PlayArrow else Icons.Outlined.PlayArrow,
                                        contentDescription = "Shorts"
                                    )
                                    Tab.Upload -> Icon(
                                        imageVector = if (isSelected) Icons.Filled.AddCircle else Icons.Outlined.AddCircle,
                                        contentDescription = "Create"
                                    )
                                    Tab.Studio -> Icon(
                                        imageVector = if (isSelected) Icons.Filled.Analytics else Icons.Outlined.Analytics,
                                        contentDescription = "Studio"
                                    )
                                    Tab.Profile -> Icon(
                                        imageVector = if (isSelected) Icons.Filled.AccountCircle else Icons.Outlined.AccountCircle,
                                        contentDescription = "Profile"
                                    )
                                }
                            },
                            label = { 
                                val translationKey = when (item) {
                                    Tab.Home -> "home"
                                    Tab.Shorts -> "shorts"
                                    Tab.Upload -> "upload"
                                    Tab.Studio -> "studio"
                                    Tab.Profile -> "profile"
                                }
                                Text(viewModel.translate(translationKey), fontSize = 10.sp) 
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                indicatorColor = Color(0xFF211F26),
                                unselectedIconColor = Color(0xFF938F99),
                                unselectedTextColor = Color(0xFF938F99)
                            ),
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            },
            containerColor = Color(0xFF0F0F0F)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Crossfade animation between bottom nav tabs
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                    },
                    label = "tab_crossfade"
                ) { targetTab ->
                    when (targetTab) {
                        Tab.Home -> HomeScreen(
                            viewModel = viewModel,
                            onNavigateToVideo = { videoId ->
                                activeVideoIdForDetail = videoId
                            },
                            onNavigateToLive = {
                                isLiveSessionActive = true
                            },
                            onNavigateToSearch = {
                                isSearchingActive = true
                            }
                        )
                        Tab.Shorts -> ShortsScreen(viewModel = viewModel)
                        Tab.Upload -> UploadScreen(
                            viewModel = viewModel,
                            onUploadSuccess = {
                                currentTab = Tab.Studio // route creators back to content cabinet on uploads finished!
                            }
                        )
                        Tab.Studio -> CreatorStudioScreen(viewModel = viewModel)
                        Tab.Profile -> ProfileScreen(
                            viewModel = viewModel,
                            onNavigateToAdmin = {
                                isAdminPanelActive = true
                            },
                            onLogout = {
                                viewModel.logout()
                            },
                            onNavigateToVideo = { videoId ->
                                activeVideoIdForDetail = videoId
                            }
                        )
                    }
                }

                // Immersive full-screen overlay for Video Details (cinema player experience)
                activeVideoIdForDetail?.let { videoId ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                    ) {
                        VideoDetailScreen(
                            videoId = videoId,
                            viewModel = viewModel,
                            onBack = { activeVideoIdForDetail = null }
                        )
                    }
                }

                // Immersive full-screen overlay for Voice and Smart Search Engine
                if (isSearchingActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                    ) {
                        SearchScreen(
                            viewModel = viewModel,
                            onNavigateToVideo = { videoId ->
                                isSearchingActive = false
                                activeVideoIdForDetail = videoId
                            },
                            onBack = { isSearchingActive = false }
                        )
                    }
                }

                // Immersive full-screen overlay for simulated Live Streams
                if (isLiveSessionActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                    ) {
                        LiveStreamScreen(
                            viewModel = viewModel,
                            onBack = { isLiveSessionActive = false }
                        )
                    }
                }

                // Immersive full-screen overlay for Admin Copyright and Moderation Panels
                if (isAdminPanelActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                    ) {
                        AdminPanelScreen(
                            viewModel = viewModel,
                            onBack = { isAdminPanelActive = false }
                        )
                    }
                }
            }
        }
    }
}
