package com.example.keyboard.service

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.view.inputmethod.InputContentInfoCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.keyboard.MainActivity
import com.example.keyboard.engine.SuggestionEngine
import com.example.keyboard.model.ClipboardEntry
import com.example.keyboard.model.GifItem
import com.example.keyboard.model.KeyboardLanguage
import com.example.keyboard.model.KeyboardMode
import com.example.keyboard.model.KeyboardTheme
import com.example.keyboard.model.WordSuggestion
import com.example.keyboard.storage.KeyboardPreferences
import com.example.keyboard.ui.components.KeyboardView

class SmartInputMethodService : android.inputmethodservice.InputMethodService(), LifecycleOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    private lateinit var preferences: KeyboardPreferences
    private val suggestionEngine = SuggestionEngine()

    // Service State
    private var currentLanguage by mutableStateOf<KeyboardLanguage>(com.example.keyboard.model.KeyboardPresets.ARABIC_LANGUAGE)
    private var currentTheme by mutableStateOf<KeyboardTheme>(com.example.keyboard.model.KeyboardPresets.THEMES.first())
    private var currentMode by mutableStateOf(KeyboardMode.ALPHA)
    private var isShifted by mutableStateOf(false)
    private var suggestions by mutableStateOf<List<WordSuggestion>>(emptyList())
    private var clipboardItems by mutableStateOf<List<ClipboardEntry>>(emptyList())
    private var isNumberRowEnabled by mutableStateOf(true)
    private var currentImeAction by mutableStateOf(EditorInfo.IME_ACTION_UNSPECIFIED)
    private var currentInputType by mutableStateOf(EditorInfo.TYPE_CLASS_TEXT)

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        preferences = KeyboardPreferences(this)
        currentLanguage = preferences.getCurrentLanguage()
        currentTheme = preferences.getCurrentTheme()
        isNumberRowEnabled = preferences.isNumberRowEnabled
        clipboardItems = preferences.getClipboardHistory()
    }

    override fun onCreateInputView(): View {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@SmartInputMethodService)
            setViewTreeSavedStateRegistryOwner(this@SmartInputMethodService)

            setContent {
                KeyboardView(
                    currentLanguage = currentLanguage,
                    currentTheme = currentTheme,
                    currentMode = currentMode,
                    isShifted = isShifted,
                    suggestions = suggestions,
                    clipboardItems = clipboardItems,
                    isNumberRowEnabled = isNumberRowEnabled,
                    imeAction = currentImeAction,
                    inputType = currentInputType,
                    onKeyClick = { text -> handleCommitText(text) },
                    onBackspace = { handleBackspace() },
                    onEnter = { handleEnter() },
                    onToggleShift = { isShifted = !isShifted },
                    onModeChange = { mode -> currentMode = mode },
                    onSwitchLanguage = {
                        currentLanguage = preferences.switchToNextLanguage()
                        updateSuggestions()
                    },
                    onOpenSettings = {
                        val intent = Intent(this@SmartInputMethodService, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    },
                    onNextTheme = {
                        val themes = com.example.keyboard.model.KeyboardPresets.THEMES
                        val idx = themes.indexOfFirst { it.id == currentTheme.id }
                        val nextIdx = if (idx >= 0 && idx < themes.size - 1) idx + 1 else 0
                        currentTheme = themes[nextIdx]
                        preferences.currentThemeId = currentTheme.id
                    },
                    onPasteClipboard = { text ->
                        handleCommitText(text)
                        currentMode = KeyboardMode.ALPHA
                    },
                    onTogglePinClipboard = { id ->
                        preferences.togglePinClipboard(id)
                        clipboardItems = preferences.getClipboardHistory()
                    },
                    onDeleteClipboard = { id ->
                        preferences.deleteClipboardItem(id)
                        clipboardItems = preferences.getClipboardHistory()
                    },
                    onClearClipboard = {
                        preferences.clearClipboardHistory()
                        clipboardItems = preferences.getClipboardHistory()
                    },
                    onGifSelected = { gif -> handleCommitGif(gif) }
                )
            }
        }
        return composeView
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)

        currentLanguage = preferences.getCurrentLanguage()
        currentTheme = preferences.getCurrentTheme()
        isNumberRowEnabled = preferences.isNumberRowEnabled
        clipboardItems = preferences.getClipboardHistory()

        currentImeAction = info?.imeOptions?.and(EditorInfo.IME_MASK_ACTION) ?: EditorInfo.IME_ACTION_UNSPECIFIED
        currentInputType = info?.inputType ?: EditorInfo.TYPE_CLASS_TEXT

        // Switch to number pad if target is number or phone
        val isNumberField = (currentInputType and EditorInfo.TYPE_MASK_CLASS) == EditorInfo.TYPE_CLASS_NUMBER ||
                (currentInputType and EditorInfo.TYPE_MASK_CLASS) == EditorInfo.TYPE_CLASS_PHONE
        if (isNumberField) {
            currentMode = KeyboardMode.NUMBERS_SYMBOLS_1
        } else {
            currentMode = KeyboardMode.ALPHA
        }

        updateSuggestions()
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        currentMode = KeyboardMode.ALPHA
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    private fun handleCommitText(text: String) {
        val ic = currentInputConnection ?: return
        ic.commitText(text, 1)

        // Capture to clipboard history if long text
        if (text.length > 15 && !isPasswordField()) {
            preferences.addClipboardText(text)
            clipboardItems = preferences.getClipboardHistory()
        }

        updateSuggestions()
    }

    private fun handleBackspace() {
        val ic = currentInputConnection ?: return
        val selected = ic.getSelectedText(0)
        if (selected != null && selected.isNotEmpty()) {
            ic.commitText("", 1)
        } else {
            ic.deleteSurroundingText(1, 0)
        }
        updateSuggestions()
    }

    private fun handleEnter() {
        val ic = currentInputConnection ?: return
        if (currentImeAction != EditorInfo.IME_ACTION_UNSPECIFIED && currentImeAction != EditorInfo.IME_ACTION_NONE) {
            ic.performEditorAction(currentImeAction)
        } else {
            sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER)
        }
    }

    private fun handleCommitGif(gif: GifItem) {
        val ic = currentInputConnection ?: return
        val editorInfo = currentInputEditorInfo ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val mimeTypes = EditorInfoCompat.getContentMimeTypes(editorInfo)
            val hasGifSupport = mimeTypes.any { it.contains("gif", ignoreCase = true) || it.contains("image", ignoreCase = true) }

            if (hasGifSupport) {
                try {
                    val contentUri = Uri.parse(gif.gifUrl)
                    val description = ClipData.newUri(contentResolver, gif.title, contentUri).description
                    val inputContentInfo = InputContentInfoCompat(contentUri, description, Uri.parse(gif.previewUrl))
                    InputConnectionCompat.commitContent(ic, editorInfo, inputContentInfo, 0, null)
                    currentMode = KeyboardMode.ALPHA
                    return
                } catch (ignored: Exception) {}
            }
        }

        // Fallback: paste link or title safely
        ic.commitText(gif.gifUrl, 1)
        currentMode = KeyboardMode.ALPHA
    }

    private fun updateSuggestions() {
        if (!preferences.isSuggestionBarEnabled || isPasswordField()) {
            suggestions = emptyList()
            return
        }

        val ic = currentInputConnection
        val before = ic?.getTextBeforeCursor(20, 0)?.toString() ?: ""
        val lastWord = before.split(Regex("\\s+")).lastOrNull() ?: ""
        val prevWord = before.split(Regex("\\s+")).dropLast(1).lastOrNull()

        suggestions = suggestionEngine.getSuggestions(lastWord, prevWord)
    }

    private fun isPasswordField(): Boolean {
        val type = currentInputType and EditorInfo.TYPE_MASK_VARIATION
        return type == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD ||
                type == EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                type == EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD
    }
}
