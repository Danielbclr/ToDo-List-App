package com.danbramos.todolist.view

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.danbramos.todolist.R
import org.junit.Rule
import org.junit.Test

class EmptyStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyState_displaysNoTasksYetMessage() {
        // Act - Render the EmptyState
        composeTestRule.setContent {
            EmptyState()
        }
        
        // Assert - Verify that the "No tasks yet" message is displayed
        // Note: This assumes that R.string.no_tasks_yet exists and contains "No tasks yet"
        // We're using hardcoded string for test simplicity
        composeTestRule.onNodeWithText("No tasks yet").assertIsDisplayed()
    }
} 