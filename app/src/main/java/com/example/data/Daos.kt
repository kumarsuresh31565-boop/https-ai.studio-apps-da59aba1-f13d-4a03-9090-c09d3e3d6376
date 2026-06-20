package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)
}

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos WHERE isShort = 0 AND isDraft = 0 AND isBanned = 0 ORDER BY id DESC")
    fun getAllActiveVideos(): Flow<List<VideoItem>>

    @Query("SELECT * FROM videos WHERE isShort = 1 AND isDraft = 0 AND isBanned = 0 ORDER BY id DESC")
    fun getAllActiveShorts(): Flow<List<VideoItem>>

    @Query("SELECT * FROM videos WHERE isDraft = 1 ORDER BY id DESC")
    fun getAllDrafts(): Flow<List<VideoItem>>

    @Query("SELECT * FROM videos WHERE id = :id LIMIT 1")
    fun getVideoById(id: Long): Flow<VideoItem?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoItem): Long

    @Update
    suspend fun updateVideo(video: VideoItem)

    @Delete
    suspend fun deleteVideo(video: VideoItem)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE videoId = :videoId ORDER BY id DESC")
    fun getCommentsForVideo(videoId: Long): Flow<List<CommentItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentItem)
}

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions ORDER BY id DESC")
    fun getAllSubscriptions(): Flow<List<Subscription>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(sub: Subscription)

    @Query("DELETE FROM subscriptions WHERE creatorName = :creatorName")
    suspend fun removeSubscription(creatorName: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY id DESC")
    fun getAllNotifications(): Flow<List<NotificationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()
}

@Dao
interface CommunityPostDao {
    @Query("SELECT * FROM community_posts ORDER BY id DESC")
    fun getAllCommunityPosts(): Flow<List<CommunityPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunityPost(post: CommunityPost)
}

@Dao
interface UserBehaviorDao {
    @Query("SELECT * FROM user_behaviors ORDER BY timestamp DESC")
    fun getAllBehaviorsFlow(): Flow<List<UserBehavior>>

    @Query("SELECT * FROM user_behaviors ORDER BY timestamp DESC")
    suspend fun getAllBehaviorsList(): List<UserBehavior>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBehavior(behavior: UserBehavior)

    @Query("DELETE FROM user_behaviors")
    suspend fun clearAllBehaviors()
}

