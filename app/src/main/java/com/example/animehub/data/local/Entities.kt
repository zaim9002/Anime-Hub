package com.example.animehub.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey
    val animeId: Int,
    val episodeNumber: Int,
    val animeTitle: String,
    val animeCover: String,
    val episodeTitle: String,
    val positionMs: Long,
    val durationMs: Long,
    val progressPercent: Float,
    val lastWatchedTimestamp: Long
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val animeId: Int,
    val title: String,
    val coverImage: String,
    val score: String,
    val genres: String,
    val format: String,
    val addedTimestamp: Long
)

@Entity(tableName = "my_list")
data class MyListEntity(
    @PrimaryKey
    val animeId: Int,
    val title: String,
    val coverImage: String,
    val status: String, // WATCHING, COMPLETED, PLAN_TO_WATCH, ON_HOLD, DROPPED
    val userRating: Int = 0,
    val updatedTimestamp: Long
)
