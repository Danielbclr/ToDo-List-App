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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(viewModel: TaskViewModel = viewModel()) {
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.syncTasks()
    }

    Scaffold(
        floatingActionButton = {
            if (viewModel.isLoading.value) {
                CircularProgressIndicator()
            } else {
                val fabContentDescription = if (viewModel.isLoading.value) "Add button disabled" else "Add button"

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
                viewModel.isLoading.value -> LoadingIndicator()
                viewModel.errorMessage.value.isNotEmpty() -> ErrorMessage(viewModel.errorMessage.value)
                viewModel.tasks.isEmpty() -> EmptyState()
                else -> TaskList(viewModel.tasks, viewModel)
            }
        }

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

@Composable
private fun TaskList(tasks: List<Task>, viewModel: TaskViewModel) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),  // Add horizontal padding
        contentPadding = PaddingValues(vertical = 8.dp)  // Add vertical padding between items
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

@Composable
private fun SyncButton(viewModel: TaskViewModel) {
    IconButton(
        onClick = { viewModel.syncTasks() },
        enabled = !viewModel.isLoading.value
    ) {
        Icon(
            imageVector = Icons.Filled.Sync,
            contentDescription = "Sync",
            tint = if (viewModel.isLoading.value) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ErrorMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Error: $message")
    }
}

