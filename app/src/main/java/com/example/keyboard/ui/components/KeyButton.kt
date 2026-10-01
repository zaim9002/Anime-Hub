package com.example.keyboard.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keyboard.model.KeyboardTheme

@Composable
fun KeyButton(
    text: String,
    theme: KeyboardTheme,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    isSpecial: Boolean = false,
    isHighlighted: Boolean = false,
    heightDp: Int = 46,
    content: (@Composable () -> Unit)? = null
) {
    val view = LocalView.current
    val context = LocalContext.current

    fun performFeedback() {
        try {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        } catch (e: Exception) {
            // fallback vibrator
            try {
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (vibrator != null && vibrator.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(15)
                    }
                }
            } catch (ignored: Exception) {}
        }
    }

    val bgColor = when {
        isHighlighted -> Color(theme.accentColor)
        isSpecial -> Color(theme.specialKeyColor)
        else -> Color(theme.keyBackgroundColor)
    }

    val textColor = when {
        isHighlighted -> Color.White
        isSpecial -> Color(theme.specialKeyTextColor)
        else -> Color(theme.keyTextColor)
    }

    Box(
        modifier = modifier
            .height(heightDp.dp)
            .padding(horizontal = 2.dp, vertical = 3.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(
                0.5.dp,
                Color.White.copy(alpha = if (theme.isDark) 0.08f else 0.15f),
                RoundedCornerShape(8.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        performFeedback()
                        onClick()
                    },
                    onLongPress = {
                        performFeedback()
                        onLongClick?.invoke() ?: onClick()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (content != null) {
            content()
        } else {
            Text(
                text = text,
                color = textColor,
                fontSize = if (text.length > 2) 13.sp else 18.sp,
                fontWeight = if (isSpecial || isHighlighted) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
