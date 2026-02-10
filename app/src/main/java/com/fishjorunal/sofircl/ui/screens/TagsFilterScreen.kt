package com.fishjorunal.sofircl.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.fishjorunal.sofircl.data.model.FishColor
import com.fishjorunal.sofircl.data.model.Note
import com.fishjorunal.sofircl.data.model.NoteType
import com.fishjorunal.sofircl.ui.components.BackgroundImage
import com.fishjorunal.sofircl.ui.components.EmptyState
import com.fishjorunal.sofircl.ui.components.NoteCard
import com.fishjorunal.sofircl.ui.components.TagChip
import com.fishjorunal.sofircl.ui.theme.*

enum class FilterType {
    ALL, NOTES, LISTS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsFilterScreen(
    notes: List<Note>,
    selectedFilter: FilterType,
    onFilterChange: (FilterType) -> Unit,
    selectedTag: String?,
    onTagSelect: (String?) -> Unit,
    onNoteClick: (Long) -> Unit,
    onBackClick: () -> Unit
) {
    val allTags = remember(notes) {
        notes.flatMap { note ->
            if (note.tags.isNotEmpty()) {
                note.tags.split(",").map { it.trim() }
            } else emptyList()
        }.distinct().sorted()
    }
    
    val filteredNotes = remember(notes, selectedFilter, selectedTag) {
        var filtered = notes
        
        filtered = when (selectedFilter) {
            FilterType.NOTES -> filtered.filter { it.type == NoteType.NOTE.name }
            FilterType.LISTS -> filtered.filter { it.type == NoteType.LIST.name }
            FilterType.ALL -> filtered
        }
        
        if (selectedTag != null) {
            filtered = filtered.filter { note ->
                note.tags.split(",").map { it.trim() }.contains(selectedTag)
            }
        }
        
        filtered
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage()
        
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Filter & Tags",
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = CardBackground
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Type",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = selectedFilter == FilterType.ALL,
                                    onClick = { onFilterChange(FilterType.ALL) },
                                    label = { Text("All") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AccentPrimary.copy(alpha = 0.3f),
                                        selectedLabelColor = AccentPrimary,
                                        containerColor = BackgroundPrimary,
                                        labelColor = TextSecondary
                                    )
                                )
                                
                                FilterChip(
                                    selected = selectedFilter == FilterType.NOTES,
                                    onClick = { onFilterChange(FilterType.NOTES) },
                                    label = { Text("Notes") },
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
                                        containerColor = BackgroundPrimary,
                                        labelColor = TextSecondary
                                    )
                                )
                                
                                FilterChip(
                                    selected = selectedFilter == FilterType.LISTS,
                                    onClick = { onFilterChange(FilterType.LISTS) },
                                    label = { Text("Lists") },
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
                                        containerColor = BackgroundPrimary,
                                        labelColor = TextSecondary
                                    )
                                )
                            }
                        }
                    }
                }
                
                if (allTags.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = CardBackground
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Tags",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = TextSecondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    
                                    if (selectedTag != null) {
                                        TextButton(onClick = { onTagSelect(null) }) {
                                            Text(
                                                text = "Clear",
                                                color = AccentPrimary
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    allTags.forEach { tag ->
                                        FilterChip(
                                            selected = selectedTag == tag,
                                            onClick = {
                                                onTagSelect(if (selectedTag == tag) null else tag)
                                            },
                                            label = { Text(tag) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = AccentSecondary.copy(alpha = 0.3f),
                                                selectedLabelColor = AccentSecondary,
                                                containerColor = BackgroundPrimary,
                                                labelColor = TextSecondary
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                item {
                    Text(
                        text = "${filteredNotes.size} note${if (filteredNotes.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                if (filteredNotes.isEmpty()) {
                    item {
                        EmptyState(
                            icon = Icons.Default.FilterAltOff,
                            title = "No Notes Found",
                            description = "Try adjusting your filters"
                        )
                    }
                } else {
                    items(filteredNotes, key = { it.id }) { note ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            NoteCard(
                                title = note.title,
                                content = note.content,
                                color = FishColor.valueOf(note.color),
                                tags = if (note.tags.isNotEmpty())
                                    note.tags.split(",").map { it.trim() }
                                else emptyList(),
                                onClick = { onNoteClick(note.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
