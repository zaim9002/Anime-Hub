package com.example.keyboard.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keyboard.model.KeyboardPresets
import com.example.keyboard.model.KeyboardTheme
import com.example.keyboard.storage.KeyboardPreferences
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary

@Composable
fun ThemesScreen(
    preferences: KeyboardPreferences,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var selectedThemeId by remember { mutableStateOf(preferences.currentThemeId) }
    val themes = KeyboardPresets.THEMES

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "الثيمات والمظهر",
                    color = DarkTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "اختر مظهر الكيبورد المفضل لديك مع التبديل الفوري",
                    color = Color(0xFF00E5FF),
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(themes, key = { it.id }) { theme ->
                val isSelected = selectedThemeId == theme.id

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(theme.backgroundColor)),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) Color(0xFF00E5FF) else Color(theme.specialKeyColor)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedThemeId = theme.id
                            preferences.currentThemeId = theme.id
                        }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Miniature Keyboard Preview
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(theme.surfaceColor))
                                .padding(6.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Row 1 keys preview
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                repeat(5) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(18.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(theme.keyBackgroundColor))
                                    )
                                }
                            }

                            // Row 2 keys preview
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                repeat(5) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(18.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(theme.keyBackgroundColor))
                                    )
                                }
                            }

                            // Space preview
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(18.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(theme.specialKeyColor))
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(3f)
                                        .height(18.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(theme.keyBackgroundColor))
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(18.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(theme.accentColor))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Theme Info & Active Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = theme.name,
                                color = Color(theme.keyTextColor),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )

                            if (isSelected) {
                                Surface(
                                    color = Color(0xFF00E5FF),
                                    shape = CircleShape,
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color.Black,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
