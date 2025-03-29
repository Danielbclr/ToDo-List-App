package com.danbramos.todolist.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.danbramos.todolist.model.Priority
import com.danbramos.todolist.model.Task

/**
 * Composable function that displays a dialog for editing an existing task.
 *
 * @param task The task to be edited.
 * @param onDismiss Callback to be invoked when the dialog is dismissed.
 * @param onUpdateTask Callback to be invoked when the task is updated. It receives the updated task as a parameter.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskDialog(task: Task, onDismiss: () -> Unit, onUpdateTask: (Task) -> Unit) {
    // State variables to hold the editable title and description, initialized with the current task details.
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var priority by remember { mutableStateOf(Priority.fromInt(task.priority)) }
    var expanded by remember { mutableStateOf(false) }

    // AlertDialog to display the task editing form.
    AlertDialog(
        // Callback to handle dialog dismissal.
        onDismissRequest = onDismiss,
        title = { Text("Edit Task") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    placeholder = { Text("Enter task title") } // Placeholder text for title.
                )
                OutlinedTextField(
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
                    OutlinedTextField(
                    readOnly = true,
                    value = if (priority != null) stringResource(priority!!.labelResId) else "",
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
        // Button to confirm and update the task details.
        confirmButton = {
            Button(onClick = {
                onUpdateTask(task.copy(title = title, description = description, priority = priority!!.value))
            }) {
                Text("Update")
            }
        },
        // Button to dismiss the dialog without making changes.
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}