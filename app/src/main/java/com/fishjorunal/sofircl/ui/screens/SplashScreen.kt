package com.fishjorunal.sofircl.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phishing
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishjorunal.sofircl.ui.components.BackgroundImage
import com.fishjorunal.sofircl.ui.theme.AccentSecondary
import com.fishjorunal.sofircl.ui.theme.TextPrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToMain: () -> Unit,
    hasSeenOnboarding: Boolean
) {
    val scale = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        delay(1500)
        if (hasSeenOnboarding) {
            onNavigateToMain()
        } else {
            onNavigateToOnboarding()
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage()
        
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Phishing,
                contentDescription = "Fish Logo",
                tint = AccentSecondary,
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale.value)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Fish Journal",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.scale(scale.value)
            )
        }
    }
}
