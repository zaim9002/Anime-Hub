package com.example.animehub.ui.admin

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.animehub.data.admin.AdminAuthManager
import com.example.animehub.data.admin.AdminDashboardStats
import com.example.animehub.data.admin.AdminRole
import com.example.animehub.data.admin.FirestoreAnime
import com.example.animehub.data.firestore.FirestoreAnimeRepository
import com.example.ui.theme.AnimePrimary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import kotlinx.coroutines.launch

enum class AdminSubScreen {
    DASHBOARD,
    ANIME_LIST,
    SEASONS_EPISODES,
    MODERATORS,
    NOTIFICATIONS
}

@Composable
fun AdminDashboardScreen(
    authManager: AdminAuthManager,
    repository: FirestoreAnimeRepository,
    onExitAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var currentSubScreen by remember { mutableStateOf(AdminSubScreen.DASHBOARD) }
    var selectedAnimeForSeasons by remember { mutableStateOf<FirestoreAnime?>(null) }
    var stats by remember { mutableStateOf(AdminDashboardStats()) }

    val adminRole = authManager.currentAdminRole ?: AdminRole.ADMIN
    val adminName = authManager.currentAdminName

    fun refreshStats() {
        scope.launch {
            stats = repository.calculateDashboardStats()
        }
    }

    LaunchedEffect(currentSubScreen) {
        refreshStats()
    }

    when (currentSubScreen) {
        AdminSubScreen.ANIME_LIST -> {
            AdminAnimeListScreen(
                repository = repository,
                adminRole = adminRole,
                onManageSeasons = { anime ->
                    selectedAnimeForSeasons = anime
                    currentSubScreen = AdminSubScreen.SEASONS_EPISODES
                }
            )
        }
        AdminSubScreen.SEASONS_EPISODES -> {
            val anime = selectedAnimeForSeasons
            if (anime != null) {
                AdminSeasonsEpisodesScreen(
                    anime = anime,
                    repository = repository,
                    onBack = { currentSubScreen = AdminSubScreen.ANIME_LIST }
                )
            } else {
                currentSubScreen = AdminSubScreen.ANIME_LIST
            }
        }
        AdminSubScreen.MODERATORS -> {
            AdminModeratorsScreen(
                authManager = authManager,
                onBack = { currentSubScreen = AdminSubScreen.DASHBOARD }
            )
        }
        AdminSubScreen.NOTIFICATIONS -> {
            AdminNotificationsScreen(
                repository = repository,
                onBack = { currentSubScreen = AdminSubScreen.DASHBOARD }
            )
        }
        AdminSubScreen.DASHBOARD -> {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(DarkBackground)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Admin Profile Header & Logout
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier.fillMaxWidth()
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
                                    color = AnimePrimary.copy(alpha = 0.2f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.AdminPanelSettings,
                                            contentDescription = null,
                                            tint = AnimePrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = adminName,
                                            color = DarkTextPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = when (adminRole) {
                                                AdminRole.SUPER_ADMIN -> Color(0xFFFFD700)
                                                AdminRole.ADMIN -> AnimePrimary
                                                AdminRole.EDITOR -> Color(0xFF00E5FF)
                                            },
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = adminRole.displayNameArabic,
                                                color = if (adminRole == AdminRole.SUPER_ADMIN) Color.Black else Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = authManager.currentAdminEmail ?: "admin@animehub.com",
                                        color = DarkTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        authManager.logout()
                                        onExitAdmin()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Logout,
                                        contentDescription = "Logout",
                                        tint = Color(0xFFFF5252),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Overview Metric Grid (2 columns)
                item {
                    Text(
                        text = "إحصائيات المنصة المباشرة (Live Analytics)",
                        color = DarkTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatMetricCard(
                                title = "إجمالي الأنميات",
                                value = "${stats.totalAnime}",
                                subtitle = "${stats.publishedAnimeCount} منشور حالياً",
                                icon = Icons.Default.Movie,
                                color = AnimePrimary,
                                modifier = Modifier.weight(1f)
                            )

                            StatMetricCard(
                                title = "إجمالي الحلقات",
                                value = "${stats.totalEpisodes}",
                                subtitle = "${stats.totalSeasons} مواسم مسجلة",
                                icon = Icons.Default.VideoLibrary,
                                color = Color(0xFF00E5FF),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatMetricCard(
                                title = "إجمالي المشاهدات",
                                value = "${stats.totalViews}",
                                subtitle = "مشاهدات نشطة",
                                icon = Icons.Default.RemoveRedEye,
                                color = Color(0xFF10B981),
                                modifier = Modifier.weight(1f)
                            )

                            StatMetricCard(
                                title = "المستخدمين والمشرفين",
                                value = "${stats.totalUsers}",
                                subtitle = "${stats.totalAdmins} مدراء نشطين",
                                icon = Icons.Default.Groups,
                                color = Color(0xFFFFB300),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Visual Analytics Bar Chart
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Analytics, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("توزيع ونشاط المحتوى هذا الأسبوع", color = DarkTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                val days = listOf("السبت", "الأحد", "الاثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة")
                                val heights = listOf(0.45f, 0.65f, 0.8f, 0.55f, 0.9f, 0.75f, 1.0f)

                                days.forEachIndexed { i, day ->
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .width(18.dp)
                                                .height((70 * heights[i]).dp)
                                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                                .background(
                                                    if (i == 6) AnimePrimary else Color(0xFF00E5FF).copy(alpha = 0.4f + heights[i] * 0.4f)
                                                )
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(day.take(2), color = DarkTextSecondary, fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Quick Navigation Menu
                item {
                    Text(
                        text = "أقسام لوحة التحكم",
                        color = DarkTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            AdminMenuItem(
                                icon = Icons.Default.Movie,
                                title = "إدارة الأنمي (Anime Management)",
                                subtitle = "إضافة وتعديل وحذف ونشر وإخفاء الأنمي والصور المتعددة",
                                onClick = { currentSubScreen = AdminSubScreen.ANIME_LIST }
                            )

                            HorizontalDivider(color = DarkCardBorder)

                            AdminMenuItem(
                                icon = Icons.Default.Security,
                                title = "إدارة المشرفين والصلاحيات (Moderators)",
                                subtitle = "إضافة وتعديل أدوار المشرفين (Super Admin, Admin, Editor)",
                                onClick = { currentSubScreen = AdminSubScreen.MODERATORS }
                            )

                            HorizontalDivider(color = DarkCardBorder)

                            AdminMenuItem(
                                icon = Icons.Default.Campaign,
                                title = "مركز الإشعارات والبث (Broadcast Notifications)",
                                subtitle = "إرسال إشعارات فورية بالحلقات الجديدة والتحديثات",
                                onClick = { currentSubScreen = AdminSubScreen.NOTIFICATIONS }
                            )
                        }
                    }
                }

                // Return to App Button
                item {
                    Button(
                        onClick = onExitAdmin,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2638)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("العودة لتطبيق الأنمي الرئيسي", color = Color(0xFF00E5FF), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun StatMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = DarkTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                Surface(
                    color = color.copy(alpha = 0.15f),
                    shape = CircleShape,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                color = DarkTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                text = subtitle,
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun AdminMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
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
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Surface(
                color = Color(0xFF1E2638),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = AnimePrimary, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(title, color = DarkTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = DarkTextSecondary, fontSize = 11.sp)
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = DarkTextSecondary,
            modifier = Modifier.size(14.dp)
        )
    }
}
