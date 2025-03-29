package com.danbramos.todolist.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.danbramos.todolist.R
import com.danbramos.todolist.model.Priority
import com.danbramos.todolist.model.Task

@Composable
fun DetailTaskDialog(
    task: Task,
    onDismiss: () -> Unit
) {
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
                    text = stringResource(R.string.task_details),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                DetailItem(label = stringResource(R.string.title), value = task.title)
                
                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailItem(label = stringResource(R.string.description), value = task.description)
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                DetailItem(
                    label = stringResource(R.string.priority), 
                    value = Priority.fromInt(task.priority)?.name ?: "Unknown",
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                DetailItem(
                    label = stringResource(R.string.status), 
                    value = if (task.completed) stringResource(R.string.status_completed) else stringResource(R.string.status_active)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = onDismiss) {
                        Text(stringResource(R.string.close))
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
} 