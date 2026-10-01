package com.example.animehub.ui.admin

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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

@Composable
fun AdminAnimeListScreen(
    repository: FirestoreAnimeRepository,
    adminRole: AdminRole,
    onManageSeasons: (FirestoreAnime) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val allAnime by repository.getAllAnimeFlow().collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("الكل") } // الكل / المنشورة / المخفية / المميزة / مستمر / مكتمل

    var showFormDialog by remember { mutableStateOf(false) }
    var editingAnime by remember { mutableStateOf<FirestoreAnime?>(null) }

    var animeToDelete by remember { mutableStateOf<FirestoreAnime?>(null) }

    val filterOptions = listOf("الكل", "المنشورة", "المخفية", "المميزة", "مستمر", "مكتمل")

    val filteredAnime = allAnime.filter { anime ->
        val matchesSearch = searchQuery.isBlank() ||
                anime.titleArabic.contains(searchQuery, ignoreCase = true) ||
                anime.titleEnglish.contains(searchQuery, ignoreCase = true) ||
                anime.genres.any { it.contains(searchQuery, ignoreCase = true) }

        val matchesFilter = when (selectedFilter) {
            "المنشورة" -> anime.isPublished
            "المخفية" -> !anime.isPublished
            "المميزة" -> anime.isFeatured
            "مستمر" -> anime.status.contains("مستمر")
            "مكتمل" -> anime.status.contains("مكتمل")
            else -> true
        }

        matchesSearch && matchesFilter
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            // Header & Total Count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "إدارة قائمة الأنمي",
                        color = DarkTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "إجمالي ${allAnime.size} أنمي مسجل في قاعدة البيانات",
                        color = Color(0xFF00E5FF),
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = {
                        editingAnime = null
                        showFormDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة أنمي جديد", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("بحث عن أنمي بالاسم أو التصنيف...", color = DarkTextSecondary, fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AnimePrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = DarkTextSecondary)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = DarkTextPrimary,
                    unfocusedTextColor = DarkTextPrimary,
                    focusedBorderColor = AnimePrimary,
                    unfocusedBorderColor = DarkCardBorder,
                    focusedContainerColor = DarkCard,
                    unfocusedContainerColor = DarkCard
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Tabs
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(filterOptions) { filter ->
                    val isSelected = selectedFilter == filter
                    Surface(
                        color = if (isSelected) AnimePrimary else DarkCard,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AnimePrimary else DarkCardBorder),
                        modifier = Modifier.clickable { selectedFilter = filter }
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) Color.White else DarkTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Anime Table / List
            if (filteredAnime.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (searchQuery.isBlank()) "لا توجد أنميات مضافة بعد" else "لا توجد نتائج مطابقة لبحثك",
                        color = DarkTextSecondary,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredAnime, key = { it.id }) { anime ->
                        AnimeAdminRowCard(
                            anime = anime,
                            canDelete = adminRole.canDeleteContent(),
                            onEdit = {
                                editingAnime = anime
                                showFormDialog = true
                            },
                            onManageSeasons = { onManageSeasons(anime) },
                            onTogglePublish = {
                                scope.launch { repository.togglePublishAnime(anime.id, !anime.isPublished) }
                            },
                            onToggleFeatured = {
                                scope.launch { repository.toggleFeaturedAnime(anime.id, !anime.isFeatured) }
                            },
                            onDelete = { animeToDelete = anime }
                        )
                    }
                }
            }
        }
    }

    // Add / Edit Modal
    if (showFormDialog) {
        AdminAnimeFormDialog(
            initialAnime = editingAnime,
            onDismiss = { showFormDialog = false },
            onSave = { animeToSave ->
                scope.launch {
                    repository.saveOrUpdateAnime(animeToSave)
                    showFormDialog = false
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (animeToDelete != null) {
        AlertDialog(
            onDismissRequest = { animeToDelete = null },
            containerColor = DarkSurface,
            title = { Text("تأكيد حذف الأنمي", color = DarkTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "هل أنت متأكد من رغبتك في حذف '${animeToDelete?.displayTitle}'؟ سيتم حذف جميع مواسمه وحلقاته.",
                    color = DarkTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val toDel = animeToDelete
                        animeToDelete = null
                        if (toDel != null) {
                            scope.launch { repository.deleteAnime(toDel.id) }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("نعم، حذف نهائي", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { animeToDelete = null }) {
                    Text("إلغاء", color = DarkTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun AnimeAdminRowCard(
    anime: FirestoreAnime,
    canDelete: Boolean,
    onEdit: () -> Unit,
    onManageSeasons: () -> Unit,
    onTogglePublish: () -> Unit,
    onToggleFeatured: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (anime.isFeatured) AnimePrimary.copy(alpha = 0.5f) else DarkCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Poster
                Box(
                    modifier = Modifier
                        .size(width = 60.dp, height = 80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black)
                ) {
                    AsyncImage(
                        model = anime.coverImage,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title and badges
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = anime.displayTitle,
                            color = DarkTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        if (anime.isFeatured) {
                            Surface(color = Color(0xFFFFD700), shape = RoundedCornerShape(4.dp)) {
                                Text("مميز", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        Surface(
                            color = if (anime.isPublished) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFF3B1219),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = if (anime.isPublished) "منشور" else "مخفي",
                                color = if (anime.isPublished) Color(0xFF10B981) else Color(0xFFFF5252),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "${anime.genres.take(3).joinToString(" • ")} • ${anime.status}",
                        color = DarkTextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "👁 ${anime.viewsCount} مشاهدة • ⭐ ${anime.formattedScore} • ${anime.seasonsCount} مواسم • ${anime.episodesCount} حلقة",
                        color = Color(0xFF00E5FF),
                        fontSize = 10.sp
                    )
                }
            }

            HorizontalDivider(color = DarkCardBorder, modifier = Modifier.padding(vertical = 8.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Seasons & Episodes CTA
                Button(
                    onClick = onManageSeasons,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2638)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = AnimePrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("المواسم والحلقات", color = AnimePrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Edit, Publish, Feature, Delete Icons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleFeatured, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (anime.isFeatured) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Feature",
                            tint = if (anime.isFeatured) Color(0xFFFFD700) else DarkTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(onClick = onTogglePublish, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (anime.isPublished) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Publish",
                            tint = if (anime.isPublished) Color(0xFF10B981) else DarkTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = DarkTextSecondary, modifier = Modifier.size(18.dp))
                    }

                    if (canDelete) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}
