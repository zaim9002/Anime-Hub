package com.example.animehub.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animehub.data.model.Anime
import com.example.ui.theme.AnimePrimary
import com.example.ui.theme.DarkTextPrimary

@Composable
fun HorizontalAnimeRow(
    title: String,
    animeList: List<Anime>,
    onAnimeClick: (Anime) -> Unit,
    onSeeAllClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (animeList.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = DarkTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            if (onSeeAllClick != null) {
                Text(
                    text = "عرض الكل",
                    color = AnimePrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable(onClick = onSeeAllClick)
                        .padding(4.dp)
                        .testTag("see_all_${title.take(5)}")
                )
            }
        }

        // Horizontal List
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(
                items = animeList,
                key = { it.id }
            ) { anime ->
                AnimeCard(
                    anime = anime,
                    onClick = { onAnimeClick(anime) }
                )
            }
        }
    }
}
