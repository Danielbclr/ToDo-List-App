package com.danbramos.todolist


import TaskListScreen
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.danbramos.todolist.ui.theme.TodoListTheme
import com.danbramos.todolist.view.SettingsScreen
import com.danbramos.todolist.viewmodel.LanguageMode
import com.danbramos.todolist.viewmodel.SettingsViewModel
import com.danbramos.todolist.viewmodel.TaskViewModel
import java.util.Locale

/**
 * The main activity of the ToDoList application.
 *
 * This activity serves as the entry point for the application and is responsible for
 * setting up the UI and managing the application's main components. It utilizes a
 * [TaskViewModel] to handle task-related data and logic.
 */
class MainActivity : ComponentActivity() {
    private val taskViewModel: TaskViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private var backPressedTime = 0L
    private val BACK_PRESS_INTERVAL = 2000L // 2 seconds

    /**
     * Attach base context with language configuration
     */
    override fun attachBaseContext(newBase: Context) {
        // Get saved language preference
        val prefs = newBase.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val languageMode = try {
            val languageModeString = prefs.getString("language_mode", LanguageMode.SYSTEM.name)
            LanguageMode.valueOf(languageModeString!!)
        } catch (e: Exception) {
            LanguageMode.SYSTEM
        }
        
        // Apply language configuration to context
        val locale = when (languageMode) {
            LanguageMode.ENGLISH -> Locale("en")
            LanguageMode.PORTUGUESE -> Locale("pt")
            LanguageMode.SYSTEM -> Locale.getDefault()
        }
        
        val configuration = Configuration(newBase.resources.configuration)
        configuration.setLocale(locale)
        val context = newBase.createConfigurationContext(configuration)
        
        super.attachBaseContext(context)
    }

    /**
     * Called when the activity is starting.
     * Initializes the UI and sets the content view.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set activity reference for recreation on language change
        settingsViewModel.setActivity(this)
        
        // Apply saved language preference
        settingsViewModel.applyInitialLanguage()

        setContent {
            TodoListTheme(settingsViewModel = settingsViewModel) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    var currentScreen by remember { mutableStateOf(Screen.TASK_LIST) }
                    var showAddDialog by remember { mutableStateOf(false) }
                    var showEditDialog by remember { mutableStateOf(false) }
                    var showDetailDialog by remember { mutableStateOf(false) }
                    var backPressTimestamp by remember { mutableLongStateOf(0L) }
                    
                    // Handle back press based on current UI state
                    BackHandler {
                        when {
                            // If any dialog is showing, close it first
                            showAddDialog -> {
                                showAddDialog = false
                            }
                            showEditDialog -> {
                                showEditDialog = false
                            }
                            showDetailDialog -> {
                                showDetailDialog = false
                            }
                            // If on settings screen, go back to task list
                            currentScreen == Screen.SETTINGS -> {
                                currentScreen = Screen.TASK_LIST
                            }
                            // If on main screen, implement double-press to exit
                            else -> {
                                val currentTime = System.currentTimeMillis()
                                if (currentTime - backPressTimestamp < BACK_PRESS_INTERVAL) {
                                    finish() // Close the app
                                } else {
                                    backPressTimestamp = currentTime
                                    Toast.makeText(
                                        this@MainActivity,
                                        this@MainActivity.getString(R.string.press_back_to_exit),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                    
                    when (currentScreen) {
                        Screen.TASK_LIST -> TaskListScreen(
                            viewModel = taskViewModel,
                            onSettingsClick = { currentScreen = Screen.SETTINGS },
                            onShowAddDialog = { showAddDialog = true },
                            onShowEditDialog = { showEditDialog = true },
                            onShowDetailDialog = { showDetailDialog = true },
                            showAddDialog = showAddDialog,
                            showEditDialog = showEditDialog,
                            showDetailDialog = showDetailDialog,
                            onDismissAddDialog = { showAddDialog = false },
                            onDismissEditDialog = { showEditDialog = false },
                            onDismissDetailDialog = { showDetailDialog = false }
                        )
                        Screen.SETTINGS -> SettingsScreen(
                            settingsViewModel = settingsViewModel,
                            onBackClick = { currentScreen = Screen.TASK_LIST }
                        )
                    }
                }
            }
        }
    }
}

enum class Screen {
    TASK_LIST, SETTINGS
}