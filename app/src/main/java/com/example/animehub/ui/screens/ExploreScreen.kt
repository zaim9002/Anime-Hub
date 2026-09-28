package com.example.animehub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animehub.data.model.Anime
import com.example.animehub.data.remote.AniListQueries
import com.example.animehub.ui.components.AnimeCard
import com.example.animehub.ui.components.SkeletonCard
import com.example.animehub.ui.i18n.LocalizedStrings
import com.example.ui.theme.AnimePrimary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary

@Composable
fun ExploreScreen(
    selectedGenre: String?,
    selectedFormat: String?,
    animeList: List<Anime>,
    isLoading: Boolean,
    strings: LocalizedStrings,
    onGenreSelected: (String?) -> Unit,
    onFormatSelected: (String?) -> Unit,
    onAnimeClick: (Anime) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(top = 16.dp)
    ) {
        // Title
        Text(
            text = strings.explore,
            color = DarkTextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Format selector (All, TV, Movie)
        val formats = listOf(
            null to "الكل",
            "TV" to "مسلسلات TV",
            "MOVIE" to "أفلام",
            "OVA" to "OVA"
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(formats) { (formatCode, label) ->
                val isSelected = selectedFormat == formatCode
                Surface(
                    color = if (isSelected) AnimePrimary else DarkCard,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) AnimePrimary else DarkCardBorder
                    ),
                    modifier = Modifier
                        .clickable { onFormatSelected(formatCode) }
                        .testTag("format_chip_$label")
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else DarkTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Genres horizontal scroll chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                val isAllSelected = selectedGenre == null
                Surface(
                    color = if (isAllSelected) AnimePrimary else DarkCard,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isAllSelected) AnimePrimary else DarkCardBorder
                    ),
                    modifier = Modifier.clickable { onGenreSelected(null) }
                ) {
                    Text(
                        text = strings.allGenres,
                        color = if (isAllSelected) Color.White else DarkTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            items(AniListQueries.GENRES) { genre ->
                val isSelected = selectedGenre == genre
                Surface(
                    color = if (isSelected) AnimePrimary else DarkCard,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) AnimePrimary else DarkCardBorder
                    ),
                    modifier = Modifier.clickable { onGenreSelected(genre) }
                ) {
                    Text(
                        text = genre,
                        color = if (isSelected) Color.White else DarkTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Grid of Anime Cards
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AnimePrimary)
            }
        } else if (animeList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = strings.emptyList,
                    color = DarkTextSecondary,
                    fontSize = 15.sp
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
                items(animeList, key = { it.id }) { anime ->
                    AnimeCard(
                        anime = anime,
                        cardWidth = 110,
                        cardHeight = 165,
                        onClick = { onAnimeClick(anime) }
                    )
                }
            }
        }
    }
}
