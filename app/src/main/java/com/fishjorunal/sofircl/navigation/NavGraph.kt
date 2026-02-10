package com.fishjorunal.sofircl.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fishjorunal.sofircl.data.model.FishColor
import com.fishjorunal.sofircl.data.model.NoteType
import com.fishjorunal.sofircl.ui.screens.*
import com.fishjorunal.sofircl.viewmodel.NoteDetailViewModel
import com.fishjorunal.sofircl.viewmodel.NotesViewModel
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object NotesList : Screen("notes_list")
    object NoteDetail : Screen("note_detail/{noteId}") {
        fun createRoute(noteId: Long) = "note_detail/$noteId"
    }
    object NoteEdit : Screen("note_edit?noteId={noteId}") {
        fun createRoute(noteId: Long? = null) = 
            if (noteId != null) "note_edit?noteId=$noteId" else "note_edit"
    }
    object Search : Screen("search")
    object TagsFilter : Screen("tags_filter")
    object Settings : Screen("settings")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    notesViewModel: NotesViewModel,
    noteDetailViewModel: NoteDetailViewModel,
    hasSeenOnboarding: Boolean = false,
    onOnboardingComplete: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate(Screen.NotesList.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                hasSeenOnboarding = hasSeenOnboarding
            )
        }
        
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    onOnboardingComplete()
                    navController.navigate(Screen.NotesList.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.NotesList.route) {
            val notes by notesViewModel.notes.collectAsState()
            
            NotesListScreen(
                notes = notes,
                onNoteClick = { noteId ->
                    navController.navigate(Screen.NoteDetail.createRoute(noteId))
                },
                onAddNote = {
                    noteDetailViewModel.resetNote()
                    navController.navigate(Screen.NoteEdit.createRoute())
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                },
                onFilterClick = {
                    navController.navigate(Screen.TagsFilter.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        composable(
            route = Screen.NoteDetail.route,
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: return@composable
            
            LaunchedEffect(noteId) {
                noteDetailViewModel.loadNote(noteId)
            }
            
            val note by noteDetailViewModel.currentNote.collectAsState()
            val checklistItems by noteDetailViewModel.checklistItems.collectAsState()
            
            note?.let { currentNote ->
                NoteDetailScreen(
                    note = currentNote,
                    checklistItems = checklistItems,
                    onBackClick = {
                        navController.navigateUp()
                    },
                    onEditClick = {
                        navController.navigate(Screen.NoteEdit.createRoute(noteId))
                    },
                    onDeleteClick = {
                        noteDetailViewModel.deleteNote {
                            navController.navigateUp()
                        }
                    },
                    onChecklistItemToggle = { item ->
                        noteDetailViewModel.updateChecklistItem(
                            item.copy(isCompleted = !item.isCompleted)
                        )
                    }
                )
            }
        }
        
        composable(
            route = Screen.NoteEdit.route,
            arguments = listOf(navArgument("noteId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val noteIdString = backStackEntry.arguments?.getString("noteId")
            val noteId = noteIdString?.toLongOrNull()
            val isEditMode = noteId != null
            
            LaunchedEffect(noteId) {
                if (noteId != null) {
                    noteDetailViewModel.loadNote(noteId)
                }
            }
            
            val title by noteDetailViewModel.title.collectAsState()
            val content by noteDetailViewModel.content.collectAsState()
            val tags by noteDetailViewModel.tags.collectAsState()
            val color by noteDetailViewModel.color.collectAsState()
            val noteType by noteDetailViewModel.noteType.collectAsState()
            val checklistItems by noteDetailViewModel.checklistItems.collectAsState()
            
            var checklistItemsState by remember {
                mutableStateOf(
                    checklistItems.map { it.text to it.isCompleted }
                )
            }
            
            LaunchedEffect(checklistItems) {
                if (isEditMode && noteType == NoteType.LIST) {
                    checklistItemsState = checklistItems.map { it.text to it.isCompleted }
                }
            }
            
            NoteEditScreen(
                title = title,
                content = content,
                tags = tags,
                color = color,
                type = noteType,
                checklistItems = checklistItemsState,
                onTitleChange = noteDetailViewModel::updateTitle,
                onContentChange = noteDetailViewModel::updateContent,
                onTagsChange = noteDetailViewModel::updateTags,
                onColorChange = noteDetailViewModel::updateColor,
                onTypeChange = { newType ->
                    noteDetailViewModel.updateNoteType(newType)
                    if (newType == NoteType.LIST && checklistItemsState.isEmpty()) {
                        checklistItemsState = listOf("" to false)
                    }
                },
                onChecklistItemChange = { index, text ->
                    checklistItemsState = checklistItemsState.toMutableList().apply {
                        if (index < size) {
                            this[index] = text to this[index].second
                        }
                    }
                },
                onChecklistItemToggle = { index, checked ->
                    checklistItemsState = checklistItemsState.toMutableList().apply {
                        if (index < size) {
                            this[index] = this[index].first to checked
                        }
                    }
                },
                onAddChecklistItem = {
                    checklistItemsState = checklistItemsState + ("" to false)
                },
                onRemoveChecklistItem = { index ->
                    checklistItemsState = checklistItemsState.toMutableList().apply {
                        removeAt(index)
                    }
                },
                onBackClick = {
                    noteDetailViewModel.resetNote()
                    navController.navigateUp()
                },
                onSaveClick = {
                    if (noteType == NoteType.NOTE) {
                        noteDetailViewModel.saveNote {
                            noteDetailViewModel.resetNote()
                            navController.navigateUp()
                        }
                    } else {
                        noteDetailViewModel.saveNoteWithChecklistItems(checklistItemsState) {
                            noteDetailViewModel.resetNote()
                            navController.navigateUp()
                        }
                    }
                },
                isEditMode = isEditMode
            )
        }
        
        composable(Screen.Search.route) {
            var searchQuery by remember { mutableStateOf("") }
            val allNotes by notesViewModel.notes.collectAsState()
            
            val searchResults = remember(searchQuery, allNotes) {
                if (searchQuery.isEmpty()) {
                    emptyList()
                } else {
                    allNotes.filter { note ->
                        note.title.contains(searchQuery, ignoreCase = true) ||
                        note.content.contains(searchQuery, ignoreCase = true) ||
                        note.tags.contains(searchQuery, ignoreCase = true)
                    }
                }
            }
            
            SearchScreen(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                searchResults = searchResults,
                onNoteClick = { noteId ->
                    navController.navigate(Screen.NoteDetail.createRoute(noteId))
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(Screen.TagsFilter.route) {
            var selectedFilter by remember { mutableStateOf(FilterType.ALL) }
            var selectedTag by remember { mutableStateOf<String?>(null) }
            val allNotes by notesViewModel.notes.collectAsState()
            
            TagsFilterScreen(
                notes = allNotes,
                selectedFilter = selectedFilter,
                onFilterChange = { selectedFilter = it },
                selectedTag = selectedTag,
                onTagSelect = { selectedTag = it },
                onNoteClick = { noteId ->
                    navController.navigate(Screen.NoteDetail.createRoute(noteId))
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(Screen.Settings.route) {
            val sortOrder by notesViewModel.sortOrder.collectAsState()
            val context = androidx.compose.ui.platform.LocalContext.current
            val scope = rememberCoroutineScope()
            
            SettingsScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                currentSortOrder = sortOrder,
                onSortOrderChange = { newOrder ->
                    notesViewModel.updateSortOrder(newOrder)
                },
                onExportNotes = {
                    scope.launch {
                        try {
                            val csvContent = notesViewModel.exportNotesToCsv()
                            
                            // Save to cache directory (no permissions needed)
                            val cacheDir = context.cacheDir
                            val timestamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(java.util.Date())
                            val fileName = "FishJournal_Export_$timestamp.csv"
                            val file = java.io.File(cacheDir, fileName)
                            
                            file.writeText(csvContent)
                            
                            // Share the file
                            val uri = androidx.core.content.FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                file
                            )
                            
                            val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/csv"
                                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                putExtra(android.content.Intent.EXTRA_SUBJECT, "Fish Journal Export")
                                putExtra(android.content.Intent.EXTRA_TEXT, "Fish Journal notes exported on $timestamp")
                                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            
                            context.startActivity(
                                android.content.Intent.createChooser(shareIntent, "Export Notes to CSV")
                            )
                        } catch (e: Exception) {
                            android.widget.Toast.makeText(
                                context,
                                "Export failed: ${e.message}",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            )
        }
    }
}
