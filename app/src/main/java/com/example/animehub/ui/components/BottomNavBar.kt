package com.example.animehub.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animehub.ui.i18n.LocalizedStrings
import com.example.ui.theme.AnimePrimary
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary

enum class NavScreen {
    HOME,
    EXPLORE,
    SEARCH,
    LIBRARY,
    PROFILE
}

@Composable
fun BottomNavBar(
    currentScreen: NavScreen,
    onScreenSelected: (NavScreen) -> Unit,
    strings: LocalizedStrings,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DarkSurface.copy(alpha = 0.95f),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.5f)),
        shadowElevation = 16.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("bottom_navigation_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = if (currentScreen == NavScreen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                label = strings.home,
                isSelected = currentScreen == NavScreen.HOME,
                onClick = { onScreenSelected(NavScreen.HOME) },
                testTag = "nav_home"
            )
            NavItem(
                icon = if (currentScreen == NavScreen.EXPLORE) Icons.Filled.Explore else Icons.Outlined.Explore,
                label = strings.explore,
                isSelected = currentScreen == NavScreen.EXPLORE,
                onClick = { onScreenSelected(NavScreen.EXPLORE) },
                testTag = "nav_explore"
            )
            NavItem(
                icon = if (currentScreen == NavScreen.SEARCH) Icons.Filled.Search else Icons.Outlined.Search,
                label = strings.search,
                isSelected = currentScreen == NavScreen.SEARCH,
                onClick = { onScreenSelected(NavScreen.SEARCH) },
                testTag = "nav_search"
            )
            NavItem(
                icon = if (currentScreen == NavScreen.LIBRARY) Icons.Filled.VideoLibrary else Icons.Outlined.VideoLibrary,
                label = strings.library,
                isSelected = currentScreen == NavScreen.LIBRARY,
                onClick = { onScreenSelected(NavScreen.LIBRARY) },
                testTag = "nav_library"
            )
            NavItem(
                icon = if (currentScreen == NavScreen.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                label = strings.profile,
                isSelected = currentScreen == NavScreen.PROFILE,
                onClick = { onScreenSelected(NavScreen.PROFILE) },
                testTag = "nav_profile"
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) AnimePrimary else DarkTextSecondary,
        label = "nav_color"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = iconColor,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
