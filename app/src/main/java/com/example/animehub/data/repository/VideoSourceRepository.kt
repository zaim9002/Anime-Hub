package com.example.animehub.data.repository

import com.example.animehub.data.model.SourceType
import com.example.animehub.data.model.SubtitleTrack
import com.example.animehub.data.model.VideoSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VideoSourceRepository {

    // Configurable custom stream override (e.g. set in Settings or Player)
    private val _customStreamUrl = MutableStateFlow<String?>(null)
    val customStreamUrl: StateFlow<String?> = _customStreamUrl.asStateFlow()

    fun setCustomStreamUrl(url: String?) {
        _customStreamUrl.value = url?.takeIf { it.isNotBlank() }
    }

    /**
     * Standard Open-licensed streams for compliant player verification and demonstration.
     * Tears of Steel and Sintel are CC licensed open anime/sci-fi productions with genuine multi-bitrate HLS and WebVTT subs.
     */
    val licensedSampleSources: List<VideoSource> = listOf(
        VideoSource(
            id = "sample-hls-1",
            animeId = 0,
            episodeNumber = 1,
            url = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
            type = SourceType.HLS,
            quality = "1080p Multi-Bitrate",
            title = "بث تجريبي مرخص - HLS متعدد الجودات",
            subtitles = listOf(
                SubtitleTrack(
                    id = "sub-ar",
                    language = "ar",
                    label = "العربية (تجريبية)",
                    url = "https://bitmovin-a.akamaihd.net/content/sintel/subtitles/subtitles_en.vtt",
                    isDefault = true
                ),
                SubtitleTrack(
                    id = "sub-en",
                    language = "en",
                    label = "English",
                    url = "https://bitmovin-a.akamaihd.net/content/sintel/subtitles/subtitles_en.vtt"
                )
            )
        ),
        VideoSource(
            id = "sample-mp4-1",
            animeId = 0,
            episodeNumber = 1,
            url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            type = SourceType.MP4,
            quality = "720p HD",
            title = "بث تجريبي مباشر - MP4 مفتوح المصدر"
        )
    )

    /**
     * Retrieves video source for a specific anime and episode.
     * In compliance with guidelines, if no private CDN / custom source is configured for this specific anime,
     * this returns null so the UI can inform the user gracefully without fake links or pirated scraping.
     */
    fun getSourceForEpisode(animeId: Int, episodeNumber: Int): VideoSource? {
        val custom = _customStreamUrl.value
        if (!custom.isNullOrBlank()) {
            val isHls = custom.contains(".m3u8", ignoreCase = true)
            return VideoSource(
                id = "custom-$animeId-$episodeNumber",
                animeId = animeId,
                episodeNumber = episodeNumber,
                url = custom,
                type = if (isHls) SourceType.HLS else SourceType.MP4,
                quality = "Custom Source",
                title = "مصدر البث المخصص"
            )
        }
        return null
    }
}
