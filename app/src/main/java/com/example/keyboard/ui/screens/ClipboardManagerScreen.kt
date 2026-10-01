package com.example.keyboard.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keyboard.storage.KeyboardPreferences
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary

@Composable
fun ClipboardManagerScreen(
    preferences: KeyboardPreferences,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var clipboardItems by remember { mutableStateOf(preferences.getClipboardHistory()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newSnippetText by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    val filtered = if (searchQuery.isBlank()) clipboardItems else clipboardItems.filter {
        it.text.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        // Header
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "الحافظة والنصوص المنسوخة",
                    color = DarkTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${clipboardItems.size} عنصر مسجل",
                    color = Color(0xFF00E5FF),
                    fontSize = 11.sp
                )
            }

            IconButton(onClick = { showAddDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Snippet",
                    tint = Color(0xFFFF2A5F)
                )
            }

            IconButton(onClick = {
                preferences.clearClipboardHistory()
                clipboardItems = preferences.getClipboardHistory()
            }) {
                Icon(
                    imageVector = Icons.Default.ClearAll,
                    contentDescription = "Clear All",
                    tint = DarkTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search in clipboard
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("بحث في النصوص المحفوظة...", color = DarkTextSecondary, fontSize = 12.sp) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkCard,
                unfocusedContainerColor = DarkCard,
                focusedTextColor = DarkTextPrimary,
                unfocusedTextColor = DarkTextPrimary,
                focusedBorderColor = Color(0xFF00E5FF),
                unfocusedBorderColor = DarkCardBorder
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "لا توجد نصوص محفوظة في الحافظة حالياً",
                    color = DarkTextSecondary,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtered, key = { it.id }) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (item.isPinned) Color(0xFFFF2A5F).copy(alpha = 0.5f) else DarkCardBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.text,
                                color = DarkTextPrimary,
                                fontSize = 13.sp,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = {
                                    preferences.togglePinClipboard(item.id)
                                    clipboardItems = preferences.getClipboardHistory()
                                }) {
                                    Icon(
                                        imageVector = if (item.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                        contentDescription = "Pin",
                                        tint = if (item.isPinned) Color(0xFFFF2A5F) else DarkTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(onClick = {
                                    preferences.deleteClipboardItem(item.id)
                                    clipboardItems = preferences.getClipboardHistory()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.Red.copy(alpha = 0.7f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = DarkSurface,
            title = {
                Text("إضافة نص أو قالب سريع", color = DarkTextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                OutlinedTextField(
                    value = newSnippetText,
                    onValueChange = { newSnippetText = it },
                    placeholder = { Text("أدخل النص المراد حفظه في الحافظة...", color = DarkTextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = DarkTextPrimary,
                        unfocusedTextColor = DarkTextPrimary,
                        focusedBorderColor = Color(0xFFFF2A5F),
                        unfocusedBorderColor = DarkCardBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newSnippetText.isNotBlank()) {
                            preferences.addClipboardText(newSnippetText)
                            clipboardItems = preferences.getClipboardHistory()
                            newSnippetText = ""
                        }
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2A5F))
                ) {
                    Text("حفظ", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("إلغاء", color = DarkTextSecondary)
                }
            }
        )
    }
}
