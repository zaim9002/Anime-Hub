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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
fun PrivacyScreen(
    preferences: KeyboardPreferences,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var isResetDone by remember { mutableStateOf(false) }

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
                        text = "الخصوصية والأمان المتقدم",
                        color = DarkTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "كيبورد آمن 100% بدون تتبع أو إرسال أي نصوص لخوادم خارجية",
                        color = Color(0xFF00E5FF),
                        fontSize = 11.sp
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF10281E)),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "معالجة محلية داخل جهازك 100%",
                            color = Color(0xFF10B981),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "جميع ما تكتبه من أحرف واقتراحات يتم تحليله ومعالجته محلياً على جهازك دون تخزين أو مشاركة أي بيانات.",
                            color = DarkTextPrimary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "معايير الأمان المطبقة",
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
                    PrivacyFeatureRow(
                        icon = Icons.Default.Password,
                        title = "حظر تسجيل كلمات المرور",
                        description = "عند الكتابة في أي حقل كلمة سر، يتم تعطيل الاقتراحات والتصحيح والحفظ تلقائياً لحماية أمان حساباتك."
                    )

                    HorizontalDivider(color = DarkCardBorder)

                    PrivacyFeatureRow(
                        icon = Icons.Default.NoAccounts,
                        title = "بدون حسابات أو تسجيل دخول",
                        description = "لا يطلب الكيبورد أي تسجيل أو بريد إلكتروني، ويعمل مباشرة بكل ميزاته."
                    )

                    HorizontalDivider(color = DarkCardBorder)

                    PrivacyFeatureRow(
                        icon = Icons.Default.Lock,
                        title = "حماية الحافظة والنصوص المنسوخة",
                        description = "يمكنك مسح وتفريغ سجل الحافظة في أي لحظة أو تثبيت العناصر الهامة فقط."
                    )
                }
            }
        }

        item {
            Button(
                onClick = {
                    preferences.clearClipboardHistory()
                    isResetDone = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2638)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Storage,
                    contentDescription = null,
                    tint = Color(0xFFFF2A5F),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isResetDone) "✓ تم مسح الذاكرة المؤقتة بنجاح" else "مسح الذاكرة المؤقتة وسجل الحافظة",
                    color = if (isResetDone) Color(0xFF10B981) else Color(0xFFFF2A5F),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PrivacyFeatureRow(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF00E5FF),
            modifier = Modifier
                .size(24.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                color = DarkTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = DarkTextSecondary,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }
    }
}
