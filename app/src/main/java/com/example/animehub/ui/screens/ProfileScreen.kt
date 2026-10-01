package com.example.animehub.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.animehub.ui.i18n.AppLanguage
import com.example.animehub.ui.i18n.LocalizedStrings
import com.example.ui.theme.AnimePrimary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary

@Composable
fun ProfileScreen(
    favoritesCount: Int,
    myListCount: Int,
    historyCount: Int,
    currentLanguage: AppLanguage,
    isDarkMode: Boolean,
    autoNextEp: Boolean,
    strings: LocalizedStrings,
    onLanguageChange: (AppLanguage) -> Unit,
    onThemeToggle: (Boolean) -> Unit,
    onAutoNextEpToggle: (Boolean) -> Unit,
    onClearCache: () -> Unit,
    onOpenAdmin: () -> Unit
) {
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var cacheClearedMessage by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .testTag("profile_screen")
    ) {
        // User Header Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(DarkSurface)
                            .border(2.dp, AnimePrimary, CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_anime_hub_logo_1790625737129),
                            contentDescription = "User Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "عاشق الأنمي",
                            color = DarkTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "عضو مميز في Anime Hub",
                            color = AnimePrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                HorizontalDivider(color = DarkCardBorder)

                // Stats Row: Favorites, My List, History count
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(count = favoritesCount.toString(), label = strings.addToFavorites, icon = Icons.Default.Star)
                    StatItem(count = myListCount.toString(), label = strings.addToList, icon = Icons.Default.Bookmark)
                    StatItem(count = historyCount.toString(), label = strings.history, icon = Icons.Default.History)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Admin Dashboard Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1528)),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AnimePrimary.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenAdmin)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = AnimePrimary,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Admin",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "لوحة تحكم الإدارة (Admin Panel)",
                                    color = DarkTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = AnimePrimary,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "PRO",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "إدارة الأنميات، المواسم، الحلقات، السيرفرات والمشرفين",
                                color = DarkTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = AnimePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // Settings Section
        item {
            Text(
                text = strings.settings,
                color = DarkTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Language setting
                    SettingRow(
                        icon = Icons.Default.Language,
                        title = strings.language,
                        value = currentLanguage.title,
                        onClick = { showLanguageDialog = true }
                    )

                    HorizontalDivider(color = DarkCardBorder)

                    // Theme toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = AnimePrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = strings.theme,
                                color = DarkTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = onThemeToggle,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AnimePrimary,
                                uncheckedThumbColor = DarkTextSecondary,
                                uncheckedTrackColor = DarkSurface
                            )
                        )
                    }

                    HorizontalDivider(color = DarkCardBorder)

                    // Auto Next Episode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = AnimePrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = strings.autoNextEp,
                                color = DarkTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Switch(
                            checked = autoNextEp,
                            onCheckedChange = onAutoNextEpToggle,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AnimePrimary,
                                uncheckedThumbColor = DarkTextSecondary,
                                uncheckedTrackColor = DarkSurface
                            )
                        )
                    }

                    HorizontalDivider(color = DarkCardBorder)

                    // Clear Cache
                    SettingRow(
                        icon = Icons.Default.History,
                        title = "مسح التخزين المؤقت (Cache)",
                        value = if (cacheClearedMessage) "تم المسح!" else "تنظيف",
                        onClick = {
                            onClearCache()
                            cacheClearedMessage = true
                        }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // About & Legal Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingRow(
                        icon = Icons.Default.Info,
                        title = "حول ANIME HUB",
                        value = "v1.0.0",
                        onClick = { showAboutDialog = true }
                    )

                    HorizontalDivider(color = DarkCardBorder)

                    SettingRow(
                        icon = Icons.Default.PrivacyTip,
                        title = "الخصوصية والشروط",
                        value = "",
                        onClick = { showPrivacyDialog = true }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(90.dp)) }
    }

    // Language Selector Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            containerColor = DarkSurface,
            title = {
                Text(text = strings.language, color = DarkTextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    AppLanguage.values().forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onLanguageChange(lang)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lang.title,
                                color = if (lang == currentLanguage) AnimePrimary else DarkTextPrimary,
                                fontWeight = if (lang == currentLanguage) FontWeight.Bold else FontWeight.Normal
                            )
                            if (lang == currentLanguage) {
                                Text(text = "✓", color = AnimePrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(text = strings.cancel, color = AnimePrimary)
                }
            }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = DarkSurface,
            title = {
                Text(text = "حول تطبيق ANIME HUB", color = DarkTextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = strings.aboutDesc + "\n\nالمطور: فريق Anime Hub\nالإصدار: 1.0.0\nمصادر البيانات: AniList GraphQL API مع دعم Jikan API الاحتياطي.",
                    color = DarkTextSecondary,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text(text = "إغلاق", color = AnimePrimary)
                }
            }
        )
    }

    // Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            containerColor = DarkSurface,
            title = {
                Text(text = "سياسة الخصوصية والشروط", color = DarkTextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "يحترم ANIME HUB خصوصية المستخدمين تماماً. يتم حفظ المفضلات وسجل المشاهدة محلياً على جهازك دون إرسال أي بيانات شخصية إلى خوادم خارجية.\n\nالتطبيق لا يستضيف أي مواد مقرصنة أو غير مصرح بها.",
                    color = DarkTextSecondary,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text(text = "موافق", color = AnimePrimary)
                }
            }
        )
    }
}

@Composable
private fun StatItem(count: String, label: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AnimePrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = count,
                color = DarkTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = DarkTextSecondary,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AnimePrimary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                color = DarkTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (value.isNotBlank()) {
                Text(
                    text = value,
                    color = DarkTextSecondary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = DarkTextSecondary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
