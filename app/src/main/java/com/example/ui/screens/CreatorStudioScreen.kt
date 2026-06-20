package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VideoItem
import com.example.ui.viewmodel.MainViewModel

@Composable
fun CreatorStudioScreen(
    viewModel: MainViewModel
) {
    val videos by viewModel.videos.collectAsState()
    val drafts by viewModel.drafts.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val myVideos = videos.filter { it.authorName == (userProfile?.name ?: "Suresh Kumar") }
    val totalViews = myVideos.sumOf { it.views } + 120500 // adding baseline mock stats for uploader
    val totalLikes = myVideos.sumOf { it.likes } + 14200

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
            .testTag("creator_studio_list"),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Welcome and uploader name
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Creator Analytics Studio",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Logged in: ${userProfile?.name ?: "Suresh Kumar"}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                Surface(
                    color = Color(0xFFE50914).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFFE50914), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Creator", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        // Earnings and monetization dashboard cards row
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("earnings_card"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF16151A)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF2D2A35))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Account Revenue Balance", color = Color.Gray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "$${"%.2f".format(userProfile?.walletBalance ?: 350.0)}",
                        color = Color(0xFFE50914),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Ad Revenue Rate", color = Color.Gray, fontSize = 10.sp)
                            Text("$124.50", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Premium Share (25%)", color = Color.Gray, fontSize = 10.sp)
                            Text("$225.50", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Live superchat fan cash", color = Color.Gray, fontSize = 10.sp)
                            Text("+$${"%.1f".format(viewModel.liveDonationAmount.collectAsState().value)}", color = Color.Green, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        // Monetization Route & Earnings destination email configuration Card
        item {
            val payoutEmail by viewModel.payoutEmail.collectAsState()
            var textInput by remember { mutableStateOf(payoutEmail) }
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("monetization_route_card"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF16151A)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFFF007F).copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = Color(0xFFFF007F),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = viewModel.translate("payout_title"),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF4CAF50).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = viewModel.translate("active_payout"),
                                color = Color.Green,
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Text(
                        text = viewModel.translate("payout_desc"),
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { 
                            textInput = it
                            viewModel.updatePayoutEmail(it) 
                        },
                        label = { Text(viewModel.translate("payout_email"), color = Color.Gray, fontSize = 11.sp) },
                        placeholder = { Text("e.g. payout@example.com", color = Color.DarkGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFFF007F),
                            unfocusedBorderColor = Color.DarkGray,
                            focusedContainerColor = Color(0xFF0F0F0F),
                            unfocusedContainerColor = Color(0xFF0F0F0F)
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("payout_email_input"),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Direct automated sweep active to payout route: $payoutEmail",
                            color = Color(0xFF4CAF50),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Live stats blocks grid row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF211F26))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(Icons.Default.Visibility, contentDescription = null, tint = Color(0xFF64B5F6))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Total Views", color = Color.Gray, fontSize = 11.sp)
                        Text(formatViews(totalViews), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF211F26))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFFF8A80))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Like count", color = Color.Gray, fontSize = 11.sp)
                        Text(formatViews(totalLikes.toLong()), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF211F26))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF81C784))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Estimate RPM", color = Color.Gray, fontSize = 11.sp)
                        Text("$4.80", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // Watch Time telemetry line curves plotted on canvas
        item {
            Text("Audience Watchtime Trends (Last 7 Days)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF211F26)
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    // Compose Canvas custom chart graph
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 5.dp.toPx()
                        val points = listOf(
                            Offset(0f, size.height * 0.8f),
                            Offset(size.width * 0.15f, size.height * 0.7f),
                            Offset(size.width * 0.35f, size.height * 0.3f),
                            Offset(size.width * 0.55f, size.height * 0.45f),
                            Offset(size.width * 0.75f, size.height * 0.15f),
                            Offset(size.width * 1.0f, size.height * 0.2f)
                        )

                        // Draw background helpers grid lines
                        drawLine(color = Color.DarkGray, start = Offset(0f, size.height * 0.5f), end = Offset(size.width, size.height * 0.5f))
                        drawLine(color = Color.DarkGray, start = Offset(0f, size.height * 0.9f), end = Offset(size.width, size.height * 0.9f))

                        val path = Path().apply {
                            moveTo(points[0].x, points[0].y)
                            for (i in 1 until points.size) {
                                lineTo(points[i].x, points[i].y)
                            }
                        }

                        // Plot continuous glowing path lines
                        drawPath(
                            path = path,
                            color = Color(0xFFE50914),
                            style = Stroke(width = strokeWidth)
                        )

                        // Dot nodes
                        points.forEach { pt ->
                            drawCircle(color = Color.White, radius = 8f, center = pt)
                            drawCircle(color = Color(0xFFE50914), radius = 5f, center = pt)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // Content Management Grid Checklist Header
        item {
            Text(
                "My Publishing Cabinet (${myVideos.size} uploads, ${drafts.size} drafts)",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Active items
        if (myVideos.isEmpty() && drafts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF16151A))
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No content uploaded yet. Use the '+' tab to publish!", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(myVideos) { video ->
                CreatorContentManagementRow(
                    video = video,
                    onDelete = { viewModel.blockVideo(video.id) }
                )
            }
            items(drafts) { draft ->
                CreatorContentManagementRow(
                    video = draft,
                    onDelete = { viewModel.blockVideo(draft.id) }
                )
            }
        }
        
        item { Spacer(modifier = Modifier.height(48.dp)) }
    }
}

@Composable
fun CreatorContentManagementRow(
    video: VideoItem,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16151A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.DarkGray)
                ) {
                    Icon(Icons.Default.VideoCall, contentDescription = null, tint = Color.LightGray, modifier = Modifier.align(Alignment.Center))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        video.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${video.views} views", color = Color.Gray, fontSize = 11.sp)
                        Text("•", color = Color.Gray, fontSize = 11.sp)
                        
                        // COPYRIGHT COMPLIANCE BADGE
                        val badgeBg = when (video.copyrightCheckStatus) {
                            "Pass" -> Color(0xFFE8F5E9)
                            "Checking" -> Color(0xFFFFF3E0)
                            else -> Color(0xFFFFEBEE)
                        }
                        val badgeText = when (video.copyrightCheckStatus) {
                            "Pass" -> Color(0xFF2E7D32)
                            "Checking" -> Color(0xFFEF6C00)
                            else -> Color(0xFFC62828)
                        }
                        Box(
                            modifier = Modifier
                                .background(badgeBg, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text("Copyright: ${video.copyrightCheckStatus}", color = badgeText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Content", tint = Color.Red, modifier = Modifier.size(20.dp))
            }
        }
    }
}
