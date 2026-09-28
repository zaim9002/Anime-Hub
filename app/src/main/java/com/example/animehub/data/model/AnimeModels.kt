package com.example.animehub.data.model

data class Anime(
    val id: Int,
    val titleEnglish: String,
    val titleRomaji: String,
    val titleNative: String,
    val coverImage: String,
    val bannerImage: String?,
    val description: String,
    val averageScore: Int?,
    val format: String,
    val status: String,
    val episodesCount: Int?,
    val durationMinutes: Int?,
    val genres: List<String>,
    val season: String?,
    val seasonYear: Int?,
    val studios: List<String> = emptyList(),
    val nextAiringEpisode: Int? = null,
    val nextAiringTime: Long? = null
) {
    val displayTitle: String
        get() = titleEnglish.ifBlank { titleRomaji.ifBlank { titleNative } }

    val formattedScore: String
        get() = averageScore?.let { "${(it / 10.0)}" } ?: "N/A"
}

data class AnimeEpisode(
    val number: Int,
    val title: String,
    val thumbnail: String? = null,
    val durationMinutes: Int? = null
)

data class AnimeCharacter(
    val id: Int,
    val name: String,
    val nativeName: String?,
    val imageUrl: String,
    val role: String
)

data class VideoSource(
    val id: String,
    val animeId: Int,
    val episodeNumber: Int,
    val url: String,
    val type: SourceType = SourceType.HLS,
    val quality: String = "1080p",
    val title: String = "خادم أساسي HD",
    val subtitles: List<SubtitleTrack> = emptyList(),
    val headers: Map<String, String> = emptyMap()
)

enum class SourceType {
    HLS,
    MP4
}

data class SubtitleTrack(
    val id: String,
    val language: String,
    val label: String,
    val url: String,
    val isDefault: Boolean = false
)

enum class MyListStatus {
    WATCHING,
    COMPLETED,
    PLAN_TO_WATCH,
    ON_HOLD,
    DROPPED
}
