package com.danbramos.todolist

import TaskListScreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.danbramos.todolist.ui.theme.TodoListTheme
import com.danbramos.todolist.viewmodel.TaskViewModel

/**
 * The main activity of the ToDoList application.
 *
 * This activity serves as the entry point for the application and is responsible for
 * setting up the UI and managing the application's main components. It utilizes a
 * [TaskViewModel] to handle task-related data and logic.
 */
class MainActivity : ComponentActivity() {
    private val viewModel: TaskViewModel by viewModels()

    /**
     * Called when the activity is starting.
     * Initializes the UI and sets the content view.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TodoListTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
                ) {
                    TaskListScreen(viewModel = viewModel)
                }
            }
        }
    }
}