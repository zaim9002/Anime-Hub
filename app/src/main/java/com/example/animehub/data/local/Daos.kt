package com.example.animehub.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchHistoryDao {
    @Query("SELECT * FROM watch_history ORDER BY lastWatchedTimestamp DESC")
    fun getAllHistory(): Flow<List<WatchHistoryEntity>>

    @Query("SELECT * FROM watch_history WHERE animeId = :animeId LIMIT 1")
    suspend fun getHistoryForAnime(animeId: Int): WatchHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(history: WatchHistoryEntity)

    @Query("DELETE FROM watch_history WHERE animeId = :animeId")
    suspend fun deleteHistoryForAnime(animeId: Int)

    @Query("DELETE FROM watch_history")
    suspend fun clearAllHistory()
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY addedTimestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE animeId = :animeId)")
    fun isFavoriteFlow(animeId: Int): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE animeId = :animeId)")
    suspend fun isFavorite(animeId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE animeId = :animeId")
    suspend fun removeFavorite(animeId: Int)
}

@Dao
interface MyListDao {
    @Query("SELECT * FROM my_list ORDER BY updatedTimestamp DESC")
    fun getAllMyList(): Flow<List<MyListEntity>>

    @Query("SELECT * FROM my_list WHERE status = :status ORDER BY updatedTimestamp DESC")
    fun getMyListByStatus(status: String): Flow<List<MyListEntity>>

    @Query("SELECT * FROM my_list WHERE animeId = :animeId LIMIT 1")
    fun getMyListItemFlow(animeId: Int): Flow<MyListEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: MyListEntity)

    @Query("DELETE FROM my_list WHERE animeId = :animeId")
    suspend fun removeFromMyList(animeId: Int)
}
