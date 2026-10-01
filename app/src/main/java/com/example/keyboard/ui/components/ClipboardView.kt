package com.example.keyboard.ui.components

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
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keyboard.model.ClipboardEntry
import com.example.keyboard.model.KeyboardTheme

@Composable
fun ClipboardView(
    clipboardItems: List<ClipboardEntry>,
    theme: KeyboardTheme,
    onPasteText: (String) -> Unit,
    onTogglePin: (String) -> Unit,
    onDeleteItem: (String) -> Unit,
    onClearAll: () -> Unit,
    onBackToAlpha: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = if (searchQuery.isBlank()) clipboardItems else clipboardItems.filter {
        it.text.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(Color(theme.backgroundColor))
    ) {
        // Header & Search
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(theme.surfaceColor))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("بحث في الحافظة...", fontSize = 11.sp, color = Color(theme.keyTextColor).copy(alpha = 0.5f)) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(theme.keyBackgroundColor),
                    unfocusedContainerColor = Color(theme.keyBackgroundColor),
                    focusedTextColor = Color(theme.keyTextColor),
                    unfocusedTextColor = Color(theme.keyTextColor),
                    focusedBorderColor = Color(theme.accentColor),
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(onClick = onClearAll, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Default.ClearAll,
                    contentDescription = "Clear",
                    tint = Color(theme.specialKeyTextColor),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Clipboard List
        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "الحافظة فارغة (انسخ أي نص وسيظهر هنا تلقائياً)",
                    color = Color(theme.keyTextColor).copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filtered, key = { it.id }) { item ->
                    Surface(
                        color = Color(theme.keyBackgroundColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPasteText(item.text) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = item.text,
                                color = Color(theme.keyTextColor),
                                fontSize = 13.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onTogglePin(item.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (item.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                        contentDescription = "Pin",
                                        tint = if (item.isPinned) Color(theme.accentColor) else Color(theme.specialKeyTextColor),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { onDeleteItem(item.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.Red.copy(alpha = 0.7f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Bar (Back to Alpha Keyboard)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(theme.surfaceColor))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(theme.specialKeyColor),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .clickable(onClick = onBackToAlpha)
                    .height(38.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = "ABC",
                        tint = Color(theme.specialKeyTextColor),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "العودة للوحة المفاتيح",
                        color = Color(theme.specialKeyTextColor),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
