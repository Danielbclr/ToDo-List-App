import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.viewmodel.compose.viewModel
import com.danbramos.todolist.model.Task
import com.danbramos.todolist.view.EmptyState
import com.danbramos.todolist.view.LoadingIndicator
import com.danbramos.todolist.view.TaskItem
import com.danbramos.todolist.viewmodel.TaskViewModel
import androidx.compose.material.icons.filled.Sync
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.danbramos.todolist.view.AddTaskDialog

/**
 * `TaskListScreen` is the main screen of the application responsible for displaying a list of tasks.
 *
 * This composable function manages the overall layout of the task list screen, including:
 * - A `Scaffold` for basic Material Design layout structure with a top app bar and a floating action button.
 * - A floating action button (`FloatingActionButton`) for adding new tasks, which is disabled while tasks are loading.
 * - A top app bar (`TopAppBar`) displaying the app title ("Todo List") and a sync button.
 * - Conditional content display within the main content area (`Box`), such as:
 *   - A loading indicator (`LoadingIndicator`) while tasks are being fetched or synced.
 *   - An error message (`ErrorMessage`) if an error occurs during data retrieval.
 *   - An empty state message (`EmptyState`) if there are no tasks in the list.
 *   - The actual task list (`TaskList`) if tasks are available.
 * - An "Add Task" dialog (`AddTaskDialog`) that appears when the floating action button is clicked.
 *
 * Functionality:
 * - **Data Loading and Synchronization:**  Uses `LaunchedEffect` to trigger the initial synchronization of tasks via `viewModel.syncTasks()` when the screen is launched.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(viewModel: TaskViewModel = viewModel()) {
    // State to control the visibility of the Add Task Dialog.
    var showDialog by remember { mutableStateOf(false) }

    // Sync tasks when the screen is first launched.
    LaunchedEffect(Unit) {
        viewModel.syncTasks()
    }

    Scaffold(
        floatingActionButton = {
            if (viewModel.isLoading.value) {
                // Show a loading indicator when tasks are loading.
                CircularProgressIndicator()
            } else {
                // Define the content description for accessibility.
                val fabContentDescription = if (viewModel.isLoading.value) "Add button disabled" else "Add button"

                // FloatingActionButton to add new tasks.
                // Disabled when tasks are loading.
                FloatingActionButton(
                    onClick = {
                        if (!viewModel.isLoading.value) {
                            showDialog = true
                        }
                    },
                    modifier = Modifier.semantics { contentDescription = fabContentDescription },
                ) {
                    Icon(Icons.Default.Add, "Add")
                }
            }
        },
        topBar = {
            TopAppBar(
                //Title and Sync Button.
                title = { Text("Todo List") },
                actions = {
                    SyncButton(viewModel)
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                // Show a loading indicator while tasks are being fetched.
                viewModel.isLoading.value -> LoadingIndicator()
                // Display an error message if there's an error.
                viewModel.errorMessage.value.isNotEmpty() -> ErrorMessage(viewModel.errorMessage.value)
                // Display an empty state message if there are no tasks.
                viewModel.tasks.isEmpty() -> EmptyState()
                // Otherwise, display the task list.
                else -> TaskList(viewModel.tasks, viewModel)
            }
        }
        //Manage the Dialog
        // Add Task Dialog
        if (showDialog) {
            AddTaskDialog(
                onDismiss = { showDialog = false },
                onAddTask = { newTask ->
                    viewModel.addTask(newTask)
                    showDialog = false
                }
            )
        }
    }
}

/**
 * Displays the list of tasks in a [LazyColumn].
 */
@Composable
private fun TaskList(tasks: List<Task>, viewModel: TaskViewModel) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),  // horizontal padding
        contentPadding = PaddingValues(vertical = 8.dp)  // vertical padding between items
    ) {
        items(tasks) { task ->
            TaskItem(
                task = task,
                onDelete = { viewModel.deleteTask(task.id!!) },
                onUpdate = { updatedTask ->
                    viewModel.updateTask(updatedTask)
                },
                isLoading = viewModel.isLoading.value
            )
        }
    }
}

/**
 * Button to manually trigger task synchronization.
 */
@Composable
private fun SyncButton(viewModel: TaskViewModel) {
    IconButton(
        //sync tasks
        onClick = { viewModel.syncTasks() },
        //disabled if loading
        enabled = !viewModel.isLoading.value
    ) {
        Icon(
            imageVector = Icons.Filled.Sync,
            contentDescription = "Sync",
            //if loading change the opacity
            tint = if (viewModel.isLoading.value) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

/** Displays an error message. */
@Composable
fun ErrorMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Error: $message")
    }
}

