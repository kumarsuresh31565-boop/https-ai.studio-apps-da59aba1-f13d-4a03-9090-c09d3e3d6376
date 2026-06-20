package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudUpload
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UploadScreen(
    viewModel: MainViewModel,
    onUploadSuccess: () -> Unit
) {
    val aiLoading by viewModel.aiLoading.collectAsState()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // Form inputs
    var titleText by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Tech") }
    var isShort by remember { mutableStateOf(false) }
    var isExclusive by remember { mutableStateOf(false) }
    var isPrivate by remember { mutableStateOf(false) }

    // AI suggestions overlays
    var aiProposalText by remember { mutableStateOf("") }
    var showAiProposal by remember { mutableStateOf(false) }

    // Advanced Scheduling state
    var scheduleDelayMinutes by remember { mutableIntStateOf(0) }

    val categoryOptions = listOf("Tech", "Music", "Gaming", "Comedy")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF09070F))
            .verticalScroll(scrollState)
            .padding(16.dp)
            .padding(bottom = 80.dp), // space for bottom menu
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFF007F).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color(0xFFFF007F))
            }
            Column {
                Text("Video Upload Center", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 20.sp)
                Text("Publish videos, shorts, exclusive content", color = Color.Gray, fontSize = 12.sp)
            }
        }

        HorizontalDivider(color = Color.DarkGray)

        // Title text input
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Video Title", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                
                // AI Title Helper BUTTON
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable {
                            if (titleText.isEmpty()) {
                                titleText = "Next-Gen Multi-Agent Systems"
                            }
                            coroutineScope.launch {
                                val suggest = viewModel.generateAITitle(titleText)
                                aiProposalText = "Gemini Suggestion:\n$suggest"
                                showAiProposal = true
                                titleText = suggest
                            }
                        }
                        .background(Color(0xFF7F00FF).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("ai_title_generator_btn")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFFFB74D), modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI Title Creator", color = Color(0xFFFFB74D), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            OutlinedTextField(
                value = titleText,
                onValueChange = { titleText = it },
                placeholder = { Text("Enter descriptive video title...", color = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFF007F),
                    unfocusedBorderColor = Color.DarkGray
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("upload_title_field")
            )
        }

        // Description text input
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Video Summary / Description", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                
                // AI Description Helper BUTTON
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable {
                            coroutineScope.launch {
                                val suggest = viewModel.generateAIDescription(titleText.ifEmpty { "A new tech framework" })
                                aiProposalText = "Gemini Structured Summary:\n$suggest"
                                showAiProposal = true
                                descriptionText = suggest
                            }
                        }
                        .background(Color(0xFF7F00FF).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("ai_desc_generator_btn")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI Description", color = Color(0xFF81C784), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            OutlinedTextField(
                value = descriptionText,
                onValueChange = { descriptionText = it },
                placeholder = { Text("Outline details, chapters, or tags...", color = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFF007F),
                    unfocusedBorderColor = Color.DarkGray
                ),
                shape = RoundedCornerShape(12.dp),
                minLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("upload_desc_field")
            )
        }

        // Gemini Dialog alerts
        AnimatedVisibility(visible = showAiProposal) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1936)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Green)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Gemini Generation Completed", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        IconButton(onClick = { showAiProposal = false }) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Close", tint = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(aiProposalText, color = Color.LightGray, fontSize = 12.sp, lineHeight = 16.sp)
                }
            }
        }

        // AI Thumbnail Descriptor helper
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    coroutineScope.launch {
                        val suggestion = viewModel.generateAIThumbnailDescription(titleText.ifEmpty { "High-Tech AI Core" })
                        aiProposalText = "Suggested Thumbnail Prompt:\n$suggestion"
                        showAiProposal = true
                    }
                }
                .testTag("ai_thumbnail_generator_btn"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF16112C)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF7F00FF).copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF64B5F6))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Need Image/Thumbnail Brief?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("AI generates highly appealing thumbnail parameters.", color = Color.Gray, fontSize = 11.sp)
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
            }
        }

        // Category selection
        Text("Content Vertical / Category", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categoryOptions.forEach { opt ->
                val isSelected = selectedCategory == opt
                Surface(
                    modifier = Modifier.clickable { selectedCategory = opt },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) Color(0xFFFF007F) else Color(0xFF1D1B26),
                    border = if (isSelected) null else BorderStroke(1.dp, Color.DarkGray)
                ) {
                    Text(
                        text = opt,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Checkbox parameter toggles (Shorts, Premium, Private)
        Text("Publishing Parameters", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1D1B26)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Vertical check
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Vertical Shorts Video", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Optimized for vertically scrollable portrait feeds", color = Color.Gray, fontSize = 11.sp)
                    }
                    Switch(
                        checked = isShort,
                        onCheckedChange = { isShort = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFF007F), checkedTrackColor = Color(0xFFFF007F).copy(alpha = 0.5f)),
                        modifier = Modifier.testTag("shorts_toggle_switch")
                    )
                }

                HorizontalDivider(color = Color.DarkGray)

                // Premium check
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Exclusive Premium Access", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Only unlocked by active premium subscribers", color = Color.Gray, fontSize = 11.sp)
                    }
                    Switch(
                        checked = isExclusive,
                        onCheckedChange = { isExclusive = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFD700), checkedTrackColor = Color(0xFFFFD700).copy(alpha = 0.5f))
                    )
                }

                HorizontalDivider(color = Color.DarkGray)

                // Safe Private Check
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Save as Private Draft", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Saved to secure content cabinet for audit and schedules", color = Color.Gray, fontSize = 11.sp)
                    }
                    Switch(
                        checked = isPrivate,
                        onCheckedChange = { isPrivate = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF7F00FF), checkedTrackColor = Color(0xFF7F00FF).copy(alpha = 0.5f))
                    )
                }
            }
        }

        // Advanced Scheduler Slider selector
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                "Advanced Scheduler: Postponement Time",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 13.sp
            )
            Text(
                text = if (scheduleDelayMinutes == 0) "Immediate Publication (No Delay)" else "Delayed release scheduled in $scheduleDelayMinutes minutes.",
                fontSize = 12.sp,
                color = if (scheduleDelayMinutes == 0) Color.LightGray else Color(0xFFFFB74D)
            )
            Slider(
                value = scheduleDelayMinutes.toFloat(),
                onValueChange = { scheduleDelayMinutes = it.toInt() },
                valueRange = 0f..120f,
                steps = 12,
                colors = SliderDefaults.colors(
                    activeTrackColor = Color(0xFFFF007F),
                    thumbColor = Color(0xFFFF007F)
                ),
                modifier = Modifier.testTag("scheduler_slider")
            )
        }

        // AI Caption auto-generator box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    coroutineScope.launch {
                        val cap = viewModel.generateAICaption(selectedCategory)
                        aiProposalText = "Generated Audio Captions Overview:\n$cap"
                        showAiProposal = true
                    }
                },
            colors = CardDefaults.cardColors(containerColor = Color(0xFF13111C)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Yellow)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Pre-Generate Voice Caption?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Creates transcripts automatically matching categories.", color = Color.Gray, fontSize = 11.sp)
                }
            }
        }

        if (aiLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFFFF007F))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Gemini LLM thinking...", color = Color.LightGray, fontSize = 12.sp)
                }
            }
        }

        Button(
            onClick = {
                if (titleText.trim().isNotEmpty()) {
                    viewModel.uploadVideo(
                        title = titleText.trim(),
                        description = descriptionText.trim().ifEmpty { "High definition stream." },
                        category = selectedCategory,
                        isShort = isShort,
                        isExclusive = isExclusive,
                        isPrivate = isPrivate,
                        scheduleDelayMinutes = scheduleDelayMinutes
                    )
                    onUploadSuccess()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF007F)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("upload_submit_button")
        ) {
            Text("Complete & Publish Video", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
