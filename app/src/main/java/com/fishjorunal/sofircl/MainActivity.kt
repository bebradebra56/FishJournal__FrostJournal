package com.fishjorunal.sofircl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.fishjorunal.sofircl.data.database.FishJournalDatabase
import com.fishjorunal.sofircl.data.preferences.PreferencesManager
import com.fishjorunal.sofircl.data.repository.NoteRepository
import com.fishjorunal.sofircl.navigation.NavGraph
import com.fishjorunal.sofircl.navigation.Screen
import com.fishjorunal.sofircl.ui.theme.FishJournalTheme
import com.fishjorunal.sofircl.viewmodel.NoteDetailViewModel
import com.fishjorunal.sofircl.viewmodel.NotesViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    private lateinit var notesViewModel: NotesViewModel
    private lateinit var noteDetailViewModel: NoteDetailViewModel
    private lateinit var preferencesManager: PreferencesManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize database and repository
        val database = FishJournalDatabase.getDatabase(applicationContext)
        val repository = NoteRepository(
            database.noteDao(),
            database.checklistItemDao()
        )
        
        // Initialize ViewModels
        notesViewModel = NotesViewModel(repository)
        noteDetailViewModel = NoteDetailViewModel(repository)
        
        // Initialize PreferencesManager
        preferencesManager = PreferencesManager(applicationContext)
        
        // Check if onboarding is completed
        val hasSeenOnboarding = runBlocking {
            preferencesManager.onboardingCompleted.first()
        }
        
        setContent {
            FishJournalTheme {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()
                
                NavGraph(
                    navController = navController,
                    startDestination = if (hasSeenOnboarding) Screen.NotesList.route else Screen.Splash.route,
                    notesViewModel = notesViewModel,
                    noteDetailViewModel = noteDetailViewModel,
                    hasSeenOnboarding = hasSeenOnboarding,
                    onOnboardingComplete = {
                        scope.launch {
                            preferencesManager.setOnboardingCompleted(true)
                        }
                    }
                )
            }
        }
    }
}