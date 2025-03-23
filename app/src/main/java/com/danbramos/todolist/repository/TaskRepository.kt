package com.danbramos.todolist.repository

import com.danbramos.todolist.RetrofitClient
import com.danbramos.todolist.model.Task

class TaskRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun getAllTasks(): List<Task> {
        return apiService.getAllTasks() // Let this throw on error
    }

    suspend fun createTask(task: Task) {
        apiService.createTask(task) // Let this throw on error
    }


    // Similar modifications for update/delete
    suspend fun updateTask(id: Long, task: Task){
        apiService.updateTask(id, task)
    }

    suspend fun deleteTask(id: Long) {
        apiService.deleteTask(id)
    }

}