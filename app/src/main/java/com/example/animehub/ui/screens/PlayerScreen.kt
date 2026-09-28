package com.example.animehub.ui.screens

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.animehub.data.model.Anime
import com.example.animehub.data.model.SourceType
import com.example.animehub.data.model.VideoSource
import com.example.animehub.data.repository.VideoSourceRepository
import com.example.animehub.ui.i18n.LocalizedStrings
import com.example.ui.theme.AnimePrimary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    anime: Anime,
    initialEpisode: Int,
    initialPositionMs: Long = 0L,
    videoSourceRepo: VideoSourceRepository,
    strings: LocalizedStrings,
    onBackClick: () -> Unit,
    onSaveProgress: (episodeNumber: Int, positionMs: Long, durationMs: Long) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var currentEpisode by remember { mutableIntStateOf(initialEpisode) }
    var activeSource by remember(currentEpisode) {
        mutableStateOf(
            videoSourceRepo.getSourceForEpisode(anime.id, currentEpisode)
                ?: videoSourceRepo.licensedSampleSources.firstOrNull() // default to compliant licensed sample
        )
    }

    var isFullscreen by remember { mutableStateOf(false) }
    var areControlsVisible by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(true) }
    var currentPositionMs by remember { mutableLongStateOf(initialPositionMs) }
    var durationMs by remember { mutableLongStateOf(0L) }
    var isBuffering by remember { mutableStateOf(true) }
    var currentSpeed by remember { mutableFloatStateOf(1.0f) }
    var showSpeedMenu by remember { mutableStateOf(false) }
    var showEpisodeSelector by remember { mutableStateOf(false) }
    var showCustomUrlDialog by remember { mutableStateOf(false) }
    var customUrlInput by remember { mutableStateOf("") }
    var showNextEpisodeDialog by remember { mutableStateOf(false) }

    // Toggle orientation on fullscreen change
    LaunchedEffect(isFullscreen) {
        activity?.requestedOrientation = if (isFullscreen) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    // Android Back Handler: exits fullscreen first, or exits player
    BackHandler {
        if (isFullscreen) {
            isFullscreen = false
        } else {
            onBackClick()
        }
    }

    // Initialize ExoPlayer
    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
        }
    }

    // Load source into ExoPlayer
    LaunchedEffect(activeSource, currentEpisode) {
        val src = activeSource
        if (src != null && src.url.isNotBlank()) {
            val mediaItemBuilder = MediaItem.Builder().setUri(src.url)
            if (src.type == SourceType.HLS || src.url.contains(".m3u8", ignoreCase = true)) {
                mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_M3U8)
            }
            exoPlayer.setMediaItem(mediaItemBuilder.build(), initialPositionMs)
            exoPlayer.prepare()
            exoPlayer.play()
        }
    }

    // Periodic position updater and auto-save
    LaunchedEffect(exoPlayer) {
        while (true) {
            if (exoPlayer.isPlaying) {
                currentPositionMs = exoPlayer.currentPosition
                durationMs = exoPlayer.duration.coerceAtLeast(0L)

                // Save progress to database every 5 seconds
                if (durationMs > 0 && currentPositionMs > 0) {
                    onSaveProgress(currentEpisode, currentPositionMs, durationMs)
                }

                // Check for end of episode / countdown to next
                if (durationMs > 20000 && (durationMs - currentPositionMs) in 1000..10000) {
                    if (!showNextEpisodeDialog && currentEpisode < (anime.episodesCount ?: 24)) {
                        showNextEpisodeDialog = true
                    }
                }
            }
            delay(1000)
        }
    }

    // Player events listener
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = playbackState == Player.STATE_BUFFERING
                if (playbackState == Player.STATE_ENDED) {
                    isPlaying = false
                    val nextEp = currentEpisode + 1
                    if (nextEp <= (anime.episodesCount ?: 24)) {
                        currentEpisode = nextEp
                        currentPositionMs = 0L
                        exoPlayer.seekTo(0)
                    }
                }
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            val finalPos = exoPlayer.currentPosition
            val finalDur = exoPlayer.duration
            if (finalDur > 0) {
                onSaveProgress(currentEpisode, finalPos, finalDur)
            }
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Auto-hide controls timer
    LaunchedEffect(areControlsVisible, isPlaying) {
        if (areControlsVisible && isPlaying) {
            delay(3500)
            areControlsVisible = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                areControlsVisible = !areControlsVisible
            }
            .testTag("player_screen")
    ) {
        // ExoPlayer View
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Buffering Indicator
        if (isBuffering) {
            CircularProgressIndicator(
                color = AnimePrimary,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
            )
        }

        // Overlay Controls
        AnimatedVisibility(
            visible = areControlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Black.copy(alpha = 0.85f), Color.Transparent)
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (isFullscreen) isFullscreen = false else onBackClick()
                        },
                        modifier = Modifier.testTag("player_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = anime.displayTitle,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "الحلقة $currentEpisode • ${activeSource?.title ?: "مشغل الفيديو"}",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }

                    // Quality / Source switcher
                    IconButton(
                        onClick = { showCustomUrlDialog = true },
                        modifier = Modifier.testTag("player_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Source Settings",
                            tint = Color.White
                        )
                    }
                }

                // Center Action Controls (Rewind 10s, Play/Pause, Forward 10s)
                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val newPos = (exoPlayer.currentPosition - 10000).coerceAtLeast(0)
                            exoPlayer.seekTo(newPos)
                        },
                        modifier = Modifier.size(54.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay10,
                            contentDescription = "Rewind 10s",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(32.dp))

                    Surface(
                        color = AnimePrimary,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(64.dp)
                            .clickable {
                                if (exoPlayer.isPlaying) {
                                    exoPlayer.pause()
                                } else {
                                    exoPlayer.play()
                                }
                            }
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(14.dp)
                                .fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(32.dp))

                    IconButton(
                        onClick = {
                            val newPos = (exoPlayer.currentPosition + 10000).coerceAtMost(durationMs)
                            exoPlayer.seekTo(newPos)
                        },
                        modifier = Modifier.size(54.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forward10,
                            contentDescription = "Forward 10s",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                // Bottom Controls Bar (Timeline Slider + Actions)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Timeline Slider & Timestamps
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatTime(currentPositionMs),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Slider(
                            value = if (durationMs > 0) currentPositionMs.toFloat() / durationMs.toFloat() else 0f,
                            onValueChange = { ratio ->
                                val target = (ratio * durationMs).toLong()
                                exoPlayer.seekTo(target)
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = AnimePrimary,
                                activeTrackColor = AnimePrimary,
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        )

                        Text(
                            text = formatTime(durationMs),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Bottom Row: Episodes Selector, Previous, Next, Speed, Fullscreen
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Episode List Sheet
                            IconButton(onClick = { showEpisodeSelector = true }) {
                                Icon(
                                    imageVector = Icons.Default.VideoLibrary,
                                    contentDescription = "Episodes",
                                    tint = Color.White
                                )
                            }

                            // Previous Episode
                            IconButton(
                                onClick = {
                                    if (currentEpisode > 1) {
                                        currentEpisode -= 1
                                    }
                                },
                                enabled = currentEpisode > 1
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SkipPrevious,
                                    contentDescription = "Previous Episode",
                                    tint = if (currentEpisode > 1) Color.White else Color.Gray
                                )
                            }

                            // Next Episode
                            val maxEp = anime.episodesCount ?: 24
                            IconButton(
                                onClick = {
                                    if (currentEpisode < maxEp) {
                                        currentEpisode += 1
                                    }
                                },
                                enabled = currentEpisode < maxEp
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SkipNext,
                                    contentDescription = "Next Episode",
                                    tint = if (currentEpisode < maxEp) Color.White else Color.Gray
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Playback Speed Button
                            Box {
                                TextButton(onClick = { showSpeedMenu = true }) {
                                    Text(
                                        text = "${currentSpeed}x",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }

                                DropdownMenu(
                                    expanded = showSpeedMenu,
                                    onDismissRequest = { showSpeedMenu = false },
                                    modifier = Modifier.background(DarkSurface)
                                ) {
                                    listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = "${speed}x",
                                                    color = if (currentSpeed == speed) AnimePrimary else DarkTextPrimary
                                                )
                                            },
                                            onClick = {
                                                currentSpeed = speed
                                                exoPlayer.playbackParameters = PlaybackParameters(speed)
                                                showSpeedMenu = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Fullscreen Toggle
                            IconButton(
                                onClick = { isFullscreen = !isFullscreen },
                                modifier = Modifier.testTag("player_fullscreen_button")
                            ) {
                                Icon(
                                    imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                    contentDescription = "Fullscreen",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Next Episode Prompt Dialog (countdown to next episode)
        if (showNextEpisodeDialog) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface.copy(alpha = 0.95f)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AnimePrimary),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .width(260.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "الحلقة التالية: ${currentEpisode + 1}",
                        color = DarkTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "ستبدأ الحلقة التالية تلقائياً عند انتهاء الحالية",
                        color = DarkTextSecondary,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showNextEpisodeDialog = false }) {
                            Text(text = strings.cancel, color = DarkTextSecondary, fontSize = 12.sp)
                        }
                        Button(
                            onClick = {
                                showNextEpisodeDialog = false
                                currentEpisode += 1
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AnimePrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "شاهد الآن", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Episode Selector Dialog
        if (showEpisodeSelector) {
            AlertDialog(
                onDismissRequest = { showEpisodeSelector = false },
                containerColor = DarkSurface,
                title = {
                    Text(text = "اختر الحلقة", color = DarkTextPrimary, fontWeight = FontWeight.Bold)
                },
                text = {
                    val count = anime.episodesCount ?: 24
                    LazyColumn(modifier = Modifier.height(300.dp)) {
                        items((1..count).toList()) { epNum ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        currentEpisode = epNum
                                        showEpisodeSelector = false
                                    }
                                    .padding(vertical = 10.dp, horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "الحلقة $epNum",
                                    color = if (epNum == currentEpisode) AnimePrimary else DarkTextPrimary,
                                    fontWeight = if (epNum == currentEpisode) FontWeight.Bold else FontWeight.Normal
                                )
                                if (epNum == currentEpisode) {
                                    Text(text = "جارية المشاهدة", color = AnimePrimary, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showEpisodeSelector = false }) {
                        Text(text = strings.cancel, color = AnimePrimary)
                    }
                }
            )
        }

        // Custom Stream URL Dialog / Licensed samples
        if (showCustomUrlDialog) {
            AlertDialog(
                onDismissRequest = { showCustomUrlDialog = false },
                containerColor = DarkSurface,
                title = {
                    Text(text = "إعدادات مصدر الفيديو", color = DarkTextPrimary, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        Text(
                            text = "يدعم المشغل صيغ HLS (m3u8) و MP4 بدون إعلانات أو روابط غير مرخصة.",
                            color = DarkTextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = customUrlInput,
                            onValueChange = { customUrlInput = it },
                            placeholder = { Text(text = "https://.../video.m3u8", color = DarkTextSecondary) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "أو اختر من النماذج المرخصة المعتمدة:",
                            color = DarkTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        videoSourceRepo.licensedSampleSources.forEach { sample ->
                            OutlinedButton(
                                onClick = {
                                    activeSource = sample
                                    showCustomUrlDialog = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(text = sample.title, fontSize = 11.sp, color = DarkTextPrimary)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (customUrlInput.isNotBlank()) {
                                videoSourceRepo.setCustomStreamUrl(customUrlInput)
                                activeSource = videoSourceRepo.getSourceForEpisode(anime.id, currentEpisode)
                            }
                            showCustomUrlDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimePrimary)
                    ) {
                        Text(text = strings.apply, color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCustomUrlDialog = false }) {
                        Text(text = strings.cancel, color = DarkTextSecondary)
                    }
                }
            )
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = (millis / 1000).toInt().coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
