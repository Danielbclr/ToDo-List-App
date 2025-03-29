import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Icon
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.danbramos.todolist.model.Task
import com.danbramos.todolist.view.EmptyState
import com.danbramos.todolist.view.LoadingIndicator
import com.danbramos.todolist.view.TaskItem
import androidx.compose.material.icons.filled.Sync
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.danbramos.todolist.view.AddTaskDialog
import com.danbramos.todolist.view.DetailTaskDialog
import com.danbramos.todolist.view.EditTaskDialog
import com.danbramos.todolist.viewmodel.TaskViewModel

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
    var showAddDialog by remember { mutableStateOf(false) }
    var editMode by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDetailDialog by remember { mutableStateOf(false) }
    var sortOrder by remember { mutableStateOf(SortOrder.PRIORITY_DESC) }

    LaunchedEffect(Unit) {
        viewModel.syncTasks()
    }

    Scaffold(
        floatingActionButton = {
            if (viewModel.isLoading.value) {
                // Show a loading indicator when tasks are loading.
                CircularProgressIndicator()
            } else {
                FloatingActionButton(
                    onClick = { showAddDialog = true }
                ) {
                    Icon(Icons.Default.Add, "Add")
                }
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("Todo List") },
                actions = {
                    SortButton(
                        sortOrder = sortOrder,
                        onSortOrderChange = { newSortOrder ->
                            sortOrder = newSortOrder
                            viewModel.sortTasks(newSortOrder)
                            viewModel.syncTasks()
                        }
                    )
                    // Edit Mode Toggle
                    IconButton(
                        onClick = { editMode = !editMode },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = if (editMode) Icons.Filled.Done else Icons.Filled.Edit,
                            contentDescription = if (editMode) "Exit Edit Mode" else "Edit Mode"
                        )
                    }
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
                viewModel.isLoading.value -> LoadingIndicator()
                viewModel.errorMessage.value.isNotEmpty() -> ErrorMessage(viewModel.errorMessage.value)
                viewModel.tasks.isEmpty() -> EmptyState()
                else -> TaskList(
                    tasks = viewModel.tasks,
                    viewModel = viewModel,
                    editMode = editMode,
                    onTaskClick = { task ->
                        if (editMode) {
                            selectedTask = task
                            showEditDialog = true
                        }
                    },
                    selectedTask = selectedTask,
                    showDetailDialog = showDetailDialog,
                    onDetailTask = { task ->
                        selectedTask = task
                        showDetailDialog = true
                    }
                )
            }
        }

        if (showAddDialog) {
            AddTaskDialog(
                onDismiss = { showAddDialog = false },
                onAddTask = { newTask ->
                    viewModel.addTask(newTask)
                    showAddDialog = false
                }
            )
        }

        selectedTask?.let { task ->
            if (showEditDialog) {
                EditTaskDialog(
                    task = task,
                    onDismiss = {
                        showEditDialog = false
                        selectedTask = null
                    },
                    onUpdateTask = { updatedTask ->
                        viewModel.updateTask(updatedTask)
                        showEditDialog = false
                        selectedTask = null
                    }
                )
            }

            if (showDetailDialog) {
                DetailTaskDialog(
                    task = selectedTask!!,
                    onDismiss = {
                        showDetailDialog = false
                        selectedTask = null
                    }
                )
            }
        }
    }
}

/**
 * Displays the list of tasks in a [LazyColumn].
 */
@Composable
private fun TaskList(
    tasks: List<Task>,
    viewModel: TaskViewModel,
    editMode: Boolean,
    onTaskClick: (Task) -> Unit,
    selectedTask: Task?,
    showDetailDialog: Boolean,
    onDetailTask: (Task) -> Unit
) {
    //Manage the state outside of LazyColumn
    var detailDialog by remember { mutableStateOf(false) }
    var selectedTaskState by remember { mutableStateOf<Task?>(null) }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(tasks) { task ->
            TaskItem(
                task = task,
                showDelete = editMode,
                isLoading = viewModel.isLoading.value,
                onDelete = { viewModel.deleteTask(task.id!!) },
                onClick = {
                    if (editMode) {
                        onTaskClick(task)
                    } else {
                        // Update the state to show the detail dialog
                        selectedTaskState = task
                        detailDialog = true
                    }
                },
                onUpdate = {
                    selectedTaskState = task
                    detailDialog = true
                }
            )
        }
    }
    // Use the state to conditionally show the dialog
    if (detailDialog && selectedTaskState != null) {
        DetailTaskDialog(
            task = selectedTaskState!!,
            onDismiss = {
                detailDialog = false
                selectedTaskState = null
            }
        )
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

enum class SortOrder {
    PRIORITY_DESC,
    PRIORITY_ASC,
    TITLE_ASC,
    TITLE_DESC
}
@Composable
private fun SortButton(
    sortOrder: SortOrder,
    onSortOrderChange: (SortOrder) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(onClick = { expanded = true }) {
        Icon(Icons.Filled.Sort, contentDescription = "Sort")
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Priority (High to Low)") },
                onClick = { onSortOrderChange(SortOrder.PRIORITY_DESC); expanded = false }
            )
            DropdownMenuItem(
                text = { Text("Priority (Low to High)") },
                onClick = { onSortOrderChange(SortOrder.PRIORITY_ASC); expanded = false }
            )
            DropdownMenuItem(
                text = { Text("Title (A to Z)") },
                onClick = { onSortOrderChange(SortOrder.TITLE_ASC); expanded = false }
            )
            DropdownMenuItem(
                text = { Text("Title (Z to A)") },
                onClick = { onSortOrderChange(SortOrder.TITLE_DESC); expanded = false }
            )
        }
    }
}