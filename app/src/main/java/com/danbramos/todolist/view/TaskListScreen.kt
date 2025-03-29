import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.danbramos.todolist.R
import com.danbramos.todolist.model.Task
import com.danbramos.todolist.ui.theme.*
import com.danbramos.todolist.view.AddTaskDialog
import com.danbramos.todolist.view.DetailTaskDialog
import com.danbramos.todolist.view.EditTaskDialog
import com.danbramos.todolist.view.EmptyState
import com.danbramos.todolist.view.LoadingIndicator
import com.danbramos.todolist.view.TaskItem
import com.danbramos.todolist.viewmodel.SettingsViewModel
import com.danbramos.todolist.viewmodel.TaskViewModel
import com.danbramos.todolist.viewmodel.ThemeMode

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
fun TaskListScreen(
    viewModel: TaskViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel(),
    onSettingsClick: () -> Unit = {},
    // Dialog visibility controlled by parent
    showAddDialog: Boolean = false,
    showEditDialog: Boolean = false,
    showDetailDialog: Boolean = false,
    // Dialog callbacks
    onShowAddDialog: () -> Unit = {},
    onShowEditDialog: () -> Unit = {},
    onShowDetailDialog: () -> Unit = {},
    onDismissAddDialog: () -> Unit = {},
    onDismissEditDialog: () -> Unit = {},
    onDismissDetailDialog: () -> Unit = {}
) {
    var editMode by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var sortOrder by remember { mutableStateOf(SortOrder.PRIORITY_DESC) }
    
    // Get current theme mode
    val themeMode by settingsViewModel.themeMode.collectAsState()
    val isDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    
    // Get theme colors
    val backgroundColor = if (isDarkTheme) DarkModeBg else LightModeBg

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
                    onClick = onShowAddDialog,
                    containerColor = if (isDarkTheme) fabBackgroundDark else fabBackgroundLight,
                    contentColor = if (isDarkTheme) fabContentDark else fabContentLight
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
                }
            }
        },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.todo_list)) },
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
                            contentDescription = if (editMode) stringResource(R.string.exit_edit_mode) else stringResource(R.string.edit_mode)
                        )
                    }
                    // Settings Button
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                    SyncButton(viewModel)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        containerColor = backgroundColor
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
                        selectedTask = task
                        if (editMode) {
                            onShowEditDialog()
                        } else {
                            onShowDetailDialog()
                        }
                    },
                    onDelete = { taskId -> viewModel.deleteTask(taskId) }
                )
            }
        }

        if (showAddDialog) {
            AddTaskDialog(
                onDismiss = onDismissAddDialog,
                onAddTask = { newTask ->
                    viewModel.addTask(newTask)
                    onDismissAddDialog()
                },
                settingsViewModel = settingsViewModel
            )
        }

        if (showEditDialog && selectedTask != null) {
            EditTaskDialog(
                task = selectedTask!!,
                onDismiss = {
                    onDismissEditDialog()
                    selectedTask = null
                },
                onUpdateTask = { updatedTask ->
                    viewModel.updateTask(updatedTask)
                    onDismissEditDialog()
                    selectedTask = null
                },
                settingsViewModel = settingsViewModel
            )
        }

        if (showDetailDialog && selectedTask != null) {
            DetailTaskDialog(
                task = selectedTask!!,
                onDismiss = {
                    onDismissDetailDialog()
                    selectedTask = null
                }
            )
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
    onDelete: (Long) -> Unit
) {
    // Get a reference to the SettingsViewModel
    val settingsViewModel = viewModel<SettingsViewModel>()
    
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(tasks) { task ->
            TaskItem(
                task = task,
                showDelete = editMode,
                isLoading = viewModel.isLoading.value,
                onDelete = { task.id?.let { onDelete(it) } },
                onClick = { onTaskClick(task) },
                onUpdate = { onTaskClick(task) },
                settingsViewModel = settingsViewModel
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
            contentDescription = stringResource(R.string.sync),
            //if loading change the opacity
            tint = if (viewModel.isLoading.value) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Dropdown menu for selecting sort order.
 */
@Composable
private fun SortButton(
    sortOrder: SortOrder,
    onSortOrderChange: (SortOrder) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    // Get current theme mode
    val settingsViewModel = viewModel<SettingsViewModel>()
    val themeMode by settingsViewModel.themeMode.collectAsState()
    val isDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    
    // Theme colors
    val backgroundColor = if (isDarkTheme) DarkModeSurface else LightModeSurface
    val textColor = if (isDarkTheme) Color.White.copy(alpha = 0.87f) else Color.Black.copy(alpha = 0.87f)
    val highlightColor = if (isDarkTheme) fabBackgroundDark else fabBackgroundLight
    
    Box {
        IconButton(
            onClick = { expanded = true }
        ) {
            Icon(
                imageVector = Icons.Filled.Sort,
                contentDescription = stringResource(R.string.sort)
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(backgroundColor)
        ) {
            DropdownMenuItem(
                text = { 
                    Text(
                        stringResource(R.string.sort_priority_high_to_low),
                        color = if (sortOrder == SortOrder.PRIORITY_DESC) highlightColor else textColor
                    ) 
                },
                onClick = {
                    onSortOrderChange(SortOrder.PRIORITY_DESC)
                    expanded = false
                },
                colors = MenuDefaults.itemColors(
                    textColor = textColor,
                    leadingIconColor = textColor,
                    trailingIconColor = textColor,
                    disabledTextColor = textColor.copy(alpha = 0.38f),
                    disabledLeadingIconColor = textColor.copy(alpha = 0.38f),
                    disabledTrailingIconColor = textColor.copy(alpha = 0.38f)
                )
            )
            DropdownMenuItem(
                text = { 
                    Text(
                        stringResource(R.string.sort_priority_low_to_high),
                        color = if (sortOrder == SortOrder.PRIORITY_ASC) highlightColor else textColor
                    ) 
                },
                onClick = {
                    onSortOrderChange(SortOrder.PRIORITY_ASC)
                    expanded = false
                },
                colors = MenuDefaults.itemColors(
                    textColor = textColor,
                    leadingIconColor = textColor,
                    trailingIconColor = textColor,
                    disabledTextColor = textColor.copy(alpha = 0.38f),
                    disabledLeadingIconColor = textColor.copy(alpha = 0.38f),
                    disabledTrailingIconColor = textColor.copy(alpha = 0.38f)
                )
            )
            DropdownMenuItem(
                text = { 
                    Text(
                        stringResource(R.string.sort_title_a_to_z),
                        color = if (sortOrder == SortOrder.TITLE_ASC) highlightColor else textColor
                    ) 
                },
                onClick = {
                    onSortOrderChange(SortOrder.TITLE_ASC)
                    expanded = false
                },
                colors = MenuDefaults.itemColors(
                    textColor = textColor,
                    leadingIconColor = textColor,
                    trailingIconColor = textColor,
                    disabledTextColor = textColor.copy(alpha = 0.38f),
                    disabledLeadingIconColor = textColor.copy(alpha = 0.38f),
                    disabledTrailingIconColor = textColor.copy(alpha = 0.38f)
                )
            )
            DropdownMenuItem(
                text = { 
                    Text(
                        stringResource(R.string.sort_title_z_to_a),
                        color = if (sortOrder == SortOrder.TITLE_DESC) highlightColor else textColor
                    ) 
                },
                onClick = {
                    onSortOrderChange(SortOrder.TITLE_DESC)
                    expanded = false
                },
                colors = MenuDefaults.itemColors(
                    textColor = textColor,
                    leadingIconColor = textColor,
                    trailingIconColor = textColor,
                    disabledTextColor = textColor.copy(alpha = 0.38f),
                    disabledLeadingIconColor = textColor.copy(alpha = 0.38f),
                    disabledTrailingIconColor = textColor.copy(alpha = 0.38f)
                )
            )
        }
    }
}

/** Displays an error message. */
@Composable
fun ErrorMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error
        )
    }
}

/**
 * Composable function to display a message when there are no tasks.
 * This is used when the list of tasks is empty.
 */
@Composable
fun EmptyState() {
    // Box composable to center the text within the available space
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(R.string.no_tasks_yet))
    }
}