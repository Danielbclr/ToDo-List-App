package com.danbramos.todolist.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAddTask: (Task) -> Unit,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.LOW.value) }
    
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
                    text = stringResource(R.string.add_new_task),
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
                                onAddTask(
                                    Task(
                                        title = title,
                                        description = description,
                                        priority = priority
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
                        Text(stringResource(R.string.add_task))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrioritySelector(
    selectedPriority: Int, 
    onPrioritySelected: (Int) -> Unit,
    isDarkTheme: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Priority.entries.forEach { priority ->
            val isSelected = selectedPriority == priority.value
            
            // Get the corresponding priority color based on priority level and theme
            val priorityColor = if (isDarkTheme) {
                when (priority) {
                    Priority.LOW -> lowPriorityColorDark
                    Priority.MED -> medPriorityColorDark
                    Priority.HIGH -> highPriorityColorDark
                    Priority.TOP -> topPriorityColorDark
                }
            } else {
                when (priority) {
                    Priority.LOW -> lowPriorityColorLight
                    Priority.MED -> medPriorityColorLight
                    Priority.HIGH -> highPriorityColorLight
                    Priority.TOP -> topPriorityColorLight
                }
            }
            
            // Calculate text color based on background color brightness
            val textColor = if (priorityColor.luminance() > 0.5) {
                Color.Black.copy(alpha = 0.87f)  // Dark text for light backgrounds
            } else {
                Color.White.copy(alpha = 0.87f)  // Light text for dark backgrounds
            }
            
            // Add elevation and border for selected chips
            val elevation = if (isSelected) 4.dp else 0.dp
            
            FilterChip(
                selected = isSelected,
                onClick = { onPrioritySelected(priority.value) },
                label = { 
                    Text(
                        text = priority.name,
                        color = textColor
                    ) 
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = priorityColor,
                    containerColor = priorityColor.copy(alpha = if (isSelected) 1f else 0.7f),
                    labelColor = textColor
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = priorityColor.copy(alpha = 0.5f),
                    selectedBorderColor = priorityColor,
                    selectedBorderWidth = if (isSelected) 2.dp else 0.dp,
                    enabled = true,
                    selected = isSelected
                ),
                elevation = FilterChipDefaults.filterChipElevation(
                    elevation = elevation,
                    pressedElevation = elevation,
                    focusedElevation = elevation,
                    hoveredElevation = elevation,
                    disabledElevation = 0.dp,
                )
            )
        }
    }
} 