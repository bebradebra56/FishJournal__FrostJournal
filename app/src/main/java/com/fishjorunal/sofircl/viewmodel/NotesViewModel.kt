package com.fishjorunal.sofircl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishjorunal.sofircl.data.model.Note
import com.fishjorunal.sofircl.data.repository.NoteRepository
import com.fishjorunal.sofircl.ui.screens.SortOrder
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NotesViewModel(private val repository: NoteRepository) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()
    
    private val _sortOrder = MutableStateFlow(SortOrder.DATE_DESC)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()
    
    val notes: StateFlow<List<Note>> = combine(
        repository.getAllNotes(),
        _searchQuery,
        _selectedFilter,
        _sortOrder
    ) { notes, query, filter, sort ->
        var filteredNotes = notes
        
        // Apply type filter
        filteredNotes = when (filter) {
            "Notes" -> notes.filter { it.type == "NOTE" }
            "Lists" -> notes.filter { it.type == "LIST" }
            else -> notes
        }
        
        // Apply search query
        if (query.isNotEmpty()) {
            filteredNotes = filteredNotes.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.content.contains(query, ignoreCase = true)
            }
        }
        
        // Apply sorting
        filteredNotes = when (sort) {
            SortOrder.DATE_DESC -> filteredNotes.sortedByDescending { it.updatedAt }
            SortOrder.DATE_ASC -> filteredNotes.sortedBy { it.updatedAt }
            SortOrder.TITLE_ASC -> filteredNotes.sortedBy { it.title.lowercase() }
            SortOrder.TITLE_DESC -> filteredNotes.sortedByDescending { it.title.lowercase() }
        }
        
        filteredNotes
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val allTags: StateFlow<List<String>> = repository.getAllTags()
        .map { tagsList ->
            tagsList.flatMap { it.split(",") }
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .distinct()
                .sorted()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun updateFilter(filter: String) {
        _selectedFilter.value = filter
    }
    
    fun updateSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }
    
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }
    
    suspend fun exportNotesToCsv(): String {
        val allNotes = repository.getAllNotes().first()
        val sb = StringBuilder()
        
        // CSV Header
        sb.appendLine("ID,Title,Type,Color,Tags,Created Date,Updated Date,Content")
        
        // CSV Data
        allNotes.forEach { note ->
            val title = escapeCsvField(note.title)
            val type = note.type
            val color = note.color
            val tags = escapeCsvField(note.tags)
            val createdDate = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(note.createdAt))
            val updatedDate = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(note.updatedAt))
            
            val content = if (note.type == "NOTE") {
                escapeCsvField(note.content)
            } else {
                val items = repository.getChecklistItems(note.id).first()
                val checklistText = items.joinToString("; ") { item ->
                    "${if (item.isCompleted) "[✓]" else "[ ]"} ${item.text}"
                }
                escapeCsvField(checklistText)
            }
            
            sb.appendLine("${note.id},$title,$type,$color,$tags,$createdDate,$updatedDate,$content")
        }
        
        return sb.toString()
    }
    
    private fun escapeCsvField(field: String): String {
        // Escape double quotes and wrap in quotes if contains comma, newline, or quote
        val escaped = field.replace("\"", "\"\"")
        return if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\"")) {
            "\"$escaped\""
        } else {
            escaped
        }
    }
}
