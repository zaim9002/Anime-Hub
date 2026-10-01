package com.example.animehub.ui.admin

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animehub.data.admin.AdminAuthManager
import com.example.ui.theme.AnimePrimary
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import kotlinx.coroutines.launch

@Composable
fun AdminLoginDialog(
    authManager: AdminAuthManager,
    onDismiss: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("admin@animehub.com") }
    var password by remember { mutableStateOf("Admin@2026") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(18.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = AnimePrimary.copy(alpha = 0.2f),
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = AnimePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "تسجيل دخول المشرفين",
                        color = DarkTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = DarkTextSecondary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "لوحة التحكم مخصصة لمدراء ومحرري التطبيق لإدارة الأنمي والحلقات والمواسم بصلاحيات كاملة.",
                    color = DarkTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                if (errorMessage != null) {
                    Surface(
                        color = Color(0xFF3B1219),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = Color(0xFFFF5252),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = null
                    },
                    label = { Text("البريد الإلكتروني للمدير", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = AnimePrimary, modifier = Modifier.size(18.dp))
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = DarkTextPrimary,
                        unfocusedTextColor = DarkTextPrimary,
                        focusedBorderColor = AnimePrimary,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedContainerColor = DarkCard,
                        unfocusedContainerColor = DarkCard
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    label = { Text("كلمة المرور", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = AnimePrimary, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle password",
                                tint = DarkTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = DarkTextPrimary,
                        unfocusedTextColor = DarkTextPrimary,
                        focusedBorderColor = AnimePrimary,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedContainerColor = DarkCard,
                        unfocusedContainerColor = DarkCard
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Surface(
                    color = Color(0xFF131D2D),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "💡 معلومات الدخول السريع للمشرف العام:",
                            color = Color(0xFF00E5FF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "البريد: admin@animehub.com\nكلمة المرور: Admin@2026",
                            color = DarkTextPrimary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        val result = authManager.login(email, password)
                        isLoading = false
                        if (result.isSuccess) {
                            onLoginSuccess()
                        } else {
                            errorMessage = result.exceptionOrNull()?.message ?: "فشل تسجيل الدخول"
                        }
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = AnimePrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(44.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text("دخول للوحة التحكم", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = DarkTextSecondary, fontSize = 13.sp)
            }
        }
    )
}
