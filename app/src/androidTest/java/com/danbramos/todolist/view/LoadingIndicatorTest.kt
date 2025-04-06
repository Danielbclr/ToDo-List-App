package com.danbramos.todolist.view

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import org.junit.Rule
import org.junit.Test

class LoadingIndicatorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loadingIndicator_displaysCircularProgressIndicator() {
        // Act - Render the LoadingIndicator
        composeTestRule.setContent {
            LoadingIndicator()
        }
        
        // Assert - Verify that a CircularProgressIndicator is displayed
        // CircularProgressIndicator has a default contentDescription of "Loading"
        composeTestRule.onNodeWithContentDescription("Loading", useUnmergedTree = true).assertExists()
    }
} 