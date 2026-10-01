package com.example.keyboard.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keyboard.model.KeyboardPresets
import com.example.keyboard.storage.KeyboardPreferences
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary

@Composable
fun LanguagesScreen(
    preferences: KeyboardPreferences,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var enabledCodes by remember { mutableStateOf(preferences.enabledLanguageCodes) }
    var currentCode by remember { mutableStateOf(preferences.currentLanguageCode) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Bar
        item {
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
                        text = "لغات الإدخال وتخطيط المفاتيح",
                        color = DarkTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "اختر اللغات المفعلة وبدّل بينها بالضغط أو بالسحب على المسافة",
                        color = Color(0xFF00E5FF),
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Info Banner about Swipe Gesture
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131E30)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "يمكنك التبديل بسرعة فائقة بين اللغات بالسحب لليمين أو اليسار على زر المسافة (Spacebar) داخل الكيبورد.",
                        color = DarkTextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Language items
        items(KeyboardPresets.ALL_SUPPORTED_LANGUAGES, key = { it.code }) { lang ->
            val isEnabled = enabledCodes.contains(lang.code)
            val isCurrent = currentCode == lang.code

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent) Color(0xFF1E2638) else DarkCard
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isCurrent) Color(0xFF00E5FF) else DarkCardBorder
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (isEnabled) {
                                    currentCode = lang.code
                                    preferences.currentLanguageCode = lang.code
                                }
                            }
                    ) {
                        Surface(
                            color = if (isCurrent) Color(0xFFFF2A5F) else Color(0xFF26334D),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = lang.code.uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = lang.name,
                                    color = DarkTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (isCurrent) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "(النشطة)",
                                        color = Color(0xFF00E5FF),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            Text(
                                text = lang.nativeName,
                                color = DarkTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { checked ->
                            val updated = enabledCodes.toMutableSet()
                            if (checked) {
                                updated.add(lang.code)
                            } else {
                                if (updated.size > 1) {
                                    updated.remove(lang.code)
                                }
                            }
                            enabledCodes = updated
                            preferences.enabledLanguageCodes = updated
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFFF2A5F),
                            uncheckedThumbColor = DarkTextSecondary,
                            uncheckedTrackColor = Color(0xFF141926)
                        )
                    )
                }
            }
        }
    }
}
