package com.danbramos.todolist.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.danbramos.todolist.model.Task

/**
 * Composable function to display a dialog for adding a new task.
 *
 * @param onDismiss Callback invoked when the dialog is dismissed.
 * @param onAddTask Callback invoked when a new task is confirmed and added.
 */
@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onAddTask: (Task) -> Unit) {
    // State variables to hold the title and description of the new task.
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // AlertDialog to display the task creation form.
    AlertDialog(
        // Callback to handle dialog dismissal.
        onDismissRequest = onDismiss,
        title = { Text("Add New Task") },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    placeholder = { Text("Enter task title") } // Placeholder text for title.
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("Enter task description (optional)") } // Placeholder text for description.
                )
            }
        },
        // Button to confirm and add the new task.
        confirmButton = {
            //Add the task only if title is not empty
            Button(onClick = {
                if (title.isNotEmpty()) {
                    onAddTask(Task(title = title, description = description))
                }
            }) {
                Text("Add")
            }
        },
        // Button to dismiss the dialog without adding a task.
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}