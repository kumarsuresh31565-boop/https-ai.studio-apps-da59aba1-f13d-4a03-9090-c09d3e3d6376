package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(
    viewModel: MainViewModel,
    onNavigateToVideo: (Long) -> Unit,
    onBack: () -> Unit
) {
    var queryText by remember { mutableStateOf("") }
    val videos by viewModel.videos.collectAsState()

    // Log searches using a debounced LaunchedEffect
    LaunchedEffect(queryText) {
        if (queryText.isNotBlank()) {
            delay(1000)
            viewModel.logSearch(queryText)
        }
    }

    // Simulated Voice Search state
    var isListeningVoice by remember { mutableStateOf(false) }
    var voiceStepText by remember { mutableStateOf("Listening closely...") }
    val voicePhrases = listOf("Nuclear Fusion", "Lofi focus beat", "Quantum Superposition", "Jetpack Compose animations")

    val trendingSearches = listOf(
        "Quantum superposition",
        "Lofi synth study session",
        "Jetpack Compose ripple customizer",
        "Gemini 3.5 native streams",
        "Mayan LiDAR discovery"
    )

    val autocompleteSuggestions = if (queryText.isEmpty()) emptyList() else {
        videos.filter { it.title.contains(queryText, ignoreCase = true) }.map { it.title }.take(4)
    }

    val searchResults = if (queryText.isEmpty()) emptyList() else {
        videos.filter {
            it.title.contains(queryText, ignoreCase = true) ||
            it.description.contains(queryText, ignoreCase = true)
        }
    }

    // Voice simulation runner
    if (isListeningVoice) {
        LaunchedEffect(Unit) {
            delay(1500)
            voiceStepText = "Parsing phrase..."
            delay(1000)
            val selectedPhrase = voicePhrases.random()
            voiceStepText = "\"$selectedPhrase\""
            delay(1000)
            queryText = selectedPhrase
            isListeningVoice = false
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF09070F))
                    .padding(horizontal = 8.dp, vertical = 12.dp)
                    .windowInsetsPadding(WindowInsets.safeDrawing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                OutlinedTextField(
                    value = queryText,
                    onValueChange = { queryText = it },
                    placeholder = { Text(viewModel.translate("search_hint"), color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFFF007F),
                        unfocusedBorderColor = Color.DarkGray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        if (queryText.isNotEmpty()) {
                            IconButton(onClick = { queryText = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    isListeningVoice = true
                                    voiceStepText = "Say something..."
                                },
                                modifier = Modifier.testTag("voice_search_trigger")
                            ) {
                                Icon(Icons.Default.Mic, contentDescription = "Simulated Voice Search", tint = Color(0xFFFF007F))
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_text_input")
                )
            }
        },
        containerColor = Color(0xFF09070F)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Render Voice search listener panel overlays
                if (isListeningVoice) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF16112C)),
                            border = BorderStroke(1.dp, Color(0xFFFF007F).copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFF007F)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = voiceStepText,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Acoustic decryption active (Simulation)", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }

                // Suggestions Autocomplete list (if typing but haven't hit enter)
                if (autocompleteSuggestions.isNotEmpty() && searchResults.isEmpty()) {
                    item {
                        Text("Suggested Matches", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 12.sp)
                    }
                    items(autocompleteSuggestions) { sugg ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { queryText = sugg }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(sugg, color = Color.LightGray, fontSize = 14.sp)
                        }
                    }
                }

                // Render Search Results
                if (searchResults.isNotEmpty()) {
                    item {
                        Text("Search Results for \"$queryText\"", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    }
                    items(searchResults) { video ->
                        VideoFeedCard(
                            video = video,
                            onClick = { onNavigateToVideo(video.id) }
                        )
                    }
                }

                // Trending search list (default landing)
                if (queryText.isEmpty()) {
                    item {
                        Text(
                            text = "Hot Trending Searches",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    items(trendingSearches) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { queryText = item }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = Color(0xFFFF007F),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = item,
                                color = Color.LightGray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
