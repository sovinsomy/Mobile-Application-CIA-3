package com.example.taskmanager.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao, private val userDao: UserDao) {

    // ── Task operations ──
    fun allTasksForUser(userId: Int): Flow<List<Task>> = taskDao.getAllTasksForUser(userId)

    fun getTaskById(taskId: Int): Flow<Task?> = taskDao.getTaskById(taskId)

    fun getTaskCountForUser(userId: Int): Flow<Int> = taskDao.getTaskCountForUser(userId)

    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun update(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun delete(task: Task) {
        taskDao.deleteTask(task)
    }

    // ── User / Auth operations ──
    suspend fun registerUser(user: User): Result<User> {
        return try {
            val existing = userDao.getUserByEmail(user.email)
            if (existing != null) {
                Result.failure(Exception("Email already registered"))
            } else {
                val id = userDao.insertUser(user)
                Result.success(user.copy(userId = id.toInt()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val user = userDao.loginUser(email, password)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Invalid email or password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUserById(userId: Int): Flow<User?> = userDao.getUserById(userId)
}
