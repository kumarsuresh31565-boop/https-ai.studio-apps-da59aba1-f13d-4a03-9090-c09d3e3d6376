package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CommentItem
import com.example.data.VideoItem
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VideoDetailScreen(
    videoId: Long,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val videos by viewModel.videos.collectAsState()
    val subscriptions by viewModel.subscriptions.collectAsState()
    val downloadProgressMap by viewModel.downloadProgressMap.collectAsState()
    val comments by viewModel.getVideoComments(videoId).collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val video = videos.find { it.id == videoId } ?: return

    val isSubscribed = subscriptions.any { it.creatorName.equals(video.authorName, ignoreCase = true) }
    val downloadProgress = downloadProgressMap[videoId]

    // Player State
    var isPlaying by remember { mutableStateOf(true) }
    var rawPosition by remember { mutableFloatStateOf(0.24f) }
    var currentQuality by remember { mutableStateOf("1080p HD") }
    var currentSpeed by remember { mutableStateOf("1.0x") }
    var showQualityMenu by remember { mutableStateOf(false) }
    var showSpeedMenu by remember { mutableStateOf(false) }
    var subtitlesEnabled by remember { mutableStateOf(true) }
    var isPipMode by remember { mutableStateOf(false) }
    var showBannerAd by remember { mutableStateOf(true) }

    // Comments Input State
    var commentText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // Likes reactive state counter
    var mutableLikes by remember(video.likes) { mutableLongStateOf(video.likes) }
    var hasLiked by remember { mutableStateOf(false) }
    var hasDisliked by remember { mutableStateOf(false) }

    // AI Neuro-Multimodal Lab state variables
    var spatialDepth by remember { mutableFloatStateOf(0.6f) }
    var upscaleFactor by remember { mutableStateOf("1080p") }
    var isNoiseFilterActive by remember { mutableStateOf(true) }
    var isBassBoostActive by remember { mutableStateOf(false) }
    var isHdrActive by remember { mutableStateOf(false) }
    var isUpscalingRunning by remember { mutableStateOf(false) }
    var upscalingStatusText by remember { mutableStateOf("Standby") }

    LaunchedEffect(videoId) {
        viewModel.logWatch(videoId, video.title, video.category)
    }

    val currentLang by viewModel.currentLanguage.collectAsState()
    
    // Subtitle tracker matching video position & language
    val baseSubtitle = when {
        rawPosition < 0.35f -> "This quantum machine processes infinite logic lines concurrently!"
        rawPosition < 0.65f -> "By utilizing cryogenic qubits, we preserve phase coherence."
        rawPosition < 0.90f -> "And that is how we solve cryptography factors in under a minute!"
        else -> "Thank you so much for watch-streaming! Check links inside description."
    }

    val subtitleText = when (currentLang) {
        "hi" -> when {
            rawPosition < 0.35f -> "यह क्वांटम मशीन असीमित लॉजिक लाइनों को एक साथ प्रोसेस करती है!"
            rawPosition < 0.65f -> "क्रायोजेनिक क्यूबिट्स का उपयोग करके, हम फेज कोहेरेंस को बनाए रखते हैं।"
            rawPosition < 0.90f -> "और इस तरह हम एक मिनट से कम समय में क्रिप्टोग्राफी फैक्टर्स हल करते हैं!"
            else -> "वॉच-स्ट्रीमिंग के लिए बहुत-बहुत धन्यवाद! विवरण में दिए गए लिंक देखें।"
        }
        "es" -> when {
            rawPosition < 0.35f -> "¡Esta máquina cuántica procesa infinitas líneas lógicas simultáneamente!"
            rawPosition < 0.65f -> "Al utilizar cúbits criogénicos, preservamos la coherencia de fase."
            rawPosition < 0.90f -> "¡Y así es como resolvemos factores criptográficos en menos de un minuto!"
            else -> "¡Muchas gracias por vernos! Revisa los enlaces en la descripción."
        }
        "fr" -> when {
            rawPosition < 0.35f -> "Cette machine quantique traite des lignes logiques infinies simultanément!"
            rawPosition < 0.65f -> "En utilisant des qubits cryogéniques, nous préservons la cohérence de phase."
            rawPosition < 0.90f -> "Et voilà comment nous résolvons les facteurs cryptographiques en moins d'une minute!"
            else -> "Merci beaucoup d'avoir regardé! Consultez les liens dans la description."
        }
        "ar" -> when {
            rawPosition < 0.35f -> "تقوم هذه الآلة الكمومية بمعالجة خطوط منطقية لا حصر لها في وقت واحد!"
            rawPosition < 0.65f -> "من خلال استخدام الكيوبتات المبردة، نحافظ على تماسك الطور."
            rawPosition < 0.90f -> "وهذه هي الطريقة التي نحل بها عوامل التشفير في أقل من دقيقة!"
            else -> "شكراً جزيلاً لكم على المشاهدة! تحقق من الروابط الموجودة في الوصف."
        }
        else -> baseSubtitle
    }

    // Auto Play timer simulation
    var autoPlayTimerSeconds by remember { mutableIntStateOf(5) }
    var isVideoCompleted by remember { mutableStateOf(false) }

    LaunchedEffect(isPlaying, isVideoCompleted) {
        if (isPlaying && !isVideoCompleted) {
            while (rawPosition < 1.0f) {
                delay(1000)
                if (isPlaying) {
                    rawPosition = (rawPosition + 0.05f).coerceAtMost(1.0f)
                    if (rawPosition >= 1.0f) {
                        isVideoCompleted = true
                    }
                }
            }
        }
    }

    val finalBody = @Composable {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF09070F))
                .testTag("video_detail_scroll"),
            contentPadding = PaddingValues(bottom = 48.dp)
        ) {
            // Header player box (Immersive advanced controller)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(Color.Black)
                ) {
                    // Thumbnail artwork background representing screen play block
                    val imageRes = if (video.thumbnailUri == "img_app_icon") {
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

                    // Dim tint overlay
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)))

                    // Subtitles Text Layer Overlay
                    if (subtitlesEnabled && isPlaying && !isVideoCompleted) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 54.dp)
                                .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = subtitleText,
                                color = Color.Yellow,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Completion or Autoplay Trigger
                    if (isVideoCompleted) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.85f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Video Completed", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Up Next in $autoPlayTimerSeconds seconds...", color = Color.LightGray, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Button(
                                        onClick = {
                                            rawPosition = 0.0f
                                            isVideoCompleted = false
                                            isPlaying = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                                    ) {
                                        Text("Replay", color = Color.White)
                                    }
                                    Button(
                                        onClick = {
                                            isVideoCompleted = false
                                            onBack()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF007F))
                                    ) {
                                        Text("Go Back", color = Color.White)
                                    }
                                }
                            }
                        }
                    }

                    // Simulated buffer layer row (little loading bar overlay)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .align(Alignment.BottomStart)
                            .background(Color.DarkGray)
                    ) {
                        // buffer progress (typically wider than current playback index)
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(fraction = (rawPosition + 0.15f).coerceAtMost(1.0f))
                                .background(Color.Gray.copy(alpha = 0.5f))
                        )
                        // active playback progress Red
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(fraction = rawPosition)
                                .background(Color(0xFFFF007F))
                        )
                    }

                    // Live playback overlay widgets controller
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .align(Alignment.TopStart),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Subtitles toggle
                            IconButton(
                                onClick = { subtitlesEnabled = !subtitlesEnabled },
                                modifier = Modifier.background(
                                    if (subtitlesEnabled) Color(0xFFFF007F).copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.6f),
                                    CircleShape
                                )
                            ) {
                                Icon(Icons.Default.Subtitles, contentDescription = "Subtitles", tint = Color.White)
                            }

                            // PiP Concept button
                            IconButton(
                                onClick = { isPipMode = !isPipMode },
                                modifier = Modifier.background(
                                    if (isPipMode) Color(0xFF7F00FF).copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.6f),
                                    CircleShape
                                )
                            ) {
                                Icon(Icons.Default.PictureInPicture, contentDescription = "Picture-in-picture", tint = Color.White)
                            }
                        }
                    }

                    // Center playback overlay controller buttons
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { rawPosition = (rawPosition - 0.1f).coerceAtLeast(0f) }) {
                            Icon(Icons.Default.Replay10, contentDescription = "Rewind", tint = Color.White, modifier = Modifier.size(32.dp))
                        }

                        FloatingActionButton(
                            onClick = { isPlaying = !isPlaying },
                            containerColor = Color(0xFFFF007F),
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier.testTag("play_pause_button")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause"
                            )
                        }

                        IconButton(onClick = { rawPosition = (rawPosition + 0.1f).coerceAtMost(1f) }) {
                            Icon(Icons.Default.Forward10, contentDescription = "Forward", tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                    }

                    // Bottom parameters indicator
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Current timestamp text
                        Text(
                            text = "03:15 / 14:20",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Settings and Speed controls
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Quality select
                            Box {
                                Text(
                                    text = currentQuality,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .background(Color.DarkGray, RoundedCornerShape(4.dp))
                                        .clickable { showQualityMenu = true }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                                DropdownMenu(
                                    expanded = showQualityMenu,
                                    onDismissRequest = { showQualityMenu = false },
                                    modifier = Modifier.background(Color(0xFF1D1B26))
                                ) {
                                    val qualities = listOf("2160p 4K", "1080p HD", "720p", "480p Auto")
                                    qualities.forEach { q ->
                                        DropdownMenuItem(
                                            text = { Text(q, color = Color.White) },
                                            onClick = {
                                                currentQuality = q
                                                showQualityMenu = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Speed select
                            Box {
                                Text(
                                    text = currentSpeed,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .background(Color.DarkGray, RoundedCornerShape(4.dp))
                                        .clickable { showSpeedMenu = true }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                                DropdownMenu(
                                    expanded = showSpeedMenu,
                                    onDismissRequest = { showSpeedMenu = false },
                                    modifier = Modifier.background(Color(0xFF1D1B26))
                                ) {
                                    val speeds = listOf("0.5x", "1.0x", "1.5x", "2.0x")
                                    speeds.forEach { s ->
                                        DropdownMenuItem(
                                            text = { Text(s, color = Color.White) },
                                            onClick = {
                                                currentSpeed = s
                                                showSpeedMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Exclusivity alert banner for non-premium
            if (video.isExclusive && userProfile?.isPremium != true) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD700)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("PREMIUM EXCLUSIVE CONTENT", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "This high-fidelity recording is designated for premium members. Activate premium status in your profile menu to support our creators and unlock full ad-free views.",
                                color = Color.DarkGray,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // Title, views, category metadata container
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = video.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("${formatViews(video.views)} views", color = Color.Gray, fontSize = 12.sp)
                        Text("•", color = Color.DarkGray, fontSize = 12.sp)
                        Text(video.uploadTime, color = Color.Gray, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Social buttons row (Like, comments slider, Share, download)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Like count
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFF1D1B26), RoundedCornerShape(20.dp))
                                .clickable {
                                    if (hasLiked) {
                                        mutableLikes--
                                        hasLiked = false
                                    } else {
                                        mutableLikes++
                                        hasLiked = true
                                        hasDisliked = false
                                        viewModel.logLike(video.id, video.title, video.category)
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("like_action_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = "Like",
                                tint = if (hasLiked) Color(0xFFFF007F) else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(mutableLikes.toString(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        // Dislike Button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFF1D1B26), RoundedCornerShape(20.dp))
                                .clickable {
                                    if (hasDisliked) {
                                        hasDisliked = false
                                    } else {
                                        hasDisliked = true
                                        if (hasLiked) {
                                            mutableLikes--
                                            hasLiked = false
                                        }
                                        viewModel.logDislike(video.id, video.title, video.category)
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("dislike_action_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ThumbDown,
                                contentDescription = "Dislike",
                                tint = if (hasDisliked) Color(0xFFFF007F) else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Share
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFF1D1B26), RoundedCornerShape(20.dp))
                                .clickable {
                                    viewModel.logShare(video.id, video.title, video.category)
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("share_action_button")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share", color = Color.White, fontSize = 12.sp)
                        }

                        // Downloads offline progress indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFF1D1B26), RoundedCornerShape(20.dp))
                                .clickable {
                                    if (downloadProgress == null) {
                                        viewModel.startVideoDownload(video.id)
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("download_action_button")
                        ) {
                            if (downloadProgress == null) {
                                Icon(Icons.Default.Download, contentDescription = "Download Offline", tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Download", color = Color.White, fontSize = 12.sp)
                            } else if (downloadProgress < 100) {
                                CircularProgressIndicator(
                                    progress = { downloadProgress / 100f },
                                    modifier = Modifier.size(14.dp),
                                    color = Color(0xFFFF007F),
                                    strokeWidth = 2.dp,
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("$downloadProgress%", color = Color(0xFFFF007F), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.Check, contentDescription = "Saved", tint = Color.Green, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Offline", color = Color.Green, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Creator Info channel card row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .background(Color(0xFF1D1B26), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF7F00FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(video.authorName.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(video.authorName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("182K Subscriptions", color = Color.Gray, fontSize = 11.sp)
                        }
                    }

                    Button(
                        onClick = { viewModel.toggleSubscription(video.authorName) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSubscribed) Color.DarkGray else Color(0xFFFF007F)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("subscribe_creator_button")
                    ) {
                        Text(if (isSubscribed) "Subscribed" else "Subscribe", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    }
                }
            }

            // Video Description drawer expansion simulator
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .background(Color(0xFF13111C), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text("Description", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = video.description,
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            // AI NEURO-MULTIMODAL TECHNOLOGY ENHANCEMENT LAB
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("ai_neural_enhancement_lab_card"),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0819)),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.5.dp, Color(0xFFFF007F).copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFFFF007F),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "एआई न्यूरो-मल्टीमोडल लैब (AI Lab)",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                            }
                            Card(
                                shape = RoundedCornerShape(6.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.15f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(Color.Green, RoundedCornerShape(3.dp))
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("EDGE-NEURAL ON", color = Color.Green, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // SECTION 1: AI MULTILINGUAL VOICE TRACK DUBBING (एआई रीयल-टाइम बहुभाषी डबिंग)
                        Text(
                            text = "1. AI REAL-TIME VOICE DUBBING (एआई रीयल-टाइम वॉइस डबिंग):",
                            color = Color(0xFFFF007F),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Select multi-translation audio voice track. Neural speech generator swaps vocal elements instantly.",
                            color = Color.Gray,
                            fontSize = 9.sp,
                            lineHeight = 11.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val dubLangs = listOf(
                                Triple("en", "English", "🇺🇸"),
                                Triple("hi", "हिंदी", "🇮🇳"),
                                Triple("es", "Español", "🇪🇸"),
                                Triple("fr", "Français", "🇫🇷"),
                                Triple("ar", "العربية", "🇸🇦")
                            )
                            dubLangs.forEach { (code, name, flag) ->
                                val isSelected = currentLang == code
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { 
                                            viewModel.setLanguage(code)
                                        }
                                        .testTag("dub_lang_button_$code"),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) Color(0xFFFF007F).copy(alpha = 0.25f) else Color(0xFF1E102B)
                                    ),
                                    border = BorderStroke(
                                        1.dp, 
                                        if (isSelected) Color(0xFFFF007F) else Color(0xFFFF007F).copy(alpha = 0.2f)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(flag, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(name, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Dubbing dynamic feedback panel
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF07040A)),
                            border = BorderStroke(1.dp, Color.DarkGray.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Mic, contentDescription = null, tint = Color(0xFFFFB74D), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Active Voice Track: " + when(currentLang) {
                                            "hi" -> "Hindi AI Speech Model (नॉयडा-27)"
                                            "es" -> "Spanish Castilian Neural-Stream"
                                            "fr" -> "French Parisian Balanced Synthesizer"
                                            "ar" -> "Arabic Gulf dialect Realtime Vocals"
                                            else -> "Original English (Hi-Fi Studio Mix)"
                                        },
                                        color = Color.LightGray,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Synchronization delay in edge device: <1.4 milliseconds (Perfect Phase Alignment)",
                                        color = Color.DarkGray,
                                        fontSize = 8.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // SECTION 2: NEURAL RESOLUTION SUPER-SAMPLING & SDR-to-HDR EXTENDER
                        Text(
                            text = "2. NEURAL VIDEO UPSCALING & SDR-to-HDR (वीडियो एन्हेंसर):",
                            color = Color(0xFF64B5F6),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // SDR-to-HDR converter switch row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isHdrActive) Icons.Default.BrightnessHigh else Icons.Default.BrightnessMedium,
                                    contentDescription = null,
                                    tint = if (isHdrActive) Color(0xFFFFB74D) else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Neural SDR to HDR Remapper", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text("Realtime wide color gamut dynamic mapping", color = Color.Gray, fontSize = 8.5.sp)
                                }
                            }
                            Switch(
                                checked = isHdrActive,
                                onCheckedChange = { isHdrActive = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFFFF007F),
                                    uncheckedThumbColor = Color.Gray,
                                    uncheckedTrackColor = Color.DarkGray
                                ),
                                modifier = Modifier.scale(0.85f).testTag("hdr_remapper_switch")
                            )
                        }

                        if (isHdrActive) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1D150B)),
                                border = BorderStroke(1.dp, Color(0xFFFFB74D).copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFFFB74D), modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "HDR Mode active! Peak intensity expanded to 1240 Nits. Color spectrum adjusted to BT.2020.",
                                        color = Color(0xFFFFB74D),
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Neural Resolution Super Sampler Choice Row
                        Text("Neural Upscaling Scale Level:", color = Color.LightGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val upsizes = listOf(
                                "1080p" to "SDR Default",
                                "4K Ultra" to "Neural 4x",
                                "8K Divine" to "Neural Custom"
                            )
                            upsizes.forEach { (factor, subtitle) ->
                                val isSelected = upscaleFactor == factor
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { 
                                            upscaleFactor = factor
                                            if (factor != "1080p") {
                                                coroutineScope.launch {
                                                    isUpscalingRunning = true
                                                    upscalingStatusText = "Initializing Shaders..."
                                                    delay(800)
                                                    upscalingStatusText = "Analyzing adjacent macroblocks..."
                                                    delay(1000)
                                                    upscalingStatusText = "Upscaling completed via neural frame prediction engine!"
                                                    isUpscalingRunning = false
                                                }
                                            } else {
                                                upscalingStatusText = "Standby"
                                            }
                                        }
                                        .testTag("upscale_factor_button_$factor"),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) Color(0xFF1A1F3E) else Color(0xFF0A0F1A)
                                    ),
                                    border = BorderStroke(
                                        1.dp, 
                                        if (isSelected) Color(0xFF64B5F6) else Color(0xFF64B5F6).copy(alpha = 0.2f)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(factor, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                        Text(subtitle, color = Color.Gray, fontSize = 7.5.sp)
                                    }
                                }
                            }
                        }

                        if (upscaleFactor != "1080p") {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF040A12)),
                                border = BorderStroke(1.dp, Color(0xFF64B5F6).copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Neural Super-Sample Telemetry Feed:", 
                                            color = Color.LightGray, 
                                            fontSize = 8.5.sp, 
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (isUpscalingRunning) {
                                            CircularProgressIndicator(
                                                color = Color(0xFF64B5F6),
                                                strokeWidth = 1.2.dp,
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = upscalingStatusText,
                                        color = if (isUpscalingRunning) Color(0xFFFFB74D) else Color(0xFF81C784),
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Medium
                                    )
                                    if (!isUpscalingRunning && upscaleFactor != "1080p") {
                                        Text(
                                            text = "Enhanced: Frame Rate Interpolated to 60fps stable | Super-resolution rendering: ON",
                                            color = Color.Gray,
                                            fontSize = 8.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // SECTION 3: AI ACOUSTIC MODELER (एआई स्पेसियल ऑडियो और बास बूस्ट)
                        Text(
                            text = "3. AI ACOUSTICS & SPATIAL ENGINE (एआई स्पेसियल ऑडियो):",
                            color = Color(0xFF81C784),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Noise filter toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Acoustic Active Noise Isolation", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Filters high frequency background buzz & wind hiss", color = Color.Gray, fontSize = 8.5.sp)
                            }
                            Switch(
                                checked = isNoiseFilterActive,
                                onCheckedChange = { isNoiseFilterActive = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF81C784),
                                    uncheckedThumbColor = Color.Gray,
                                    uncheckedTrackColor = Color.DarkGray
                                ),
                                modifier = Modifier.scale(0.85f).testTag("noise_filter_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Bass boost toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("True-Sonic Bass Boost EQ", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Amplifies sub-frequencies cleanly for immersive staging", color = Color.Gray, fontSize = 8.5.sp)
                            }
                            Switch(
                                checked = isBassBoostActive,
                                onCheckedChange = { isBassBoostActive = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF81C784),
                                    uncheckedThumbColor = Color.Gray,
                                    uncheckedTrackColor = Color.DarkGray
                                ),
                                modifier = Modifier.scale(0.85f).testTag("bass_boost_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Spatial depth slider
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Dynamic Atmos Spatial Width", color = Color.LightGray, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                Text("${(spatialDepth * 100).toInt()}% Depth", color = Color(0xFF81C784), fontSize = 9.5.sp, fontWeight = FontWeight.Black)
                            }
                            Slider(
                                value = spatialDepth,
                                onValueChange = { spatialDepth = it },
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF81C784),
                                    activeTrackColor = Color(0xFF81C784),
                                    inactiveTrackColor = Color.DarkGray
                                ),
                                valueRange = 0.1f..1.0f,
                                modifier = Modifier.height(24.dp).testTag("spatial_depth_slider")
                            )
                        }
                    }
                }
            }

            // Real-time banner monetization ad overlay (unless premium is toggled!)
            if (showBannerAd && userProfile?.isPremium != true) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A24)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .background(Color.Yellow, RoundedCornerShape(2.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("AD", color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("AstroGamer Pro Headset", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("Enjoy crystal clear spatial 4D sound. Buy now!", color = Color.Gray, fontSize = 11.sp)
                                }
                            }
                            IconButton(onClick = { showBannerAd = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close Ad", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Comments lists header
            item {
                Text(
                    text = "Discussion Chat (${comments.size})",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            // Keyboard or comment writing prompt tray
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Add professional comment...", color = Color.Gray, fontSize = 13.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFFF007F),
                            unfocusedBorderColor = Color.DarkGray
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("comment_input_field"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (commentText.trim().isNotEmpty()) {
                                viewModel.addComment(video.id, commentText.trim())
                                commentText = ""
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFFFF007F)
                        ),
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("submit_comment_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Submit", tint = Color.White)
                    }
                }
            }

            // Dynamic comments rows
            if (comments.isEmpty()) {
                item {
                    Text(
                        "No discussions. Be the first to comment!",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
                    )
                }
            } else {
                items(comments) { comment ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .background(Color(0xFF13111C), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF5D3FDB)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(comment.authorName.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(comment.authorName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(comment.timestamp, color = Color.Gray, fontSize = 10.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(comment.text, color = Color.LightGray, fontSize = 12.sp, lineHeight = 16.sp)
                        }
                    }
                }
            }
        }
    }

    // PiP Overlay handler visualizer!
    if (isPipMode) {
        // Render float box
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .width(180.dp)
                    .height(110.dp)
                    .clickable { isPipMode = false },
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                border = BorderStroke(2.dp, Color(0xFFFF007F)),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    val fallbackImg = if (video.thumbnailUri == "img_app_icon") {
                        R.drawable.img_app_icon
                    } else {
                        R.drawable.img_hero_banner
                    }
                    Image(
                        painter = painterResource(id = fallbackImg),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f))
                    )
                    Text(
                        "Picture-In-Picture Active",
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                            .padding(4.dp)
                    )
                }
            }
        }
    } else {
        finalBody()
    }
}
