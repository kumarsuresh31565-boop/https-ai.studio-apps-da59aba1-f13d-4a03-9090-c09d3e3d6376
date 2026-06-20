package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.UpdateApprovalState
import com.example.ui.viewmodel.UpdateReport

@Composable
fun AdminPanelScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var activeAdminTab by remember { mutableStateOf(0) } // 0 = Moderation, 1 = AI Autonomous Update Builder

    val videos by viewModel.videos.collectAsState()
    val auditedVideos = videos.take(4)

    // AI Update Manager states
    val simulationDays by viewModel.simulationDays.collectAsState()
    val configuredAdminEmail by viewModel.configuredAdminEmail.collectAsState()
    val isAnalyzingUpdates by viewModel.isAnalyzingUpdates.collectAsState()
    val activeUpdateReport by viewModel.activeUpdateReport.collectAsState()
    val approvalState by viewModel.approvalState.collectAsState()
    val activePlatformFeatures by viewModel.activePlatformFeatures.collectAsState()
    val updateLogs by viewModel.updateLogs.collectAsState()

    var emailInput by remember(configuredAdminEmail) { mutableStateOf(configuredAdminEmail) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF200F21))
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (activeAdminTab == 0) "Copyright Moderation Hub" else "AI Update Management System",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                TabRow(
                    selectedTabIndex = activeAdminTab,
                    containerColor = Color(0xFF140716),
                    contentColor = Color.White
                ) {
                    Tab(
                        selected = activeAdminTab == 0,
                        onClick = { activeAdminTab = 0 },
                        text = { Text("Copyright Audit Hub", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        selectedContentColor = Color(0xFFFF007F),
                        unselectedContentColor = Color.Gray
                    )
                    Tab(
                        selected = activeAdminTab == 1,
                        onClick = { activeAdminTab = 1 },
                        text = { Text("AI Update Manager", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        selectedContentColor = Color(0xFFFF007F),
                        unselectedContentColor = Color.Gray,
                        modifier = Modifier.testTag("ai_update_manager_tab")
                    )
                }
            }
        },
        containerColor = Color(0xFF07040B)
    ) { innerPadding ->
        if (activeAdminTab == 0) {
            // ORIGINAL COPYRIGHT MODERATION HUB
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1C1C))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = Color.Red)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("System Health & Security Summary", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Automatic heuristic scanners are actively scanning uplinks. Videos containing high-risk phrases like 'crack' or 'pirated' are automatically tagged as flagged drafts for copyright containment.",
                                color = Color.LightGray,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF16151A))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Suspended", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 18.sp)
                                Text("5 cases", color = Color.Gray, fontSize = 11.sp)
                            }
                        }
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF16151A))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Pending Audit", color = Color.Yellow, fontWeight = FontWeight.Black, fontSize = 18.sp)
                                Text("2 clips", color = Color.Gray, fontSize = 11.sp)
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Active Video Audit Log (${auditedVideos.size} items)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                items(auditedVideos) { video ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("moderation_row_${video.id}"),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF16151A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(video.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Uploaded by: ${video.authorName}", color = Color.Gray, fontSize = 11.sp)
                                }

                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (video.copyrightCheckStatus == "Pass") Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = video.copyrightCheckStatus,
                                        color = if (video.copyrightCheckStatus == "Pass") Color(0xFF2E7D32) else Color(0xFFC62828),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.flagVideoCopyright(video.id, "Flagged") },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Yellow),
                                    border = BorderStroke(1.dp, Color.Yellow),
                                    modifier = Modifier.weight(1f).testTag("action_flag_${video.id}")
                                ) {
                                    Text("Flag Claim", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.blockVideo(video.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    modifier = Modifier.weight(1f).testTag("action_ban_${video.id}")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Block, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Ban Context", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // NEW AUTONOMOUS UPDATE MANAGEMENT SYSTEM TAB
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. AI Co-Pilot Summary Hub
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B0F2A)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, Color(0xFFFF007F).copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFFF007F))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "AI Autonomous Update Manager",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp
                                    )
                                }
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFF007F).copy(alpha = 0.15f))
                                ) {
                                    Text(
                                        text = "SIMULATOR",
                                        color = Color(0xFFFF007F),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "This platform integrates an offline model analyzing user feeds, server loads, and creator reports continuously to compile upgrade roadmaps automatically every 60 days.",
                                color = Color.LightGray,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("CURRENT SIMULATION TIME", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text("Day $simulationDays Elapsed", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.advanceSimulationDaysAndAnalyze() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF007F)),
                                    modifier = Modifier.testTag("advance_simulation_60_days_button"),
                                    enabled = !isAnalyzingUpdates
                                ) {
                                    if (isAnalyzingUpdates) {
                                        CircularProgressIndicator(color = Color.White, strokeWidth = 1.5.dp, modifier = Modifier.size(14.dp))
                                    } else {
                                        Icon(Icons.Default.Update, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Simulate +60 Days", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Security Sign-off Credentials (Admin email input)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF13101E)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF64B5F6), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Security Sign-off Administrator Credentials", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Text(
                                text = "Major updates, system changes, or account actions require explicit consent and security clearances sent to the configured address below.",
                                color = Color.Gray,
                                fontSize = 10.sp,
                                lineHeight = 14.sp
                            )
                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = {
                                    emailInput = it
                                    viewModel.setConfiguredAdminEmail(it)
                                },
                                placeholder = { Text("administrator@example.com", color = Color.DarkGray) },
                                label = { Text("Administrator Sign-off Email", fontSize = 10.sp) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFFFF007F),
                                    unfocusedBorderColor = Color.DarkGray,
                                    focusedContainerColor = Color(0xFF090610),
                                    unfocusedContainerColor = Color(0xFF090610)
                                ),
                                textStyle = LocalTextStyle.current.copy(fontSize = 11.sp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_dispatch_email_input")
                            )
                        }
                    }
                }

                // 3. Deployed Modern Platform Engines
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C130E)),
                        border = BorderStroke(1.dp, Color(0xFF81C784).copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Active Platform Engines", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            activePlatformFeatures.forEach { feature ->
                                Row(
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(5.dp).background(Color(0xFF81C784), RoundedCornerShape(2.5.dp)))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(feature, color = Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }

                // 4. Staged Update Report
                activeUpdateReport?.let { report ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131118)),
                            border = BorderStroke(1.5.dp, Color(0xFFFF007F).copy(alpha = 0.7f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                // Header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFFFFB74D), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Upgrade Report V${report.dayOfSimulation / 60}.0",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .background(
                                                when (approvalState) {
                                                    UpdateApprovalState.PENDING_APPROVAL -> Color(0xFFFFE0B2)
                                                    UpdateApprovalState.APPROVED -> Color(0xFFE8F5E9)
                                                    UpdateApprovalState.REJECTED -> Color(0xFFFFEBEE)
                                                    UpdateApprovalState.DEPLOYED -> Color(0xFFE3F2FD)
                                                    else -> Color.DarkGray
                                                },
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = when (approvalState) {
                                                UpdateApprovalState.PENDING_APPROVAL -> "PENDING APPROVAL"
                                                UpdateApprovalState.APPROVED -> "APPROVED"
                                                UpdateApprovalState.REJECTED -> "REJECTED"
                                                UpdateApprovalState.DEPLOYED -> "DEPLOYED LIVE"
                                                else -> "IDLE"
                                            },
                                            color = when (approvalState) {
                                                UpdateApprovalState.PENDING_APPROVAL -> Color(0xFFE65100)
                                                UpdateApprovalState.APPROVED -> Color(0xFF2E7D32)
                                                UpdateApprovalState.REJECTED -> Color(0xFFC62828)
                                                UpdateApprovalState.DEPLOYED -> Color(0xFF1565C0)
                                                else -> Color.White
                                            },
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Divider(color = Color.DarkGray.copy(alpha = 0.5f))

                                // AI Responsibilities Summary Banner
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1D1414)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("⚠️ IMPORTANT POLICY RULE", color = Color(0xFFE57373), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = "AI-CoPilot executes continuous diagnostic planning, roadmapping, and testing. However, major updates, resource adjustments, and service deployments STRICTLY require sign-off authorization from: $configuredAdminEmail",
                                            color = Color.LightGray,
                                            fontSize = 8.sp,
                                            lineHeight = 11.sp
                                        )
                                    }
                                }

                                // Interactive Sections
                                val sections = listOf(
                                    "1. User Feedback Metrics" to report.feedbackAnalysis,
                                    "2. Platform Performance Insights" to report.performanceAnalysis,
                                    "3. Creator Requests & Sentiment" to report.creatorRequestsAnalysis,
                                    "4. Global Video Market Trends" to report.marketTrendsAnalysis,
                                    "5. Proposing New Features" to report.featureProposals,
                                    "6. Proposing UI Architecture Upgrades" to report.uiProposals,
                                    "7. Proposing Security Enforcements" to report.securityProposals,
                                    "8. Proposing Multimodal AI Capabilities" to report.aiProposals,
                                    "9. Interactive Release Notes Draft" to report.releaseNotes,
                                    "10. 60-Day staged Roadmap" to report.roadmap,
                                    "11. Staged Risk Assessment" to report.riskAssessment
                                )

                                sections.forEach { (title, desc) ->
                                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Text(title, color = Color(0xFFFFB74D), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Text(desc, color = Color.LightGray, fontSize = 10.sp, lineHeight = 13.sp)
                                    }
                                }

                                Divider(color = Color.DarkGray.copy(alpha = 0.5f))

                                // Workflow Clearance Actions
                                if (approvalState == UpdateApprovalState.PENDING_APPROVAL) {
                                    Text(
                                        text = "Awaiting verification clearance from platform owner...",
                                        color = Color.Yellow,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { viewModel.rejectUpdate() },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                            border = BorderStroke(1.dp, Color.Red),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("reject_update_button")
                                        ) {
                                            Text("Reject Update", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = { viewModel.approveUpdate() },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("approve_update_button")
                                        ) {
                                            Text("Approve Update", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }

                                if (approvalState == UpdateApprovalState.APPROVED) {
                                    Text(
                                        text = "Update verified by $configuredAdminEmail. Platform ready to write deployment loops.",
                                        color = Color.Green,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Button(
                                        onClick = { viewModel.deployUpdate() },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("deploy_update_button")
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Deploy & Apply Live Features", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                if (approvalState == UpdateApprovalState.REJECTED) {
                                    Text(
                                        text = "Update roadmap rejected by owner. Please advance simulation to trigger next compile trial.",
                                        color = Color.Red,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (approvalState == UpdateApprovalState.DEPLOYED) {
                                    Text(
                                        text = "Release V${report.dayOfSimulation / 60}.0 is fully active and synchronized in memory.",
                                        color = Color.Green,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // 5. System Terminal console Feed logs
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.Black),
                        border = BorderStroke(1.dp, Color.DarkGray)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("CO-PILOT COMPILING CONSOLE LOGS", color = Color.Gray, fontSize = 8.sp, fontWeight = FontWeight.Black)
                            Spacer(modifier = Modifier.height(8.dp))
                            updateLogs.take(15).forEach { log ->
                                Text(
                                    text = log,
                                    color = if (log.contains("SUCCESS") || log.contains("APPROVED")) Color.Green else if (log.contains("REJECT")) Color.Red else Color.LightGray,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 8.2.sp,
                                    lineHeight = 11.sp,
                                    modifier = Modifier.padding(vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
