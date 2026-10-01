package com.example.keyboard.ui.screens

import android.view.inputmethod.EditorInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keyboard.engine.SuggestionEngine
import com.example.keyboard.model.ClipboardEntry
import com.example.keyboard.model.GifItem
import com.example.keyboard.model.KeyboardLanguage
import com.example.keyboard.model.KeyboardMode
import com.example.keyboard.model.KeyboardPresets
import com.example.keyboard.model.KeyboardTheme
import com.example.keyboard.model.WordSuggestion
import com.example.keyboard.storage.KeyboardPreferences
import com.example.keyboard.ui.components.KeyboardView
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary

enum class FocusedField {
    NORMAL,
    EMAIL,
    PASSWORD,
    NUMBERS
}

@Composable
fun TestKeyboardScreen(
    preferences: KeyboardPreferences,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var normalText by remember { mutableStateOf("مرحباً بك في لوحة المفاتيح الذكية Smart Keyboard Pro!") }
    var emailText by remember { mutableStateOf("user@example.com") }
    var passwordText by remember { mutableStateOf("SecretPass123!") }
    var numberText by remember { mutableStateOf("1234567890") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var focusedField by remember { mutableStateOf(FocusedField.NORMAL) }

    // Live In-App Keyboard Interactive State
    var currentLanguage by remember { mutableStateOf(preferences.getCurrentLanguage()) }
    var currentTheme by remember { mutableStateOf(preferences.getCurrentTheme()) }
    var currentMode by remember { mutableStateOf(KeyboardMode.ALPHA) }
    var isShifted by remember { mutableStateOf(false) }
    var suggestions by remember { mutableStateOf<List<WordSuggestion>>(emptyList()) }
    var clipboardItems by remember { mutableStateOf(preferences.getClipboardHistory()) }

    val suggestionEngine = remember { SuggestionEngine() }

    fun updateActiveText(newText: String) {
        when (focusedField) {
            FocusedField.NORMAL -> normalText = newText
            FocusedField.EMAIL -> emailText = newText
            FocusedField.PASSWORD -> passwordText = newText
            FocusedField.NUMBERS -> numberText = newText
        }

        val lastWord = newText.split(Regex("\\s+")).lastOrNull() ?: ""
        val prevWord = newText.split(Regex("\\s+")).dropLast(1).lastOrNull()
        suggestions = suggestionEngine.getSuggestions(lastWord, prevWord)
    }

    fun getActiveText(): String = when (focusedField) {
        FocusedField.NORMAL -> normalText
        FocusedField.EMAIL -> emailText
        FocusedField.PASSWORD -> passwordText
        FocusedField.NUMBERS -> numberText
    }

    fun handleKeyClick(key: String) {
        val current = getActiveText()
        updateActiveText(current + key)
    }

    fun handleBackspace() {
        val current = getActiveText()
        if (current.isNotEmpty()) {
            updateActiveText(current.dropLast(1))
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "مربع تجربة واختبار الكيبورد",
                    color = DarkTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "اختبر الكتابة، الأرقام، الإيموجي، الحافظة، وتغيير اللغة مباشرة",
                    color = Color(0xFF00E5FF),
                    fontSize = 11.sp
                )
            }
        }

        // Test Input Fields Area (Scrollable)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Normal Text Field
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (focusedField == FocusedField.NORMAL) Color(0xFF1E2638) else DarkCard
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (focusedField == FocusedField.NORMAL) Color(0xFF00E5FF) else DarkCardBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { focusedField = FocusedField.NORMAL }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "حقل نص عام (Normal Text)",
                            color = if (focusedField == FocusedField.NORMAL) Color(0xFF00E5FF) else DarkTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { normalText = "" }, modifier = Modifier.size(24.dp)) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = DarkTextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                    OutlinedTextField(
                        value = normalText,
                        onValueChange = { updateActiveText(it) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DarkTextPrimary,
                            unfocusedTextColor = DarkTextPrimary,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 2. Email Field
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (focusedField == FocusedField.EMAIL) Color(0xFF1E2638) else DarkCard
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (focusedField == FocusedField.EMAIL) Color(0xFF00E5FF) else DarkCardBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { focusedField = FocusedField.EMAIL }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "حقل بريد إلكتروني (Email @ .com)",
                        color = if (focusedField == FocusedField.EMAIL) Color(0xFF00E5FF) else DarkTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = emailText,
                        onValueChange = { updateActiveText(it) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DarkTextPrimary,
                            unfocusedTextColor = DarkTextPrimary,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 3. Password Field
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (focusedField == FocusedField.PASSWORD) Color(0xFF1E2638) else DarkCard
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (focusedField == FocusedField.PASSWORD) Color(0xFF00E5FF) else DarkCardBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { focusedField = FocusedField.PASSWORD }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "حقل كلمة مرور (Secure Password)",
                            color = if (focusedField == FocusedField.PASSWORD) Color(0xFF00E5FF) else DarkTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }, modifier = Modifier.size(24.dp)) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Visibility",
                                tint = DarkTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    OutlinedTextField(
                        value = passwordText,
                        onValueChange = { updateActiveText(it) },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DarkTextPrimary,
                            unfocusedTextColor = DarkTextPrimary,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 4. Numbers Field
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (focusedField == FocusedField.NUMBERS) Color(0xFF1E2638) else DarkCard
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (focusedField == FocusedField.NUMBERS) Color(0xFF00E5FF) else DarkCardBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        focusedField = FocusedField.NUMBERS
                        currentMode = KeyboardMode.NUMBERS_SYMBOLS_1
                    }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "حقل أرقام وهاتف (Numeric Keypad 123)",
                        color = if (focusedField == FocusedField.NUMBERS) Color(0xFF00E5FF) else DarkTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = numberText,
                        onValueChange = { updateActiveText(it) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DarkTextPrimary,
                            unfocusedTextColor = DarkTextPrimary,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Live Interactive Keyboard Preview Docked at Bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(currentTheme.backgroundColor))
        ) {
            KeyboardView(
                currentLanguage = currentLanguage,
                currentTheme = currentTheme,
                currentMode = currentMode,
                isShifted = isShifted,
                suggestions = suggestions,
                clipboardItems = clipboardItems,
                isNumberRowEnabled = preferences.isNumberRowEnabled,
                imeAction = EditorInfo.IME_ACTION_DONE,
                inputType = if (focusedField == FocusedField.EMAIL) EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS else EditorInfo.TYPE_CLASS_TEXT,
                onKeyClick = { key -> handleKeyClick(key) },
                onBackspace = { handleBackspace() },
                onEnter = { handleKeyClick("\n") },
                onToggleShift = { isShifted = !isShifted },
                onModeChange = { mode -> currentMode = mode },
                onSwitchLanguage = {
                    currentLanguage = preferences.switchToNextLanguage()
                },
                onOpenSettings = onBack,
                onNextTheme = {
                    val themes = KeyboardPresets.THEMES
                    val idx = themes.indexOfFirst { it.id == currentTheme.id }
                    val nextIdx = if (idx >= 0 && idx < themes.size - 1) idx + 1 else 0
                    currentTheme = themes[nextIdx]
                    preferences.currentThemeId = currentTheme.id
                },
                onPasteClipboard = { text ->
                    handleKeyClick(text)
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
                onGifSelected = { gif ->
                    handleKeyClick(" [GIF: ${gif.title}] ")
                    currentMode = KeyboardMode.ALPHA
                }
            )
        }
    }
}
