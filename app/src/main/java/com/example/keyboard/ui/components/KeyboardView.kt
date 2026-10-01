package com.example.keyboard.ui.components

import android.view.inputmethod.EditorInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keyboard.model.ClipboardEntry
import com.example.keyboard.model.GifItem
import com.example.keyboard.model.KeyboardLanguage
import com.example.keyboard.model.KeyboardMode
import com.example.keyboard.model.KeyboardPresets
import com.example.keyboard.model.KeyboardTheme
import com.example.keyboard.model.WordSuggestion

@Composable
fun KeyboardView(
    currentLanguage: KeyboardLanguage,
    currentTheme: KeyboardTheme,
    currentMode: KeyboardMode,
    isShifted: Boolean,
    suggestions: List<WordSuggestion>,
    clipboardItems: List<ClipboardEntry>,
    isNumberRowEnabled: Boolean,
    imeAction: Int = EditorInfo.IME_ACTION_UNSPECIFIED,
    inputType: Int = EditorInfo.TYPE_CLASS_TEXT,
    onKeyClick: (String) -> Unit,
    onBackspace: () -> Unit,
    onEnter: () -> Unit,
    onToggleShift: () -> Unit,
    onModeChange: (KeyboardMode) -> Unit,
    onSwitchLanguage: () -> Unit,
    onOpenSettings: () -> Unit,
    onNextTheme: () -> Unit,
    onPasteClipboard: (String) -> Unit,
    onTogglePinClipboard: (String) -> Unit,
    onDeleteClipboard: (String) -> Unit,
    onClearClipboard: () -> Unit,
    onGifSelected: (GifItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var dragAmount by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(currentTheme.backgroundColor))
    ) {
        // Suggestion Bar (Always Top)
        SuggestionBar(
            suggestions = suggestions,
            theme = currentTheme,
            onSuggestionClick = { onKeyClick("$it ") },
            onModeChange = onModeChange,
            onSwitchLanguage = onSwitchLanguage,
            onOpenSettings = onOpenSettings,
            onNextTheme = onNextTheme
        )

        // Main Keyboard Area
        when (currentMode) {
            KeyboardMode.EMOJI -> {
                EmojiPickerView(
                    theme = currentTheme,
                    onEmojiSelected = onKeyClick,
                    onBackspace = onBackspace,
                    onBackToAlpha = { onModeChange(KeyboardMode.ALPHA) }
                )
            }
            KeyboardMode.CLIPBOARD -> {
                ClipboardView(
                    clipboardItems = clipboardItems,
                    theme = currentTheme,
                    onPasteText = onPasteClipboard,
                    onTogglePin = onTogglePinClipboard,
                    onDeleteItem = onDeleteClipboard,
                    onClearAll = onClearClipboard,
                    onBackToAlpha = { onModeChange(KeyboardMode.ALPHA) }
                )
            }
            KeyboardMode.GIF -> {
                GifPickerView(
                    theme = currentTheme,
                    onGifSelected = onGifSelected,
                    onBackToAlpha = { onModeChange(KeyboardMode.ALPHA) }
                )
            }
            KeyboardMode.ALPHA,
            KeyboardMode.NUMBERS_SYMBOLS_1,
            KeyboardMode.NUMBERS_SYMBOLS_2 -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 3.dp, vertical = 4.dp)
                ) {
                    // Number Row (Optional/Customizable)
                    if (isNumberRowEnabled && currentMode == KeyboardMode.ALPHA) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            KeyboardPresets.NUMBER_ROW.forEach { num ->
                                KeyButton(
                                    text = num,
                                    theme = currentTheme,
                                    onClick = { onKeyClick(num) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Dynamic Key Rows
                    val rows: List<List<String>> = when (currentMode) {
                        KeyboardMode.NUMBERS_SYMBOLS_1 -> KeyboardPresets.SYMBOLS_PAGE_1
                        KeyboardMode.NUMBERS_SYMBOLS_2 -> KeyboardPresets.SYMBOLS_PAGE_2
                        KeyboardMode.ALPHA -> {
                            if (isShifted && currentLanguage.shiftedRows != null) {
                                currentLanguage.shiftedRows
                            } else {
                                currentLanguage.defaultRows
                            }
                        }
                        else -> currentLanguage.defaultRows
                    }

                    rows.forEachIndexed { rowIndex, rowKeys ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Row 3 leading button (Shift / 123 / =<)
                            if (rowIndex == rows.size - 1) {
                                if (currentMode == KeyboardMode.ALPHA) {
                                    KeyButton(
                                        text = "⇧",
                                        theme = currentTheme,
                                        onClick = onToggleShift,
                                        isSpecial = true,
                                        isHighlighted = isShifted,
                                        modifier = Modifier.weight(1.3f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowUpward,
                                            contentDescription = "Shift",
                                            tint = if (isShifted) Color.White else Color(currentTheme.specialKeyTextColor),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                } else if (currentMode == KeyboardMode.NUMBERS_SYMBOLS_1) {
                                    KeyButton(
                                        text = "=\\<",
                                        theme = currentTheme,
                                        onClick = { onModeChange(KeyboardMode.NUMBERS_SYMBOLS_2) },
                                        isSpecial = true,
                                        modifier = Modifier.weight(1.3f)
                                    )
                                } else if (currentMode == KeyboardMode.NUMBERS_SYMBOLS_2) {
                                    KeyButton(
                                        text = "?123",
                                        theme = currentTheme,
                                        onClick = { onModeChange(KeyboardMode.NUMBERS_SYMBOLS_1) },
                                        isSpecial = true,
                                        modifier = Modifier.weight(1.3f)
                                    )
                                }
                            }

                            // Standard Characters in this row
                            rowKeys.forEach { keyStr ->
                                val letter = if (isShifted && currentLanguage.shiftedRows == null) {
                                    keyStr.uppercase()
                                } else {
                                    keyStr
                                }
                                KeyButton(
                                    text = letter,
                                    theme = currentTheme,
                                    onClick = { onKeyClick(letter) },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Row 3 trailing button (Backspace)
                            if (rowIndex == rows.size - 1) {
                                KeyButton(
                                    text = "⌫",
                                    theme = currentTheme,
                                    onClick = onBackspace,
                                    isSpecial = true,
                                    modifier = Modifier.weight(1.3f)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                                        contentDescription = "Delete",
                                        tint = Color(currentTheme.specialKeyTextColor),
                                        modifier = Modifier.size(19.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Bottom Control Row: ?123, Emoji/Globe, Comma, Space, Dot, Action/Enter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mode Switcher (?123 or ABC)
                        KeyButton(
                            text = if (currentMode == KeyboardMode.ALPHA) "?123" else "ABC",
                            theme = currentTheme,
                            onClick = {
                                onModeChange(if (currentMode == KeyboardMode.ALPHA) KeyboardMode.NUMBERS_SYMBOLS_1 else KeyboardMode.ALPHA)
                            },
                            isSpecial = true,
                            modifier = Modifier.weight(1.2f)
                        )

                        // Emoji Key
                        KeyButton(
                            text = "😊",
                            theme = currentTheme,
                            onClick = { onModeChange(KeyboardMode.EMOJI) },
                            isSpecial = true,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SentimentSatisfiedAlt,
                                contentDescription = "Emoji",
                                tint = Color(currentTheme.specialKeyTextColor),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Comma / Language Key
                        KeyButton(
                            text = ",",
                            theme = currentTheme,
                            onClick = { onKeyClick(",") },
                            isSpecial = true,
                            modifier = Modifier.weight(0.9f)
                        )

                        // Spacebar (with swipe gesture support for language switching)
                        Box(
                            modifier = Modifier
                                .weight(3.6f)
                                .pointerInput(Unit) {
                                    detectHorizontalDragGestures(
                                        onDragEnd = {
                                            if (dragAmount > 50 || dragAmount < -50) {
                                                onSwitchLanguage()
                                            }
                                            dragAmount = 0f
                                        },
                                        onHorizontalDrag = { _, dragDelta ->
                                            dragAmount += dragDelta
                                        }
                                    )
                                }
                        ) {
                            KeyButton(
                                text = currentLanguage.name,
                                theme = currentTheme,
                                onClick = { onKeyClick(" ") },
                                isSpecial = false,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = currentLanguage.name,
                                        color = Color(currentTheme.keyTextColor).copy(alpha = 0.7f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Dot or Domain key
                        val isEmail = inputType and EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS != 0
                        val isUri = inputType and EditorInfo.TYPE_TEXT_VARIATION_URI != 0
                        val dotLabel = when {
                            isEmail -> "@"
                            isUri -> "/"
                            else -> "."
                        }
                        KeyButton(
                            text = dotLabel,
                            theme = currentTheme,
                            onClick = { onKeyClick(dotLabel) },
                            isSpecial = true,
                            modifier = Modifier.weight(0.9f)
                        )

                        // Enter / Action Key
                        val actionIcon = when (imeAction) {
                            EditorInfo.IME_ACTION_SEARCH -> Icons.Default.Search
                            EditorInfo.IME_ACTION_SEND -> Icons.Default.Send
                            EditorInfo.IME_ACTION_DONE,
                            EditorInfo.IME_ACTION_GO -> Icons.Default.Check
                            else -> Icons.AutoMirrored.Filled.KeyboardReturn
                        }
                        KeyButton(
                            text = "↵",
                            theme = currentTheme,
                            onClick = onEnter,
                            isHighlighted = true,
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Icon(
                                imageVector = actionIcon,
                                contentDescription = "Enter",
                                tint = Color.White,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
