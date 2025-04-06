package com.danbramos.todolist.viewmodel

import SortOrder
import com.danbramos.todolist.model.Task
import com.danbramos.todolist.repository.TaskRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class TaskViewModelTest {

    // Test dispatcher for controlling coroutines during tests
    private val testDispatcher = StandardTestDispatcher()
    
    // Mock repository to avoid actual network calls
    private lateinit var mockRepository: TaskRepository
    
    // The view model under test
    private lateinit var viewModel: TaskViewModel

    @Before
    fun setup() {
        // Set the main dispatcher to our test dispatcher
        Dispatchers.setMain(testDispatcher)
        
        // Create mock repository
        mockRepository = mockk(relaxed = true)
        
        // Create the view model with mocked dependencies
        viewModel = TaskViewModel()
        // Use reflection to replace the private repository field
        val field = TaskViewModel::class.java.getDeclaredField("repository")
        field.isAccessible = true
        field.set(viewModel, mockRepository)
    }

    @After
    fun cleanup() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun `syncTasks fetches and updates tasks successfully`() = runTest {
        // Arrange
        val mockTasks = listOf(
            Task(id = 1, title = "Task 1", description = "Description 1", priority = 1, completed = false),
            Task(id = 2, title = "Task 2", description = "Description 2", priority = 2, completed = true)
        )
        coEvery { mockRepository.getAllTasks() } returns mockTasks

        // Act
        viewModel.syncTasks()
        testDispatcher.scheduler.advanceUntilIdle() // Wait for coroutines to complete

        // Assert
        assertEquals(2, viewModel.tasks.size)
        assertEquals("", viewModel.errorMessage.value)
        assertFalse(viewModel.isLoading.value)
        coVerify(exactly = 1) { mockRepository.getAllTasks() }
    }

    @Test
    fun `syncTasks handles errors correctly`() = runTest {
        // Arrange
        val errorMessage = "Network error"
        coEvery { mockRepository.getAllTasks() } throws Exception(errorMessage)

        // Act
        viewModel.syncTasks()
        testDispatcher.scheduler.advanceUntilIdle() // Wait for coroutines to complete

        // Assert
        assertTrue(viewModel.errorMessage.value.contains(errorMessage))
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `addTask calls repository and syncs tasks on success`() = runTest {
        // Arrange
        val newTask = Task(title = "New Task", description = "New Description", priority = 3, completed = false)
        coEvery { mockRepository.createTask(any()) } returns Unit
        coEvery { mockRepository.getAllTasks() } returns emptyList()

        // Act
        viewModel.addTask(newTask)
        testDispatcher.scheduler.advanceUntilIdle() // Wait for coroutines to complete

        // Assert
        coVerify(exactly = 1) { mockRepository.createTask(newTask) }
        coVerify(exactly = 1) { mockRepository.getAllTasks() }
        assertEquals("", viewModel.errorMessage.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `sortTasks sorts tasks correctly`() = runTest {
        // Arrange
        val mockTasks = listOf(
            Task(id = 1, title = "B Task", description = "Description", priority = 3, completed = false),
            Task(id = 2, title = "A Task", description = "Description", priority = 1, completed = false),
            Task(id = 3, title = "C Task", description = "Description", priority = 2, completed = false)
        )
        coEvery { mockRepository.getAllTasks() } returns mockTasks
        
        // Load tasks first
        viewModel.syncTasks()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Act & Assert - Test different sort orders
        
        // By priority descending (default)
        val priorityDescTasks = viewModel.sortTasks(SortOrder.PRIORITY_DESC)
        assertEquals(3, priorityDescTasks[0].priority)
        assertEquals(2, priorityDescTasks[1].priority)
        assertEquals(1, priorityDescTasks[2].priority)
        
        // By priority ascending
        val priorityAscTasks = viewModel.sortTasks(SortOrder.PRIORITY_ASC)
        assertEquals(1, priorityAscTasks[0].priority)
        assertEquals(2, priorityAscTasks[1].priority)
        assertEquals(3, priorityAscTasks[2].priority)
        
        // By title ascending
        val titleAscTasks = viewModel.sortTasks(SortOrder.TITLE_ASC)
        assertEquals("A Task", titleAscTasks[0].title)
        assertEquals("B Task", titleAscTasks[1].title)
        assertEquals("C Task", titleAscTasks[2].title)
        
        // By title descending
        val titleDescTasks = viewModel.sortTasks(SortOrder.TITLE_DESC)
        assertEquals("C Task", titleDescTasks[0].title)
        assertEquals("B Task", titleDescTasks[1].title)
        assertEquals("A Task", titleDescTasks[2].title)
    }
    
    @Test
    fun `addTask handles errors correctly`() = runTest {
        // Arrange
        val newTask = Task(title = "New Task", description = "New Description", priority = 3, completed = false)
        val errorMessage = "Failed to create task"
        coEvery { mockRepository.createTask(any()) } throws Exception(errorMessage)

        // Act
        viewModel.addTask(newTask)
        testDispatcher.scheduler.advanceUntilIdle() // Wait for coroutines to complete

        // Assert
        assertTrue(viewModel.errorMessage.value.contains(errorMessage))
        assertFalse(viewModel.isLoading.value)
        coVerify(exactly = 0) { mockRepository.getAllTasks() }
    }
    
    @Test
    fun `updateTask calls repository with valid ID and syncs tasks on success`() = runTest {
        // Arrange
        val existingTask = Task(id = 1, title = "Updated Task", description = "Updated Description", priority = 2, completed = true)
        coEvery { mockRepository.updateTask(any(), any()) } returns Unit
        coEvery { mockRepository.getAllTasks() } returns emptyList()

        // Act
        viewModel.updateTask(existingTask)
        testDispatcher.scheduler.advanceUntilIdle() // Wait for coroutines to complete

        // Assert
        coVerify(exactly = 1) { mockRepository.updateTask(1, existingTask) }
        coVerify(exactly = 1) { mockRepository.getAllTasks() }
        assertEquals("", viewModel.errorMessage.value)
        assertFalse(viewModel.isLoading.value)
    }
    
    @Test
    fun `updateTask handles null ID correctly`() = runTest {
        // Arrange
        val invalidTask = Task(id = null, title = "Invalid Task", description = "No ID", priority = 2, completed = false)

        // Act
        viewModel.updateTask(invalidTask)
        testDispatcher.scheduler.advanceUntilIdle() // Wait for coroutines to complete

        // Assert
        assertTrue(viewModel.errorMessage.value.contains("Invalid task ID"))
        assertFalse(viewModel.isLoading.value)
        coVerify(exactly = 0) { mockRepository.updateTask(any(), any()) }
        coVerify(exactly = 0) { mockRepository.getAllTasks() }
    }
    
    @Test
    fun `updateTask handles repository errors correctly`() = runTest {
        // Arrange
        val task = Task(id = 1, title = "Error Task", description = "Will fail", priority = 2, completed = false)
        val errorMessage = "Failed to update task"
        coEvery { mockRepository.updateTask(any(), any()) } throws Exception(errorMessage)

        // Act
        viewModel.updateTask(task)
        testDispatcher.scheduler.advanceUntilIdle() // Wait for coroutines to complete

        // Assert
        assertTrue(viewModel.errorMessage.value.contains(errorMessage))
        assertFalse(viewModel.isLoading.value)
        coVerify(exactly = 0) { mockRepository.getAllTasks() }
    }
    
    @Test
    fun `deleteTask calls repository and syncs tasks on success`() = runTest {
        // Arrange
        val taskId = 1L
        coEvery { mockRepository.deleteTask(any()) } returns Unit
        coEvery { mockRepository.getAllTasks() } returns emptyList()

        // Act
        viewModel.deleteTask(taskId)
        testDispatcher.scheduler.advanceUntilIdle() // Wait for coroutines to complete

        // Assert
        coVerify(exactly = 1) { mockRepository.deleteTask(taskId) }
        coVerify(exactly = 1) { mockRepository.getAllTasks() }
        assertEquals("", viewModel.errorMessage.value)
        assertFalse(viewModel.isLoading.value)
    }
    
    @Test
    fun `deleteTask handles repository errors correctly`() = runTest {
        // Arrange
        val taskId = 1L
        val errorMessage = "Failed to delete task"
        coEvery { mockRepository.deleteTask(any()) } throws Exception(errorMessage)

        // Act
        viewModel.deleteTask(taskId)
        testDispatcher.scheduler.advanceUntilIdle() // Wait for coroutines to complete

        // Assert
        assertTrue(viewModel.errorMessage.value.contains(errorMessage))
        assertFalse(viewModel.isLoading.value)
        coVerify(exactly = 0) { mockRepository.getAllTasks() }
    }
} 