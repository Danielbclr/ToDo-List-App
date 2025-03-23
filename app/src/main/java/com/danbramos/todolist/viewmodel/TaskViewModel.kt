package com.danbramos.todolist.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danbramos.todolist.model.Task
import com.danbramos.todolist.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {
    private val repository = TaskRepository()
    val tasks = mutableStateListOf<Task>()
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf("")

    fun syncTasks() {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val fetchedTasks = repository.getAllTasks()
                tasks.clear()
                tasks.addAll(fetchedTasks)
                errorMessage.value = ""
            } catch (e: Exception) {
                errorMessage.value = "Sync failed: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

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
}