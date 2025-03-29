package com.danbramos.todolist.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.danbramos.todolist.model.Priority
import com.danbramos.todolist.model.Task

/**
 * Composable function to display a dialog for adding a new task.
 *
 * @param onDismiss Callback invoked when the dialog is dismissed.
 * @param onAddTask Callback invoked when a new task is confirmed and added.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onAddTask: (Task) -> Unit) {
    // State variables to hold the title and description of the new task.
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.LOW) }  // Default priority
    var expanded by remember { mutableStateOf(false) }

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

                // Priority Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                ) {
                    TextField(
                        readOnly = true,
                        value = priority.name,
                        onValueChange = {},
                        label = { Text("Priority") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded
                            )
                        },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        Priority.entries.forEach { currentPriority ->
                            DropdownMenuItem(
                                text = { Text(text = currentPriority.name) },
                                onClick = {
                                    priority = currentPriority
                                    expanded = false
                                },
                            )
                        }
                    }
                }
            }
        },
        // Button to confirm and add the new task.
        confirmButton = {
            //Add the task only if title is not empty
            Button(onClick = {
                if (title.isNotEmpty()) {
                    onAddTask(Task(title = title, description = description, priority = priority.value))
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