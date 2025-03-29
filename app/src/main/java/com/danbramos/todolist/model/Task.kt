package com.danbramos.todolist.model

data class Task(
    val id: Long? = null,
    val title: String,
    val description: String,
    val priority: Int = Priority.LOW.value,
    val completed: Boolean = false
)