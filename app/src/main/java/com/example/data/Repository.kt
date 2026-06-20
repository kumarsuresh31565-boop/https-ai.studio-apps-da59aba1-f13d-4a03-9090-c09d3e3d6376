package com.example.data

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Repository(private val db: AppDatabase) {

    val userProfile: Flow<UserProfile?> = db.userProfileDao().getUserProfile()
    val videos: Flow<List<VideoItem>> = db.videoDao().getAllActiveVideos()
    val shorts: Flow<List<VideoItem>> = db.videoDao().getAllActiveShorts()
    val drafts: Flow<List<VideoItem>> = db.videoDao().getAllDrafts()
    val subscriptions: Flow<List<Subscription>> = db.subscriptionDao().getAllSubscriptions()
    val notifications: Flow<List<NotificationItem>> = db.notificationDao().getAllNotifications()
    val communityPosts: Flow<List<CommunityPost>> = db.communityPostDao().getAllCommunityPosts()
    val userBehaviors: Flow<List<UserBehavior>> = db.userBehaviorDao().getAllBehaviorsFlow()

    suspend fun getBehaviorsList(): List<UserBehavior> = withContext(Dispatchers.IO) {
        db.userBehaviorDao().getAllBehaviorsList()
    }

    suspend fun logBehavior(behavior: UserBehavior) = withContext(Dispatchers.IO) {
        db.userBehaviorDao().insertBehavior(behavior)
    }

    suspend fun logWatch(videoId: Long, title: String, category: String) = withContext(Dispatchers.IO) {
        db.userBehaviorDao().insertBehavior(
            UserBehavior(actionType = "WATCH", videoId = videoId, videoTitle = title, videoCategory = category)
        )
    }

    suspend fun logLike(videoId: Long, title: String, category: String) = withContext(Dispatchers.IO) {
        db.userBehaviorDao().insertBehavior(
            UserBehavior(actionType = "LIKE", videoId = videoId, videoTitle = title, videoCategory = category)
        )
    }

    suspend fun logDislike(videoId: Long, title: String, category: String) = withContext(Dispatchers.IO) {
        db.userBehaviorDao().insertBehavior(
            UserBehavior(actionType = "DISLIKE", videoId = videoId, videoTitle = title, videoCategory = category)
        )
    }

    suspend fun logShare(videoId: Long, title: String, category: String) = withContext(Dispatchers.IO) {
        db.userBehaviorDao().insertBehavior(
            UserBehavior(actionType = "SHARE", videoId = videoId, videoTitle = title, videoCategory = category)
        )
    }

    suspend fun logSubscribe(creatorName: String) = withContext(Dispatchers.IO) {
        db.userBehaviorDao().insertBehavior(
            UserBehavior(actionType = "SUBSCRIBE", creatorName = creatorName)
        )
    }

    suspend fun logUnsubscribe(creatorName: String) = withContext(Dispatchers.IO) {
        db.userBehaviorDao().insertBehavior(
            UserBehavior(actionType = "UNSUBSCRIBE", creatorName = creatorName)
        )
    }

    suspend fun logSearch(query: String) = withContext(Dispatchers.IO) {
        db.userBehaviorDao().insertBehavior(
            UserBehavior(actionType = "SEARCH", searchQuery = query)
        )
    }

    suspend fun clearAllBehaviors() = withContext(Dispatchers.IO) {
        db.userBehaviorDao().clearAllBehaviors()
    }

    fun getCommentsForVideo(videoId: Long): Flow<List<CommentItem>> {
        return db.commentDao().getCommentsForVideo(videoId)
    }

    suspend fun insertUserProfile(profile: UserProfile) = withContext(Dispatchers.IO) {
        db.userProfileDao().insertUserProfile(profile)
    }

    suspend fun insertVideo(video: VideoItem): Long = withContext(Dispatchers.IO) {
        db.videoDao().insertVideo(video)
    }

    suspend fun updateVideo(video: VideoItem) = withContext(Dispatchers.IO) {
        db.videoDao().updateVideo(video)
    }

    suspend fun deleteVideo(video: VideoItem) = withContext(Dispatchers.IO) {
        db.videoDao().deleteVideo(video)
    }

    suspend fun addComment(comment: CommentItem) = withContext(Dispatchers.IO) {
        db.commentDao().insertComment(comment)
        // Auto alert creator
        val video = db.videoDao().getAllActiveVideos().firstOrNull()?.find { it.id == comment.videoId }
        if (video != null) {
            db.notificationDao().insertNotification(
                NotificationItem(
                    title = "New comment on your video",
                    description = "${comment.authorName} commented: \"${comment.text}\" on video \"${video.title}\"",
                    category = "comment"
                )
            )
        }
    }

    suspend fun toggleSubscription(creatorName: String, creatorAvatar: String = "ic_profile") = withContext(Dispatchers.IO) {
        val currentSubs = db.subscriptionDao().getAllSubscriptions().firstOrNull() ?: emptyList()
        val existing = currentSubs.find { it.creatorName.equals(creatorName, ignoreCase = true) }
        if (existing != null) {
            db.subscriptionDao().removeSubscription(creatorName)
        } else {
            db.subscriptionDao().insertSubscription(
                Subscription(creatorName = creatorName, creatorAvatar = creatorAvatar)
            )
            // Send subscription notify
            db.notificationDao().insertNotification(
                NotificationItem(
                    title = "New Channel Subscribed",
                    description = "You subscribed to $creatorName! Stay tuned for their latest uploads.",
                    category = "upload"
                )
            )
        }
    }

    suspend fun uploadVideoItem(
        title: String,
        description: String,
        category: String,
        isShort: Boolean,
        isExclusive: Boolean,
        isPrivate: Boolean,
        scheduleTime: Long = 0
    ) = withContext(Dispatchers.IO) {
        val user = db.userProfileDao().getUserProfile().firstOrNull() ?: UserProfile()
        
        // Simulating immediate copyright checking
        val hasCopyrightInfringement = title.lowercase().contains("pirated") || title.lowercase().contains("crack")
        val copyrightStatus = if (hasCopyrightInfringement) "Flagged" else "Pass"
        
        val video = VideoItem(
            title = title,
            description = description,
            category = category,
            authorName = user.name,
            authorIconUrl = user.avatarUrl,
            thumbnailUri = if (isShort) "img_short_placeholder" else "img_hero_banner", // Default resources fallback
            videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4", // Real sample video
            isShort = isShort,
            isExclusive = isExclusive,
            isDraft = isPrivate, // Private videos treated as persistent drafts / unlisted
            scheduleTime = scheduleTime,
            copyrightCheckStatus = copyrightStatus
        )
        
        val insertedId = db.videoDao().insertVideo(video)
        
        db.notificationDao().insertNotification(
            NotificationItem(
                title = "Video Upload Completed",
                description = "Your video \"$title\" was successfully uploaded. Copyright Check: $copyrightStatus.",
                category = "upload"
            )
        )
        
        insertedId
    }

    suspend fun insertNotification(notification: NotificationItem) = withContext(Dispatchers.IO) {
        db.notificationDao().insertNotification(notification)
    }

    suspend fun clearAllNotifications() = withContext(Dispatchers.IO) {
        db.notificationDao().markAllAsRead()
    }

    suspend fun insertCommunityPost(message: String) = withContext(Dispatchers.IO) {
        val user = db.userProfileDao().getUserProfile().firstOrNull() ?: UserProfile()
        db.communityPostDao().insertCommunityPost(
            CommunityPost(
                authorName = user.name,
                authorAvatar = user.avatarUrl,
                message = message,
                likes = 0
            )
        )
    }

    suspend fun populateInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        val currentProfile = db.userProfileDao().getUserProfile().firstOrNull()
        if (currentProfile == null) {
            // First time launch, populate all database items
            db.userProfileDao().insertUserProfile(UserProfile())

            // Video items
            val v1 = db.videoDao().insertVideo(
                VideoItem(
                    title = "Next-Gen Quantum Computing: What You Need to Know",
                    description = "We dive deep into the absolute frontier of technology: Quantum computing. Discover qubits, superposition, quantum entanglement, and how these systems will solve problems that are currently impossible for classical supercomputers in seconds. What does this mean for encryption, medicine development, and AI training? Stay tuned and find out!",
                    category = "Tech",
                    authorName = "AstroTech",
                    authorIconUrl = "ic_profile",
                    thumbnailUri = "img_app_icon", // Reusing app logo as a cool technology thumbnail graphic!
                    videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    views = 284000,
                    likes = 18320,
                    uploadTime = "2 hours ago"
                )
            )

            val v2 = db.videoDao().insertVideo(
                VideoItem(
                    title = "Cyberpunk Coding Beat - Lofi Synth Chill Study Session",
                    description = "Sit back, plug in your headphones, and immerse yourself in this high-tech cyberpunk lo-fi chill beat session. Perfect for software developers, students coding late into the night, UI designers, or anyone seeking focus in a neon-lit atmosphere. All tracks composed locally.",
                    category = "Music",
                    authorName = "LofiVibes",
                    authorIconUrl = "ic_profile",
                    thumbnailUri = "img_hero_banner", // Reusing hero banner illustration!
                    videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                    views = 421000,
                    likes = 34000,
                    uploadTime = "12 hours ago"
                )
            )

            val v3 = db.videoDao().insertVideo(
                VideoItem(
                    title = "Absolute Future of Artificial Intelligence: Gemini 3.5 & Veo",
                    description = "Explore the groundbreaking intelligence models of 2026. This comprehensive overview covers multimodal native stream parsing, automated background action coordination, ultra-realistic video synthesis with Veo, and the structural advances powering the latest AI developer agents. A complete paradigm shift.",
                    category = "Tech",
                    authorName = "DeepMind Pro",
                    authorIconUrl = "ic_profile",
                    thumbnailUri = "img_app_icon",
                    videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                    views = 98100,
                    likes = 12450,
                    uploadTime = "Yesterday",
                    isExclusive = true // Exclusive to premium members
                )
            )

            val v4 = db.videoDao().insertVideo(
                VideoItem(
                    title = "Uncovering Ancient Ruins Under Dense Mayan Jungles",
                    description = "Armed with state-of-the-art Light Detection and Ranging (LiDAR) tech, archaeologists fly above the pristine Guatemalan canopy to strip away centuries of jungle growth, revealing hidden pyramids, massive agricultural terraces, and defensive highways from the classic Mayan civilization.",
                    category = "Comedy", // Reused section, or general category
                    authorName = "Expedition Earth",
                    authorIconUrl = "ic_profile",
                    thumbnailUri = "img_hero_banner",
                    videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                    views = 124000,
                    likes = 8790,
                    uploadTime = "3 days ago"
                )
            )

            // Dynamic Comments
            db.commentDao().insertComment(CommentItem(videoId = v1, authorName = "Alex Dev", text = "This is incredibly lucid. Understanding qubits has never been this simple!"))
            db.commentDao().insertComment(CommentItem(videoId = v1, authorName = "Suresh Kumar", text = "I am mindblown by the cryogenic cooler requirements. Incredible science."))
            db.commentDao().insertComment(CommentItem(videoId = v1, authorName = "NeonByte", text = "Cryptography is going to change dramatically! Great summary."))

            db.commentDao().insertComment(CommentItem(videoId = v2, authorName = "KotlinQueen", text = "Been coding my Room DB models with this beat on repeat. Excellent work! 🎧"))
            db.commentDao().insertComment(CommentItem(videoId = v2, authorName = "Suresh Kumar", text = "This track at 01:23 hits so different. Pure synth heaven."))

            db.commentDao().insertComment(CommentItem(videoId = v3, authorName = "CyberSam", text = "Agents writing full Android builds natively is insane. The future is here!"))

            // Short Video Items
            db.videoDao().insertVideo(
                VideoItem(
                    title = "Compose Button Ripple Tricks #Shorts",
                    description = "Quick guide to customize ripple triggers in Material 3 Compose!",
                    category = "Tech",
                    authorName = "ComposeKing",
                    thumbnailUri = "img_app_icon",
                    videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
                    isShort = true,
                    likes = 18230,
                    uploadTime = "2 hours ago"
                )
            )

            db.videoDao().insertVideo(
                VideoItem(
                    title = "POV: You write code in 2026 🤯 #Shorts",
                    description = "Programming with autonomous AI builders is a whole vibe.",
                    category = "Tech",
                    authorName = "CodeHustler",
                    thumbnailUri = "img_hero_banner",
                    videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
                    isShort = true,
                    likes = 45290,
                    uploadTime = "1 hour ago"
                )
            )

            db.videoDao().insertVideo(
                VideoItem(
                    title = "Quantum bits in 30 seconds! ⚛️ #Shorts",
                    description = "Everything you need to know about Superposition! ",
                    category = "Tech",
                    authorName = "AstroTech",
                    thumbnailUri = "img_app_icon",
                    videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4",
                    isShort = true,
                    likes = 9430,
                    uploadTime = "9 hours ago"
                )
            )

            // Community Posts
            db.communityPostDao().insertCommunityPost(
                CommunityPost(
                    authorName = "LofiVibes",
                    message = "Going live with a brand new cyberpunk synth track tonight! Drop your request parameters in the chat below! 🎹✨",
                    likes = 455,
                    commentsCount = 23
                )
            )
            db.communityPostDao().insertCommunityPost(
                CommunityPost(
                    authorName = "AstroTech",
                    message = "We just finished recording an exclusive studio tour in Sweden. Exclusive viewing dropping for Premium subscribers tomorrow morning! 🙌",
                    likes = 2120,
                    commentsCount = 143
                )
            )

            // Subscriptions
            db.subscriptionDao().insertSubscription(Subscription(creatorName = "AstroTech"))
            db.subscriptionDao().insertSubscription(Subscription(creatorName = "LofiVibes"))

            // Notifications
            db.notificationDao().insertNotification(
                NotificationItem(
                    title = "Welcome to StreamView!",
                    description = "Explore interactive streaming, Creator analytics, AI thumbnail generators, live chat streams, and exclusive content access.",
                    category = "alert"
                )
            )

            // Initial User Behaviors to seed recommendation engine
            db.userBehaviorDao().insertBehavior(UserBehavior(actionType = "SEARCH", searchQuery = "quantum computing"))
            db.userBehaviorDao().insertBehavior(UserBehavior(actionType = "WATCH", videoId = v1, videoTitle = "Next-Gen Quantum Computing: What You Need to Know", videoCategory = "Tech"))
            db.userBehaviorDao().insertBehavior(UserBehavior(actionType = "LIKE", videoId = v1, videoTitle = "Next-Gen Quantum Computing: What You Need to Know", videoCategory = "Tech"))
            db.userBehaviorDao().insertBehavior(UserBehavior(actionType = "SUBSCRIBE", creatorName = "AstroTech"))
        }
    }
}

object DatabaseProvider {
    private var dbInstance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return dbInstance ?: synchronized(this) {
            val db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "streamview_db"
            )
                .fallbackToDestructiveMigration()
                .build()
            dbInstance = db
            db
        }
    }
}
