package com.example.animehub.data.remote

import android.content.Context
import android.util.Log
import com.example.animehub.data.model.Anime
import com.example.animehub.data.model.AnimeCharacter
import com.example.animehub.data.model.AnimeEpisode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

class AnimeApiService(context: Context) {

    private val cacheDir = File(context.cacheDir, "http_anime_cache")
    private val client: OkHttpClient = OkHttpClient.Builder()
        .cache(Cache(cacheDir, 20L * 1024 * 1024)) // 20 MB disk cache
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val anilistUrl = "https://graphql.anilist.co"
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun getHomeFeed(): Result<HomeFeedData> = withContext(Dispatchers.IO) {
        try {
            val variables = JSONObject().apply {
                put("page", 1)
                put("perPage", 14)
            }
            val payload = JSONObject().apply {
                put("query", AniListQueries.HOME_FEED_QUERY)
                put("variables", variables)
            }

            val request = Request.Builder()
                .url(anilistUrl)
                .post(payload.toString().toRequestBody(jsonMediaType))
                .header("Accept", "application/json")
                .build()

            val response = client.newCall(request).execute()
            val bodyString = response.body?.string() ?: ""
            if (!response.isSuccessful || bodyString.isBlank()) {
                // Try fallback to Jikan if AniList is unreachable
                return@withContext fetchHomeFeedFromJikan()
            }

            val json = JSONObject(bodyString)
            val data = json.optJSONObject("data") ?: return@withContext fetchHomeFeedFromJikan()

            val trendingList = parseMediaList(data.optJSONObject("trending")?.optJSONArray("media"))
            val popularList = parseMediaList(data.optJSONObject("popular")?.optJSONArray("media"))
            val airingList = parseMediaList(data.optJSONObject("airing")?.optJSONArray("media"))
            val moviesList = parseMediaList(data.optJSONObject("movies")?.optJSONArray("media"))

            Result.success(
                HomeFeedData(
                    trending = trendingList,
                    popular = popularList,
                    airing = airingList,
                    movies = moviesList
                )
            )
        } catch (e: Exception) {
            Log.e("AnimeApiService", "AniList home feed failed, trying Jikan fallback", e)
            fetchHomeFeedFromJikan()
        }
    }

    suspend fun searchAnime(
        query: String?,
        genre: String? = null,
        year: Int? = null,
        status: String? = null,
        format: String? = null,
        page: Int = 1,
        perPage: Int = 20
    ): Result<List<Anime>> = withContext(Dispatchers.IO) {
        try {
            val variables = JSONObject().apply {
                if (!query.isNullOrBlank()) put("search", query.trim())
                if (!genre.isNullOrBlank()) put("genre", genre.trim())
                if (year != null && year > 0) put("year", year)
                if (!status.isNullOrBlank()) put("status", status)
                if (!format.isNullOrBlank()) put("format", format)
                put("page", page)
                put("perPage", perPage)
                val sortArr = JSONArray()
                if (query.isNullOrBlank()) {
                    sortArr.put("POPULARITY_DESC")
                } else {
                    sortArr.put("SEARCH_MATCH")
                }
                put("sort", sortArr)
            }

            val payload = JSONObject().apply {
                put("query", AniListQueries.SEARCH_QUERY)
                put("variables", variables)
            }

            val request = Request.Builder()
                .url(anilistUrl)
                .post(payload.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""
            if (!response.isSuccessful || body.isBlank()) {
                return@withContext fetchSearchFromJikan(query, genre)
            }

            val json = JSONObject(body)
            val mediaArray = json.optJSONObject("data")
                ?.optJSONObject("Page")
                ?.optJSONArray("media")

            val list = parseMediaList(mediaArray)
            Result.success(list)
        } catch (e: Exception) {
            Log.e("AnimeApiService", "Search failed, trying Jikan fallback", e)
            fetchSearchFromJikan(query, genre)
        }
    }

    suspend fun getAnimeDetails(animeId: Int): Result<AnimeDetailResult> = withContext(Dispatchers.IO) {
        try {
            val variables = JSONObject().apply {
                put("id", animeId)
            }
            val payload = JSONObject().apply {
                put("query", AniListQueries.DETAILS_QUERY)
                put("variables", variables)
            }

            val request = Request.Builder()
                .url(anilistUrl)
                .post(payload.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""
            if (!response.isSuccessful || body.isBlank()) {
                return@withContext Result.failure(Exception("Failed to load anime details"))
            }

            val json = JSONObject(body)
            val media = json.optJSONObject("data")?.optJSONObject("Media")
                ?: return@withContext Result.failure(Exception("Anime not found"))

            val anime = parseSingleAnime(media)
            val characters = parseCharacters(media.optJSONObject("characters")?.optJSONArray("nodes"))
            val related = parseMediaList(media.optJSONObject("relations")?.optJSONArray("nodes"))

            // Recommendations
            val recArray = media.optJSONObject("recommendations")?.optJSONArray("nodes")
            val recList = mutableListOf<Anime>()
            if (recArray != null) {
                for (i in 0 until recArray.length()) {
                    val recObj = recArray.optJSONObject(i)?.optJSONObject("mediaRecommendation")
                    if (recObj != null) {
                        recList.add(parseSingleAnime(recObj))
                    }
                }
            }

            // Generate episode models based on episode count
            val episodesCount = anime.episodesCount ?: 12
            val episodesList = (1..episodesCount.coerceIn(1, 100)).map { epNum ->
                AnimeEpisode(
                    number = epNum,
                    title = "الحلقة $epNum",
                    durationMinutes = anime.durationMinutes ?: 24
                )
            }

            Result.success(
                AnimeDetailResult(
                    anime = anime,
                    characters = characters,
                    related = related,
                    recommendations = recList,
                    episodes = episodesList
                )
            )
        } catch (e: Exception) {
            Log.e("AnimeApiService", "Error getting anime details", e)
            Result.failure(e)
        }
    }

    // Jikan API Fallback Implementations
    private fun fetchHomeFeedFromJikan(): Result<HomeFeedData> {
        try {
            val request = Request.Builder()
                .url("https://api.jikan.moe/v4/top/anime?filter=bypopularity&limit=14")
                .build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""
            val json = JSONObject(body)
            val dataArray = json.optJSONArray("data")
            val list = parseJikanList(dataArray)

            return Result.success(
                HomeFeedData(
                    trending = list.take(8),
                    popular = list,
                    airing = list.filter { it.status == "RELEASING" },
                    movies = list.filter { it.format == "MOVIE" }
                )
            )
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private fun fetchSearchFromJikan(query: String?, genre: String?): Result<List<Anime>> {
        try {
            val encodedQuery = java.net.URLEncoder.encode(query ?: (genre ?: "anime"), "UTF-8")
            val request = Request.Builder()
                .url("https://api.jikan.moe/v4/anime?q=$encodedQuery&limit=20")
                .build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""
            val json = JSONObject(body)
            val dataArray = json.optJSONArray("data")
            return Result.success(parseJikanList(dataArray))
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private fun parseMediaList(array: JSONArray?): List<Anime> {
        if (array == null) return emptyList()
        val list = mutableListOf<Anime>()
        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            try {
                list.add(parseSingleAnime(obj))
            } catch (e: Exception) {
                // skip malformed
            }
        }
        return list
    }

    private fun parseSingleAnime(obj: JSONObject): Anime {
        val id = obj.optInt("id", 0)
        val titleObj = obj.optJSONObject("title")
        val romaji = titleObj?.optString("romaji") ?: ""
        val english = titleObj?.optString("english") ?: romaji
        val native = titleObj?.optString("native") ?: ""

        val coverObj = obj.optJSONObject("coverImage")
        val cover = coverObj?.optString("extraLarge")
            ?: coverObj?.optString("large")
            ?: coverObj?.optString("medium") ?: ""

        val banner = obj.optString("bannerImage", "")

        val rawDesc = obj.optString("description", "")
        val cleanDesc = rawDesc.replace(Regex("<[^>]*>"), "")

        val score = if (obj.has("averageScore") && !obj.isNull("averageScore")) {
            obj.optInt("averageScore")
        } else null

        val format = obj.optString("format", "TV")
        val status = obj.optString("status", "FINISHED")
        val episodes = if (obj.has("episodes") && !obj.isNull("episodes")) obj.optInt("episodes") else null
        val duration = if (obj.has("duration") && !obj.isNull("duration")) obj.optInt("duration") else 24

        val genresArray = obj.optJSONArray("genres")
        val genres = mutableListOf<String>()
        if (genresArray != null) {
            for (g in 0 until genresArray.length()) {
                genres.add(genresArray.optString(g))
            }
        }

        val studiosList = mutableListOf<String>()
        val studioNodes = obj.optJSONObject("studios")?.optJSONArray("nodes")
        if (studioNodes != null) {
            for (s in 0 until studioNodes.length()) {
                studiosList.add(studioNodes.optJSONObject(s)?.optString("name") ?: "")
            }
        }

        val nextEpObj = obj.optJSONObject("nextAiringEpisode")
        val nextEp = nextEpObj?.optInt("episode")
        val nextAirTime = nextEpObj?.optLong("airingAt")

        return Anime(
            id = id,
            titleEnglish = english,
            titleRomaji = romaji,
            titleNative = native,
            coverImage = cover,
            bannerImage = banner.ifBlank { null },
            description = cleanDesc,
            averageScore = score,
            format = format,
            status = status,
            episodesCount = episodes,
            durationMinutes = duration,
            genres = genres,
            season = obj.optString("season", ""),
            seasonYear = obj.optInt("seasonYear", 0),
            studios = studiosList.filter { it.isNotBlank() },
            nextAiringEpisode = nextEp,
            nextAiringTime = nextAirTime
        )
    }

    private fun parseCharacters(array: JSONArray?): List<AnimeCharacter> {
        if (array == null) return emptyList()
        val list = mutableListOf<AnimeCharacter>()
        for (i in 0 until array.length()) {
            val node = array.optJSONObject(i) ?: continue
            val id = node.optInt("id")
            val nameObj = node.optJSONObject("name")
            val name = nameObj?.optString("full") ?: "Unknown"
            val native = nameObj?.optString("native")
            val img = node.optJSONObject("image")?.optString("large")
                ?: node.optJSONObject("image")?.optString("medium") ?: ""
            list.add(
                AnimeCharacter(
                    id = id,
                    name = name,
                    nativeName = native,
                    imageUrl = img,
                    role = "MAIN"
                )
            )
        }
        return list
    }

    private fun parseJikanList(array: JSONArray?): List<Anime> {
        if (array == null) return emptyList()
        val list = mutableListOf<Anime>()
        for (i in 0 until array.length()) {
            val item = array.optJSONObject(i) ?: continue
            val id = item.optInt("mal_id")
            val title = item.optString("title")
            val titleEnglish = item.optString("title_english", title)
            val titleJapanese = item.optString("title_japanese", "")

            val images = item.optJSONObject("images")?.optJSONObject("jpg")
            val cover = images?.optString("large_image_url") ?: images?.optString("image_url") ?: ""

            val synopsis = item.optString("synopsis", "")
            val scoreVal = item.optDouble("score", 0.0)
            val averageScore = if (scoreVal > 0) (scoreVal * 10).toInt() else null

            val type = item.optString("type", "TV")
            val status = item.optString("status", "")
            val episodes = if (item.has("episodes") && !item.isNull("episodes")) item.optInt("episodes") else null

            list.add(
                Anime(
                    id = id,
                    titleEnglish = titleEnglish,
                    titleRomaji = title,
                    titleNative = titleJapanese,
                    coverImage = cover,
                    bannerImage = null,
                    description = synopsis,
                    averageScore = averageScore,
                    format = type,
                    status = if (status.contains("Airing", true)) "RELEASING" else "FINISHED",
                    episodesCount = episodes,
                    durationMinutes = 24,
                    genres = emptyList(),
                    season = null,
                    seasonYear = null
                )
            )
        }
        return list
    }
}

data class HomeFeedData(
    val trending: List<Anime>,
    val popular: List<Anime>,
    val airing: List<Anime>,
    val movies: List<Anime>
)

data class AnimeDetailResult(
    val anime: Anime,
    val characters: List<AnimeCharacter>,
    val related: List<Anime>,
    val recommendations: List<Anime>,
    val episodes: List<AnimeEpisode>
)
