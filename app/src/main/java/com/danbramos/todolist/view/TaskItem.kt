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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
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
    onUpdate: () -> Unit
) {
    var backgroundColor = when (Priority.fromInt(task.priority)) {
        Priority.LOW -> lowPriorityColorLight
        Priority.MED -> medPriorityColorLight
        Priority.HIGH -> highPriorityColorLight
        Priority.TOP -> topPriorityColorLight
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    if(isSystemInDarkTheme()) {
        backgroundColor = when (Priority.fromInt(task.priority)) {
            Priority.LOW -> lowPriorityColorDark
            Priority.MED -> medPriorityColorDark
            Priority.HIGH -> highPriorityColorDark
            Priority.TOP -> topPriorityColorDark
            else -> MaterialTheme.colorScheme.surfaceVariant
        }
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
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (task.description.isNotEmpty()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (showDelete) {
                IconButton(
                    onClick = onDelete,
                    enabled = !isLoading
                ) {
                    Icon(Icons.Filled.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
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
        Text(text = "No tasks yet!")
    }
}