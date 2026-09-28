package com.example.animehub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animehub.data.local.WatchHistoryEntity
import com.example.animehub.data.model.Anime
import com.example.animehub.data.remote.HomeFeedData
import com.example.animehub.ui.components.ContinueWatchingRow
import com.example.animehub.ui.components.HeroBanner
import com.example.animehub.ui.components.HorizontalAnimeRow
import com.example.animehub.ui.components.SkeletonHeroBanner
import com.example.animehub.ui.components.SkeletonRow
import com.example.animehub.ui.i18n.LocalizedStrings
import com.example.ui.theme.AnimePrimary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary

@Composable
fun HomeScreen(
    isLoading: Boolean,
    homeFeed: HomeFeedData?,
    watchHistory: List<WatchHistoryEntity>,
    errorMessage: String?,
    strings: LocalizedStrings,
    onRetry: () -> Unit,
    onAnimeClick: (Anime) -> Unit,
    onContinueWatchingClick: (WatchHistoryEntity) -> Unit,
    onWatchHeroClick: (Anime) -> Unit,
    onAddHeroToListClick: (Anime) -> Unit,
    onSeeAllCategory: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        if (isLoading && homeFeed == null) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item { SkeletonHeroBanner() }
                item { SkeletonRow() }
                item { SkeletonRow() }
                item { SkeletonRow() }
            }
        } else if (errorMessage != null && homeFeed == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Text(
                    text = strings.errorLoading,
                    color = DarkTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = AnimePrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = strings.retry, color = Color.White)
                }
            }
        } else if (homeFeed != null) {
            val heroAnime = homeFeed.trending.firstOrNull() ?: homeFeed.popular.firstOrNull()

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // Hero Banner
                if (heroAnime != null) {
                    item {
                        HeroBanner(
                            anime = heroAnime,
                            strings = strings,
                            onWatchClick = { onWatchHeroClick(heroAnime) },
                            onAddToListClick = { onAddHeroToListClick(heroAnime) },
                            onBannerClick = { onAnimeClick(heroAnime) }
                        )
                    }
                }

                // Continue Watching Row (if user has active history)
                if (watchHistory.isNotEmpty()) {
                    item {
                        ContinueWatchingRow(
                            title = strings.continueWatching,
                            historyList = watchHistory,
                            onItemClick = onContinueWatchingClick
                        )
                    }
                }

                // Trending Now
                if (homeFeed.trending.isNotEmpty()) {
                    item {
                        HorizontalAnimeRow(
                            title = strings.trending,
                            animeList = homeFeed.trending,
                            onAnimeClick = onAnimeClick,
                            onSeeAllClick = { onSeeAllCategory("TRENDING") }
                        )
                    }
                }

                // Popular Anime
                if (homeFeed.popular.isNotEmpty()) {
                    item {
                        HorizontalAnimeRow(
                            title = strings.popular,
                            animeList = homeFeed.popular,
                            onAnimeClick = onAnimeClick,
                            onSeeAllClick = { onSeeAllCategory("POPULAR") }
                        )
                    }
                }

                // Airing Now
                if (homeFeed.airing.isNotEmpty()) {
                    item {
                        HorizontalAnimeRow(
                            title = strings.airingNow,
                            animeList = homeFeed.airing,
                            onAnimeClick = onAnimeClick,
                            onSeeAllClick = { onSeeAllCategory("AIRING") }
                        )
                    }
                }

                // Movies
                if (homeFeed.movies.isNotEmpty()) {
                    item {
                        HorizontalAnimeRow(
                            title = strings.movies,
                            animeList = homeFeed.movies,
                            onAnimeClick = onAnimeClick,
                            onSeeAllClick = { onSeeAllCategory("MOVIES") }
                        )
                    }
                }

                // Bottom padding so content is not hidden by BottomNavBar
                item {
                    Spacer(modifier = Modifier.height(90.dp))
                }
            }
        }
    }
}
