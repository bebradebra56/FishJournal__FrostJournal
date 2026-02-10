package com.fishjorunal.sofircl.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishjorunal.sofircl.data.model.ChecklistItem
import com.fishjorunal.sofircl.data.model.FishColor
import com.fishjorunal.sofircl.data.model.Note
import com.fishjorunal.sofircl.data.model.NoteType
import com.fishjorunal.sofircl.ui.components.BackgroundImage
import com.fishjorunal.sofircl.ui.components.FishIcon
import com.fishjorunal.sofircl.ui.components.TagChip
import com.fishjorunal.sofircl.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    note: Note,
    checklistItems: List<ChecklistItem> = emptyList(),
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onChecklistItemToggle: (ChecklistItem) -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage()
        
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FishIcon(
                                color = FishColor.valueOf(note.color),
                                size = 1.2f
                            )
                            Text(
                                text = note.title,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
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
                    ),
                    actions = {
                        IconButton(onClick = onEditClick) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = TextPrimary
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = ErrorColor
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp)
            ) {
                if (NoteType.valueOf(note.type) == NoteType.NOTE) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = CardBackground
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = note.content,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextPrimary,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(checklistItems) { item ->
                        ChecklistItemView(
                            item = item,
                            onToggle = { onChecklistItemToggle(item) }
                        )
                    }
                }
                
                if (note.tags.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tags",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            note.tags.split(",").forEach { tag ->
                                TagChip(tag = tag.trim())
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Delete Note",
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete this note?",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorColor
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(
                        text = "Cancel",
                        color = TextSecondary
                    )
                }
            },
            containerColor = CardBackground
        )
    }
}

@Composable
fun ChecklistItemView(
    item: ChecklistItem,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = AccentSecondary,
                    uncheckedColor = TextSecondary
                )
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = item.text,
                style = MaterialTheme.typography.bodyLarge,
                color = if (item.isCompleted) TextSecondary else TextPrimary
            )
        }
    }
}
