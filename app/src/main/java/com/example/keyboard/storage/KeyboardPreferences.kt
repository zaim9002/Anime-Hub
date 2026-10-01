package com.example.keyboard.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.keyboard.model.ClipboardEntry
import com.example.keyboard.model.KeyboardLanguage
import com.example.keyboard.model.KeyboardPresets
import com.example.keyboard.model.KeyboardTheme
import org.json.JSONArray
import org.json.JSONObject

class KeyboardPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("smart_keyboard_prefs", Context.MODE_PRIVATE)

    // Language settings
    var currentLanguageCode: String
        get() = prefs.getString("current_language_code", "ar") ?: "ar"
        set(value) = prefs.edit().putString("current_language_code", value).apply()

    var enabledLanguageCodes: Set<String>
        get() = prefs.getStringSet("enabled_languages", setOf("ar", "en")) ?: setOf("ar", "en")
        set(value) = prefs.edit().putStringSet("enabled_languages", value).apply()

    fun getActiveLanguages(): List<KeyboardLanguage> {
        val codes = enabledLanguageCodes
        val matched = KeyboardPresets.ALL_SUPPORTED_LANGUAGES.filter { codes.contains(it.code) }
        return if (matched.isEmpty()) listOf(KeyboardPresets.ARABIC_LANGUAGE, KeyboardPresets.ENGLISH_LANGUAGE) else matched
    }

    fun getCurrentLanguage(): KeyboardLanguage {
        val code = currentLanguageCode
        return KeyboardPresets.ALL_SUPPORTED_LANGUAGES.firstOrNull { it.code == code }
            ?: KeyboardPresets.ARABIC_LANGUAGE
    }

    fun switchToNextLanguage(): KeyboardLanguage {
        val active = getActiveLanguages()
        val currentIndex = active.indexOfFirst { it.code == currentLanguageCode }
        val nextIndex = if (currentIndex >= 0 && currentIndex < active.size - 1) currentIndex + 1 else 0
        val nextLang = active[nextIndex]
        currentLanguageCode = nextLang.code
        return nextLang
    }

    // Theme settings
    var currentThemeId: String
        get() = prefs.getString("current_theme_id", "dark_neon") ?: "dark_neon"
        set(value) = prefs.edit().putString("current_theme_id", value).apply()

    fun getCurrentTheme(): KeyboardTheme {
        val id = currentThemeId
        return KeyboardPresets.THEMES.firstOrNull { it.id == id } ?: KeyboardPresets.THEMES.first()
    }

    // Typing features
    var isAutoCorrectionEnabled: Boolean
        get() = prefs.getBoolean("auto_correction_enabled", true)
        set(value) = prefs.edit().putBoolean("auto_correction_enabled", value).apply()

    var isSuggestionBarEnabled: Boolean
        get() = prefs.getBoolean("suggestion_bar_enabled", true)
        set(value) = prefs.edit().putBoolean("suggestion_bar_enabled", value).apply()

    var isNextWordPredictionEnabled: Boolean
        get() = prefs.getBoolean("next_word_prediction", true)
        set(value) = prefs.edit().putBoolean("next_word_prediction", value).apply()

    var isVibrationEnabled: Boolean
        get() = prefs.getBoolean("vibration_enabled", true)
        set(value) = prefs.edit().putBoolean("vibration_enabled", value).apply()

    var isSoundEnabled: Boolean
        get() = prefs.getBoolean("sound_enabled", false)
        set(value) = prefs.edit().putBoolean("sound_enabled", value).apply()

    var isKeyPopupEnabled: Boolean
        get() = prefs.getBoolean("key_popup_enabled", true)
        set(value) = prefs.edit().putBoolean("key_popup_enabled", value).apply()

    var isNumberRowEnabled: Boolean
        get() = prefs.getBoolean("number_row_enabled", true)
        set(value) = prefs.edit().putBoolean("number_row_enabled", value).apply()

    var isSwipeSpaceLanguageSwitchEnabled: Boolean
        get() = prefs.getBoolean("swipe_space_language_switch", true)
        set(value) = prefs.edit().putBoolean("swipe_space_language_switch", value).apply()

    var keyHeightScale: Float
        get() = prefs.getFloat("key_height_scale", 1.0f)
        set(value) = prefs.edit().putFloat("key_height_scale", value).apply()

    // Clipboard persistence
    fun getClipboardHistory(): List<ClipboardEntry> {
        val jsonString = prefs.getString("clipboard_history_json", "[]") ?: "[]"
        val list = mutableListOf<ClipboardEntry>()
        try {
            val arr = JSONArray(jsonString)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    ClipboardEntry(
                        id = obj.getString("id"),
                        text = obj.getString("text"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        isPinned = obj.optBoolean("isPinned", false)
                    )
                )
            }
        } catch (e: Exception) {
            // fallback
        }
        return list
    }

    fun addClipboardText(text: String) {
        if (text.isBlank()) return
        val current = getClipboardHistory().toMutableList()
        current.removeAll { it.text == text && !it.isPinned }
        current.add(0, ClipboardEntry(id = System.currentTimeMillis().toString(), text = text))
        // limit to 30 items
        val trimmed = current.take(30)
        saveClipboardHistory(trimmed)
    }

    fun togglePinClipboard(id: String) {
        val current = getClipboardHistory().map {
            if (it.id == id) it.copy(isPinned = !it.isPinned) else it
        }
        saveClipboardHistory(current)
    }

    fun deleteClipboardItem(id: String) {
        val current = getClipboardHistory().filter { it.id != id }
        saveClipboardHistory(current)
    }

    fun clearClipboardHistory() {
        val pinned = getClipboardHistory().filter { it.isPinned }
        saveClipboardHistory(pinned)
    }

    private fun saveClipboardHistory(list: List<ClipboardEntry>) {
        val arr = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("text", item.text)
                put("timestamp", item.timestamp)
                put("isPinned", item.isPinned)
            }
            arr.put(obj)
        }
        prefs.edit().putString("clipboard_history_json", arr.toString()).apply()
    }
}
