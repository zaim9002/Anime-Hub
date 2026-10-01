package com.example.keyboard.ui.components

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.keyboard.model.GifItem
import com.example.keyboard.model.KeyboardPresets
import com.example.keyboard.model.KeyboardTheme

@Composable
fun GifPickerView(
    theme: KeyboardTheme,
    onGifSelected: (GifItem) -> Unit,
    onBackToAlpha: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("All", "Reaction", "Happy", "Love", "Anime", "Meme")
    var selectedCategory by remember { mutableStateOf("All") }

    val gifs = KeyboardPresets.SAMPLE_GIFS
    val filtered = if (selectedCategory == "All") gifs else gifs.filter { it.category == selectedCategory }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(Color(theme.backgroundColor))
    ) {
        // Categories Tabs
        LazyRow(
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(theme.surfaceColor))
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    color = if (isSelected) Color(theme.accentColor) else Color(theme.keyBackgroundColor),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.clickable { selectedCategory = cat }
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) Color.White else Color(theme.keyTextColor),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // GIF Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(filtered, key = { it.id }) { item ->
                Box(
                    modifier = Modifier
                        .height(95.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(theme.keyBackgroundColor))
                        .clickable { onGifSelected(item) }
                ) {
                    AsyncImage(
                        model = item.previewUrl,
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(4.dp)
                    ) {
                        Text(
                            text = item.title,
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Bottom Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(theme.surfaceColor))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(theme.specialKeyColor),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .clickable(onClick = onBackToAlpha)
                    .height(38.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = "ABC",
                        tint = Color(theme.specialKeyTextColor),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "العودة للوحة المفاتيح",
                        color = Color(theme.specialKeyTextColor),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
