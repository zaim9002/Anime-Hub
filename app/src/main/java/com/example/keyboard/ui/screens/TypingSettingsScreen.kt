package com.example.keyboard.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keyboard.storage.KeyboardPreferences
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary

@Composable
fun TypingSettingsScreen(
    preferences: KeyboardPreferences,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var isAutoCorrection by remember { mutableStateOf(preferences.isAutoCorrectionEnabled) }
    var isSuggestionBar by remember { mutableStateOf(preferences.isSuggestionBarEnabled) }
    var isNextWord by remember { mutableStateOf(preferences.isNextWordPredictionEnabled) }
    var isNumberRow by remember { mutableStateOf(preferences.isNumberRowEnabled) }
    var isVibration by remember { mutableStateOf(preferences.isVibrationEnabled) }
    var isSound by remember { mutableStateOf(preferences.isSoundEnabled) }
    var isSwipeSpace by remember { mutableStateOf(preferences.isSwipeSpaceLanguageSwitchEnabled) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
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
                Column {
                    Text(
                        text = "إعدادات الكتابة والتصحيح",
                        color = DarkTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "خصص سرعة ودقة الكتابة وردود الفعل اللمسية والصوتية",
                        color = Color(0xFF00E5FF),
                        fontSize = 11.sp
                    )
                }
            }
        }

        item {
            Text(
                text = "التصحيح والاقتراحات الذكية",
                color = DarkTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ToggleSettingItem(
                        title = "التصحيح التلقائي للأخطاء",
                        subtitle = "تصحيح الكلمات الشائعة والأخطاء الإملائية تلقائياً",
                        checked = isAutoCorrection,
                        onCheckedChange = {
                            isAutoCorrection = it
                            preferences.isAutoCorrectionEnabled = it
                        }
                    )

                    HorizontalDivider(color = DarkCardBorder)

                    ToggleSettingItem(
                        title = "شريط الاقتراحات والكلمات",
                        subtitle = "عرض 3 اقتراحات ذكية أعلى الكيبورد أثناء الكتابة",
                        checked = isSuggestionBar,
                        onCheckedChange = {
                            isSuggestionBar = it
                            preferences.isSuggestionBarEnabled = it
                        }
                    )

                    HorizontalDivider(color = DarkCardBorder)

                    ToggleSettingItem(
                        title = "اقتراح الكلمة التالية",
                        subtitle = "توقع الكلمة التالية بناءً على السياق لزيادة سرعة الكتابة",
                        checked = isNextWord,
                        onCheckedChange = {
                            isNextWord = it
                            preferences.isNextWordPredictionEnabled = it
                        }
                    )
                }
            }
        }

        item {
            Text(
                text = "المظهر والتخطيط",
                color = DarkTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ToggleSettingItem(
                        title = "صف الأرقام المستقل",
                        subtitle = "إظهار صف الأرقام (1-0) بشكل دائم أعلى الحروف",
                        checked = isNumberRow,
                        onCheckedChange = {
                            isNumberRow = it
                            preferences.isNumberRowEnabled = it
                        }
                    )

                    HorizontalDivider(color = DarkCardBorder)

                    ToggleSettingItem(
                        title = "التبديل بالسحب على زر المسافة",
                        subtitle = "اسحب يميناً أو يساراً على زر Space لتبديل لغة الإدخال فوراً",
                        checked = isSwipeSpace,
                        onCheckedChange = {
                            isSwipeSpace = it
                            preferences.isSwipeSpaceLanguageSwitchEnabled = it
                        }
                    )
                }
            }
        }

        item {
            Text(
                text = "ردود الفعل اللمسية والصوتية",
                color = DarkTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ToggleSettingItem(
                        title = "الاهتزاز اللمسي عند الضغط (Haptic)",
                        subtitle = "نبضة اهتزاز خفيفة وسريعة لتأكيد الضغط على المفاتيح",
                        checked = isVibration,
                        onCheckedChange = {
                            isVibration = it
                            preferences.isVibrationEnabled = it
                        }
                    )

                    HorizontalDivider(color = DarkCardBorder)

                    ToggleSettingItem(
                        title = "صوت النقر على المفاتيح",
                        subtitle = "تشغيل صوت نقر ناعم عند الضغط",
                        checked = isSound,
                        onCheckedChange = {
                            isSound = it
                            preferences.isSoundEnabled = it
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ToggleSettingItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = DarkTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = DarkTextSecondary,
                fontSize = 11.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFFFF2A5F),
                uncheckedThumbColor = DarkTextSecondary,
                uncheckedTrackColor = Color(0xFF141926)
            )
        )
    }
}
