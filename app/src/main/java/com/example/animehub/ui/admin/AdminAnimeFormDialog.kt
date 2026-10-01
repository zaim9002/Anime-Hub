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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.animehub.data.admin.FirestoreAnime
import com.example.ui.theme.AnimePrimary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary

@Composable
fun AdminAnimeFormDialog(
    initialAnime: FirestoreAnime? = null,
    onDismiss: () -> Unit,
    onSave: (FirestoreAnime) -> Unit
) {
    val isEdit = initialAnime != null

    var titleArabic by remember { mutableStateOf(initialAnime?.titleArabic ?: "") }
    var titleEnglish by remember { mutableStateOf(initialAnime?.titleEnglish ?: "") }
    var titleRomaji by remember { mutableStateOf(initialAnime?.titleRomaji ?: "") }
    var description by remember { mutableStateOf(initialAnime?.description ?: "") }
    var coverImage by remember { mutableStateOf(initialAnime?.coverImage ?: "") }
    var bannerImage by remember { mutableStateOf(initialAnime?.bannerImage ?: "") }
    var releaseYear by remember { mutableStateOf((initialAnime?.seasonYear ?: 2026).toString()) }
    var studio by remember { mutableStateOf(initialAnime?.studios?.firstOrNull() ?: "MAPPA") }
    var format by remember { mutableStateOf(initialAnime?.format ?: "TV") }
    var status by remember { mutableStateOf(initialAnime?.status ?: "مستمر") }
    var rating by remember { mutableStateOf((initialAnime?.averageScore ?: 85).toString()) }
    var isPublished by remember { mutableStateOf(initialAnime?.isPublished ?: true) }
    var isFeatured by remember { mutableStateOf(initialAnime?.isFeatured ?: false) }
    var isNew by remember { mutableStateOf(initialAnime?.isNew ?: true) }

    val allGenres = listOf("أكشن", "مغامرات", "فانتازيا", "شونين", "سحر", "رعب", "كوميديا", "دراما", "خيال علمي", "غموض", "شياطين", "تاريخي")
    var selectedGenres by remember {
        mutableStateOf(initialAnime?.genres?.toSet() ?: setOf("أكشن", "شونين", "فانتازيا"))
    }

    var screenshots by remember {
        mutableStateOf(initialAnime?.screenshots ?: emptyList())
    }
    var newScreenshotUrl by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEdit) "تعديل بيانات الأنمي" else "إضافة أنمي جديد",
                        color = DarkTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = DarkTextSecondary)
                    }
                }

                HorizontalDivider(color = DarkCardBorder, modifier = Modifier.padding(vertical = 10.dp))

                // Scrollable Form
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (errorMessage != null) {
                        Surface(
                            color = Color(0xFF3B1219),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                color = Color(0xFFFF5252),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // Titles Section
                    Text("المعلومات الأساسية والأسماء", color = Color(0xFF00E5FF), fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = titleArabic,
                        onValueChange = { titleArabic = it },
                        label = { Text("الاسم باللغة العربية *", fontSize = 12.sp) },
                        colors = fieldColors(),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = titleEnglish,
                        onValueChange = { titleEnglish = it },
                        label = { Text("الاسم باللغة الإنجليزية", fontSize = 12.sp) },
                        colors = fieldColors(),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("قصة ووصف الأنمي *", fontSize = 12.sp) },
                        minLines = 3,
                        colors = fieldColors(),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Multi-Images Section (Cover, Banner, Screenshots)
                    Text("الصور والوسائط المتعددة (Multi-Image)", color = Color(0xFF00E5FF), fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = coverImage,
                        onValueChange = { coverImage = it },
                        label = { Text("رابط صورة الغلاف (Poster Image URL) *", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Image, contentDescription = null, tint = AnimePrimary) },
                        colors = fieldColors(),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = bannerImage,
                        onValueChange = { bannerImage = it },
                        label = { Text("رابط صورة البانر العريض (Banner Image URL)", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Image, contentDescription = null, tint = AnimePrimary) },
                        colors = fieldColors(),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Screenshots Management (Multi-select / Add images)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("صور إضافية ولقطات (Screenshots)", color = DarkTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = newScreenshotUrl,
                                    onValueChange = { newScreenshotUrl = it },
                                    placeholder = { Text("رابط لقطة شاشة جديدة...", color = DarkTextSecondary, fontSize = 11.sp) },
                                    colors = fieldColors(),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (newScreenshotUrl.isNotBlank()) {
                                            screenshots = screenshots + newScreenshotUrl.trim()
                                            newScreenshotUrl = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AnimePrimary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add")
                                }
                            }

                            if (screenshots.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(screenshots) { url ->
                                        Box(
                                            modifier = Modifier
                                                .size(width = 100.dp, height = 65.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color.Black)
                                        ) {
                                            AsyncImage(
                                                model = url,
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                            IconButton(
                                                onClick = { screenshots = screenshots.filter { it != url } },
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .size(24.dp)
                                                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Genres Selection
                    Text("التصنيفات والأنواع", color = Color(0xFF00E5FF), fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(allGenres) { genre ->
                            val isSelected = selectedGenres.contains(genre)
                            Surface(
                                color = if (isSelected) AnimePrimary else DarkCard,
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AnimePrimary else DarkCardBorder),
                                modifier = Modifier.clickable {
                                    selectedGenres = if (isSelected) selectedGenres - genre else selectedGenres + genre
                                }
                            ) {
                                Text(
                                    text = genre,
                                    color = if (isSelected) Color.White else DarkTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Metadata Row (Year, Status, Studio, Score)
                    Text("بيانات الإنتاج والحالة", color = Color(0xFF00E5FF), fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = releaseYear,
                            onValueChange = { releaseYear = it },
                            label = { Text("سنة الإصدار", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = fieldColors(),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = rating,
                            onValueChange = { rating = it },
                            label = { Text("التقييم (0-100)", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = fieldColors(),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = studio,
                            onValueChange = { studio = it },
                            label = { Text("الاستوديو", fontSize = 11.sp) },
                            colors = fieldColors(),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = status,
                            onValueChange = { status = it },
                            label = { Text("الحالة (مستمر / مكتمل)", fontSize = 11.sp) },
                            colors = fieldColors(),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Badges & Publishing Toggles
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("حالة النشر (ظاهر للمستخدمين)", color = DarkTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("عند التعطيل يتم إخفاء الأنمي مؤقتاً", color = DarkTextSecondary, fontSize = 11.sp)
                                }
                                Switch(
                                    checked = isPublished,
                                    onCheckedChange = { isPublished = it },
                                    colors = switchColors()
                                )
                            }

                            HorizontalDivider(color = DarkCardBorder, modifier = Modifier.padding(vertical = 8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("أنمي مميز (Featured)", color = DarkTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("يظهر في البانر العلوي بالصفحة الرئيسية", color = DarkTextSecondary, fontSize = 11.sp)
                                }
                                Switch(
                                    checked = isFeatured,
                                    onCheckedChange = { isFeatured = it },
                                    colors = switchColors()
                                )
                            }

                            HorizontalDivider(color = DarkCardBorder, modifier = Modifier.padding(vertical = 8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("وسم أنمي جديد (New)", color = DarkTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("إظهار شارة 'جديد' على بطاقة الأنمي", color = DarkTextSecondary, fontSize = 11.sp)
                                }
                                Switch(
                                    checked = isNew,
                                    onCheckedChange = { isNew = it },
                                    colors = switchColors()
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = DarkCardBorder, modifier = Modifier.padding(vertical = 10.dp))

                // Bottom Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("إلغاء", color = DarkTextSecondary)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (titleArabic.isBlank() && titleEnglish.isBlank()) {
                                errorMessage = "يرجى إدخال اسم الأنمي بالعربية أو الإنجليزية"
                                return@Button
                            }
                            if (description.isBlank()) {
                                errorMessage = "يرجى إدخال وصف وقصة الأنمي"
                                return@Button
                            }
                            if (coverImage.isBlank()) {
                                coverImage = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600&auto=format&fit=crop&q=80"
                            }

                            val animeId = initialAnime?.id ?: (System.currentTimeMillis() % 1000000).toInt()

                            val updatedAnime = FirestoreAnime(
                                id = animeId,
                                titleArabic = titleArabic.trim(),
                                titleEnglish = titleEnglish.trim(),
                                titleRomaji = titleRomaji.trim(),
                                description = description.trim(),
                                coverImage = coverImage.trim(),
                                bannerImage = bannerImage.ifBlank { null },
                                screenshots = screenshots,
                                genres = selectedGenres.toList(),
                                format = format,
                                status = status,
                                seasonYear = releaseYear.toIntOrNull() ?: 2026,
                                seasonName = "موسم $releaseYear",
                                studios = listOf(studio.trim()),
                                averageScore = rating.toIntOrNull() ?: 85,
                                viewsCount = initialAnime?.viewsCount ?: 0L,
                                isPublished = isPublished,
                                isFeatured = isFeatured,
                                isNew = isNew,
                                createdAt = initialAnime?.createdAt ?: System.currentTimeMillis(),
                                updatedAt = System.currentTimeMillis(),
                                seasonsCount = initialAnime?.seasonsCount ?: 1,
                                episodesCount = initialAnime?.episodesCount ?: 12
                            )

                            onSave(updatedAnime)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimePrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (isEdit) "حفظ التعديلات" else "إضافة ونشر الأنمي", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = DarkTextPrimary,
    unfocusedTextColor = DarkTextPrimary,
    focusedBorderColor = AnimePrimary,
    unfocusedBorderColor = DarkCardBorder,
    focusedContainerColor = DarkCard,
    unfocusedContainerColor = DarkCard
)

@Composable
private fun switchColors() = SwitchDefaults.colors(
    checkedThumbColor = Color.White,
    checkedTrackColor = AnimePrimary,
    uncheckedThumbColor = DarkTextSecondary,
    uncheckedTrackColor = Color(0xFF141926)
)
