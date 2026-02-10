package com.fishjorunal.sofircl.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishjorunal.sofircl.data.model.FishColor
import com.fishjorunal.sofircl.data.model.NoteType
import com.fishjorunal.sofircl.ui.components.BackgroundImage
import com.fishjorunal.sofircl.ui.components.ColorSelector
import com.fishjorunal.sofircl.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    title: String,
    content: String,
    tags: String,
    color: FishColor,
    type: NoteType,
    checklistItems: List<Pair<String, Boolean>>,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onTagsChange: (String) -> Unit,
    onColorChange: (FishColor) -> Unit,
    onTypeChange: (NoteType) -> Unit,
    onChecklistItemChange: (Int, String) -> Unit,
    onChecklistItemToggle: (Int, Boolean) -> Unit,
    onAddChecklistItem: () -> Unit,
    onRemoveChecklistItem: (Int) -> Unit,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    isEditMode: Boolean
) {
    val focusManager = LocalFocusManager.current
    
    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage()
        
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (isEditMode) "Edit Note" else "New Note",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel",
                                tint = TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BackgroundPrimary.copy(alpha = 0.9f)
                    ),
                    actions = {
                        IconButton(
                            onClick = onSaveClick,
                            enabled = title.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Save",
                                tint = if (title.isNotBlank()) AccentSecondary else TextSecondary
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
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = onTitleChange,
                        label = { Text("Title", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = CardBackground,
                            unfocusedContainerColor = CardBackground,
                            focusedBorderColor = AccentPrimary,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )
                }
                
                item {
                    Column {
                        Text(
                            text = "Type",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FilterChip(
                                selected = type == NoteType.NOTE,
                                onClick = { onTypeChange(NoteType.NOTE) },
                                label = { Text("Note") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Note,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AccentPrimary.copy(alpha = 0.3f),
                                    selectedLabelColor = AccentPrimary,
                                    containerColor = CardBackground,
                                    labelColor = TextSecondary
                                )
                            )
                            
                            FilterChip(
                                selected = type == NoteType.LIST,
                                onClick = { onTypeChange(NoteType.LIST) },
                                label = { Text("List") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Checklist,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AccentPrimary.copy(alpha = 0.3f),
                                    selectedLabelColor = AccentPrimary,
                                    containerColor = CardBackground,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }
                }
                
                if (type == NoteType.NOTE) {
                    item {
                        OutlinedTextField(
                            value = content,
                            onValueChange = onContentChange,
                            label = { Text("Content", color = TextSecondary) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 200.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = CardBackground,
                                unfocusedContainerColor = CardBackground,
                                focusedBorderColor = AccentPrimary,
                                unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 10
                        )
                    }
                } else {
                    itemsIndexed(checklistItems) { index, item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Checkbox(
                                checked = item.second,
                                onCheckedChange = { onChecklistItemToggle(index, it) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = AccentSecondary,
                                    uncheckedColor = TextSecondary
                                )
                            )
                            
                            OutlinedTextField(
                                value = item.first,
                                onValueChange = { onChecklistItemChange(index, it) },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Item ${index + 1}", color = TextSecondary) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = CardBackground,
                                    unfocusedContainerColor = CardBackground,
                                    focusedBorderColor = AccentPrimary,
                                    unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            
                            IconButton(onClick = { onRemoveChecklistItem(index) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove",
                                    tint = ErrorColor
                                )
                            }
                        }
                    }
                    
                    item {
                        OutlinedButton(
                            onClick = onAddChecklistItem,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = AccentPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Item")
                        }
                    }
                }
                
                item {
                    Column {
                        Text(
                            text = "Color",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ColorSelector(
                            selectedColor = color,
                            onColorSelected = onColorChange
                        )
                    }
                }
                
                item {
                    OutlinedTextField(
                        value = tags,
                        onValueChange = onTagsChange,
                        label = { Text("Tags (comma separated)", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = CardBackground,
                            unfocusedContainerColor = CardBackground,
                            focusedBorderColor = AccentPrimary,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("work, personal, ideas", color = TextSecondary.copy(alpha = 0.6f)) }
                    )
                }
            }
        }
    }
}
