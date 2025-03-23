package com.danbramos.todolist.model

data class Task(
    val id: Long? = null,
    val title: String,
    val description: String,
    val completed: Boolean = false
)