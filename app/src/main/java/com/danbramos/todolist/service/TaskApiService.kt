package com.danbramos.todolist.service

import com.danbramos.todolist.model.Task
import retrofit2.http.*

/**
 * Interface defining the API service for interacting with tasks.
 *
 * This interface utilizes Retrofit annotations to define HTTP requests and handle data serialization.
 * It provides methods for retrieving, creating, updating, and deleting task data from a remote server.
 */
interface TaskApiService {
    /**
     * Retrieves all tasks.
     *
     * @return A list of [Task] objects.
     */
    @GET("tasks")
    suspend fun getAllTasks(): List<Task>

    /**
     * Retrieves a specific task by its ID.
     *
     * @param id The ID of the task to retrieve.
     * @return The [Task] object with the given ID.
     */
    @GET("tasks/{id}")
    suspend fun getTaskById(@Path("id") id: Long): Task

    /**
     * Creates a new task.
     *
     * @param task The [Task] object to create.
     * @return The created [Task] object.
     */
    @POST("tasks")
    suspend fun createTask(@Body task: Task): Task

    /**
     * Updates an existing task.
     *
     * @param id The ID of the task to update.
     * @param task The updated [Task] object.
     * @return The updated [Task] object.
     */
    @PUT("tasks/{id}")
    suspend fun updateTask(@Path("id") id: Long, @Body task: Task): Task

    /** Deletes a task by its ID. */
    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: Long)
}