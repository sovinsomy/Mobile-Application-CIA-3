package com.example.taskmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.Task
import com.example.taskmanager.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _currentUserId = MutableStateFlow(0)

    fun setUserId(userId: Int) {
        _currentUserId.value = userId
    }

    val allTasks: StateFlow<List<Task>> = _currentUserId
        .flatMapLatest { userId ->
            if (userId > 0) repository.allTasksForUser(userId)
            else flowOf(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val taskCount: StateFlow<Int> = _currentUserId
        .flatMapLatest { userId ->
            if (userId > 0) repository.getTaskCountForUser(userId)
            else flowOf(0)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    fun getTaskById(taskId: Int) = repository.getTaskById(taskId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun addTask(title: String, description: String, dueDate: String) {
        viewModelScope.launch {
            repository.insert(
                Task(
                    userId = _currentUserId.value,
                    title = title,
                    description = description,
                    dueDate = dueDate
                )
            )
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.update(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.delete(task)
        }
    }
}
