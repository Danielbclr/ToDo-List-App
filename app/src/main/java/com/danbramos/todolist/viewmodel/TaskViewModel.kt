package com.danbramos.todolist.viewmodel

import SortOrder
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danbramos.todolist.model.Task
import com.danbramos.todolist.repository.TaskRepository
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing the task data and interacting with the TaskRepository.
 *
 * This class provides methods to fetch, add, update, and delete tasks, and exposes
 * the current state of tasks, loading status, and any error messages to the UI.
 */
class TaskViewModel : ViewModel() {
    // Instance of TaskRepository to interact with data layer
    private val repository = TaskRepository()
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf("")
    private val _tasks = mutableStateListOf<Task>()
    private var _sortOrder = SortOrder.PRIORITY_DESC
    val tasks: SnapshotStateList<Task>
        get() = SnapshotStateList<Task>().also { it.addAll(sortTasks(_sortOrder)) }

    /**
     * Fetches all tasks from the repository and updates the [tasks] state.
     *
     * Sets [isLoading] to true while fetching, then false when done.
     * Updates [errorMessage] if an exception occurs during the fetch.
     */
    fun syncTasks() {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val fetchedTasks = repository.getAllTasks()
                _tasks.clear()
                _tasks.addAll(fetchedTasks)
                errorMessage.value = ""
            } catch (e: Exception) {
                errorMessage.value = "Sync failed: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * Adds a new task to the repository and refreshes the task list.
     *
     * Sets [isLoading] to true while adding, then false if an error occurs.
     * Updates [errorMessage] if an exception occurs.
     * @param task The task to be added.
     */
    fun addTask(task: Task) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                repository.createTask(task)
                syncTasks() // Refresh list from server after successful add
            } catch (e: Exception) {
                errorMessage.value = "Failed to create task: ${e.localizedMessage}"
                isLoading.value = false
            }
        }
    }

    /**
     * Updates an existing task in the repository and refreshes the task list.
     *
     * Sets [isLoading] to true while updating, then false if an error occurs.
     * Updates [errorMessage] if an exception occurs or if the task ID is invalid.
     * @param task The task to be updated. Must contain the task's ID.
     */
    fun updateTask(task: Task) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                task.id?.let { id ->
                    repository.updateTask(id, task)
                    syncTasks() // Refresh list from server
                } ?: run {
                    errorMessage.value = "Invalid task ID"
                    isLoading.value = false
                }
            } catch (e: Exception) {
                errorMessage.value = "Failed to update task: ${e.localizedMessage}"
                isLoading.value = false
            }
        }
    }

    /**
     * Deletes a task from the repository and refreshes the task list.
     *
     * Sets [isLoading] to true while deleting, then false if an error occurs.
     * Updates [errorMessage] if an exception occurs.
     * @param id The ID of the task to be deleted.
     */
    fun deleteTask(id: Long) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                repository.deleteTask(id)
                syncTasks() // Refresh list from server
            } catch (e: Exception) {
                errorMessage.value = "Failed to delete task: ${e.localizedMessage}"
                isLoading.value = false
            }
        }
    }

    fun sortTasks(sortOrder: SortOrder): List<Task> {
        _sortOrder = sortOrder
        return when (sortOrder) {
            SortOrder.PRIORITY_DESC -> _tasks.sortedByDescending { it.priority}
            SortOrder.PRIORITY_ASC -> _tasks.sortedBy { it.priority }
            SortOrder.TITLE_ASC -> _tasks.sortedBy { it.title }
            SortOrder.TITLE_DESC -> _tasks.sortedByDescending { it.title }
        }
    }
}