package com.danbramos.todolist.repository

import com.danbramos.todolist.RetrofitClient
import com.danbramos.todolist.model.Task


/**
 * Repository class responsible for handling task-related data operations.
 *
 * This class acts as an intermediary between the data sources (e.g., API) and the application's
 * higher-level components (e.g., ViewModels). It provides a clean and consistent interface
 * for interacting with task data, abstracting away the underlying data retrieval and manipulation
 * mechanisms.
 *
 * It utilizes an instance of `ApiService` to perform network requests for task management.
 */
class TaskRepository {
    /**
     * The ApiService instance used to make network requests.
     */
    private val apiService = RetrofitClient.apiService

    /**
     * Retrieves all tasks from the API.
     *
     * @return A list of [Task] objects.
     * @throws Exception if an error occurs during the network request.
     */
    suspend fun getAllTasks(): List<Task> {
        return apiService.getAllTasks() // Allow the exception to be thrown and handled by the caller
    }

    /**
     * Creates a new task via the API.
     *
     * @param task The [Task] object to be created.
     * @throws Exception if an error occurs during the network request.
     */
    suspend fun createTask(task: Task) {
        apiService.createTask(task) // Allow the exception to be thrown and handled by the caller
    }

    /**
     * Updates an existing task via the API.
     *
     * @param id The ID of the task to be updated.
     * @param task The [Task] object with the updated data.
     * @throws Exception if an error occurs during the network request.
     */
    suspend fun updateTask(id: Long, task: Task){
        apiService.updateTask(id, task)
    }

    /**
     * Deletes a task via the API.
     */
    suspend fun deleteTask(id: Long) {
        apiService.deleteTask(id)
    }
}