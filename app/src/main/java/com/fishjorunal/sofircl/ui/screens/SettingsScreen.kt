package com.fishjorunal.sofircl.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishjorunal.sofircl.ui.components.BackgroundImage
import com.fishjorunal.sofircl.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

enum class SortOrder {
    DATE_DESC,
    DATE_ASC,
    TITLE_ASC,
    TITLE_DESC
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    currentSortOrder: SortOrder = SortOrder.DATE_DESC,
    onSortOrderChange: (SortOrder) -> Unit = {},
    onExportNotes: () -> Unit = {}
) {
    var showAboutDialog by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage()
        
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Settings",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BackgroundPrimary.copy(alpha = 0.9f)
                    )
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "General",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                item {
                    SettingsItem(
                        icon = Icons.Default.Sort,
                        title = "Sort Notes",
                        subtitle = getSortOrderText(currentSortOrder),
                        onClick = { showSortDialog = true }
                    )
                }
                
                item {
                    SettingsItem(
                        icon = Icons.Default.Upload,
                        title = "Export Notes",
                        subtitle = "Export to CSV file",
                        onClick = { showExportDialog = true }
                    )
                }
                
                item {
                    HorizontalDivider(
                        color = TextSecondary.copy(alpha = 0.2f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                item {
                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                item {
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "About App",
                        subtitle = "Version 1.0",
                        onClick = { showAboutDialog = true }
                    )
                }
                
                item {
                    SettingsItem(
                        icon = Icons.Default.PrivacyTip,
                        title = "Privacy Policy",
                        subtitle = "Tap to read",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://fiishjournal.com/privacy-policy.html"))
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
    
    // Sort Dialog
    if (showSortDialog) {
        AlertDialog(
            onDismissRequest = { showSortDialog = false },
            title = {
                Text(
                    text = "Sort Notes",
                    color = TextPrimary
                )
            },
            text = {
                Column {
                    SortOption(
                        text = "Date (Newest First)",
                        isSelected = currentSortOrder == SortOrder.DATE_DESC,
                        onClick = {
                            onSortOrderChange(SortOrder.DATE_DESC)
                            showSortDialog = false
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    SortOption(
                        text = "Date (Oldest First)",
                        isSelected = currentSortOrder == SortOrder.DATE_ASC,
                        onClick = {
                            onSortOrderChange(SortOrder.DATE_ASC)
                            showSortDialog = false
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    SortOption(
                        text = "Title (A-Z)",
                        isSelected = currentSortOrder == SortOrder.TITLE_ASC,
                        onClick = {
                            onSortOrderChange(SortOrder.TITLE_ASC)
                            showSortDialog = false
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    SortOption(
                        text = "Title (Z-A)",
                        isSelected = currentSortOrder == SortOrder.TITLE_DESC,
                        onClick = {
                            onSortOrderChange(SortOrder.TITLE_DESC)
                            showSortDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showSortDialog = false }) {
                    Text(
                        text = "Close",
                        color = AccentPrimary
                    )
                }
            },
            containerColor = CardBackground
        )
    }
    
    // Export Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Text(
                    text = "Export Notes",
                    color = TextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Export all your notes to a CSV file and share it.",
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You can open CSV files in Excel, Google Sheets, or any text editor.",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onExportNotes()
                        showExportDialog = false
                        Toast.makeText(context, "Opening share dialog...", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export & Share")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text(
                        text = "Cancel",
                        color = TextSecondary
                    )
                }
            },
            containerColor = CardBackground
        )
    }
    
    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phishing,
                        contentDescription = null,
                        tint = AccentSecondary,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "Frost Journal",
                        color = TextPrimary
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Version 1.0",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "A beautiful and intuitive note-taking app with fish-themed organization.",
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text(
                        text = "Close",
                        color = AccentPrimary
                    )
                }
            },
            containerColor = CardBackground
        )
    }

}

@Composable
fun SortOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AccentPrimary.copy(alpha = 0.2f) else BackgroundPrimary
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                color = if (isSelected) AccentPrimary else TextPrimary,
                style = MaterialTheme.typography.bodyLarge
            )
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = AccentPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AccentPrimary,
                modifier = Modifier.size(24.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

fun getSortOrderText(sortOrder: SortOrder): String {
    return when (sortOrder) {
        SortOrder.DATE_DESC -> "Date (Newest First)"
        SortOrder.DATE_ASC -> "Date (Oldest First)"
        SortOrder.TITLE_ASC -> "Title (A-Z)"
        SortOrder.TITLE_DESC -> "Title (Z-A)"
    }
}
