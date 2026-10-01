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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.animehub.data.admin.FirestoreAnime
import com.example.animehub.data.admin.FirestoreEpisode
import com.example.animehub.data.admin.FirestoreEpisodeServer
import com.example.animehub.data.admin.FirestoreSeason
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
fun AdminSeasonsEpisodesScreen(
    anime: FirestoreAnime,
    repository: FirestoreAnimeRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val seasons by repository.getSeasonsFlow(anime.id).collectAsState(initial = emptyList())
    var selectedSeasonIndex by remember { mutableIntStateOf(0) }

    val currentSeason = seasons.getOrNull(selectedSeasonIndex) ?: seasons.firstOrNull()

    val episodes by if (currentSeason != null) {
        repository.getEpisodesFlow(anime.id, currentSeason.id).collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList()) }
    }

    var showSeasonDialog by remember { mutableStateOf(false) }
    var editingSeason by remember { mutableStateOf<FirestoreSeason?>(null) }

    var showEpisodeDialog by remember { mutableStateOf(false) }
    var editingEpisode by remember { mutableStateOf<FirestoreEpisode?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "إدارة المواسم والحلقات",
                    color = DarkTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = anime.displayTitle,
                    color = Color(0xFF00E5FF),
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }

            Button(
                onClick = {
                    editingSeason = null
                    showSeasonDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2638)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = AnimePrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("إضافة موسم", color = AnimePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Seasons Tabs
        if (seasons.isNotEmpty()) {
            ScrollableTabRow(
                selectedTabIndex = selectedSeasonIndex.coerceIn(0, (seasons.size - 1).coerceAtLeast(0)),
                containerColor = DarkCard,
                contentColor = AnimePrimary,
                indicator = { tabPositions ->
                    if (selectedSeasonIndex < tabPositions.size) {
                        Surface(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[selectedSeasonIndex])
                                .height(3.dp),
                            color = AnimePrimary
                        ) {}
                    }
                },
                edgePadding = 12.dp
            ) {
                seasons.forEachIndexed { idx, s ->
                    Tab(
                        selected = selectedSeasonIndex == idx,
                        onClick = { selectedSeasonIndex = idx },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = s.title,
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedSeasonIndex == idx) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedSeasonIndex == idx) Color.White else DarkTextSecondary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "(${s.episodesCount})",
                                    fontSize = 11.sp,
                                    color = AnimePrimary
                                )
                            }
                        }
                    )
                }
            }
        }

        // Active Season Controls & Episodes List
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            if (currentSeason == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("لا يوجد مواسم مضافة لهذا الأنمي بعد", color = DarkTextSecondary)
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                editingSeason = null
                                showSeasonDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AnimePrimary)
                        ) {
                            Text("إضافة الموسم 1 الآن", color = Color.White)
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${currentSeason.title} (${episodes.size} حلقة)",
                            color = DarkTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                editingSeason = currentSeason
                                showSeasonDialog = true
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Season", tint = DarkTextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }

                    Button(
                        onClick = {
                            editingEpisode = null
                            showEpisodeDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimePrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إضافة حلقة جديدة", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (episodes.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("لا توجد حلقات في هذا الموسم حتى الآن.", color = DarkTextSecondary, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(episodes, key = { it.id }) { ep ->
                            EpisodeAdminCard(
                                episode = ep,
                                onEdit = {
                                    editingEpisode = ep
                                    showEpisodeDialog = true
                                },
                                onDelete = {
                                    scope.launch {
                                        repository.deleteEpisode(anime.id, currentSeason.id, ep.id)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Season Dialog
    if (showSeasonDialog) {
        SeasonFormDialog(
            animeId = anime.id,
            initialSeason = editingSeason,
            nextSeasonNumber = (seasons.maxOfOrNull { it.seasonNumber } ?: 0) + 1,
            onDismiss = { showSeasonDialog = false },
            onSave = { season ->
                scope.launch {
                    repository.saveSeason(season)
                    showSeasonDialog = false
                }
            }
        )
    }

    // Add / Edit Episode Dialog (with Multi-Server support)
    if (showEpisodeDialog && currentSeason != null) {
        EpisodeFormDialog(
            anime = anime,
            season = currentSeason,
            initialEpisode = editingEpisode,
            nextEpisodeNumber = (episodes.maxOfOrNull { it.episodeNumber } ?: 0) + 1,
            onDismiss = { showEpisodeDialog = false },
            onSave = { episode, sendNotification ->
                scope.launch {
                    repository.saveEpisode(episode, sendNotification = sendNotification, animeTitle = anime.displayTitle)
                    showEpisodeDialog = false
                }
            }
        )
    }
}

@Composable
private fun EpisodeAdminCard(
    episode: FirestoreEpisode,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Surface(
                    color = if (episode.isPublished) AnimePrimary.copy(alpha = 0.2f) else Color(0xFF333333),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${episode.episodeNumber}",
                            color = if (episode.isPublished) AnimePrimary else DarkTextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = episode.title,
                            color = DarkTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        if (!episode.isPublished) {
                            Surface(color = Color(0xFF333333), shape = RoundedCornerShape(4.dp)) {
                                Text("مخفية", color = DarkTextSecondary, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                    }

                    Text(
                        text = "${episode.servers.size} سيرفرات • ${episode.durationMinutes ?: 24} دقيقة",
                        color = Color(0xFF00E5FF),
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = DarkTextSecondary, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun SeasonFormDialog(
    animeId: Int,
    initialSeason: FirestoreSeason?,
    nextSeasonNumber: Int,
    onDismiss: () -> Unit,
    onSave: (FirestoreSeason) -> Unit
) {
    var seasonNum by remember { mutableStateOf((initialSeason?.seasonNumber ?: nextSeasonNumber).toString()) }
    var title by remember { mutableStateOf(initialSeason?.title ?: "الموسم $nextSeasonNumber") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = { Text(if (initialSeason != null) "تعديل الموسم" else "إضافة موسم جديد", color = DarkTextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = seasonNum,
                    onValueChange = { seasonNum = it },
                    label = { Text("رقم الموسم", fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = dialogFieldColors(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("اسم الموسم (مثال: الموسم 1)", fontSize = 12.sp) },
                    colors = dialogFieldColors(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val sNum = seasonNum.toIntOrNull() ?: 1
                    val s = FirestoreSeason(
                        id = initialSeason?.id ?: "season_$sNum",
                        animeId = animeId,
                        seasonNumber = sNum,
                        title = title.ifBlank { "الموسم $sNum" },
                        episodesCount = initialSeason?.episodesCount ?: 0,
                        order = sNum
                    )
                    onSave(s)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AnimePrimary)
            ) {
                Text("حفظ", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = DarkTextSecondary) }
        }
    )
}

@Composable
private fun EpisodeFormDialog(
    anime: FirestoreAnime,
    season: FirestoreSeason,
    initialEpisode: FirestoreEpisode?,
    nextEpisodeNumber: Int,
    onDismiss: () -> Unit,
    onSave: (FirestoreEpisode, Boolean) -> Unit
) {
    var epNumber by remember { mutableStateOf((initialEpisode?.episodeNumber ?: nextEpisodeNumber).toString()) }
    var title by remember { mutableStateOf(initialEpisode?.title ?: "الحلقة $nextEpisodeNumber: بداية المغامرة") }
    var duration by remember { mutableStateOf((initialEpisode?.durationMinutes ?: 24).toString()) }
    var isPublished by remember { mutableStateOf(initialEpisode?.isPublished ?: true) }
    var sendNotification by remember { mutableStateOf(true) }

    // Multi Servers List
    var servers by remember {
        mutableStateOf(
            initialEpisode?.servers ?: listOf(
                FirestoreEpisodeServer("srv_1", "سيرفر 1 (FHD Ultra - أساسي)", "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8", "1080p", "HLS", true),
                FirestoreEpisodeServer("srv_2", "سيرفر 2 (Fast Stream)", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "720p", "MP4", false),
                FirestoreEpisodeServer("srv_3", "سيرفر 3 (توفير البيانات)", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4", "480p", "MP4", false)
            )
        )
    }

    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = DarkSurface,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialEpisode != null) "تعديل الحلقة" else "إضافة حلقة جديدة (${season.title})",
                        color = DarkTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = DarkTextSecondary)
                    }
                }

                HorizontalDivider(color = DarkCardBorder, modifier = Modifier.padding(vertical = 10.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = epNumber,
                            onValueChange = { epNumber = it },
                            label = { Text("رقم الحلقة", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = dialogFieldColors(),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = duration,
                            onValueChange = { duration = it },
                            label = { Text("المدة (دقيقة)", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = dialogFieldColors(),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("عنوان الحلقة *", fontSize = 12.sp) },
                        colors = dialogFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Multi-Server Management (Server 1, Server 2, Server 3)
                    Text("سيرفرات وروابط المشاهدة (Multi-Server Streaming)", color = Color(0xFF00E5FF), fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    servers.forEachIndexed { sIdx, srv ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkCard),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (srv.isDefault) AnimePrimary else DarkCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Dns, contentDescription = null, tint = AnimePrimary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(srv.name, color = DarkTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        if (srv.isDefault) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(color = AnimePrimary, shape = RoundedCornerShape(4.dp)) {
                                                Text("الافتراضي", color = Color.White, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                            }
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        TextButton(
                                            onClick = {
                                                servers = servers.mapIndexed { i, s -> s.copy(isDefault = i == sIdx) }
                                            }
                                        ) {
                                            Text(if (srv.isDefault) "✓ الافتراضي" else "جعله افتراضي", fontSize = 11.sp, color = if (srv.isDefault) AnimePrimary else DarkTextSecondary)
                                        }

                                        if (servers.size > 1) {
                                            IconButton(
                                                onClick = { servers = servers.filterIndexed { i, _ -> i != sIdx } },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = srv.url,
                                    onValueChange = { newUrl ->
                                        servers = servers.mapIndexed { i, s -> if (i == sIdx) s.copy(url = newUrl) else s }
                                    },
                                    label = { Text("رابط الفيديو (HLS M3U8 أو MP4 مباشر)", fontSize = 11.sp) },
                                    colors = dialogFieldColors(),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = srv.name,
                                        onValueChange = { newName ->
                                            servers = servers.mapIndexed { i, s -> if (i == sIdx) s.copy(name = newName) else s }
                                        },
                                        label = { Text("اسم السيرفر", fontSize = 10.sp) },
                                        colors = dialogFieldColors(),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f)
                                    )

                                    OutlinedTextField(
                                        value = srv.quality,
                                        onValueChange = { newQ ->
                                            servers = servers.mapIndexed { i, s -> if (i == sIdx) s.copy(quality = newQ) else s }
                                        },
                                        label = { Text("الجودة (1080p)", fontSize = 10.sp) },
                                        colors = dialogFieldColors(),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            val newSrvNum = servers.size + 1
                            servers = servers + FirestoreEpisodeServer(
                                id = "srv_$newSrvNum",
                                name = "سيرفر $newSrvNum (سيرفر إضافي)",
                                url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                                quality = "720p",
                                type = "MP4",
                                isDefault = false
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2638)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = AnimePrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("إضافة سيرفر مشاهدة إضافي (+ Server)", color = AnimePrimary, fontSize = 12.sp)
                    }

                    // Publishing and notification options
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("نشر الحلقة فوراً للمستخدمين", color = DarkTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Switch(
                                    checked = isPublished,
                                    onCheckedChange = { isPublished = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AnimePrimary)
                                )
                            }

                            HorizontalDivider(color = DarkCardBorder, modifier = Modifier.padding(vertical = 6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("إرسال إشعار للمستخدمين فور النشر", color = DarkTextPrimary, fontSize = 12.sp)
                                }
                                Checkbox(
                                    checked = sendNotification,
                                    onCheckedChange = { sendNotification = it },
                                    colors = CheckboxDefaults.colors(checkedColor = AnimePrimary)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = DarkCardBorder, modifier = Modifier.padding(vertical = 10.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) { Text("إلغاء", color = DarkTextSecondary) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val epN = epNumber.toIntOrNull() ?: 1
                            val defIdx = servers.indexOfFirst { it.isDefault }.coerceAtLeast(0)

                            val ep = FirestoreEpisode(
                                id = initialEpisode?.id ?: "ep_$epN",
                                animeId = anime.id,
                                seasonId = season.id,
                                seasonNumber = season.seasonNumber,
                                episodeNumber = epN,
                                title = title.ifBlank { "الحلقة $epN" },
                                durationMinutes = duration.toIntOrNull() ?: 24,
                                isPublished = isPublished,
                                defaultServerIndex = defIdx,
                                createdAt = initialEpisode?.createdAt ?: System.currentTimeMillis(),
                                servers = servers
                            )
                            onSave(ep, sendNotification && isPublished)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimePrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (initialEpisode != null) "حفظ التعديلات" else "إضافة ونشر الحلقة", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun dialogFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = DarkTextPrimary,
    unfocusedTextColor = DarkTextPrimary,
    focusedBorderColor = AnimePrimary,
    unfocusedBorderColor = DarkCardBorder,
    focusedContainerColor = DarkCard,
    unfocusedContainerColor = DarkCard
)
