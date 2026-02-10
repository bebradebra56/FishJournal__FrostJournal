package com.fishjorunal.sofircl.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fishjorunal.sofircl.R
import com.fishjorunal.sofircl.data.model.FishColor
import com.fishjorunal.sofircl.ui.theme.*

@Composable
fun BackgroundImage(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fish_journal_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.3f
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            BackgroundPrimary.copy(alpha = 0.8f),
                            BackgroundPrimary.copy(alpha = 0.95f)
                        )
                    )
                )
        )
    }
}

@Composable
fun FishIcon(
    color: FishColor,
    modifier: Modifier = Modifier,
    size: Float = 1f
) {
    val fishColor = when (color) {
        FishColor.BLUE -> FishBlue
        FishColor.GREEN -> FishGreen
        FishColor.TEAL -> FishTeal
        FishColor.LIGHT_BLUE -> FishLightBlue
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "fish")
    val rotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )
    
    Icon(
        imageVector = Icons.Default.Phishing,
        contentDescription = "Fish",
        tint = fishColor,
        modifier = modifier
            .size((24 * size).dp)
            .rotate(rotation)
    )
}

@Composable
fun NoteCard(
    title: String,
    content: String,
    color: FishColor,
    tags: List<String> = emptyList(),
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            FishIcon(
                color = color,
                modifier = Modifier.padding(end = 12.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                
                if (content.isNotEmpty()) {
                    Text(
                        text = content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        maxLines = 2,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                if (tags.isNotEmpty()) {
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tags.take(3).forEach { tag ->
                            TagChip(tag = tag)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TagChip(tag: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = AccentPrimary.copy(alpha = 0.2f)
    ) {
        Text(
            text = tag,
            style = MaterialTheme.typography.labelSmall,
            color = AccentPrimary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun ColorSelector(
    selectedColor: FishColor,
    onColorSelected: (FishColor) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FishColor.values().forEach { color ->
            ColorDot(
                color = color,
                isSelected = color == selectedColor,
                onClick = { onColorSelected(color) }
            )
        }
    }
}

@Composable
fun ColorDot(
    color: FishColor,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val dotColor = when (color) {
        FishColor.BLUE -> FishBlue
        FishColor.GREEN -> FishGreen
        FishColor.TEAL -> FishTeal
        FishColor.LIGHT_BLUE -> FishLightBlue
    }
    
    Box(
        modifier = Modifier
            .size(if (isSelected) 36.dp else 32.dp)
            .clip(CircleShape)
            .background(if (isSelected) dotColor.copy(alpha = 0.3f) else Color.Transparent)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = dotColor,
            modifier = Modifier.size(if (isSelected) 24.dp else 20.dp)
        ) {}
    }
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AccentSecondary.copy(alpha = 0.5f),
            modifier = Modifier.size(80.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = AccentPrimary
        )
    }
}
