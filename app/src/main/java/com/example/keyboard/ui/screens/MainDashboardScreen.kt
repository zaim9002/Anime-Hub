package com.example.keyboard.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keyboard.model.KeyboardPresets
import com.example.keyboard.storage.KeyboardPreferences
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary

enum class AppNavScreen {
    DASHBOARD,
    TEST_PLAYGROUND,
    LANGUAGES,
    THEMES,
    CLIPBOARD,
    TYPING_SETTINGS,
    PRIVACY
}

@Composable
fun MainDashboardScreen(
    preferences: KeyboardPreferences,
    onNavigate: (AppNavScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isImeEnabled by remember { mutableStateOf(false) }
    var isImeSelected by remember { mutableStateOf(false) }

    fun checkImeStatus() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val list = imm?.enabledInputMethodList.orEmpty()
        val packageName = context.packageName
        isImeEnabled = list.any { it.packageName == packageName }

        val defaultIme = Settings.Secure.getString(context.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD) ?: ""
        isImeSelected = defaultIme.contains(packageName)
    }

    LaunchedEffect(Unit) {
        checkImeStatus()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "Smart Keyboard Pro",
                                color = DarkTextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "لوحة المفاتيح الذكية المتكاملة للأندرويد",
                                color = Color(0xFF00E5FF),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Surface(
                            color = Color(0xFFFF2A5F),
                            shape = CircleShape,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Keyboard,
                                    contentDescription = "Keyboard",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Setup Steps (Enable & Select Keyboard)
        item {
            Text(
                text = "خطوات تفعيل الكيبورد في النظام",
                color = DarkTextPrimary,
                fontSize = 15.sp,
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
                Column(modifier = Modifier.padding(16.dp)) {
                    // Step 1: Enable in system settings
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = if (isImeEnabled) Color(0xFF10B981) else Color(0xFFFF2A5F),
                                shape = CircleShape,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "1",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "تفعيل في إعدادات أندرويد",
                                    color = DarkTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (isImeEnabled) "✓ مفعل بنجاح" else "غير مفعل حالياً",
                                    color = if (isImeEnabled) Color(0xFF10B981) else DarkTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isImeEnabled) Color(0xFF1F2937) else Color(0xFFFF2A5F)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = if (isImeEnabled) "الإعدادات" else "تفعيل الآن", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Step 2: Set as default IME
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = if (isImeSelected) Color(0xFF10B981) else Color(0xFF3B82F6),
                                shape = CircleShape,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "2",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "اختيار كلوحة افتراضية",
                                    color = DarkTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (isImeSelected) "✓ هي اللوحة النشطة الآن" else "اضغط لاختيارها",
                                    color = if (isImeSelected) Color(0xFF10B981) else DarkTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                imm?.showInputMethodPicker()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isImeSelected) Color(0xFF1F2937) else Color(0xFF3B82F6)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = if (isImeSelected) "تغيير" else "اختيار", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Test Playground Highlight Button
        item {
            Button(
                onClick = { onNavigate(AppNavScreen.TEST_PLAYGROUND) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2A5F)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "تجربة واختبار الكيبورد مباشرة",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Feature Navigation Cards
        item {
            Text(
                text = "الأقسام والإعدادات",
                color = DarkTextPrimary,
                fontSize = 15.sp,
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
                    MenuRowItem(
                        icon = Icons.Default.Language,
                        title = "لغات الإدخال وتخطيط المفاتيح",
                        subtitle = "العربية، English، ولغات متعددة مع التبديل بالسحب",
                        onClick = { onNavigate(AppNavScreen.LANGUAGES) }
                    )

                    androidx.compose.material3.HorizontalDivider(color = DarkCardBorder)

                    MenuRowItem(
                        icon = Icons.Default.Palette,
                        title = "الثيمات والمظهر",
                        subtitle = "Dark Neon، AMOLED، Light، Sunset Glow والمزيد",
                        onClick = { onNavigate(AppNavScreen.THEMES) }
                    )

                    androidx.compose.material3.HorizontalDivider(color = DarkCardBorder)

                    MenuRowItem(
                        icon = Icons.Default.Assignment,
                        title = "الحافظة والنصوص المنسوخة",
                        subtitle = "حفظ تلقائي، تثبيت النصوص، واللصق بضغطة واحدة",
                        onClick = { onNavigate(AppNavScreen.CLIPBOARD) }
                    )

                    androidx.compose.material3.HorizontalDivider(color = DarkCardBorder)

                    MenuRowItem(
                        icon = Icons.Default.Tune,
                        title = "إعدادات الكتابة والتصحيح",
                        subtitle = "التصحيح التلقائي، شريط الاقتراحات، الاهتزاز والصوت",
                        onClick = { onNavigate(AppNavScreen.TYPING_SETTINGS) }
                    )

                    androidx.compose.material3.HorizontalDivider(color = DarkCardBorder)

                    MenuRowItem(
                        icon = Icons.Default.Security,
                        title = "الأمان والخصوصية",
                        subtitle = "حماية كلمات المرور، معالجة البيانات محلياً 100%",
                        onClick = { onNavigate(AppNavScreen.PRIVACY) }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun MenuRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Surface(
                color = Color(0xFF1E2638),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
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
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = DarkTextSecondary,
            modifier = Modifier.size(14.dp)
        )
    }
}
