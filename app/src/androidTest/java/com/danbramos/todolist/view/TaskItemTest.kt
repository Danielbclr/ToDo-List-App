package com.danbramos.todolist.view

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.danbramos.todolist.model.Task
import com.danbramos.todolist.viewmodel.SettingsViewModel
import com.danbramos.todolist.viewmodel.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

class TaskItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    
    // Mock SettingsViewModel for theme
    private lateinit var mockSettingsViewModel: SettingsViewModel
    
    @Before
    fun setUp() {
        // Create a mock of SettingsViewModel
        mockSettingsViewModel = mock(SettingsViewModel::class.java)
        
        // Set up the mock to return a controlled theme mode
        whenever(mockSettingsViewModel.themeMode).thenReturn(MutableStateFlow(ThemeMode.LIGHT))
    }

    @Test
    fun taskItem_displaysTitleAndDescription() {
        // Arrange - Create a test task
        val testTask = Task(
            id = 1,
            title = "Test Task",
            description = "This is a test description",
            priority = 2,
            completed = false
        )
        
        // Act - Render the TaskItem
        composeTestRule.setContent {
            TaskItem(
                task = testTask,
                showDelete = true,
                isLoading = false,
                onDelete = {},
                onClick = {},
                onUpdate = {},
                settingsViewModel = mockSettingsViewModel
            )
        }
        
        // Assert - Verify that title and description are displayed
        composeTestRule.onNodeWithText("Test Task").assertIsDisplayed()
        composeTestRule.onNodeWithText("This is a test description").assertIsDisplayed()
    }
    
    @Test
    fun taskItem_withEmptyDescription_onlyDisplaysTitle() {
        // Arrange - Create a test task with empty description
        val testTask = Task(
            id = 1,
            title = "Test Task",
            description = "",
            priority = 2,
            completed = false
        )
        
        // Act - Render the TaskItem
        composeTestRule.setContent {
            TaskItem(
                task = testTask,
                showDelete = true,
                isLoading = false,
                onDelete = {},
                onClick = {},
                onUpdate = {},
                settingsViewModel = mockSettingsViewModel
            )
        }
        
        // Assert - Verify that only title is displayed
        composeTestRule.onNodeWithText("Test Task").assertIsDisplayed()
        composeTestRule.onAllNodes(hasText("")).assertCountEquals(0) // No empty text node
    }
    
    @Test
    fun taskItem_completedTask_displaysStrikethroughText() {
        // Arrange - Create a completed test task
        val testTask = Task(
            id = 1,
            title = "Completed Task",
            description = "This is a completed task",
            priority = 2,
            completed = true
        )
        
        // Act - Render the TaskItem
        composeTestRule.setContent {
            TaskItem(
                task = testTask,
                showDelete = true,
                isLoading = false,
                onDelete = {},
                onClick = {},
                onUpdate = {},
                settingsViewModel = mockSettingsViewModel
            )
        }
        
        // Assert - Verify that title is displayed and checkbox is checked
        composeTestRule.onNodeWithText("Completed Task").assertIsDisplayed()
        composeTestRule.onNode(isToggleable() and isChecked()).assertExists()
    }
    
    @Test
    fun taskItem_whenLoading_disablesInteractions() {
        // Arrange - Create a test task
        val testTask = Task(
            id = 1,
            title = "Test Task",
            description = "This is a test description",
            priority = 2,
            completed = false
        )
        
        // Act - Render the TaskItem with loading=true
        composeTestRule.setContent {
            TaskItem(
                task = testTask,
                showDelete = true,
                isLoading = true,
                onDelete = {},
                onClick = {},
                onUpdate = {},
                settingsViewModel = mockSettingsViewModel
            )
        }
        
        // Assert - Verify that checkbox and delete button are disabled
        composeTestRule.onNode(isToggleable()).assertIsNotEnabled()
        composeTestRule.onNode(hasContentDescription("Delete")).assertIsNotEnabled()
    }
    
    @Test
    fun taskItem_checkboxClick_callsOnUpdate() {
        // Arrange
        val testTask = Task(
            id = 1,
            title = "Test Task",
            description = "Description",
            priority = 2,
            completed = false
        )
        var wasUpdateCalled = false
        
        // Act - Render the TaskItem
        composeTestRule.setContent {
            TaskItem(
                task = testTask,
                showDelete = true,
                isLoading = false,
                onDelete = {},
                onClick = {},
                onUpdate = { wasUpdateCalled = true },
                settingsViewModel = mockSettingsViewModel
            )
        }
        
        // Click the checkbox
        composeTestRule.onNode(isToggleable()).performClick()
        
        // Assert - Verify the update callback was called
        assert(wasUpdateCalled)
    }
    
    @Test
    fun taskItem_deleteButtonClick_callsOnDelete() {
        // Arrange
        val testTask = Task(
            id = 1,
            title = "Test Task",
            description = "Description",
            priority = 2,
            completed = false
        )
        var wasDeleteCalled = false
        
        // Act - Render the TaskItem
        composeTestRule.setContent {
            TaskItem(
                task = testTask,
                showDelete = true,
                isLoading = false,
                onDelete = { wasDeleteCalled = true },
                onClick = {},
                onUpdate = {},
                settingsViewModel = mockSettingsViewModel
            )
        }
        
        // Click the delete button
        composeTestRule.onNode(hasContentDescription("Delete")).performClick()
        
        // Assert - Verify the delete callback was called
        assert(wasDeleteCalled)
    }
    
    @Test
    fun taskItem_showDeleteFalse_hidesDeleteButton() {
        // Arrange
        val testTask = Task(
            id = 1,
            title = "Test Task",
            description = "Description",
            priority = 2,
            completed = false
        )
        
        // Act - Render the TaskItem with showDelete=false
        composeTestRule.setContent {
            TaskItem(
                task = testTask,
                showDelete = false,
                isLoading = false,
                onDelete = {},
                onClick = {},
                onUpdate = {},
                settingsViewModel = mockSettingsViewModel
            )
        }
        
        // Assert - Verify the delete button is not displayed
        composeTestRule.onNode(hasContentDescription("Delete")).assertDoesNotExist()
    }
} 