package com.danbramos.todolist.model

import com.danbramos.todolist.R

enum class Priority(val value: Int, val labelResId: Int) {
    LOW(1, R.string.priority_low),
    MED(2, R.string.priority_medium),
    HIGH(3, R.string.priority_high),
    TOP(4, R.string.priority_top);
    companion object {
        fun fromInt(value: Int): Priority? {
            return entries.find { it.value == value }
        }
    }
}