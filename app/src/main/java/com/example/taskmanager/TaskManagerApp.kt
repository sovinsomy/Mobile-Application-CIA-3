package com.example.taskmanager

import android.app.Application
import com.example.taskmanager.data.TaskDatabase
import com.example.taskmanager.data.TaskRepository

class TaskManagerApp : Application() {

    val database by lazy { TaskDatabase.getDatabase(this) }
    val repository by lazy { TaskRepository(database.taskDao(), database.userDao()) }
}
