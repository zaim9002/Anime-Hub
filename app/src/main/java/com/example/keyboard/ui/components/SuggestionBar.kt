package com.example.keyboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keyboard.model.KeyboardMode
import com.example.keyboard.model.KeyboardTheme
import com.example.keyboard.model.WordSuggestion

@Composable
fun SuggestionBar(
    suggestions: List<WordSuggestion>,
    theme: KeyboardTheme,
    onSuggestionClick: (String) -> Unit,
    onModeChange: (KeyboardMode) -> Unit,
    onSwitchLanguage: () -> Unit,
    onOpenSettings: () -> Unit,
    onNextTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Surface(
        color = Color(theme.suggestionBgColor),
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Quick Action Icons (Left)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onModeChange(KeyboardMode.CLIPBOARD) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = "Clipboard",
                        tint = Color(theme.specialKeyTextColor),
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = { onModeChange(KeyboardMode.GIF) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Gif,
                        contentDescription = "GIF",
                        tint = Color(theme.specialKeyTextColor),
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = { onModeChange(KeyboardMode.EMOJI) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SentimentSatisfiedAlt,
                        contentDescription = "Emoji",
                        tint = Color(theme.specialKeyTextColor),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Middle: Suggestions Strip
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (suggestions.isEmpty()) {
                    Text(
                        text = "Smart Keyboard Pro",
                        color = Color(theme.keyTextColor).copy(alpha = 0.4f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    suggestions.forEach { sug ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clickable { onSuggestionClick(sug.text) }
                                .background(
                                    if (sug.isAutoCorrect) Color(theme.accentColor).copy(alpha = 0.2f) else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = sug.text,
                                color = if (sug.isAutoCorrect) Color(theme.accentColor) else Color(theme.keyTextColor),
                                fontSize = 13.sp,
                                fontWeight = if (sug.isAutoCorrect) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Quick Tools (Right)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onSwitchLanguage,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = Color(theme.specialKeyTextColor),
                        modifier = Modifier.size(19.dp)
                    )
                }

                IconButton(
                    onClick = onNextTheme,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Theme",
                        tint = Color(theme.specialKeyTextColor),
                        modifier = Modifier.size(19.dp)
                    )
                }

                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color(theme.specialKeyTextColor),
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}
