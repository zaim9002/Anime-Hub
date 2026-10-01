package com.example.keyboard.model

enum class KeyboardMode {
    ALPHA,
    NUMBERS_SYMBOLS_1,
    NUMBERS_SYMBOLS_2,
    EMOJI,
    CLIPBOARD,
    GIF
}

data class KeyboardLanguage(
    val code: String,
    val name: String,
    val nativeName: String,
    val isRtl: Boolean = false,
    val defaultRows: List<List<String>>,
    val shiftedRows: List<List<String>>? = null
)

data class KeyboardTheme(
    val id: String,
    val name: String,
    val backgroundColor: Long,
    val surfaceColor: Long,
    val keyBackgroundColor: Long,
    val keyTextColor: Long,
    val specialKeyColor: Long,
    val specialKeyTextColor: Long,
    val accentColor: Long,
    val suggestionBgColor: Long,
    val isDark: Boolean = true
)

data class ClipboardEntry(
    val id: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
)

data class EmojiItem(
    val emoji: String,
    val description: String = "",
    val category: String = "Smileys"
)

data class GifItem(
    val id: String,
    val title: String,
    val previewUrl: String,
    val gifUrl: String,
    val category: String
)

data class WordSuggestion(
    val text: String,
    val isAutoCorrect: Boolean = false
)
