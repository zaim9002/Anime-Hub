package com.example.animehub.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.animehub.data.admin.AdminAuthManager
import com.example.animehub.data.firestore.FirestoreAnimeRepository
import com.example.animehub.data.model.Anime
import com.example.animehub.data.repository.VideoSourceRepository
import com.example.animehub.ui.admin.AdminDashboardScreen
import com.example.animehub.ui.admin.AdminLoginDialog
import com.example.animehub.ui.components.BottomNavBar
import com.example.animehub.ui.components.NavScreen
import com.example.animehub.ui.i18n.AppLanguage
import com.example.animehub.ui.i18n.LocalizedStrings
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
import com.example.ui.theme.DarkBackground

@Composable
fun AnimeHubApp(
    viewModel: AnimeHubViewModel = viewModel()
) {
    val context = LocalContext.current
    val authManager = remember { AdminAuthManager(context) }
    val firestoreRepo = remember { FirestoreAnimeRepository(context) }
    val videoSourceRepo = remember { VideoSourceRepository() }

    val isSplash by viewModel.isSplashScreen.collectAsState()
    val currentNavScreen by viewModel.currentNavScreen.collectAsState()
    val selectedAnime by viewModel.selectedAnime.collectAsState()
    val playerState by viewModel.playerState.collectAsState()

    val homeFeed by viewModel.homeFeed.collectAsState()
    val isHomeLoading by viewModel.isHomeLoading.collectAsState()
    val homeError by viewModel.homeError.collectAsState()

    val exploreGenre by viewModel.exploreGenre.collectAsState()
    val exploreFormat by viewModel.exploreFormat.collectAsState()
    val exploreList by viewModel.exploreList.collectAsState()
    val isExploreLoading by viewModel.isExploreLoading.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchGenre by viewModel.searchGenre.collectAsState()
    val searchYear by viewModel.searchYear.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    val detailResult by viewModel.detailResult.collectAsState()
    val isLoadingDetails by viewModel.isLoadingDetails.collectAsState()

    val favorites by viewModel.favoritesList.collectAsState()
    val myList by viewModel.myListItems.collectAsState()
    val watchHistory by viewModel.watchHistoryList.collectAsState()

    val currentLang by viewModel.appLanguage.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val autoNextEp by viewModel.autoNextEp.collectAsState()

    val strings = if (currentLang == AppLanguage.ARABIC) LocalizedStrings.Arabic else LocalizedStrings.English
    val layoutDirection = if (currentLang == AppLanguage.ARABIC) LayoutDirection.Rtl else LayoutDirection.Ltr

    var showAdminLoginDialog by remember { mutableStateOf(false) }
    var isAdminScreenActive by remember { mutableStateOf(false) }

    // Android Hardware Back Handler stack
    BackHandler(enabled = playerState != null || selectedAnime != null || isAdminScreenActive || currentNavScreen != NavScreen.HOME) {
        when {
            playerState != null -> viewModel.closePlayer()
            selectedAnime != null -> viewModel.closeAnimeDetail()
            isAdminScreenActive -> isAdminScreenActive = false
            currentNavScreen != NavScreen.HOME -> viewModel.setNavScreen(NavScreen.HOME)
        }
    }

    AnimeHubTheme(darkTheme = isDarkMode) {
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBackground)
            ) {
                if (isSplash) {
                    SplashScreen(
                        strings = strings,
                        onSplashFinished = { viewModel.dismissSplash() }
                    )
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = DarkBackground,
                        bottomBar = {
                            if (selectedAnime == null && playerState == null && !isAdminScreenActive) {
                                BottomNavBar(
                                    currentScreen = currentNavScreen,
                                    onScreenSelected = { screen -> viewModel.setNavScreen(screen) },
                                    strings = strings
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            // Primary Tab Destinations
                            when (currentNavScreen) {
                                NavScreen.HOME -> {
                                    HomeScreen(
                                        isLoading = isHomeLoading,
                                        homeFeed = homeFeed,
                                        watchHistory = watchHistory,
                                        errorMessage = homeError,
                                        strings = strings,
                                        onRetry = { viewModel.loadHomeFeed(forceRefresh = true) },
                                        onAnimeClick = { anime -> viewModel.openAnimeDetail(anime) },
                                        onContinueWatchingClick = { history ->
                                            val anime = Anime(
                                                id = history.animeId,
                                                titleArabic = history.animeTitle,
                                                titleEnglish = history.animeTitle,
                                                coverImage = history.animeCover
                                            )
                                            viewModel.openPlayer(anime, history.episodeNumber, history.positionMs)
                                        },
                                        onWatchHeroClick = { anime ->
                                            viewModel.openPlayer(anime, 1, 0L)
                                        },
                                        onAddHeroToListClick = { anime ->
                                            viewModel.toggleFavorite(anime)
                                        },
                                        onSeeAllCategory = { _ ->
                                            viewModel.setNavScreen(NavScreen.EXPLORE)
                                        }
                                    )
                                }
                                NavScreen.EXPLORE -> {
                                    ExploreScreen(
                                        selectedGenre = exploreGenre,
                                        selectedFormat = exploreFormat,
                                        animeList = exploreList,
                                        isLoading = isExploreLoading,
                                        strings = strings,
                                        onGenreSelected = { genre -> viewModel.setExploreGenre(genre) },
                                        onFormatSelected = { format -> viewModel.setExploreFormat(format) },
                                        onAnimeClick = { anime -> viewModel.openAnimeDetail(anime) }
                                    )
                                }
                                NavScreen.SEARCH -> {
                                    SearchScreen(
                                        searchQuery = searchQuery,
                                        onQueryChange = { q -> viewModel.onSearchQueryChange(q) },
                                        selectedGenre = searchGenre,
                                        onGenreChange = { g -> viewModel.onSearchGenreChange(g) },
                                        selectedYear = searchYear,
                                        onYearChange = { y -> viewModel.onSearchYearChange(y) },
                                        searchResults = searchResults,
                                        isSearching = isSearching,
                                        strings = strings,
                                        onAnimeClick = { anime -> viewModel.openAnimeDetail(anime) }
                                    )
                                }
                                NavScreen.LIBRARY -> {
                                    LibraryScreen(
                                        favorites = favorites,
                                        myList = myList,
                                        history = watchHistory,
                                        strings = strings,
                                        onAnimeIdClick = { id ->
                                            val anime = Anime(id = id, titleArabic = "أنمي #$id", titleEnglish = "Anime #$id", coverImage = "")
                                            viewModel.openAnimeDetail(anime)
                                        },
                                        onContinueHistoryClick = { hist ->
                                            val anime = Anime(id = hist.animeId, titleArabic = hist.animeTitle, titleEnglish = hist.animeTitle, coverImage = hist.animeCover)
                                            viewModel.openPlayer(anime, hist.episodeNumber, hist.positionMs)
                                        },
                                        onClearHistory = { viewModel.clearWatchHistory() }
                                    )
                                }
                                NavScreen.PROFILE -> {
                                    ProfileScreen(
                                        favoritesCount = favorites.size,
                                        myListCount = myList.size,
                                        historyCount = watchHistory.size,
                                        currentLanguage = currentLang,
                                        isDarkMode = isDarkMode,
                                        autoNextEp = autoNextEp,
                                        strings = strings,
                                        onLanguageChange = { lang -> viewModel.setLanguage(lang) },
                                        onThemeToggle = { dark -> viewModel.setDarkMode(dark) },
                                        onAutoNextEpToggle = { auto -> viewModel.setAutoNextEp(auto) },
                                        onClearCache = { viewModel.clearWatchHistory() },
                                        onOpenAdmin = {
                                            if (authManager.isLoggedIn()) {
                                                isAdminScreenActive = true
                                            } else {
                                                showAdminLoginDialog = true
                                            }
                                        }
                                    )
                                }
                            }

                            // Secondary Stack Screens (Detail, Player, Admin)
                            selectedAnime?.let { anime ->
                                val isFav = favorites.any { it.animeId == anime.id }
                                val status = myList.find { it.animeId == anime.id }?.status
                                val hist = watchHistory.find { it.animeId == anime.id }

                                AnimeDetailScreen(
                                    anime = anime,
                                    detailResult = detailResult,
                                    isLoadingDetails = isLoadingDetails,
                                    isFavorite = isFav,
                                    myListStatus = status,
                                    watchHistory = hist,
                                    strings = strings,
                                    onBackClick = { viewModel.closeAnimeDetail() },
                                    onToggleFavorite = { viewModel.toggleFavorite(anime) },
                                    onSetMyListStatus = { st -> viewModel.setMyListStatus(anime, st) },
                                    onRemoveFromMyList = { viewModel.removeFromMyList(anime.id) },
                                    onPlayEpisode = { epNum ->
                                        val startPos = if (hist?.episodeNumber == epNum) hist.positionMs else 0L
                                        viewModel.openPlayer(anime, epNum, startPos)
                                    },
                                    onRelatedAnimeClick = { related ->
                                        viewModel.openAnimeDetail(related)
                                    }
                                )
                            }

                            playerState?.let { ps ->
                                PlayerScreen(
                                    anime = ps.anime,
                                    initialEpisode = ps.episodeNumber,
                                    initialPositionMs = ps.startPositionMs,
                                    videoSourceRepo = videoSourceRepo,
                                    strings = strings,
                                    onBackClick = { viewModel.closePlayer() },
                                    onSaveProgress = { ep, pos, dur ->
                                        viewModel.saveWatchProgress(
                                            animeId = ps.anime.id,
                                            episodeNumber = ep,
                                            title = ps.anime.displayTitle,
                                            cover = ps.anime.coverImage,
                                            posMs = pos,
                                            durMs = dur
                                        )
                                    }
                                )
                            }

                            if (isAdminScreenActive) {
                                AdminDashboardScreen(
                                    authManager = authManager,
                                    repository = firestoreRepo,
                                    onExitAdmin = { isAdminScreenActive = false }
                                )
                            }
                        }
                    }

                    // Admin Login Dialog Modal
                    if (showAdminLoginDialog) {
                        AdminLoginDialog(
                            authManager = authManager,
                            onDismiss = { showAdminLoginDialog = false },
                            onLoginSuccess = {
                                showAdminLoginDialog = false
                                isAdminScreenActive = true
                            }
                        )
                    }
                }
            }
        }
    }
}
