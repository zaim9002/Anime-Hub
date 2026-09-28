package com.example.animehub.data.repository

import com.example.animehub.data.local.AnimeDatabase
import com.example.animehub.data.local.FavoriteEntity
import com.example.animehub.data.local.MyListEntity
import com.example.animehub.data.local.WatchHistoryEntity
import com.example.animehub.data.model.Anime
import com.example.animehub.data.model.MyListStatus
import com.example.animehub.data.remote.AnimeApiService
import com.example.animehub.data.remote.AnimeDetailResult
import com.example.animehub.data.remote.HomeFeedData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AnimeRepository(
    private val apiService: AnimeApiService,
    private val database: AnimeDatabase,
    val videoSourceRepo: VideoSourceRepository = VideoSourceRepository()
) {
    // In-memory cache for fast tab switching
    private var cachedHomeFeed: HomeFeedData? = null

    suspend fun getHomeFeed(forceRefresh: Boolean = false): Result<HomeFeedData> {
        if (!forceRefresh && cachedHomeFeed != null) {
            return Result.success(cachedHomeFeed!!)
        }
        val result = apiService.getHomeFeed()
        if (result.isSuccess) {
            cachedHomeFeed = result.getOrNull()
        }
        return result
    }

    suspend fun searchAnime(
        query: String?,
        genre: String? = null,
        year: Int? = null,
        status: String? = null,
        format: String? = null,
        page: Int = 1
    ): Result<List<Anime>> {
        return apiService.searchAnime(
            query = query,
            genre = genre,
            year = year,
            status = status,
            format = format,
            page = page
        )
    }

    suspend fun getAnimeDetails(animeId: Int): Result<AnimeDetailResult> {
        return apiService.getAnimeDetails(animeId)
    }

    // Favorites
    fun getFavorites(): Flow<List<FavoriteEntity>> = database.favoriteDao().getAllFavorites()

    fun isFavorite(animeId: Int): Flow<Boolean> = database.favoriteDao().isFavoriteFlow(animeId)

    suspend fun toggleFavorite(anime: Anime) {
        val favDao = database.favoriteDao()
        val exists = favDao.isFavorite(anime.id)
        if (exists) {
            favDao.removeFavorite(anime.id)
        } else {
            favDao.addFavorite(
                FavoriteEntity(
                    animeId = anime.id,
                    title = anime.displayTitle,
                    coverImage = anime.coverImage,
                    score = anime.formattedScore,
                    genres = anime.genres.joinToString(", "),
                    format = anime.format,
                    addedTimestamp = System.currentTimeMillis()
                )
            )
        }
    }

    // My List
    fun getMyList(): Flow<List<MyListEntity>> = database.myListDao().getAllMyList()

    fun getMyListByStatus(status: MyListStatus): Flow<List<MyListEntity>> =
        database.myListDao().getMyListByStatus(status.name)

    fun getMyListItem(animeId: Int): Flow<MyListEntity?> =
        database.myListDao().getMyListItemFlow(animeId)

    suspend fun setMyListStatus(anime: Anime, status: MyListStatus, rating: Int = 0) {
        database.myListDao().insertOrUpdate(
            MyListEntity(
                animeId = anime.id,
                title = anime.displayTitle,
                coverImage = anime.coverImage,
                status = status.name,
                userRating = rating,
                updatedTimestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun removeFromMyList(animeId: Int) {
        database.myListDao().removeFromMyList(animeId)
    }

    // Watch History
    fun getWatchHistory(): Flow<List<WatchHistoryEntity>> =
        database.watchHistoryDao().getAllHistory()

    suspend fun getHistoryForAnime(animeId: Int): WatchHistoryEntity? =
        database.watchHistoryDao().getHistoryForAnime(animeId)

    suspend fun saveWatchProgress(
        animeId: Int,
        episodeNumber: Int,
        animeTitle: String,
        animeCover: String,
        episodeTitle: String,
        positionMs: Long,
        durationMs: Long
    ) {
        if (durationMs <= 0) return
        val percent = ((positionMs.toDouble() / durationMs.toDouble()) * 100.0).toFloat().coerceIn(0f, 100f)
        database.watchHistoryDao().insertOrUpdate(
            WatchHistoryEntity(
                animeId = animeId,
                episodeNumber = episodeNumber,
                animeTitle = animeTitle,
                animeCover = animeCover,
                episodeTitle = episodeTitle,
                positionMs = positionMs,
                durationMs = durationMs,
                progressPercent = percent,
                lastWatchedTimestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun clearWatchHistory() {
        database.watchHistoryDao().clearAllHistory()
    }
}
