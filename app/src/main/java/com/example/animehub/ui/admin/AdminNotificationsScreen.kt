package com.example.animehub.ui.admin

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animehub.data.admin.AppNotification
import com.example.animehub.data.firestore.FirestoreAnimeRepository
import com.example.ui.theme.AnimePrimary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminNotificationsScreen(
    repository: FirestoreAnimeRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val notifications by repository.getNotificationsFlow().collectAsState(initial = emptyList())

    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var sendSuccessMessage by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(14.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = "مركز الإشعارات والتنبيهات",
                    color = DarkTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "إرسال تنبيهات فورية للمستخدمين بالحلقات والتحديثات",
                    color = Color(0xFF00E5FF),
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Send Notification Box
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Campaign, contentDescription = null, tint = AnimePrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("إرسال إشعار عام للمستخدمين", color = DarkTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                if (sendSuccessMessage) {
                    Surface(color = Color(0xFF10281E), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("✓ تم إرسال الإشعار بنجاح لجميع المستخدمين!", color = Color(0xFF10B981), fontSize = 12.sp)
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        sendSuccessMessage = false
                    },
                    placeholder = { Text("عنوان الإشعار (مثال: حلقة حصرية جديدة! 🔥)", color = DarkTextSecondary, fontSize = 12.sp) },
                    colors = notifFieldColors(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = {
                        message = it
                        sendSuccessMessage = false
                    },
                    placeholder = { Text("نص الإشعار والتفاصيل...", color = DarkTextSecondary, fontSize = 12.sp) },
                    minLines = 2,
                    colors = notifFieldColors(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Quick Templates
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = {
                            title = "حلقة جديدة متاحة الآن! 🎬"
                            message = "تمت إضافة حلقة جديدة بجودة فائقة وسيرفرات متعددة. شاهدها الآن!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2638)),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("قالب: حلقة جديدة", color = Color(0xFF00E5FF), fontSize = 10.sp)
                    }

                    Button(
                        onClick = {
                            title = "تحديث هام في التطبيق ⚡"
                            message = "تمت إضافة سيرفرات مشاهدة جديدة وسريعة لتجربة مشاهدة ممتعة وبدون تقطيع."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2638)),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("قالب: تحديث سيرفرات", color = Color(0xFF00E5FF), fontSize = 10.sp)
                    }
                }

                Button(
                    onClick = {
                        if (title.isNotBlank() && message.isNotBlank()) {
                            scope.launch {
                                isSending = true
                                repository.sendAppNotification(
                                    AppNotification(
                                        id = System.currentTimeMillis().toString(),
                                        title = title.trim(),
                                        message = message.trim(),
                                        target = "all",
                                        timestamp = System.currentTimeMillis()
                                    )
                                )
                                isSending = false
                                sendSuccessMessage = true
                                title = ""
                                message = ""
                            }
                        }
                    },
                    enabled = !isSending && title.isNotBlank() && message.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = AnimePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("إرسال الإشعار الآن", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text("سجل الإشعارات المرسلة (${notifications.size})", color = DarkTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        // Notifications History
        if (notifications.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("لا توجد إشعارات سابقة مسجلة", color = DarkTextSecondary, fontSize = 12.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notifications, key = { it.id }) { notif ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                color = AnimePrimary.copy(alpha = 0.2f),
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = AnimePrimary, modifier = Modifier.size(18.dp))
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(notif.title, color = DarkTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(notif.message, color = DarkTextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = dateFormat.format(Date(notif.timestamp)),
                                    color = Color(0xFF00E5FF),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun notifFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = DarkTextPrimary,
    unfocusedTextColor = DarkTextPrimary,
    focusedBorderColor = AnimePrimary,
    unfocusedBorderColor = DarkCardBorder,
    focusedContainerColor = Color(0xFF141926),
    unfocusedContainerColor = Color(0xFF141926)
)
