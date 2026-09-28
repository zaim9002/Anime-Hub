package com.example.animehub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.animehub.data.local.FavoriteEntity
import com.example.animehub.data.local.MyListEntity
import com.example.animehub.data.local.WatchHistoryEntity
import com.example.animehub.data.model.Anime
import com.example.animehub.data.model.MyListStatus
import com.example.animehub.ui.components.AnimeCard
import com.example.animehub.ui.i18n.LocalizedStrings
import com.example.ui.theme.AnimePrimary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary

@Composable
fun LibraryScreen(
    favorites: List<FavoriteEntity>,
    myList: List<MyListEntity>,
    history: List<WatchHistoryEntity>,
    strings: LocalizedStrings,
    onAnimeIdClick: (Int) -> Unit,
    onContinueHistoryClick: (WatchHistoryEntity) -> Unit,
    onClearHistory: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedMyListFilter by remember { mutableStateOf<MyListStatus?>(null) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(top = 16.dp)
    ) {
        // Screen Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.library,
                color = DarkTextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            if (selectedTab == 2 && history.isNotEmpty()) {
                IconButton(onClick = { showClearHistoryDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear History",
                        tint = DarkTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tabs: Favorites, My List, History
        val tabs = listOf(strings.addToFavorites, strings.addToList, strings.history)
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkBackground,
            contentColor = AnimePrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = AnimePrimary
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTab == index) AnimePrimary else DarkTextSecondary,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            0 -> {
                // Favorites Grid
                if (favorites.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.emptyList,
                            color = DarkTextSecondary,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 110.dp),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(favorites, key = { it.animeId }) { fav ->
                            val syntheticAnime = Anime(
                                id = fav.animeId,
                                titleEnglish = fav.title,
                                titleRomaji = fav.title,
                                titleNative = "",
                                coverImage = fav.coverImage,
                                bannerImage = null,
                                description = "",
                                averageScore = null,
                                format = fav.format,
                                status = "",
                                episodesCount = null,
                                durationMinutes = null,
                                genres = if (fav.genres.isNotBlank()) fav.genres.split(", ") else emptyList(),
                                season = null,
                                seasonYear = null
                            )
                            AnimeCard(
                                anime = syntheticAnime,
                                cardWidth = 110,
                                cardHeight = 165,
                                onClick = { onAnimeIdClick(fav.animeId) }
                            )
                        }
                    }
                }
            }
            1 -> {
                // My List (with status filters)
                val filteredList = if (selectedMyListFilter == null) {
                    myList
                } else {
                    myList.filter { it.status == selectedMyListFilter!!.name }
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterStatusChip(
                                label = "الكل (${myList.size})",
                                isSelected = selectedMyListFilter == null,
                                onClick = { selectedMyListFilter = null }
                            )
                        }
                        MyListStatus.values().forEach { st ->
                            val count = myList.count { it.status == st.name }
                            val label = when (st) {
                                MyListStatus.WATCHING -> "${strings.watching} ($count)"
                                MyListStatus.COMPLETED -> "${strings.completed} ($count)"
                                MyListStatus.PLAN_TO_WATCH -> "${strings.planToWatch} ($count)"
                                MyListStatus.ON_HOLD -> "${strings.onHold} ($count)"
                                MyListStatus.DROPPED -> "${strings.dropped} ($count)"
                            }
                            item {
                                FilterStatusChip(
                                    label = label,
                                    isSelected = selectedMyListFilter == st,
                                    onClick = { selectedMyListFilter = st }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (filteredList.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = strings.emptyList,
                                color = DarkTextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 110.dp),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredList, key = { it.animeId }) { item ->
                                val syntheticAnime = Anime(
                                    id = item.animeId,
                                    titleEnglish = item.title,
                                    titleRomaji = item.title,
                                    titleNative = "",
                                    coverImage = item.coverImage,
                                    bannerImage = null,
                                    description = "",
                                    averageScore = null,
                                    format = item.status,
                                    status = "",
                                    episodesCount = null,
                                    durationMinutes = null,
                                    genres = emptyList(),
                                    season = null,
                                    seasonYear = null
                                )
                                AnimeCard(
                                    anime = syntheticAnime,
                                    cardWidth = 110,
                                    cardHeight = 165,
                                    onClick = { onAnimeIdClick(item.animeId) }
                                )
                            }
                        }
                    }
                }
            }
            2 -> {
                // Watch History List
                if (history.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.emptyList,
                            color = DarkTextSecondary,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(history, key = { "${it.animeId}_${it.episodeNumber}" }) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DarkCard)
                                    .clickable { onContinueHistoryClick(item) }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 80.dp, height = 55.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DarkSurface)
                                ) {
                                    AsyncImage(
                                        model = item.animeCover,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.35f))
                                    )
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .align(Alignment.Center)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.animeTitle,
                                        color = DarkTextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "الحلقة ${item.episodeNumber}",
                                        color = AnimePrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { (item.progressPercent / 100f).coerceIn(0f, 1f) },
                                        color = AnimePrimary,
                                        trackColor = DarkCardBorder,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(3.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            containerColor = DarkSurface,
            title = {
                Text(text = strings.clearHistory, color = DarkTextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(text = strings.clearHistoryConfirm, color = DarkTextSecondary)
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearHistory()
                        showClearHistoryDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimePrimary)
                ) {
                    Text(text = "مسح", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text(text = strings.cancel, color = DarkTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun FilterStatusChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) AnimePrimary else DarkCard,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) AnimePrimary else DarkCardBorder
        ),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else DarkTextSecondary,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
