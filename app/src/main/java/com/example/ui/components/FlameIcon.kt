package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.OrangePrimaryDark

@Composable
fun FlameStreakBadge(
    streakCount: Int,
    modifier: Modifier = Modifier,
    isHero: Boolean = false,
    size: Dp = if (isHero) 36.dp else 18.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "flamePulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (streakCount > 0) 1.12f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flameScale"
    )

    val isActive = streakCount > 0
    val flameColor = if (isActive) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    val bgColor = if (isActive) {
        OrangePrimary.copy(alpha = 0.12f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    if (isHero) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFFF7ED),
                            Color(0xFFFFEDD5)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(size)
                    .scale(scale),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = "Streak Flame",
                    tint = OrangePrimary,
                    modifier = Modifier.size(size)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "$streakCount DAY STREAK",
                color = OrangePrimaryDark,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                letterSpacing = 1.sp
            )
        }
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .background(bgColor, RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(size)
                    .scale(if (isActive) scale else 1f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = "Streak Flame",
                    tint = flameColor,
                    modifier = Modifier.size(size)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$streakCount",
                color = if (isActive) OrangePrimaryDark else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}
