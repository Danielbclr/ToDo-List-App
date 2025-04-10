package com.danbramos.todolist.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.danbramos.todolist.R
import com.danbramos.todolist.model.Priority
import com.danbramos.todolist.model.Task
import com.danbramos.todolist.ui.theme.*
import com.danbramos.todolist.viewmodel.SettingsViewModel
import com.danbramos.todolist.viewmodel.ThemeMode

/**
 * Composable function that displays a dialog for editing an existing task.
 *
 * @param task The task to be edited.
 * @param onDismiss Callback to be invoked when the dialog is dismissed.
 * @param onUpdateTask Callback to be invoked when the task is updated. It receives the updated task as a parameter.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onUpdateTask: (Task) -> Unit,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var priority by remember { mutableStateOf(task.priority) }
    var completed by remember { mutableStateOf(task.completed) }
    
    // Get current theme mode
    val themeMode by settingsViewModel.themeMode.collectAsState()
    val isDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    
    // Handle back button press
    BackHandler {
        onDismiss()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.edit_task),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.title)) },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.description)) },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = stringResource(R.string.priority),
                    color = MaterialTheme.colorScheme.onSurface
                )
                PrioritySelector(
                    selectedPriority = priority,
                    onPrioritySelected = { priority = it },
                    isDarkTheme = isDarkTheme
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Checkbox(
                        checked = completed,
                        onCheckedChange = { completed = it }
                    )
                    Text(
                        text = stringResource(R.string.completed),
                        modifier = Modifier.padding(start = 8.dp, top = 12.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onUpdateTask(
                                    Task(
                                        id = task.id,
                                        title = title,
                                        description = description,
                                        priority = priority,
                                        completed = completed
                                    )
                                )
                            }
                        },
                        enabled = title.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDarkTheme) fabBackgroundDark else fabBackgroundLight,
                            contentColor = if (isDarkTheme) fabContentDark else fabContentLight
                        )
                    ) {
                        Text(stringResource(R.string.update))
                    }
                }
            }
        }
    }
}