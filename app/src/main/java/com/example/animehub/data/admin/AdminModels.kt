package com.example.animehub.data.admin

enum class AdminRole(val displayNameArabic: String, val level: Int) {
    SUPER_ADMIN("مدير عام (Super Admin)", 3),
    ADMIN("مدير محتوى (Admin)", 2),
    EDITOR("محرر (Editor)", 1);

    fun canManageAdmins(): Boolean = this == SUPER_ADMIN
    fun canDeleteContent(): Boolean = this == SUPER_ADMIN || this == ADMIN
    fun canEditContent(): Boolean = true
    fun canPublish(): Boolean = this == SUPER_ADMIN || this == ADMIN
}

data class AdminUser(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val role: AdminRole = AdminRole.ADMIN,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
)

data class FirestoreAnime(
    val id: Int = 0,
    val titleArabic: String = "",
    val titleEnglish: String = "",
    val titleRomaji: String = "",
    val description: String = "",
    val coverImage: String = "",
    val bannerImage: String? = null,
    val screenshots: List<String> = emptyList(),
    val genres: List<String> = emptyList(),
    val format: String = "TV",
    val status: String = "مستمر", // مستمر / مكتمل
    val seasonYear: Int? = 2026,
    val seasonName: String? = "خريف 2026",
    val studios: List<String> = emptyList(),
    val averageScore: Int? = 85,
    val viewsCount: Long = 0L,
    val isPublished: Boolean = true,
    val isFeatured: Boolean = false,
    val isNew: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val seasonsCount: Int = 1,
    val episodesCount: Int = 12
) {
    val displayTitle: String
        get() = titleArabic.ifBlank { titleEnglish.ifBlank { titleRomaji.ifBlank { "أنمي #$id" } } }

    val formattedScore: String
        get() = averageScore?.let { "${(it / 10.0)}" } ?: "8.5"
}

data class FirestoreSeason(
    val id: String = "",
    val animeId: Int = 0,
    val seasonNumber: Int = 1,
    val title: String = "الموسم 1",
    val coverImage: String? = null,
    val episodesCount: Int = 0,
    val order: Int = 1
)

data class FirestoreEpisodeServer(
    val id: String = "",
    val name: String = "Server 1 (HD)",
    val url: String = "",
    val quality: String = "1080p",
    val type: String = "HLS", // HLS or MP4
    val isDefault: Boolean = false
)

data class FirestoreEpisode(
    val id: String = "",
    val animeId: Int = 0,
    val seasonId: String = "",
    val seasonNumber: Int = 1,
    val episodeNumber: Int = 1,
    val title: String = "الحلقة 1",
    val description: String? = null,
    val thumbnailUrl: String? = null,
    val durationMinutes: Int? = 24,
    val isPublished: Boolean = true,
    val defaultServerIndex: Int = 0,
    val downloadUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val servers: List<FirestoreEpisodeServer> = emptyList()
)

data class AppNotification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val animeId: Int? = null,
    val animeTitle: String? = null,
    val episodeNumber: Int? = null,
    val target: String = "all", // all / watchers
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

data class AdminDashboardStats(
    val totalAnime: Int = 0,
    val totalSeasons: Int = 0,
    val totalEpisodes: Int = 0,
    val totalViews: Long = 0L,
    val totalUsers: Int = 0,
    val totalAdmins: Int = 0,
    val publishedAnimeCount: Int = 0,
    val recentAnime: List<FirestoreAnime> = emptyList(),
    val recentEpisodes: List<FirestoreEpisode> = emptyList()
)
