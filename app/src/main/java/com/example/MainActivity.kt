package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.animehub.data.model.Anime
import com.example.animehub.data.model.MyListStatus
import com.example.animehub.ui.components.BottomNavBar
import com.example.animehub.ui.components.NavScreen
import com.example.animehub.ui.i18n.AppLanguage
import com.example.animehub.ui.i18n.LocalizationManager
import com.example.animehub.ui.screens.AnimeDetailScreen
import com.example.animehub.ui.screens.ExploreScreen
import com.example.animehub.ui.screens.HomeScreen
import com.example.animehub.ui.screens.LibraryScreen
import com.example.animehub.ui.screens.PlayerScreen
import com.example.animehub.ui.screens.ProfileScreen
import com.example.animehub.ui.screens.SearchScreen
import com.example.animehub.ui.screens.SplashScreen
import com.example.animehub.ui.viewmodel.AnimeHubViewModel
import com.example.ui.theme.AnimeHubTheme

class MainActivity : ComponentActivity() {

    private val viewModel: AnimeHubViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by viewModel.isDarkMode.collectAsState()
            val language by viewModel.appLanguage.collectAsState()
            val strings = remember(language) { LocalizationManager.getStrings(language) }

            AnimeHubTheme(darkTheme = isDarkTheme) {
                CompositionLocalProvider(
                    LocalLayoutDirection provides if (language == AppLanguage.ARABIC) LayoutDirection.Rtl else LayoutDirection.Ltr
                ) {
                    AnimeHubApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AnimeHubApp(viewModel: AnimeHubViewModel) {
    val isSplash by viewModel.isSplashScreen.collectAsState()
    val language by viewModel.appLanguage.collectAsState()
    val strings = remember(language) { LocalizationManager.getStrings(language) }
    val currentNavScreen by viewModel.currentNavScreen.collectAsState()
    val selectedAnime by viewModel.selectedAnime.collectAsState()
    val playerState by viewModel.playerState.collectAsState()

    if (isSplash) {
        SplashScreen(
            strings = strings,
            onSplashFinished = { viewModel.dismissSplash() }
        )
        return
    }

    // Active Fullscreen Video Player
    if (playerState != null) {
        val pState = playerState!!
        PlayerScreen(
            anime = pState.anime,
            initialEpisode = pState.episodeNumber,
            initialPositionMs = pState.startPositionMs,
            videoSourceRepo = viewModel.repository.videoSourceRepo,
            strings = strings,
            onBackClick = { viewModel.closePlayer() },
            onSaveProgress = { epNum, posMs, durMs ->
                viewModel.saveWatchProgress(
                    animeId = pState.anime.id,
                    episodeNumber = epNum,
                    title = pState.anime.displayTitle,
                    cover = pState.anime.coverImage,
                    posMs = posMs,
                    durMs = durMs
                )
            }
        )
        return
    }

    // Anime Detail View
    if (selectedAnime != null) {
        val anime = selectedAnime!!
        val detailResult by viewModel.detailResult.collectAsState()
        val isLoadingDetails by viewModel.isLoadingDetails.collectAsState()
        val favorites by viewModel.favoritesList.collectAsState()
        val myList by viewModel.myListItems.collectAsState()
        val history by viewModel.watchHistoryList.collectAsState()

        val isFav = favorites.any { it.animeId == anime.id }
        val myListItem = myList.firstOrNull { it.animeId == anime.id }
        val myListStatus = myListItem?.let {
            try {
                MyListStatus.valueOf(it.status)
            } catch (e: Exception) {
                null
            }
        }
        val animeHistory = history.firstOrNull { it.animeId == anime.id }

        AnimeDetailScreen(
            anime = anime,
            detailResult = detailResult,
            isLoadingDetails = isLoadingDetails,
            isFavorite = isFav,
            myListStatus = myListStatus,
            watchHistory = animeHistory,
            strings = strings,
            onBackClick = { viewModel.closeAnimeDetail() },
            onToggleFavorite = { viewModel.toggleFavorite(anime) },
            onSetMyListStatus = { status -> viewModel.setMyListStatus(anime, status) },
            onRemoveFromMyList = { viewModel.removeFromMyList(anime.id) },
            onPlayEpisode = { epNum ->
                val resumePos = if (animeHistory != null && animeHistory.episodeNumber == epNum) {
                    animeHistory.positionMs
                } else 0L
                viewModel.openPlayer(anime, epNum, resumePos)
            },
            onRelatedAnimeClick = { relatedAnime ->
                viewModel.openAnimeDetail(relatedAnime)
            }
        )
        return
    }

    // Back button behavior for main navigation tabs
    BackHandler(enabled = currentNavScreen != NavScreen.HOME) {
        viewModel.setNavScreen(NavScreen.HOME)
    }

    // Main App with Bottom Navigation Bar
    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentScreen = currentNavScreen,
                onScreenSelected = { screen -> viewModel.setNavScreen(screen) },
                strings = strings
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when (currentNavScreen) {
                NavScreen.HOME -> {
                    val isHomeLoading by viewModel.isHomeLoading.collectAsState()
                    val homeFeed by viewModel.homeFeed.collectAsState()
                    val homeError by viewModel.homeError.collectAsState()
                    val history by viewModel.watchHistoryList.collectAsState()

                    HomeScreen(
                        isLoading = isHomeLoading,
                        homeFeed = homeFeed,
                        watchHistory = history,
                        errorMessage = homeError,
                        strings = strings,
                        onRetry = { viewModel.loadHomeFeed(forceRefresh = true) },
                        onAnimeClick = { anime -> viewModel.openAnimeDetail(anime) },
                        onContinueWatchingClick = { item ->
                            val syntheticAnime = Anime(
                                id = item.animeId,
                                titleEnglish = item.animeTitle,
                                titleRomaji = item.animeTitle,
                                titleNative = "",
                                coverImage = item.animeCover,
                                bannerImage = null,
                                description = "",
                                averageScore = null,
                                format = "TV",
                                status = "",
                                episodesCount = 24,
                                durationMinutes = 24,
                                genres = emptyList(),
                                season = null,
                                seasonYear = null
                            )
                            viewModel.openPlayer(syntheticAnime, item.episodeNumber, item.positionMs)
                        },
                        onWatchHeroClick = { heroAnime ->
                            viewModel.openPlayer(heroAnime, 1, 0L)
                        },
                        onAddHeroToListClick = { heroAnime ->
                            viewModel.setMyListStatus(heroAnime, MyListStatus.PLAN_TO_WATCH)
                        },
                        onSeeAllCategory = { _ ->
                            viewModel.setNavScreen(NavScreen.EXPLORE)
                        }
                    )
                }

                NavScreen.EXPLORE -> {
                    val genre by viewModel.exploreGenre.collectAsState()
                    val format by viewModel.exploreFormat.collectAsState()
                    val list by viewModel.exploreList.collectAsState()
                    val isLoading by viewModel.isExploreLoading.collectAsState()

                    ExploreScreen(
                        selectedGenre = genre,
                        selectedFormat = format,
                        animeList = list,
                        isLoading = isLoading,
                        strings = strings,
                        onGenreSelected = { newGenre -> viewModel.setExploreGenre(newGenre) },
                        onFormatSelected = { newFormat -> viewModel.setExploreFormat(newFormat) },
                        onAnimeClick = { anime -> viewModel.openAnimeDetail(anime) }
                    )
                }

                NavScreen.SEARCH -> {
                    val query by viewModel.searchQuery.collectAsState()
                    val genre by viewModel.searchGenre.collectAsState()
                    val year by viewModel.searchYear.collectAsState()
                    val results by viewModel.searchResults.collectAsState()
                    val isSearching by viewModel.isSearching.collectAsState()

                    SearchScreen(
                        searchQuery = query,
                        onQueryChange = { q -> viewModel.onSearchQueryChange(q) },
                        selectedGenre = genre,
                        onGenreChange = { g -> viewModel.onSearchGenreChange(g) },
                        selectedYear = year,
                        onYearChange = { y -> viewModel.onSearchYearChange(y) },
                        searchResults = results,
                        isSearching = isSearching,
                        strings = strings,
                        onAnimeClick = { anime -> viewModel.openAnimeDetail(anime) }
                    )
                }

                NavScreen.LIBRARY -> {
                    val favorites by viewModel.favoritesList.collectAsState()
                    val myList by viewModel.myListItems.collectAsState()
                    val history by viewModel.watchHistoryList.collectAsState()

                    LibraryScreen(
                        favorites = favorites,
                        myList = myList,
                        history = history,
                        strings = strings,
                        onAnimeIdClick = { id ->
                            viewModel.openAnimeDetail(
                                Anime(
                                    id = id,
                                    titleEnglish = "",
                                    titleRomaji = "",
                                    titleNative = "",
                                    coverImage = "",
                                    bannerImage = null,
                                    description = "",
                                    averageScore = null,
                                    format = "TV",
                                    status = "",
                                    episodesCount = null,
                                    durationMinutes = null,
                                    genres = emptyList(),
                                    season = null,
                                    seasonYear = null
                                )
                            )
                        },
                        onContinueHistoryClick = { item ->
                            val syntheticAnime = Anime(
                                id = item.animeId,
                                titleEnglish = item.animeTitle,
                                titleRomaji = item.animeTitle,
                                titleNative = "",
                                coverImage = item.animeCover,
                                bannerImage = null,
                                description = "",
                                averageScore = null,
                                format = "TV",
                                status = "",
                                episodesCount = 24,
                                durationMinutes = 24,
                                genres = emptyList(),
                                season = null,
                                seasonYear = null
                            )
                            viewModel.openPlayer(syntheticAnime, item.episodeNumber, item.positionMs)
                        },
                        onClearHistory = { viewModel.clearWatchHistory() }
                    )
                }

                NavScreen.PROFILE -> {
                    val favorites by viewModel.favoritesList.collectAsState()
                    val myList by viewModel.myListItems.collectAsState()
                    val history by viewModel.watchHistoryList.collectAsState()
                    val isDarkMode by viewModel.isDarkMode.collectAsState()
                    val autoNextEp by viewModel.autoNextEp.collectAsState()

                    ProfileScreen(
                        favoritesCount = favorites.size,
                        myListCount = myList.size,
                        historyCount = history.size,
                        currentLanguage = language,
                        isDarkMode = isDarkMode,
                        autoNextEp = autoNextEp,
                        strings = strings,
                        onLanguageChange = { lang -> viewModel.setLanguage(lang) },
                        onThemeToggle = { isDark -> viewModel.setDarkMode(isDark) },
                        onAutoNextEpToggle = { autoNext -> viewModel.setAutoNextEp(autoNext) },
                        onClearCache = {
                            viewModel.loadHomeFeed(forceRefresh = true)
                        }
                    )
                }
            }
        }
    }
}
