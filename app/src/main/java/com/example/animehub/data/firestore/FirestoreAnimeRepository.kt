package com.example.animehub.data.firestore

import com.example.animehub.data.admin.AdminDashboardStats
import com.example.animehub.data.admin.AppNotification
import com.example.animehub.data.admin.FirestoreAnime
import com.example.animehub.data.admin.FirestoreEpisode
import com.example.animehub.data.admin.FirestoreEpisodeServer
import com.example.animehub.data.admin.FirestoreSeason
import com.example.animehub.data.model.Anime
import com.example.animehub.data.model.AnimeEpisode
import com.example.animehub.data.model.SourceType
import com.example.animehub.data.model.VideoSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreAnimeRepository {

    private val firestore = FirebaseFirestore.getInstance()

    // ---------------------------------------------------------------------------------------------
    // ANIME COLLECTION (Central Database)
    // ---------------------------------------------------------------------------------------------

    fun getAllAnimeFlow(): Flow<List<FirestoreAnime>> = callbackFlow {
        val listener = firestore.collection("anime")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    docToAnime(doc)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getPublishedAnimeFlow(): Flow<List<FirestoreAnime>> = callbackFlow {
        val listener = firestore.collection("anime")
            .whereEqualTo("isPublished", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    docToAnime(doc)
                }?.sortedByDescending { it.createdAt } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getAnimeById(animeId: Int): FirestoreAnime? {
        return try {
            val doc = firestore.collection("anime").document(animeId.toString()).get().await()
            if (doc.exists()) docToAnime(doc) else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveOrUpdateAnime(anime: FirestoreAnime): Result<Unit> {
        return try {
            val idStr = if (anime.id == 0) (System.currentTimeMillis() % 1000000).toInt().toString() else anime.id.toString()
            val targetId = idStr.toIntOrNull() ?: 1001

            val data = hashMapOf(
                "id" to targetId,
                "titleArabic" to anime.titleArabic,
                "titleEnglish" to anime.titleEnglish,
                "titleRomaji" to anime.titleRomaji,
                "description" to anime.description,
                "coverImage" to anime.coverImage,
                "bannerImage" to (anime.bannerImage ?: ""),
                "screenshots" to anime.screenshots,
                "genres" to anime.genres,
                "format" to anime.format,
                "status" to anime.status,
                "seasonYear" to (anime.seasonYear ?: 2026),
                "seasonName" to (anime.seasonName ?: "2026"),
                "studios" to anime.studios,
                "averageScore" to (anime.averageScore ?: 85),
                "viewsCount" to anime.viewsCount,
                "isPublished" to anime.isPublished,
                "isFeatured" to anime.isFeatured,
                "isNew" to anime.isNew,
                "createdAt" to (if (anime.createdAt == 0L) System.currentTimeMillis() else anime.createdAt),
                "updatedAt" to System.currentTimeMillis(),
                "seasonsCount" to anime.seasonsCount,
                "episodesCount" to anime.episodesCount
            )

            firestore.collection("anime").document(targetId.toString()).set(data).await()

            // Ensure at least Season 1 exists
            ensureDefaultSeasonExists(targetId)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAnime(animeId: Int): Result<Unit> {
        return try {
            firestore.collection("anime").document(animeId.toString()).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun togglePublishAnime(animeId: Int, isPublished: Boolean): Result<Unit> {
        return try {
            firestore.collection("anime").document(animeId.toString())
                .update("isPublished", isPublished).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleFeaturedAnime(animeId: Int, isFeatured: Boolean): Result<Unit> {
        return try {
            firestore.collection("anime").document(animeId.toString())
                .update("isFeatured", isFeatured).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun incrementAnimeViews(animeId: Int) {
        try {
            firestore.collection("anime").document(animeId.toString())
                .update("viewsCount", com.google.firebase.firestore.FieldValue.increment(1))
        } catch (ignored: Exception) {}
    }

    // ---------------------------------------------------------------------------------------------
    // SEASONS COLLECTION (Anime -> Seasons)
    // ---------------------------------------------------------------------------------------------

    private suspend fun ensureDefaultSeasonExists(animeId: Int) {
        try {
            val seasons = firestore.collection("anime").document(animeId.toString())
                .collection("seasons").get().await()
            if (seasons.isEmpty) {
                val s1 = FirestoreSeason(
                    id = "season_1",
                    animeId = animeId,
                    seasonNumber = 1,
                    title = "الموسم 1 (Season 1)",
                    episodesCount = 12,
                    order = 1
                )
                saveSeason(s1)
            }
        } catch (ignored: Exception) {}
    }

    fun getSeasonsFlow(animeId: Int): Flow<List<FirestoreSeason>> = callbackFlow {
        val listener = firestore.collection("anime").document(animeId.toString())
            .collection("seasons")
            .orderBy("seasonNumber")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    FirestoreSeason(
                        id = doc.id,
                        animeId = doc.getLong("animeId")?.toInt() ?: animeId,
                        seasonNumber = doc.getLong("seasonNumber")?.toInt() ?: 1,
                        title = doc.getString("title") ?: "الموسم 1",
                        coverImage = doc.getString("coverImage"),
                        episodesCount = doc.getLong("episodesCount")?.toInt() ?: 0,
                        order = doc.getLong("order")?.toInt() ?: 1
                    )
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveSeason(season: FirestoreSeason): Result<Unit> {
        return try {
            val id = if (season.id.isBlank()) "season_${season.seasonNumber}" else season.id
            val data = hashMapOf(
                "id" to id,
                "animeId" to season.animeId,
                "seasonNumber" to season.seasonNumber,
                "title" to season.title,
                "coverImage" to (season.coverImage ?: ""),
                "episodesCount" to season.episodesCount,
                "order" to season.order
            )
            firestore.collection("anime").document(season.animeId.toString())
                .collection("seasons").document(id).set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSeason(animeId: Int, seasonId: String): Result<Unit> {
        return try {
            firestore.collection("anime").document(animeId.toString())
                .collection("seasons").document(seasonId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------------------------------------------------------------------------------------
    // EPISODES & MULTI-SERVERS (Season -> Episodes)
    // ---------------------------------------------------------------------------------------------

    fun getEpisodesFlow(animeId: Int, seasonId: String): Flow<List<FirestoreEpisode>> = callbackFlow {
        val listener = firestore.collection("anime").document(animeId.toString())
            .collection("seasons").document(seasonId)
            .collection("episodes")
            .orderBy("episodeNumber")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    docToEpisode(doc, animeId, seasonId)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveEpisode(episode: FirestoreEpisode, sendNotification: Boolean = false, animeTitle: String = ""): Result<Unit> {
        return try {
            val id = if (episode.id.isBlank()) "ep_${episode.episodeNumber}" else episode.id

            val serversData = episode.servers.map { s ->
                hashMapOf(
                    "id" to s.id,
                    "name" to s.name,
                    "url" to s.url,
                    "quality" to s.quality,
                    "type" to s.type,
                    "isDefault" to s.isDefault
                )
            }

            val data = hashMapOf(
                "id" to id,
                "animeId" to episode.animeId,
                "seasonId" to episode.seasonId,
                "seasonNumber" to episode.seasonNumber,
                "episodeNumber" to episode.episodeNumber,
                "title" to episode.title,
                "description" to (episode.description ?: ""),
                "thumbnailUrl" to (episode.thumbnailUrl ?: ""),
                "durationMinutes" to (episode.durationMinutes ?: 24),
                "isPublished" to episode.isPublished,
                "defaultServerIndex" to episode.defaultServerIndex,
                "downloadUrl" to (episode.downloadUrl ?: ""),
                "createdAt" to (if (episode.createdAt == 0L) System.currentTimeMillis() else episode.createdAt),
                "servers" to serversData
            )

            firestore.collection("anime").document(episode.animeId.toString())
                .collection("seasons").document(episode.seasonId)
                .collection("episodes").document(id).set(data).await()

            // Update episodes count on season & anime
            updateEpisodeCounts(episode.animeId, episode.seasonId)

            if (sendNotification) {
                sendAppNotification(
                    AppNotification(
                        id = System.currentTimeMillis().toString(),
                        title = "حلقة جديدة متاحة الآن! 🎬",
                        message = "تمت إضافة الحلقة ${episode.episodeNumber} من أنمي $animeTitle",
                        animeId = episode.animeId,
                        animeTitle = animeTitle,
                        episodeNumber = episode.episodeNumber,
                        target = "all",
                        timestamp = System.currentTimeMillis()
                    )
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteEpisode(animeId: Int, seasonId: String, episodeId: String): Result<Unit> {
        return try {
            firestore.collection("anime").document(animeId.toString())
                .collection("seasons").document(seasonId)
                .collection("episodes").document(episodeId).delete().await()
            updateEpisodeCounts(animeId, seasonId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun updateEpisodeCounts(animeId: Int, seasonId: String) {
        try {
            val eps = firestore.collection("anime").document(animeId.toString())
                .collection("seasons").document(seasonId)
                .collection("episodes").get().await()

            val count = eps.size()
            firestore.collection("anime").document(animeId.toString())
                .collection("seasons").document(seasonId)
                .update("episodesCount", count).await()

            firestore.collection("anime").document(animeId.toString())
                .update("episodesCount", count).await()
        } catch (ignored: Exception) {}
    }

    // ---------------------------------------------------------------------------------------------
    // NOTIFICATIONS
    // ---------------------------------------------------------------------------------------------

    fun getNotificationsFlow(): Flow<List<AppNotification>> = callbackFlow {
        val listener = firestore.collection("notifications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(30)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    AppNotification(
                        id = doc.id,
                        title = doc.getString("title") ?: "إشعار جديد",
                        message = doc.getString("message") ?: "",
                        animeId = doc.getLong("animeId")?.toInt(),
                        animeTitle = doc.getString("animeTitle"),
                        episodeNumber = doc.getLong("episodeNumber")?.toInt(),
                        target = doc.getString("target") ?: "all",
                        timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                        isRead = doc.getBoolean("isRead") ?: false
                    )
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun sendAppNotification(notification: AppNotification): Result<Unit> {
        return try {
            val id = if (notification.id.isBlank()) System.currentTimeMillis().toString() else notification.id
            val data = hashMapOf(
                "id" to id,
                "title" to notification.title,
                "message" to notification.message,
                "animeId" to (notification.animeId ?: 0),
                "animeTitle" to (notification.animeTitle ?: ""),
                "episodeNumber" to (notification.episodeNumber ?: 1),
                "target" to notification.target,
                "timestamp" to notification.timestamp,
                "isRead" to false
            )
            firestore.collection("notifications").document(id).set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------------------------------------------------------------------------------------
    // STATS
    // ---------------------------------------------------------------------------------------------

    suspend fun calculateDashboardStats(): AdminDashboardStats {
        return try {
            val animeSnap = firestore.collection("anime").get().await()
            val animeList = animeSnap.documents.mapNotNull { docToAnime(it) }

            val totalAnime = animeList.size
            val publishedCount = animeList.count { it.isPublished }
            val totalViews = animeList.sumOf { it.viewsCount }

            val adminsSnap = firestore.collection("admins").get().await()
            val totalAdmins = adminsSnap.size()

            AdminDashboardStats(
                totalAnime = totalAnime,
                totalSeasons = animeList.sumOf { it.seasonsCount },
                totalEpisodes = animeList.sumOf { it.episodesCount },
                totalViews = totalViews,
                totalUsers = 1240 + totalViews.toInt() / 10,
                totalAdmins = if (totalAdmins > 0) totalAdmins else 1,
                publishedAnimeCount = publishedCount,
                recentAnime = animeList.take(5)
            )
        } catch (e: Exception) {
            AdminDashboardStats()
        }
    }

    // ---------------------------------------------------------------------------------------------
    // SEED INITIAL CURATED ANIME (If Database is empty)
    // ---------------------------------------------------------------------------------------------

    suspend fun seedInitialDataIfEmpty() {
        try {
            val snap = firestore.collection("anime").limit(1).get().await()
            if (snap.isEmpty) {
                val curatedList = CuratedInitialAnime.getInitialAnimeList()
                for (anime in curatedList) {
                    saveOrUpdateAnime(anime)
                    // Add Season 1 with sample episodes and multiple servers
                    val seasonId = "season_1"
                    val season = FirestoreSeason(
                        id = seasonId,
                        animeId = anime.id,
                        seasonNumber = 1,
                        title = "الموسم 1 (Season 1)",
                        episodesCount = 4,
                        order = 1
                    )
                    saveSeason(season)

                    // Add episodes with servers
                    for (epNum in 1..4) {
                        val servers = listOf(
                            FirestoreEpisodeServer(
                                id = "srv_1",
                                name = "سيرفر 1 (FHD Ultra - أساسي)",
                                url = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
                                quality = "1080p",
                                type = "HLS",
                                isDefault = true
                            ),
                            FirestoreEpisodeServer(
                                id = "srv_2",
                                name = "سيرفر 2 (Fast Stream HD)",
                                url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                                quality = "720p",
                                type = "MP4",
                                isDefault = false
                            ),
                            FirestoreEpisodeServer(
                                id = "srv_3",
                                name = "سيرفر 3 (سيرفر توفير البيانات)",
                                url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                                quality = "480p",
                                type = "MP4",
                                isDefault = false
                            )
                        )

                        val episode = FirestoreEpisode(
                            id = "ep_$epNum",
                            animeId = anime.id,
                            seasonId = seasonId,
                            seasonNumber = 1,
                            episodeNumber = epNum,
                            title = "الحلقة $epNum: بداية المغامرة والأحداث المشوقة",
                            description = "أحداث مثيرة وانطلاق رحلة الأبطال في مواجهة التحديات الأسطورية.",
                            thumbnailUrl = anime.bannerImage ?: anime.coverImage,
                            durationMinutes = 24,
                            isPublished = true,
                            defaultServerIndex = 0,
                            servers = servers
                        )
                        saveEpisode(episode, sendNotification = false)
                    }
                }
            }
        } catch (ignored: Exception) {}
    }

    // Helper converters
    @Suppress("UNCHECKED_CAST")
    private fun docToAnime(doc: com.google.firebase.firestore.DocumentSnapshot): FirestoreAnime {
        val id = doc.getLong("id")?.toInt() ?: doc.id.toIntOrNull() ?: 1001
        return FirestoreAnime(
            id = id,
            titleArabic = doc.getString("titleArabic") ?: "",
            titleEnglish = doc.getString("titleEnglish") ?: "",
            titleRomaji = doc.getString("titleRomaji") ?: "",
            description = doc.getString("description") ?: "",
            coverImage = doc.getString("coverImage") ?: "",
            bannerImage = doc.getString("bannerImage"),
            screenshots = (doc.get("screenshots") as? List<String>) ?: emptyList(),
            genres = (doc.get("genres") as? List<String>) ?: emptyList(),
            format = doc.getString("format") ?: "TV",
            status = doc.getString("status") ?: "مستمر",
            seasonYear = doc.getLong("seasonYear")?.toInt() ?: 2026,
            seasonName = doc.getString("seasonName") ?: "2026",
            studios = (doc.get("studios") as? List<String>) ?: emptyList(),
            averageScore = doc.getLong("averageScore")?.toInt() ?: 85,
            viewsCount = doc.getLong("viewsCount") ?: 0L,
            isPublished = doc.getBoolean("isPublished") ?: true,
            isFeatured = doc.getBoolean("isFeatured") ?: false,
            isNew = doc.getBoolean("isNew") ?: true,
            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
            updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis(),
            seasonsCount = doc.getLong("seasonsCount")?.toInt() ?: 1,
            episodesCount = doc.getLong("episodesCount")?.toInt() ?: 12
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun docToEpisode(
        doc: com.google.firebase.firestore.DocumentSnapshot,
        animeId: Int,
        seasonId: String
    ): FirestoreEpisode {
        val serversRaw = (doc.get("servers") as? List<Map<String, Any>>) ?: emptyList()
        val servers = serversRaw.map { map ->
            FirestoreEpisodeServer(
                id = map["id"] as? String ?: "srv_1",
                name = map["name"] as? String ?: "سيرفر 1 (HD)",
                url = map["url"] as? String ?: "",
                quality = map["quality"] as? String ?: "1080p",
                type = map["type"] as? String ?: "HLS",
                isDefault = map["isDefault"] as? Boolean ?: false
            )
        }

        return FirestoreEpisode(
            id = doc.id,
            animeId = doc.getLong("animeId")?.toInt() ?: animeId,
            seasonId = doc.getString("seasonId") ?: seasonId,
            seasonNumber = doc.getLong("seasonNumber")?.toInt() ?: 1,
            episodeNumber = doc.getLong("episodeNumber")?.toInt() ?: 1,
            title = doc.getString("title") ?: "الحلقة ${doc.getLong("episodeNumber") ?: 1}",
            description = doc.getString("description"),
            thumbnailUrl = doc.getString("thumbnailUrl"),
            durationMinutes = doc.getLong("durationMinutes")?.toInt() ?: 24,
            isPublished = doc.getBoolean("isPublished") ?: true,
            defaultServerIndex = doc.getLong("defaultServerIndex")?.toInt() ?: 0,
            downloadUrl = doc.getString("downloadUrl"),
            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
            servers = servers
        )
    }
}
