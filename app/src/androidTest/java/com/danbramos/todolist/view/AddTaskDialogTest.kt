package com.danbramos.todolist.view

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.danbramos.todolist.model.Priority
import com.danbramos.todolist.model.Task
import com.danbramos.todolist.viewmodel.SettingsViewModel
import com.danbramos.todolist.viewmodel.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

class AddTaskDialogTest {

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
    fun addTaskDialog_initialState_hasEmptyFieldsAndDisabledButton() {
        // Act - Render the dialog
        composeTestRule.setContent {
            AddTaskDialog(
                onDismiss = {},
                onAddTask = {},
                settingsViewModel = mockSettingsViewModel
            )
        }
        
        // Assert - Verify initial state
        composeTestRule.onNodeWithText("Add New Task").assertIsDisplayed()
        
        // Check that text fields are empty
        composeTestRule.onNodeWithText("Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Description").assertIsDisplayed()
        
        // Add button should be disabled initially (since title is empty)
        composeTestRule.onNodeWithText("Add Task").assertIsNotEnabled()
    }
    
    @Test
    fun addTaskDialog_enteringTitle_enablesAddButton() {
        // Act - Render the dialog
        composeTestRule.setContent {
            AddTaskDialog(
                onDismiss = {},
                onAddTask = {},
                settingsViewModel = mockSettingsViewModel
            )
        }
        
        // Enter text in title field
        composeTestRule.onNodeWithText("Title").performTextInput("Test Task")
        
        // Assert - Add button should now be enabled
        composeTestRule.onNodeWithText("Add Task").assertIsEnabled()
    }
    
    @Test
    fun addTaskDialog_clickAddButton_callsOnAddTaskWithCorrectData() {
        // Arrange
        var capturedTask: Task? = null
        
        // Act - Render the dialog
        composeTestRule.setContent {
            AddTaskDialog(
                onDismiss = {},
                onAddTask = { task -> capturedTask = task },
                settingsViewModel = mockSettingsViewModel
            )
        }
        
        // Enter data in the form
        composeTestRule.onNodeWithText("Title").performTextInput("Test Task")
        composeTestRule.onNodeWithText("Description").performTextInput("Test Description")
        
        // Change priority from default (LOW) to HIGH
        composeTestRule.onNodeWithText("HIGH").performClick()
        
        // Click Add button
        composeTestRule.onNodeWithText("Add Task").performClick()
        
        // Assert - Verify the task was created with correct data
        assert(capturedTask != null)
        capturedTask?.let {
            assert(it.title == "Test Task") 
            assert(it.description == "Test Description")
            assert(it.priority == Priority.HIGH.value)
        }
    }
    
    @Test
    fun addTaskDialog_clickCancel_callsOnDismiss() {
        // Arrange
        var dismissCalled = false
        
        // Act - Render the dialog
        composeTestRule.setContent {
            AddTaskDialog(
                onDismiss = { dismissCalled = true },
                onAddTask = {},
                settingsViewModel = mockSettingsViewModel
            )
        }
        
        // Click cancel button
        composeTestRule.onNodeWithText("Cancel").performClick()
        
        // Assert - Verify onDismiss was called
        assert(dismissCalled)
    }
    
    @Test
    fun addTaskDialog_prioritySelection_updatesSelectedPriority() {
        // Act - Render the dialog
        composeTestRule.setContent {
            AddTaskDialog(
                onDismiss = {},
                onAddTask = {},
                settingsViewModel = mockSettingsViewModel
            )
        }
        
        // By default, LOW should be selected
        composeTestRule.onNode(hasText("LOW") and isSelected()).assertExists()
        
        // Click on MED priority
        composeTestRule.onNodeWithText("MED").performClick()
        
        // MED should now be selected and LOW not selected
        composeTestRule.onNode(hasText("MED") and isSelected()).assertExists()
        composeTestRule.onNode(hasText("LOW") and isNotSelected()).assertExists()
    }
} 