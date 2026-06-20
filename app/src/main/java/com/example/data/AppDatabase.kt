package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserProfile::class,
        VideoItem::class,
        CommentItem::class,
        Subscription::class,
        NotificationItem::class,
        CommunityPost::class,
        UserBehavior::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun videoDao(): VideoDao
    abstract fun commentDao(): CommentDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun notificationDao(): NotificationDao
    abstract fun communityPostDao(): CommunityPostDao
    abstract fun userBehaviorDao(): UserBehaviorDao
}
