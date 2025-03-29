package com.danbramos.todolist.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.danbramos.todolist.model.Priority
import com.danbramos.todolist.model.Task

@Composable
fun DetailTaskDialog(task: Task, onDismiss: () -> Unit) {
    val priority = Priority.fromInt(task.priority)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Task Details") },
        text = {
            Column {
                Text(text = "Title: ${task.title}", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Description: ${task.description.takeIf { it.isNotBlank() } ?: "No description"}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                val priorityLabel = priority?.let { stringResource(it.labelResId) } ?: "Unknown"
                Text(text = "Priority: $priorityLabel", style = MaterialTheme.typography.bodyMedium)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
