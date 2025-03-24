package com.danbramos.todolist.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.danbramos.todolist.model.Task

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
    onDelete: () -> Unit,
    onUpdate: (Task) -> Unit,
    isLoading: Boolean
) {
    // Card composable to wrap the task item
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .alpha(if (isLoading) 0.5f else 1f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Display the task title with bold font weight
                Text(text = task.title, fontWeight = FontWeight.Bold)
                // Display the task description
                Text(text = task.description)
            }
            // Delete button for the task
            IconButton(
                onClick = onDelete, // Calls the onDelete callback
                enabled = !isLoading
            ) {
                Icon(Icons.Default.Delete, "Delete")
            }

            Checkbox(
                checked = task.completed,
                onCheckedChange = { newState -> // Calls the onUpdate callback with the new state
                    onUpdate(task.copy(completed = newState))
                },
                enabled = !isLoading // Checkbox is disabled while loading
            )
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