package com.danbramos.todolist.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.lang.ref.WeakReference
import java.util.Locale

@ExperimentalCoroutinesApi
class SettingsViewModelTest {

    // Test dispatcher for controlling coroutines during tests
    private val testDispatcher = StandardTestDispatcher()
    
    // Mock application
    private lateinit var mockApplication: Application
    
    // Mock SharedPreferences
    private lateinit var mockSharedPreferences: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor
    
    // Mock Activity
    private lateinit var mockActivity: Activity
    
    // Mock Resources
    private lateinit var mockResources: Resources
    private lateinit var mockConfiguration: Configuration
    
    // The view model under test
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        // Set the main dispatcher to our test dispatcher
        Dispatchers.setMain(testDispatcher)
        
        // Create mocks
        mockApplication = mockk(relaxed = true)
        mockSharedPreferences = mockk(relaxed = true)
        mockEditor = mockk(relaxed = true)
        mockActivity = mockk(relaxed = true)
        mockResources = mockk(relaxed = true)
        mockConfiguration = mockk(relaxed = true)
        
        // Setup SharedPreferences mock
        every { mockApplication.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) } returns mockSharedPreferences
        every { mockSharedPreferences.edit() } returns mockEditor
        every { mockEditor.putString(any(), any()) } returns mockEditor
        every { mockEditor.apply() } just Runs
        
        // Default SharedPreferences values
        every { mockSharedPreferences.getString("theme_mode", ThemeMode.SYSTEM.name) } returns ThemeMode.SYSTEM.name
        every { mockSharedPreferences.getString("language_mode", LanguageMode.SYSTEM.name) } returns LanguageMode.SYSTEM.name
        
        // Setup activity and resources
        every { mockActivity.resources } returns mockResources
        every { mockResources.configuration } returns mockConfiguration
        every { mockResources.updateConfiguration(any(), any()) } just Runs
        
        // Create the view model with mocked dependencies
        viewModel = SettingsViewModel(mockApplication)
    }

    @After
    fun cleanup() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun `initial theme mode is loaded from preferences`() = runTest {
        // Arrange
        every { mockSharedPreferences.getString("theme_mode", ThemeMode.SYSTEM.name) } returns ThemeMode.DARK.name
        
        // Act - Create new ViewModel to trigger init
        val viewModel = SettingsViewModel(mockApplication)
        
        // Assert
        assertEquals(ThemeMode.DARK, viewModel.themeMode.value)
    }
    
    @Test
    fun `initial language mode is loaded from preferences`() = runTest {
        // Arrange
        every { mockSharedPreferences.getString("language_mode", LanguageMode.SYSTEM.name) } returns LanguageMode.ENGLISH.name
        
        // Act - Create new ViewModel to trigger init
        val viewModel = SettingsViewModel(mockApplication)
        
        // Assert
        assertEquals(LanguageMode.ENGLISH, viewModel.languageMode.value)
    }
    
    @Test
    fun `setThemeMode updates state and saves to preferences`() = runTest {
        // Arrange
        val stringSlot = slot<String>()
        every { mockEditor.putString("theme_mode", capture(stringSlot)) } returns mockEditor
        
        // Act
        viewModel.setThemeMode(ThemeMode.LIGHT)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Assert
        assertEquals(ThemeMode.LIGHT, viewModel.themeMode.value)
        assertEquals(ThemeMode.LIGHT.name, stringSlot.captured)
        verify { mockEditor.apply() }
    }
    
    @Test
    fun `setLanguageMode updates state and saves to preferences`() = runTest {
        // Arrange
        val stringSlot = slot<String>()
        every { mockEditor.putString("language_mode", capture(stringSlot)) } returns mockEditor
        viewModel.setActivity(mockActivity)
        
        // Mock configuration update
        every { mockActivity.intent } returns mockk(relaxed = true)
        every { mockActivity.finish() } just Runs
        every { mockActivity.startActivity(any()) } just Runs
        
        // Act
        viewModel.setLanguageMode(LanguageMode.ENGLISH)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Assert
        assertEquals(LanguageMode.ENGLISH, viewModel.languageMode.value)
        assertEquals(LanguageMode.ENGLISH.name, stringSlot.captured)
        verify { mockEditor.apply() }
    }

    @Test
    fun `setLanguageMode restarts activity to apply changes`() = runTest {
        // Arrange
        viewModel.setActivity(mockActivity)
        every { mockActivity.intent } returns mockk(relaxed = true)
        every { mockActivity.finish() } just Runs
        every { mockActivity.startActivity(any()) } just Runs
        
        // Act
        viewModel.setLanguageMode(LanguageMode.PORTUGUESE)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Assert
        verify { mockActivity.finish() }
        verify { mockActivity.startActivity(any()) }
    }
    
    @Test
    fun `setLanguageMode does nothing if language hasn't changed`() = runTest {
        // Arrange
        // Initial value is already SYSTEM
        viewModel.setActivity(mockActivity)
        
        // Act
        viewModel.setLanguageMode(LanguageMode.SYSTEM)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Assert - verify activity was not restarted
        verify(exactly = 0) { mockActivity.finish() }
        verify(exactly = 0) { mockActivity.startActivity(any()) }
    }
    
    @Test
    fun `getThemePreference returns default if preference is invalid`() {
        // Arrange
        every { mockSharedPreferences.getString("theme_mode", ThemeMode.SYSTEM.name) } returns "INVALID_THEME"
        
        // Act - Create new ViewModel to trigger init with the invalid preference
        val viewModel = SettingsViewModel(mockApplication)
        
        // Assert
        assertEquals(ThemeMode.SYSTEM, viewModel.themeMode.value)
    }
    
    @Test
    fun `getLanguagePreference returns default if preference is invalid`() {
        // Arrange
        every { mockSharedPreferences.getString("language_mode", LanguageMode.SYSTEM.name) } returns "INVALID_LANGUAGE"
        
        // Act - Create new ViewModel to trigger init with the invalid preference
        val viewModel = SettingsViewModel(mockApplication)
        
        // Assert
        assertEquals(LanguageMode.SYSTEM, viewModel.languageMode.value)
    }
    
    @Test
    fun `applyInitialLanguage updates resources with current language mode`() {
        // Arrange
        viewModel.setActivity(mockActivity)
        
        // Act
        viewModel.applyInitialLanguage()
        
        // Assert
        verify { mockResources.updateConfiguration(any(), any()) }
    }
    
    @Test
    fun `setActivity correctly sets activity reference`() = runTest {
        // Arrange
        viewModel.setActivity(mockActivity)
        
        // Use reflection to verify the activity reference was set
        val field = SettingsViewModel::class.java.getDeclaredField("activityRef")
        field.isAccessible = true
        val activityRef = field.get(viewModel) as WeakReference<*>
        
        // Assert
        assertEquals(mockActivity, activityRef.get())
    }
} 