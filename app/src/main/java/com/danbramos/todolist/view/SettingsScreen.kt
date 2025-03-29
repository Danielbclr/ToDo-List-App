package com.danbramos.todolist.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.danbramos.todolist.R
import com.danbramos.todolist.ui.theme.*
import com.danbramos.todolist.viewmodel.LanguageMode
import com.danbramos.todolist.viewmodel.SettingsViewModel
import com.danbramos.todolist.viewmodel.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    val themeMode by settingsViewModel.themeMode.collectAsState()
    val languageMode by settingsViewModel.languageMode.collectAsState()
    
    // Determine if dark theme should be used based on settings
    val isDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    
    // Theme-specific colors
    val backgroundColor = if (isDarkTheme) DarkModeBg else LightModeBg
    val surfaceColor = if (isDarkTheme) DarkModeSurface else LightModeSurface
    val primaryColor = if (isDarkTheme) fabBackgroundDark else fabBackgroundLight
    val radioButtonColor = if (isDarkTheme) lowPriorityColorDark else lowPriorityColorLight
    
    // Handle system back button
    BackHandler {
        onBackClick()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Theme Section
            Text(
                text = stringResource(R.string.theme),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp),
                color = MaterialTheme.colorScheme.onBackground
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = surfaceColor
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .selectableGroup()
                        .padding(16.dp)
                ) {
                    ThemeOption(
                        text = stringResource(R.string.light_theme),
                        selected = themeMode == ThemeMode.LIGHT,
                        onClick = { settingsViewModel.setThemeMode(ThemeMode.LIGHT) },
                        color = radioButtonColor,
                        isDarkTheme = isDarkTheme
                    )
                    
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                    
                    ThemeOption(
                        text = stringResource(R.string.dark_theme),
                        selected = themeMode == ThemeMode.DARK,
                        onClick = { settingsViewModel.setThemeMode(ThemeMode.DARK) },
                        color = radioButtonColor,
                        isDarkTheme = isDarkTheme
                    )
                    
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                    
                    ThemeOption(
                        text = stringResource(R.string.system_default),
                        selected = themeMode == ThemeMode.SYSTEM,
                        onClick = { settingsViewModel.setThemeMode(ThemeMode.SYSTEM) },
                        color = radioButtonColor,
                        isDarkTheme = isDarkTheme
                    )
                }
            }
            
            // Language Section
            Text(
                text = stringResource(R.string.language),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = surfaceColor
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .selectableGroup()
                        .padding(16.dp)
                ) {
                    LanguageOption(
                        text = stringResource(R.string.system_language),
                        selected = languageMode == LanguageMode.SYSTEM,
                        onClick = { settingsViewModel.setLanguageMode(LanguageMode.SYSTEM) },
                        color = radioButtonColor,
                        isDarkTheme = isDarkTheme
                    )
                    
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                    
                    LanguageOption(
                        text = stringResource(R.string.language_english),
                        selected = languageMode == LanguageMode.ENGLISH,
                        onClick = { settingsViewModel.setLanguageMode(LanguageMode.ENGLISH) },
                        color = radioButtonColor,
                        isDarkTheme = isDarkTheme
                    )
                    
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                    
                    LanguageOption(
                        text = stringResource(R.string.language_portuguese),
                        selected = languageMode == LanguageMode.PORTUGUESE,
                        onClick = { settingsViewModel.setLanguageMode(LanguageMode.PORTUGUESE) },
                        color = radioButtonColor,
                        isDarkTheme = isDarkTheme
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    color: Color,
    isDarkTheme: Boolean
) {
    val textColor = MaterialTheme.colorScheme.onSurface
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(MaterialTheme.shapes.small)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null, // null because the parent row handles the click
            colors = RadioButtonDefaults.colors(
                selectedColor = color,
                unselectedColor = textColor.copy(alpha = 0.6f)
            )
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp),
            color = textColor
        )
    }
}

@Composable
private fun LanguageOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    color: Color,
    isDarkTheme: Boolean
) {
    val textColor = MaterialTheme.colorScheme.onSurface
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(MaterialTheme.shapes.small)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null, // null because the parent row handles the click
            colors = RadioButtonDefaults.colors(
                selectedColor = color,
                unselectedColor = textColor.copy(alpha = 0.6f)
            )
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp),
            color = textColor
        )
    }
}