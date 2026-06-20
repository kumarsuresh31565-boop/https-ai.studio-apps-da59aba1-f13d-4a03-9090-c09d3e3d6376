package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LiveStreamScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val liveChatMessages by viewModel.liveChatMessages.collectAsState()
    val liveViewerCount by viewModel.liveViewerCount.collectAsState()
    val totalDonations by viewModel.liveDonationAmount.collectAsState()

    var userMessageInput by remember { mutableStateOf("") }
    var celebrationVisible by remember { mutableStateOf(false) }
    var celebratoryDonor by remember { mutableStateOf("") }
    var celebratoryAmount by remember { mutableDoubleStateOf(0.0) }

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Trigger recurring updates on entering
    LaunchedEffect(Unit) {
        viewModel.startLiveStreamInteractions()
    }

    // Auto scroll chat list to bottom as new comments arrive
    LaunchedEffect(liveChatMessages.size) {
        if (liveChatMessages.isNotEmpty()) {
            listState.animateScrollToItem(liveChatMessages.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("live_stream_container")
    ) {
        // Live Camera broadcast placeholder
        Image(
            painter = painterResource(id = R.drawable.img_hero_banner),
            contentDescription = "Live Broadcaster view",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Vignette dimmer overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.4f),
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Top Status Overlays (Back, Live stamp, viewers counts, donor amount)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Exit broadcast", tint = Color.White)
                }

                Box(
                    modifier = Modifier
                        .background(Color.Red, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("LIVE FEED", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Group, contentDescription = null, tint = Color.Green, modifier = Modifier.size(16.dp))
                Text("$liveViewerCount viewers", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Live Chat scrolling Panel + Input Box + Superchats row at bottom half
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Live superchat banner indicator
            if (totalDonations > 0) {
                Surface(
                    color = Color(0xFFFFD700).copy(alpha = 0.85f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Live Stream Superchat Donations: $$totalDonations",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Scrolling Chat column
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(liveChatMessages) { msg ->
                    val isSuperChat = msg.first.startsWith("👑")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (isSuperChat) Color(0xFFFFD700).copy(alpha = 0.15f) else Color.Transparent,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "${msg.first}: ",
                            color = if (isSuperChat) Color(0xFFFFD700) else Color(0xFFE50914),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = msg.second,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Donation bar selector (Donations instantly credit Creator Account studio earnings)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Support stream:", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                val amounts = listOf(5.0, 10.0, 20.0)
                amounts.forEach { amt ->
                    Text(
                        text = "$$amt",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .background(Color(0xFFFFD700), RoundedCornerShape(12.dp))
                            .clickable {
                                viewModel.makeLiveDonation("CreativeFan", amt)
                                celebratoryDonor = "CreativeFan"
                                celebratoryAmount = amt
                                celebrationVisible = true
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("superchat_btn_${amt.toInt()}")
                    )
                }
            }

            // Chat typing row input box
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = userMessageInput,
                    onValueChange = { userMessageInput = it },
                    placeholder = { Text("Chat publicly...", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFE50914),
                        unfocusedBorderColor = Color.DarkGray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("live_chat_field"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (userMessageInput.isNotEmpty()) {
                            viewModel.makeLiveDonation("Me", 0.0) // regular comment
                            userMessageInput = ""
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFE50914))
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send Chat", tint = Color.White)
                }
            }
        }

        // Celebration SUPERCHAT modal overlay dialog
        if (celebrationVisible) {
            LaunchedEffect(Unit) {
                delay(3000)
                celebrationVisible = false
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .width(280.dp)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1D1B26)),
                    border = BorderStroke(2.dp, Color(0xFFFFD700)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFD700)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red, modifier = Modifier.size(28.dp))
                        }
                        Text("Superchat Received!", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.White)
                        Text(
                            "$$celebratoryAmount donated by $celebratoryDonor!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                        HorizontalDivider(color = Color.DarkGray)
                        Text(
                            "Funds will be added immediately to your Creator Account's revenue logs.",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}
