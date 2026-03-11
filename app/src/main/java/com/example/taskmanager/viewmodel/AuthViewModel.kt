package com.example.taskmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.TaskRepository
import com.example.taskmanager.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Track session start time
    private var _sessionStartTime: Long = 0L
    val sessionStartTime: Long get() = _sessionStartTime

    val isLoggedIn: Boolean get() = _currentUser.value != null

    // Reactive user data (refreshes from DB)
    val userData: StateFlow<User?> = _currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getUserById(user.userId)
            else flowOf(null)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun signUp(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null
            val result = repository.registerUser(
                User(fullName = fullName, email = email, password = password)
            )
            result.onSuccess { user ->
                _currentUser.value = user
                _sessionStartTime = System.currentTimeMillis()
            }.onFailure { e ->
                _authError.value = e.message ?: "Registration failed"
            }
            _isLoading.value = false
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null
            val result = repository.loginUser(email, password)
            result.onSuccess { user ->
                _currentUser.value = user
                _sessionStartTime = System.currentTimeMillis()
            }.onFailure { e ->
                _authError.value = e.message ?: "Login failed"
            }
            _isLoading.value = false
        }
    }

    fun logout() {
        _currentUser.value = null
        _sessionStartTime = 0L
    }

    fun clearError() {
        _authError.value = null
    }
}
