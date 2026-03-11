package com.example.taskmanager.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.example.taskmanager.ui.theme.CyanAccent
import com.example.taskmanager.ui.theme.DarkBackground
import com.example.taskmanager.ui.theme.DarkCard
import com.example.taskmanager.ui.theme.PurpleAccent
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        // Animate scale and alpha simultaneously
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
        // Hold the splash screen for a short moment after animation
        delay(1200L)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        // Animated App Logo
        Box(
            modifier = Modifier
                .size(140.dp)
                .scale(scale.value)
                .alpha(alpha.value)
                .background(color = DarkCard, shape = CircleShape)
                .border(width = 3.dp, color = PurpleAccent, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.TaskAlt,
                contentDescription = "App Logo",
                tint = CyanAccent,
                modifier = Modifier.size(70.dp)
            )
        }
    }
}
