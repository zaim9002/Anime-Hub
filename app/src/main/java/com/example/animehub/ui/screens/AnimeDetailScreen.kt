package com.example.animehub.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.animehub.data.local.WatchHistoryEntity
import com.example.animehub.data.model.Anime
import com.example.animehub.data.model.AnimeCharacter
import com.example.animehub.data.model.AnimeEpisode
import com.example.animehub.data.model.MyListStatus
import com.example.animehub.data.remote.AnimeDetailResult
import com.example.animehub.ui.components.AnimeCard
import com.example.animehub.ui.i18n.LocalizedStrings
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AnimePrimary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DarkTextMuted
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnimeDetailScreen(
    anime: Anime,
    detailResult: AnimeDetailResult?,
    isLoadingDetails: Boolean,
    isFavorite: Boolean,
    myListStatus: MyListStatus?,
    watchHistory: WatchHistoryEntity?,
    strings: LocalizedStrings,
    onBackClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onSetMyListStatus: (MyListStatus) -> Unit,
    onRemoveFromMyList: () -> Unit,
    onPlayEpisode: (Int) -> Unit,
    onRelatedAnimeClick: (Anime) -> Unit
) {
    BackHandler { onBackClick() }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var isDescriptionExpanded by remember { mutableStateOf(false) }
    var showMyListMenu by remember { mutableStateOf(false) }

    val displayAnime = detailResult?.anime ?: anime
    val episodes = detailResult?.episodes ?: (1..(anime.episodesCount ?: 12)).map {
        AnimeEpisode(number = it, title = "الحلقة $it", durationMinutes = anime.durationMinutes ?: 24)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Hero Banner & Cover section
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    val bannerSource = displayAnime.bannerImage ?: displayAnime.coverImage
                    AsyncImage(
                        model = bannerSource,
                        contentDescription = displayAnime.displayTitle,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Transparent,
                                        DarkBackground.copy(alpha = 0.85f),
                                        DarkBackground
                                    )
                                )
                            )
                    )

                    // Back Button
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopStart)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            }

            // Info Header: Cover + Titles + Status badges
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-40).dp)
                ) {
                    // Floating Poster
                    Box(
                        modifier = Modifier
                            .width(115.dp)
                            .height(170.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkCard)
                            .border(2.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = displayAnime.coverImage,
                            contentDescription = displayAnime.displayTitle,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 45.dp)
                    ) {
                        Text(
                            text = displayAnime.displayTitle,
                            color = DarkTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (displayAnime.titleNative.isNotBlank() && displayAnime.titleNative != displayAnime.displayTitle) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = displayAnime.titleNative,
                                color = DarkTextSecondary,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Score & Format Badges
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = Color.Black.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(4.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = AccentGold,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = displayAnime.formattedScore,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Surface(
                                color = AnimePrimary.copy(alpha = 0.85f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = displayAnime.format,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            val statusArabic = when (displayAnime.status) {
                                "RELEASING" -> "يعرض حالياً"
                                "NOT_YET_RELEASED" -> "قريباً"
                                else -> "مكتمل"
                            }
                            Surface(
                                color = DarkCard,
                                shape = RoundedCornerShape(4.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                            ) {
                                Text(
                                    text = statusArabic,
                                    color = DarkTextSecondary,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Studio & Episodes Count
                        Spacer(modifier = Modifier.height(6.dp))
                        val studioName = displayAnime.studios.firstOrNull() ?: "الاستوديو غير محدد"
                        Text(
                            text = "$studioName • ${displayAnime.episodesCount ?: "?"} حلقة",
                            color = DarkTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Quick Actions Bar: Watch / Resume, Favorite, My List
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-20).dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val resumeEp = watchHistory?.episodeNumber ?: 1
                    Button(
                        onClick = { onPlayEpisode(resumeEp) },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimePrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .testTag("detail_play_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (watchHistory != null) "استئناف ح ${watchHistory.episodeNumber}" else strings.watchNow,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Favorite Button
                    OutlinedButton(
                        onClick = onToggleFavorite,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isFavorite) AnimePrimary.copy(alpha = 0.15f) else DarkCard,
                            contentColor = if (isFavorite) AnimePrimary else Color.White
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isFavorite) AnimePrimary else DarkCardBorder
                        ),
                        modifier = Modifier
                            .weight(0.9f)
                            .height(44.dp)
                            .testTag("detail_favorite_button")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) AccentGold else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isFavorite) strings.inFavorites else strings.addToFavorites,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // My List Dropdown Selector
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { showMyListMenu = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (myListStatus != null) AnimePrimary.copy(alpha = 0.15f) else DarkCard,
                                contentColor = if (myListStatus != null) AnimePrimary else Color.White
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (myListStatus != null) AnimePrimary else DarkCardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("detail_mylist_button")
                        ) {
                            Icon(
                                imageVector = if (myListStatus != null) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = "My List",
                                tint = if (myListStatus != null) AnimePrimary else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (myListStatus) {
                                    MyListStatus.WATCHING -> strings.watching
                                    MyListStatus.COMPLETED -> strings.completed
                                    MyListStatus.PLAN_TO_WATCH -> strings.planToWatch
                                    MyListStatus.ON_HOLD -> strings.onHold
                                    MyListStatus.DROPPED -> strings.dropped
                                    null -> strings.addToList
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }

                        DropdownMenu(
                            expanded = showMyListMenu,
                            onDismissRequest = { showMyListMenu = false },
                            modifier = Modifier.background(DarkSurface)
                        ) {
                            MyListStatus.values().forEach { status ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = when (status) {
                                                MyListStatus.WATCHING -> strings.watching
                                                MyListStatus.COMPLETED -> strings.completed
                                                MyListStatus.PLAN_TO_WATCH -> strings.planToWatch
                                                MyListStatus.ON_HOLD -> strings.onHold
                                                MyListStatus.DROPPED -> strings.dropped
                                            },
                                            color = if (myListStatus == status) AnimePrimary else DarkTextPrimary
                                        )
                                    },
                                    onClick = {
                                        onSetMyListStatus(status)
                                        showMyListMenu = false
                                    }
                                )
                            }
                            if (myListStatus != null) {
                                DropdownMenuItem(
                                    text = { Text("إزالة من قائمتي", color = Color.Red) },
                                    onClick = {
                                        onRemoveFromMyList()
                                        showMyListMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Genre Chips
            if (displayAnime.genres.isNotEmpty()) {
                item {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        displayAnime.genres.forEach { genre ->
                            Surface(
                                color = DarkCard,
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                            ) {
                                Text(
                                    text = genre,
                                    color = DarkTextSecondary,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Description / Synopsis
            if (displayAnime.description.isNotBlank()) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable { isDescriptionExpanded = !isDescriptionExpanded }
                    ) {
                        Text(
                            text = strings.synopsis,
                            color = DarkTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = displayAnime.description,
                            color = DarkTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (isDescriptionExpanded) "عرض أقل" else "قراءة المزيد...",
                            color = AnimePrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // Tabs: Episodes (الحلقات), Characters (الشخصيات), Related (أعمال مشابهة)
            item {
                Spacer(modifier = Modifier.height(16.dp))
                val tabTitles = listOf(strings.episodes, strings.characters, strings.related)
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = DarkBackground,
                    contentColor = AnimePrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = AnimePrimary
                        )
                    }
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    color = if (selectedTabIndex == index) AnimePrimary else DarkTextSecondary,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        )
                    }
                }
            }

            // Tab Content
            when (selectedTabIndex) {
                0 -> {
                    // Episodes Tab
                    items(episodes, key = { it.number }) { ep ->
                        val isWatched = watchHistory != null && watchHistory.episodeNumber >= ep.number
                        val isCurrent = watchHistory != null && watchHistory.episodeNumber == ep.number

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPlayEpisode(ep.number) }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                                .testTag("episode_item_${ep.number}"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = 75.dp, height = 50.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkCard)
                                    .border(1.dp, DarkCardBorder, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = displayAnime.coverImage,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.4f))
                                )
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = if (isCurrent) AnimePrimary else Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ep.title,
                                    color = if (isCurrent) AnimePrimary else DarkTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${ep.durationMinutes ?: 24} دقيقة",
                                    color = DarkTextMuted,
                                    fontSize = 11.sp
                                )
                            }

                            if (isWatched) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Watched",
                                    tint = AnimePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // Characters Tab
                    val characters = detailResult?.characters.orEmpty()
                    if (characters.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isLoadingDetails) "جاري جلب قائمة الشخصيات..." else "لا توجد تفاصيل شخصيات متاحة حالياً",
                                    color = DarkTextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        items(characters, key = { it.id }) { character ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(DarkCard)
                                ) {
                                    AsyncImage(
                                        model = character.imageUrl,
                                        contentDescription = character.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = character.name,
                                        color = DarkTextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    if (!character.nativeName.isNullOrBlank()) {
                                        Text(
                                            text = character.nativeName,
                                            color = DarkTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Related & Recommendations Tab
                    val relatedList = (detailResult?.related.orEmpty() + detailResult?.recommendations.orEmpty()).distinctBy { it.id }
                    if (relatedList.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isLoadingDetails) "جاري جلب الأعمال المقترحة..." else "لا توجد أعمال مشابهة مسجلة",
                                    color = DarkTextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(relatedList, key = { it.id }) { relAnime ->
                                    AnimeCard(
                                        anime = relAnime,
                                        cardWidth = 115,
                                        cardHeight = 170,
                                        onClick = { onRelatedAnimeClick(relAnime) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
