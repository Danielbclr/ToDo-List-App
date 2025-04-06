package com.danbramos.todolist.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.danbramos.todolist.R
import com.danbramos.todolist.model.Priority
import com.danbramos.todolist.model.Task
import com.danbramos.todolist.ui.theme.highPriorityColorDark
import com.danbramos.todolist.ui.theme.highPriorityColorLight
import com.danbramos.todolist.ui.theme.lowPriorityColorDark
import com.danbramos.todolist.ui.theme.lowPriorityColorLight
import com.danbramos.todolist.ui.theme.medPriorityColorDark
import com.danbramos.todolist.ui.theme.medPriorityColorLight
import com.danbramos.todolist.ui.theme.topPriorityColorDark
import com.danbramos.todolist.ui.theme.topPriorityColorLight
import com.danbramos.todolist.viewmodel.SettingsViewModel
import com.danbramos.todolist.viewmodel.ThemeMode

/**
 * Composable function to display an individual task item within a list.
 *
 * This function renders a single task as a card, showing its title, description, a delete button, and a completion checkbox.
 *
 * @param task The [Task] object representing the task to display. Contains the task's title, description, and completion status.
 * @param onDelete Callback function invoked when the user clicks the delete button for this task. This should handle the removal of the task from the data source.
 * @param onUpdate Callback function invoked when the user toggles the completion checkbox. It receives the updated [Task] object with the modified `completed` status. This should handle updating the task's completion status in the data source.
 * @param isLoading A boolean flag indicating whether the task list is currently loading. When true, the task item will be visually dimmed and the delete button and checkbox will be disabled to prevent user interaction during loading.
 */
@Composable
fun TaskItem(
    task: Task,
    showDelete: Boolean,
    isLoading: Boolean,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    onUpdate: (Task) -> Unit,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    // Get the current theme mode from settings
    val themeMode by settingsViewModel.themeMode.collectAsState()
    
    // Determine if dark theme should be used based on settings
    val useDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    
    // Select background color based on priority and theme
    val backgroundColor = if (useDarkTheme) {
        when (Priority.fromInt(task.priority)) {
            Priority.LOW -> lowPriorityColorDark
            Priority.MED -> medPriorityColorDark
            Priority.HIGH -> highPriorityColorDark
            Priority.TOP -> topPriorityColorDark
            else -> MaterialTheme.colorScheme.surfaceVariant
        }
    } else {
        when (Priority.fromInt(task.priority)) {
            Priority.LOW -> lowPriorityColorLight
            Priority.MED -> medPriorityColorLight
            Priority.HIGH -> highPriorityColorLight
            Priority.TOP -> topPriorityColorLight
            else -> MaterialTheme.colorScheme.surfaceVariant
        }
    }

    // Calculate text color for better contrast with background
    val textColor = if (backgroundColor.luminance() > 0.5) {
        Color.Black.copy(alpha = 0.87f)  // Dark text for light backgrounds
    } else {
        Color.White  // Light text for dark backgrounds
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(enabled = !isLoading, onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Add checkbox to toggle completion status
            Checkbox(
                checked = task.completed,
                onCheckedChange = { isChecked ->
                    // Create updated task with new completion status
                    val updatedTask = task.copy(completed = isChecked)
                    onUpdate(updatedTask)
                },
                enabled = !isLoading,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(20.dp),
                colors = CheckboxDefaults.colors(
                        checkedColor = textColor,
                        uncheckedColor = textColor.copy(alpha = 0.6f),
                        checkmarkColor = backgroundColor,
                        disabledCheckedColor = textColor.copy(alpha = 0.4f),
                        disabledUncheckedColor = textColor.copy(alpha = 0.3f),
                    )
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = textColor
                )
                if (task.description.isNotEmpty()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor.copy(alpha = 0.7f)
                    )
                }
            }

            if (showDelete) {
                IconButton(
                    onClick = onDelete,
                    enabled = !isLoading
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete), tint = MaterialTheme.colorScheme.error)
                }
            }
        }
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