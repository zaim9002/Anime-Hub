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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animehub.data.admin.AdminAuthManager
import com.example.animehub.data.admin.AdminRole
import com.example.animehub.data.admin.AdminUser
import com.example.ui.theme.AnimePrimary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminModeratorsScreen(
    authManager: AdminAuthManager,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val admins by authManager.getAllAdminsFlow().collectAsState(initial = emptyList())
    val isSuperAdmin = authManager.isSuperAdmin

    var showAddDialog by remember { mutableStateOf(false) }
    var adminToDelete by remember { mutableStateOf<AdminUser?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(14.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "إدارة المشرفين والصلاحيات",
                        color = DarkTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "نظام الصلاحيات متعدد المستويات (RBAC)",
                        color = Color(0xFF00E5FF),
                        fontSize = 11.sp
                    )
                }
            }

            if (isSuperAdmin) {
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimePrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة مشرف", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Role Matrix Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("مصفوفة صلاحيات الأدوار في النظام:", color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("• Super Admin: إدارة المشرفين، الحذف النهائي للمحتوى، إرسال الإشعارات، وتعديل كافة الإعدادات.", color = DarkTextSecondary, fontSize = 11.sp)
                Text("• Admin: إضافة وتعديل وحذف الأنمي والحلقات والمواسم ونشرها.", color = DarkTextSecondary, fontSize = 11.sp)
                Text("• Editor: إضافة وتعديل المحتوى فقط دون صلاحيات الحذف أو إدارة المشرفين.", color = DarkTextSecondary, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // List of Admins
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Seed item fallback if empty
            val displayList = if (admins.isEmpty()) {
                listOf(
                    AdminUser(
                        uid = "admin@animehub.com",
                        email = "admin@animehub.com",
                        displayName = "المدير العام (Super Admin)",
                        role = AdminRole.SUPER_ADMIN,
                        isActive = true,
                        createdAt = System.currentTimeMillis()
                    )
                )
            } else {
                admins
            }

            items(displayList, key = { it.email }) { admin ->
                ModeratorCard(
                    admin = admin,
                    isCurrentSuperAdmin = isSuperAdmin,
                    onToggleActive = { active ->
                        scope.launch { authManager.toggleAdminStatus(admin.email, active) }
                    },
                    onDelete = { adminToDelete = admin }
                )
            }
        }
    }

    // Add Moderator Dialog
    if (showAddDialog) {
        AddModeratorDialog(
            onDismiss = { showAddDialog = false },
            onSave = { newAdmin, password ->
                scope.launch {
                    val res = authManager.createOrUpdateAdmin(newAdmin, password)
                    if (res.isSuccess) {
                        showAddDialog = false
                    } else {
                        errorMessage = res.exceptionOrNull()?.message
                    }
                }
            }
        )
    }

    // Delete Confirmation
    if (adminToDelete != null) {
        AlertDialog(
            onDismissRequest = { adminToDelete = null },
            containerColor = DarkSurface,
            title = { Text("حذف المشرف", color = DarkTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "هل أنت متأكد من حذف حساب المشرف '${adminToDelete?.displayName}' (${adminToDelete?.email})؟",
                    color = DarkTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val email = adminToDelete?.email ?: ""
                        adminToDelete = null
                        scope.launch { authManager.deleteAdmin(email) }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("تأكيد الحذف", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { adminToDelete = null }) { Text("إلغاء", color = DarkTextSecondary) }
            }
        )
    }
}

@Composable
private fun ModeratorCard(
    admin: AdminUser,
    isCurrentSuperAdmin: Boolean,
    onToggleActive: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()) }

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Surface(
                    color = when (admin.role) {
                        AdminRole.SUPER_ADMIN -> Color(0xFFFFD700).copy(alpha = 0.2f)
                        AdminRole.ADMIN -> AnimePrimary.copy(alpha = 0.2f)
                        AdminRole.EDITOR -> Color(0xFF00E5FF).copy(alpha = 0.2f)
                    },
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = when (admin.role) {
                                AdminRole.SUPER_ADMIN -> Color(0xFFFFD700)
                                AdminRole.ADMIN -> AnimePrimary
                                AdminRole.EDITOR -> Color(0xFF00E5FF)
                            },
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = admin.displayName,
                            color = DarkTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = when (admin.role) {
                                AdminRole.SUPER_ADMIN -> Color(0xFFFFD700)
                                AdminRole.ADMIN -> AnimePrimary
                                AdminRole.EDITOR -> Color(0xFF00E5FF)
                            },
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = admin.role.displayNameArabic,
                                color = if (admin.role == AdminRole.SUPER_ADMIN) Color.Black else Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = admin.email,
                        color = DarkTextSecondary,
                        fontSize = 11.sp
                    )

                    Text(
                        text = "تاريخ الإضافة: ${dateFormat.format(Date(admin.createdAt))}",
                        color = Color(0xFF00E5FF),
                        fontSize = 10.sp
                    )
                }
            }

            if (isCurrentSuperAdmin && admin.role != AdminRole.SUPER_ADMIN) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = admin.isActive,
                        onCheckedChange = onToggleActive,
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AnimePrimary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddModeratorDialog(
    onDismiss: () -> Unit,
    onSave: (AdminUser, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(AdminRole.ADMIN) }
    var roleDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = { Text("إضافة مشرف جديد", color = DarkTextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم المشرف", fontSize = 12.sp) },
                    colors = modFieldColors(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("البريد الإلكتروني", fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = modFieldColors(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("كلمة المرور الأولية", fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = modFieldColors(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = roleDropdownExpanded,
                    onExpandedChange = { roleDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedRole.displayNameArabic,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("الدور والصلاحية", fontSize = 12.sp) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleDropdownExpanded) },
                        colors = modFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = roleDropdownExpanded,
                        onDismissRequest = { roleDropdownExpanded = false },
                        modifier = Modifier.background(DarkSurface)
                    ) {
                        listOf(AdminRole.ADMIN, AdminRole.EDITOR).forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role.displayNameArabic, color = DarkTextPrimary) },
                                onClick = {
                                    selectedRole = role
                                    roleDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        val newAdmin = AdminUser(
                            uid = email.trim().lowercase(),
                            email = email.trim().lowercase(),
                            displayName = name.ifBlank { "مشرف جديد" },
                            role = selectedRole,
                            isActive = true,
                            createdAt = System.currentTimeMillis(),
                            lastLoginAt = 0L
                        )
                        onSave(newAdmin, password.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AnimePrimary)
            ) {
                Text("إضافة المشرف", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = DarkTextSecondary) }
        }
    )
}

@Composable
private fun modFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = DarkTextPrimary,
    unfocusedTextColor = DarkTextPrimary,
    focusedBorderColor = AnimePrimary,
    unfocusedBorderColor = DarkCardBorder,
    focusedContainerColor = DarkCard,
    unfocusedContainerColor = DarkCard
)
