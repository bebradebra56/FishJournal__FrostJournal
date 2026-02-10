package com.fishjorunal.sofircl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishjorunal.sofircl.data.model.ChecklistItem
import com.fishjorunal.sofircl.data.model.FishColor
import com.fishjorunal.sofircl.data.model.Note
import com.fishjorunal.sofircl.data.model.NoteType
import com.fishjorunal.sofircl.data.repository.NoteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NoteDetailViewModel(private val repository: NoteRepository) : ViewModel() {
    
    private val _noteId = MutableStateFlow<Long?>(null)
    
    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()
    
    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()
    
    private val _tags = MutableStateFlow("")
    val tags: StateFlow<String> = _tags.asStateFlow()
    
    private val _color = MutableStateFlow(FishColor.BLUE)
    val color: StateFlow<FishColor> = _color.asStateFlow()
    
    private val _noteType = MutableStateFlow(NoteType.NOTE)
    val noteType: StateFlow<NoteType> = _noteType.asStateFlow()
    
    private val _checklistItems = MutableStateFlow<List<ChecklistItem>>(emptyList())
    val checklistItems: StateFlow<List<ChecklistItem>> = _checklistItems.asStateFlow()
    
    private val _currentNote = MutableStateFlow<Note?>(null)
    val currentNote: StateFlow<Note?> = _currentNote.asStateFlow()
    
    fun loadNote(noteId: Long) {
        _noteId.value = noteId
        viewModelScope.launch {
            val note = repository.getNoteById(noteId)
            note?.let {
                _currentNote.value = it
                _title.value = it.title
                _content.value = it.content
                _tags.value = it.tags
                _color.value = FishColor.valueOf(it.color)
                _noteType.value = NoteType.valueOf(it.type)
                
                if (it.type == NoteType.LIST.name) {
                    repository.getChecklistItems(noteId).collectLatest { items ->
                        _checklistItems.value = items
                    }
                }
            }
        }
    }
    
    fun updateTitle(newTitle: String) {
        _title.value = newTitle
    }
    
    fun updateContent(newContent: String) {
        _content.value = newContent
    }
    
    fun updateTags(newTags: String) {
        _tags.value = newTags
    }
    
    fun updateColor(newColor: FishColor) {
        _color.value = newColor
    }
    
    fun updateNoteType(newType: NoteType) {
        _noteType.value = newType
    }
    
    fun addChecklistItem(text: String) {
        val noteId = _noteId.value ?: return
        viewModelScope.launch {
            val item = ChecklistItem(
                noteId = noteId,
                text = text,
                position = _checklistItems.value.size
            )
            repository.insertChecklistItem(item)
        }
    }
    
    fun updateChecklistItem(item: ChecklistItem) {
        viewModelScope.launch {
            repository.updateChecklistItem(item)
        }
    }
    
    fun deleteChecklistItem(item: ChecklistItem) {
        viewModelScope.launch {
            repository.deleteChecklistItem(item)
        }
    }
    
    fun saveNote(onSaved: () -> Unit) {
        viewModelScope.launch {
            val noteId = _noteId.value
            val note = Note(
                id = noteId ?: 0,
                title = _title.value.ifEmpty { "Untitled" },
                content = _content.value,
                tags = _tags.value,
                color = _color.value.name,
                type = _noteType.value.name,
                updatedAt = System.currentTimeMillis()
            )
            
            if (noteId == null) {
                val newId = repository.insertNote(note)
                _noteId.value = newId
                _currentNote.value = note.copy(id = newId)
            } else {
                repository.updateNote(note)
                _currentNote.value = note
            }
            
            onSaved()
        }
    }
    
    fun saveNoteWithChecklistItems(
        checklistItemsData: List<Pair<String, Boolean>>,
        onSaved: () -> Unit
    ) {
        viewModelScope.launch {
            // First save the note
            val noteId = _noteId.value
            val note = Note(
                id = noteId ?: 0,
                title = _title.value.ifEmpty { "Untitled" },
                content = _content.value,
                tags = _tags.value,
                color = _color.value.name,
                type = _noteType.value.name,
                updatedAt = System.currentTimeMillis()
            )
            
            val savedNoteId = if (noteId == null) {
                repository.insertNote(note)
            } else {
                repository.updateNote(note)
                noteId
            }
            
            _noteId.value = savedNoteId
            _currentNote.value = note.copy(id = savedNoteId)
            
            // Delete existing checklist items if editing
            if (noteId != null) {
                _checklistItems.value.forEach { item ->
                    repository.deleteChecklistItem(item)
                }
            }
            
            // Insert new checklist items
            checklistItemsData.forEachIndexed { index, (text, isCompleted) ->
                if (text.isNotBlank()) {
                    val item = ChecklistItem(
                        noteId = savedNoteId,
                        text = text,
                        isCompleted = isCompleted,
                        position = index
                    )
                    repository.insertChecklistItem(item)
                }
            }
            
            onSaved()
        }
    }
    
    fun deleteNote(onDeleted: () -> Unit) {
        viewModelScope.launch {
            _currentNote.value?.let {
                repository.deleteNote(it)
                onDeleted()
            }
        }
    }
    
    fun resetNote() {
        _noteId.value = null
        _title.value = ""
        _content.value = ""
        _tags.value = ""
        _color.value = FishColor.BLUE
        _noteType.value = NoteType.NOTE
        _checklistItems.value = emptyList()
        _currentNote.value = null
    }
}
