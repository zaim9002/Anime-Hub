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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keyboard.model.KeyboardPresets
import com.example.keyboard.model.KeyboardTheme

@Composable
fun EmojiPickerView(
    theme: KeyboardTheme,
    onEmojiSelected: (String) -> Unit,
    onBackspace: () -> Unit,
    onBackToAlpha: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val categories = KeyboardPresets.EMOJI_CATEGORIES

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(Color(theme.backgroundColor))
    ) {
        // Categories Tabs Header
        LazyRow(
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(theme.surfaceColor))
        ) {
            itemsIndexed(categories) { index, (catName, _) ->
                val isSelected = selectedCategoryIndex == index
                Surface(
                    color = if (isSelected) Color(theme.accentColor) else Color(theme.keyBackgroundColor),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.clickable { selectedCategoryIndex = index }
                ) {
                    Text(
                        text = catName,
                        color = if (isSelected) Color.White else Color(theme.keyTextColor),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Emoji Grid
        val currentEmojis = categories[selectedCategoryIndex].second
        val isKaomoji = categories[selectedCategoryIndex].first.contains("Kaomoji")

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyVerticalGrid(
                columns = if (isKaomoji) GridCells.Fixed(3) else GridCells.Adaptive(minSize = 38.dp),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(currentEmojis) { item ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onEmojiSelected(item) }
                            .padding(vertical = if (isKaomoji) 8.dp else 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item,
                            fontSize = if (isKaomoji) 13.sp else 24.sp,
                            color = Color(theme.keyTextColor)
                        )
                    }
                }
            }
        }

        // Bottom Controls Bar (Return to ABC & Backspace)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(theme.surfaceColor))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
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
                        text = "ABC",
                        color = Color(theme.specialKeyTextColor),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            IconButton(
                onClick = onBackspace,
                modifier = Modifier
                    .size(width = 54.dp, height = 38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(theme.specialKeyColor))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Delete",
                    tint = Color(theme.specialKeyTextColor),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
