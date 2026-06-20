package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortsScreen(
    viewModel: MainViewModel
) {
    val shortsList by viewModel.shorts.collectAsState()
    val subscriptions by viewModel.subscriptions.collectAsState()

    var activeIndex by remember { mutableIntStateOf(0) }
    val activeShort = shortsList.getOrNull(activeIndex)

    // Dynamic states
    var likedState by remember(activeIndex) { mutableStateOf(false) }
    var likesCount by remember(activeIndex) { mutableLongStateOf(0) }
    var followState by remember(activeIndex, activeShort) {
        mutableStateOf(
            activeShort?.let { short ->
                subscriptions.any { it.creatorName.equals(short.authorName, ignoreCase = true) }
            } ?: false
        )
    }

    var showCommentDrawer by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }

    // Init likes count and log watch behavior
    LaunchedEffect(activeShort) {
        if (activeShort != null) {
            likesCount = activeShort.likes
            viewModel.logWatch(activeShort.id, activeShort.title, activeShort.category)
        }
    }

    // Floating heart pops on double clicks
    var heartPopVisible by remember { mutableStateOf(false) }
    val heartScale by animateFloatAsState(
        targetValue = if (heartPopVisible) 1.5f else 0.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow),
        label = "heart_scale"
    )

    if (activeShort == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F0F0F)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFFE50914))
        }
        return
    }

    val scaffoldComments = viewModel.getVideoComments(activeShort.id).collectAsState()

    // Full screen relative container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("shorts_container")
    ) {
        // Vertical Short Media Placeholder
        val fallbackImg = if (activeShort.thumbnailUri == "img_app_icon") {
            R.drawable.img_app_icon
        } else {
            R.drawable.img_hero_banner
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    // Double tap simulator - triggers gorgeous floating heart
                    likedState = true
                    likesCount = activeShort.likes + 1
                    heartPopVisible = true
                }
        ) {
            Image(
                painter = painterResource(id = fallbackImg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Cyber Gradient Overlay at the bottom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.2f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )
        }

        // Animated heart on double-tap
        if (heartPopVisible) {
            LaunchedEffect(Unit) {
                delay(800)
                heartPopVisible = false
            }
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Color(0xFFE50914),
                modifier = Modifier
                    .size(100.dp)
                    .scale(heartScale)
                    .align(Alignment.Center)
            )
        }

        // Swiper buttons (floating on top/bottom edges)
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 120.dp)
                .align(Alignment.CenterStart)
                .padding(start = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = {
                    if (activeIndex > 0) {
                        activeIndex--
                        likedState = false
                    }
                },
                modifier = Modifier.background(Color.Black.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(Icons.Default.ArrowUpward, contentDescription = "Prev Short", tint = Color.White)
            }

            IconButton(
                onClick = {
                    if (activeIndex < shortsList.size - 1) {
                        activeIndex++
                        likedState = false
                    }
                },
                modifier = Modifier.background(Color.Black.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(Icons.Default.ArrowDownward, contentDescription = "Next Short", tint = Color.White)
            }
        }

        // Floating Action Buttons sidebar (Like, comments slider, Share, uploader)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 100.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Channel Avatar with Subscribe follow hook
            Box(contentAlignment = Alignment.BottomCenter) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(colors = listOf(Color(0xFF2D2A35), Color(0xFF16151A)))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(activeShort.authorName.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .offset(y = 8.dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(if (followState) Color.Gray else Color(0xFFE50914))
                        .clickable {
                            viewModel.toggleSubscription(activeShort.authorName)
                            followState = !followState
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (followState) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = "Follow",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Like action
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {
                        likedState = !likedState
                        likesCount = if (likedState) activeShort.likes + 1 else activeShort.likes
                    },
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.6f), CircleShape).testTag("short_like_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Like",
                        tint = if (likedState) Color(0xFFE50914) else Color.White
                    )
                }
                Text(likesCount.toString(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // Comment drawer
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { showCommentDrawer = true },
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.6f), CircleShape).testTag("short_comment_button")
                ) {
                    Icon(Icons.Default.Comment, contentDescription = "Comments", tint = Color.White)
                }
                Text(scaffoldComments.value.size.toString(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // Share action
            IconButton(
                onClick = { /* Share dialog mock */ },
                modifier = Modifier.background(Color.Black.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
            }
        }

        // In-Player bottom uploader details metadata Overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 100.dp, start = 16.dp, end = 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "@${activeShort.authorName}",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Box(
                    modifier = Modifier
                        .background(Color(0xFF211F26), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text("SHORT", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Black)
                }

                // Interactive Localized Subscribe Button
                Box(
                    modifier = Modifier
                        .background(
                            color = if (followState) Color.White.copy(alpha = 0.2f) else Color(0xFFE50914),
                            shape = RoundedCornerShape(50)
                        )
                        .clickable {
                            viewModel.toggleSubscription(activeShort.authorName)
                            followState = !followState
                        }
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .testTag("shorts_subscribe_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (followState) viewModel.translate("subscribed") else viewModel.translate("subscribe"),
                        color = if (followState) Color.LightGray else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = activeShort.title,
                color = Color.LightGray,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            // Music rotation sticker simulation
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Original Audio - ${activeShort.authorName}", color = Color.Yellow, fontSize = 11.sp)
            }
        }

        // Embedded Comments sheet overlay drawer
        if (showCommentDrawer) {
            ModalBottomSheet(
                onDismissRequest = { showCommentDrawer = false },
                containerColor = Color(0xFF16151A),
                contentColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 36.dp)
                ) {
                    Text(
                        text = "Comments (${scaffoldComments.value.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )

                    // Comments entry list
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .testTag("shorts_comment_list"),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        if (scaffoldComments.value.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(48.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Join the discussion! Write first comment below.", color = Color.Gray, fontSize = 12.sp)
                                }
                            }
                        } else {
                            items(scaffoldComments.value) { comment ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF211F26)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(comment.authorName.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(comment.authorName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        Text(comment.text, color = Color.LightGray, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Keyboard input rows
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            placeholder = { Text("Add comment...", color = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFE50914),
                                unfocusedBorderColor = Color.DarkGray
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("shorts_comment_field")
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        IconButton(
                            onClick = {
                                if (commentText.isNotEmpty()) {
                                    viewModel.addComment(activeShort.id, commentText)
                                    commentText = ""
                                }
                            },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFE50914))
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}
