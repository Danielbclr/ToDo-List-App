package com.danbramos.todolist.repository

import com.danbramos.todolist.RetrofitClient
import com.danbramos.todolist.model.Task
import com.danbramos.todolist.service.TaskApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TaskRepositoryTest {

    // Mock API service
    private lateinit var mockApiService: TaskApiService
    
    // The repository under test
    private lateinit var repository: TaskRepository

    @Before
    fun setup() {
        // Create mock API service
        mockApiService = mockk<TaskApiService>(relaxed = true)
        
        // Mock RetrofitClient to return our mock API service
        mockkObject(RetrofitClient)
        every { RetrofitClient.apiService } returns mockApiService
        
        // Create the repository
        repository = TaskRepository()
    }

    @After
    fun tearDown() {
        // Clean up mocks
        unmockkAll()
    }

    @Test
    fun `getAllTasks returns tasks from API service`() = runTest {
        // Arrange
        val mockTasks = listOf(
            Task(id = 1, title = "Task 1", description = "Description 1", priority = 1, completed = false),
            Task(id = 2, title = "Task 2", description = "Description 2", priority = 2, completed = true)
        )
        coEvery { mockApiService.getAllTasks() } returns mockTasks

        // Act
        val result = repository.getAllTasks()

        // Assert
        assertEquals(mockTasks, result)
        coVerify(exactly = 1) { mockApiService.getAllTasks() }
    }

    @Test
    fun `createTask calls API service with correct parameters`() = runTest {
        // Arrange
        val task = Task(title = "New Task", description = "Description", priority = 1, completed = false)
        coEvery { mockApiService.createTask(any()) } returns task
        
        // Act
        repository.createTask(task)
        
        // Assert
        coVerify(exactly = 1) { mockApiService.createTask(task) }
    }

    @Test
    fun `updateTask calls API service with correct parameters`() = runTest {
        // Arrange
        val taskId = 1L
        val task = Task(id = taskId, title = "Updated Task", description = "Updated Description", priority = 2, completed = true)
        coEvery { mockApiService.updateTask(any(), any()) } returns task
        
        // Act
        repository.updateTask(taskId, task)
        
        // Assert
        coVerify(exactly = 1) { mockApiService.updateTask(taskId, task) }
    }

    @Test
    fun `deleteTask calls API service with correct parameters`() = runTest {
        // Arrange
        val taskId = 1L
        coEvery { mockApiService.deleteTask(any()) } returns Unit
        
        // Act
        repository.deleteTask(taskId)
        
        // Assert
        coVerify(exactly = 1) { mockApiService.deleteTask(taskId) }
    }
} 