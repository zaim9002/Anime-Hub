package com.example.animehub.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.animehub.data.local.AnimeDatabase
import com.example.animehub.data.local.FavoriteEntity
import com.example.animehub.data.local.MyListEntity
import com.example.animehub.data.local.WatchHistoryEntity
import com.example.animehub.data.model.Anime
import com.example.animehub.data.model.MyListStatus
import com.example.animehub.data.remote.AnimeApiService
import com.example.animehub.data.remote.AnimeDetailResult
import com.example.animehub.data.remote.HomeFeedData
import com.example.animehub.data.repository.AnimeRepository
import com.example.animehub.ui.components.NavScreen
import com.example.animehub.ui.i18n.AppLanguage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AnimeHubViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = AnimeApiService(application)
    private val database = AnimeDatabase.getDatabase(application)
    val repository = AnimeRepository(apiService, database)

    // Splash State
    private val _isSplashScreen = MutableStateFlow(true)
    val isSplashScreen: StateFlow<Boolean> = _isSplashScreen.asStateFlow()

    fun dismissSplash() {
        _isSplashScreen.value = false
    }

    // Navigation State
    private val _currentNavScreen = MutableStateFlow(NavScreen.HOME)
    val currentNavScreen: StateFlow<NavScreen> = _currentNavScreen.asStateFlow()

    private val _selectedAnime = MutableStateFlow<Anime?>(null)
    val selectedAnime: StateFlow<Anime?> = _selectedAnime.asStateFlow()

    private val _playerState = MutableStateFlow<PlayerLaunchState?>(null)
    val playerState: StateFlow<PlayerLaunchState?> = _playerState.asStateFlow()

    fun setNavScreen(screen: NavScreen) {
        _selectedAnime.value = null
        _playerState.value = null
        _currentNavScreen.value = screen
    }

    fun openAnimeDetail(anime: Anime) {
        _selectedAnime.value = anime
        loadAnimeDetails(anime.id)
    }

    fun closeAnimeDetail() {
        _selectedAnime.value = null
    }

    fun openPlayer(anime: Anime, episodeNumber: Int, positionMs: Long = 0L) {
        _playerState.value = PlayerLaunchState(anime, episodeNumber, positionMs)
    }

    fun closePlayer() {
        _playerState.value = null
    }

    // Home Feed State
    private val _isHomeLoading = MutableStateFlow(true)
    val isHomeLoading: StateFlow<Boolean> = _isHomeLoading.asStateFlow()

    private val _homeFeed = MutableStateFlow<HomeFeedData?>(null)
    val homeFeed: StateFlow<HomeFeedData?> = _homeFeed.asStateFlow()

    private val _homeError = MutableStateFlow<String?>(null)
    val homeError: StateFlow<String?> = _homeError.asStateFlow()

    fun loadHomeFeed(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isHomeLoading.value = true
            _homeError.value = null
            val result = repository.getHomeFeed(forceRefresh)
            if (result.isSuccess) {
                _homeFeed.value = result.getOrNull()
            } else {
                _homeError.value = result.exceptionOrNull()?.message ?: "Failed to load"
            }
            _isHomeLoading.value = false
        }
    }

    // Explore Screen State
    private val _exploreGenre = MutableStateFlow<String?>(null)
    val exploreGenre: StateFlow<String?> = _exploreGenre.asStateFlow()

    private val _exploreFormat = MutableStateFlow<String?>(null)
    val exploreFormat: StateFlow<String?> = _exploreFormat.asStateFlow()

    private val _exploreList = MutableStateFlow<List<Anime>>(emptyList())
    val exploreList: StateFlow<List<Anime>> = _exploreList.asStateFlow()

    private val _isExploreLoading = MutableStateFlow(false)
    val isExploreLoading: StateFlow<Boolean> = _isExploreLoading.asStateFlow()

    fun setExploreGenre(genre: String?) {
        _exploreGenre.value = genre
        loadExploreData()
    }

    fun setExploreFormat(format: String?) {
        _exploreFormat.value = format
        loadExploreData()
    }

    private fun loadExploreData() {
        viewModelScope.launch {
            _isExploreLoading.value = true
            val result = repository.searchAnime(
                query = null,
                genre = _exploreGenre.value,
                format = _exploreFormat.value
            )
            _exploreList.value = result.getOrDefault(emptyList())
            _isExploreLoading.value = false
        }
    }

    // Search Screen State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchGenre = MutableStateFlow<String?>(null)
    val searchGenre: StateFlow<String?> = _searchGenre.asStateFlow()

    private val _searchYear = MutableStateFlow<Int?>(null)
    val searchYear: StateFlow<Int?> = _searchYear.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Anime>>(emptyList())
    val searchResults: StateFlow<List<Anime>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private var searchDebounceJob: Job? = null

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            delay(400) // Debounce 400ms
            performSearch()
        }
    }

    fun onSearchGenreChange(genre: String?) {
        _searchGenre.value = genre
        performSearch()
    }

    fun onSearchYearChange(year: Int?) {
        _searchYear.value = year
        performSearch()
    }

    private fun performSearch() {
        viewModelScope.launch {
            val q = _searchQuery.value
            val g = _searchGenre.value
            val y = _searchYear.value
            if (q.isBlank() && g == null && y == null) {
                _searchResults.value = emptyList()
                return@launch
            }
            _isSearching.value = true
            val result = repository.searchAnime(query = q.takeIf { it.isNotBlank() }, genre = g, year = y)
            _searchResults.value = result.getOrDefault(emptyList())
            _isSearching.value = false
        }
    }

    // Anime Detail State
    private val _detailResult = MutableStateFlow<AnimeDetailResult?>(null)
    val detailResult: StateFlow<AnimeDetailResult?> = _detailResult.asStateFlow()

    private val _isLoadingDetails = MutableStateFlow(false)
    val isLoadingDetails: StateFlow<Boolean> = _isLoadingDetails.asStateFlow()

    private fun loadAnimeDetails(animeId: Int) {
        viewModelScope.launch {
            _isLoadingDetails.value = true
            _detailResult.value = null
            val res = repository.getAnimeDetails(animeId)
            _detailResult.value = res.getOrNull()
            _isLoadingDetails.value = false
        }
    }

    // Room Database Flows
    val favoritesList: StateFlow<List<FavoriteEntity>> = repository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myListItems: StateFlow<List<MyListEntity>> = repository.getMyList()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val watchHistoryList: StateFlow<List<WatchHistoryEntity>> = repository.getWatchHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavorite(anime: Anime) {
        viewModelScope.launch {
            repository.toggleFavorite(anime)
        }
    }

    fun setMyListStatus(anime: Anime, status: MyListStatus) {
        viewModelScope.launch {
            repository.setMyListStatus(anime, status)
        }
    }

    fun removeFromMyList(animeId: Int) {
        viewModelScope.launch {
            repository.removeFromMyList(animeId)
        }
    }

    fun saveWatchProgress(animeId: Int, episodeNumber: Int, title: String, cover: String, posMs: Long, durMs: Long) {
        viewModelScope.launch {
            repository.saveWatchProgress(
                animeId = animeId,
                episodeNumber = episodeNumber,
                animeTitle = title,
                animeCover = cover,
                episodeTitle = "الحلقة $episodeNumber",
                positionMs = posMs,
                durationMs = durMs
            )
        }
    }

    fun clearWatchHistory() {
        viewModelScope.launch {
            repository.clearWatchHistory()
        }
    }

    // Settings State
    private val _appLanguage = MutableStateFlow(AppLanguage.ARABIC) // Arabic default
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true) // Dark Mode default
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _autoNextEp = MutableStateFlow(true)
    val autoNextEp: StateFlow<Boolean> = _autoNextEp.asStateFlow()

    fun setLanguage(lang: AppLanguage) {
        _appLanguage.value = lang
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    fun setAutoNextEp(enabled: Boolean) {
        _autoNextEp.value = enabled
    }

    init {
        loadHomeFeed()
        loadExploreData()
    }
}

data class PlayerLaunchState(
    val anime: Anime,
    val episodeNumber: Int,
    val startPositionMs: Long = 0L
)
