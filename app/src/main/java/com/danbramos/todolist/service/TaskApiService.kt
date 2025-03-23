package com.danbramos.todolist.service

import com.danbramos.todolist.model.Task
import retrofit2.http.*

interface TaskApiService {
    @GET("tasks")
    suspend fun getAllTasks(): List<Task>

    @GET("tasks/{id}")
    suspend fun getTaskById(@Path("id") id: Long): Task

    @POST("tasks")
    suspend fun createTask(@Body task: Task): Task

    @PUT("tasks/{id}")
    suspend fun updateTask(@Path("id") id: Long, @Body task: Task): Task

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: Long)
}