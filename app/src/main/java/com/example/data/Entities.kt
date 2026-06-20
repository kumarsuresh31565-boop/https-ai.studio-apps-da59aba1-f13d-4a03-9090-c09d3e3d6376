package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1, // Singleton profile
    val email: String = "kumarsuresh31565@gmail.com",
    val name: String = "Suresh Kumar",
    val avatarUrl: String = "ic_profile",
    val bio: String = "Digital Creator & Tech enthusiast. Crafting deep experiences.",
    val isCreator: Boolean = true,
    val isPremium: Boolean = false,
    val isTwoFactorEnabled: Boolean = false,
    val walletBalance: Double = 350.0
) : Serializable

@Entity(tableName = "videos")
data class VideoItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val thumbnailUri: String, // String resource name or local path or uri
    val description: String,
    val category: String, // "Trending", "Gaming", "Tech", "Music", "Comedy"
    val authorName: String,
    val authorIconUrl: String = "ic_profile",
    val videoUrl: String, // Web or mock streaming source
    val views: Long = 1024,
    val likes: Long = 120,
    val uploadTime: String = "2 hours ago",
    val isShort: Boolean = false,
    val isLive: Boolean = false,
    val isExclusive: Boolean = false,
    val isDraft: Boolean = false,
    val scheduleTime: Long = 0,
    val isBanned: Boolean = false,
    val copyrightCheckStatus: String = "Pass" // "Pass", "Checking", "Flagged"
) : Serializable

@Entity(tableName = "comments")
data class CommentItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val videoId: Long,
    val authorName: String,
    val authorAvatar: String = "ic_profile",
    val text: String,
    val timestamp: String = "Just now"
) : Serializable

@Entity(tableName = "subscriptions")
data class Subscription(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val creatorName: String,
    val creatorAvatar: String = "ic_profile"
) : Serializable

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val category: String, // "alert", "upload", "like", "comment"
    val timestamp: String = "Just now",
    val isRead: Boolean = false
) : Serializable

@Entity(tableName = "community_posts")
data class CommunityPost(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorName: String = "Suresh Kumar",
    val authorAvatar: String = "ic_profile",
    val message: String,
    val timestamp: String = "Just now",
    val likes: Int = 12,
    val commentsCount: Int = 2
) : Serializable

@Entity(tableName = "user_behaviors")
data class UserBehavior(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actionType: String, // "WATCH", "LIKE", "DISLIKE", "SHARE", "SUBSCRIBE", "UNSUBSCRIBE", "SEARCH"
    val videoId: Long? = null,
    val videoTitle: String? = null,
    val videoCategory: String? = null,
    val creatorName: String? = null,
    val searchQuery: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

