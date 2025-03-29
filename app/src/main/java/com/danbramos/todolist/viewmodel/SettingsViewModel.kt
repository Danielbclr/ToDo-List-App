package com.danbramos.todolist.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import java.lang.ref.WeakReference

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

enum class LanguageMode {
    SYSTEM, ENGLISH, PORTUGUESE
}

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    
    // Hold a weak reference to the activity
    private var activityRef: WeakReference<Activity>? = null
    
    // Theme mode state flow
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode
    
    // Language mode state flow
    private val _languageMode = MutableStateFlow(LanguageMode.SYSTEM)
    val languageMode: StateFlow<LanguageMode> = _languageMode

    init {
        // Now set the initial values in the init block after sharedPreferences is initialized
        _themeMode.value = getThemePreference()
        _languageMode.value = getLanguagePreference()
    }
    
    fun setActivity(activity: Activity) {
        activityRef = WeakReference(activity)
    }
    
    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        saveThemePreference(mode)
    }
    
    fun setLanguageMode(mode: LanguageMode) {
        if (_languageMode.value != mode) {
            _languageMode.value = mode
            saveLanguagePreference(mode)
            
            // Get the activity to apply language change
            val activity = activityRef?.get() ?: return
            
            // Apply the new language
            updateResources(activity, mode)
            
            // Restart the activity to apply changes
            val intent = activity.intent
            activity.finish()
            activity.startActivity(intent)
        }
    }

    private fun getThemePreference(): ThemeMode {
        val themeModeString = sharedPreferences.getString("theme_mode", ThemeMode.SYSTEM.name)
        return try {
            ThemeMode.valueOf(themeModeString!!)
        } catch (e: Exception) {
            ThemeMode.SYSTEM // Default to system theme if there's an error
        }
    }

    private fun saveThemePreference(mode: ThemeMode) {
        viewModelScope.launch {
            with(sharedPreferences.edit()) {
                putString("theme_mode", mode.name)
                apply()
            }
        }
    }
    
    private fun getLanguagePreference(): LanguageMode {
        val languageModeString = sharedPreferences.getString("language_mode", LanguageMode.SYSTEM.name)
        return try {
            LanguageMode.valueOf(languageModeString!!)
        } catch (e: Exception) {
            LanguageMode.SYSTEM // Default to system language if there's an error
        }
    }
    
    private fun saveLanguagePreference(mode: LanguageMode) {
        viewModelScope.launch {
            with(sharedPreferences.edit()) {
                putString("language_mode", mode.name)
                apply()
            }
        }
    }
    
    private fun updateResources(context: Context, mode: LanguageMode) {
        val locale = when (mode) {
            LanguageMode.ENGLISH -> Locale("en")
            LanguageMode.PORTUGUESE -> Locale("pt")
            LanguageMode.SYSTEM -> Locale.getDefault()
        }
        
        try {
            // Set locale directly on the context's resources configuration
            val resources = context.resources
            val configuration = Configuration(resources.configuration)
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLocale(locale)
                val localeList = LocaleList(locale)
                LocaleList.setDefault(localeList)
                configuration.setLocales(localeList)
            } else {
                configuration.locale = locale
                Locale.setDefault(locale)
            }
            
            resources.updateConfiguration(configuration, resources.displayMetrics)
            
            // Also use the AppCompatDelegate method for compatibility
            if (mode != LanguageMode.SYSTEM) {
                val localeList = LocaleListCompat.create(locale)
                AppCompatDelegate.setApplicationLocales(localeList)
            } else {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    // Call this method during app startup to set the saved language
    fun applyInitialLanguage() {
        try {
            val activity = activityRef?.get() ?: return
            updateResources(activity, _languageMode.value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}