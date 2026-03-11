package com.example.taskmanager.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.taskmanager.data.Task
import com.example.taskmanager.ui.theme.CyanAccent
import com.example.taskmanager.ui.theme.DarkCard
import com.example.taskmanager.ui.theme.DarkSurfaceVariant
import com.example.taskmanager.ui.theme.PurpleAccent
import com.example.taskmanager.ui.theme.PurpleDark
import com.example.taskmanager.ui.theme.TextDimGrey
import com.example.taskmanager.ui.theme.TextGrey
import com.example.taskmanager.viewmodel.TaskViewModel
import java.util.Calendar

@Composable
fun AddEditTaskScreen(
    viewModel: TaskViewModel,
    taskId: Int? = null,
    onTaskSaved: () -> Unit
) {
    val isEditing = taskId != null && taskId != -1

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var existingTask by remember { mutableStateOf<Task?>(null) }

    // Load existing task data if editing
    if (isEditing) {
        val task by viewModel.getTaskById(taskId!!).collectAsState()
        LaunchedEffect(task) {
            task?.let {
                if (existingTask == null) {
                    title = it.title
                    description = it.description
                    dueDate = it.dueDate
                    existingTask = it
                }
            }
        }
    }

    // Date picker
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                dueDate = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PurpleAccent,
        unfocusedBorderColor = DarkSurfaceVariant,
        focusedLabelColor = PurpleAccent,
        unfocusedLabelColor = TextDimGrey,
        cursorColor = PurpleAccent,
        focusedTextColor = Color.White,
        unfocusedTextColor = TextGrey,
        focusedContainerColor = DarkCard,
        unfocusedContainerColor = DarkCard
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isEditing) "Edit Task" else "Create New Task",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isEditing) "Update the details below" else "Fill in the details to add a new task",
            style = MaterialTheme.typography.bodyMedium,
            color = TextGrey
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Title field
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Task Title") },
            placeholder = { Text("Enter task title", color = TextDimGrey) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description field
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            placeholder = { Text("Describe your task...", color = TextDimGrey) },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Due date field
        OutlinedTextField(
            value = dueDate,
            onValueChange = {},
            label = { Text("Due Date") },
            placeholder = { Text("Select due date", color = TextDimGrey) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { datePickerDialog.show() },
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            readOnly = true,
            enabled = false,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = "Pick date",
                    tint = PurpleAccent,
                    modifier = Modifier.clickable { datePickerDialog.show() }
                )
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Save button with gradient
        Button(
            onClick = {
                if (title.isNotBlank() && description.isNotBlank() && dueDate.isNotBlank()) {
                    if (isEditing && existingTask != null) {
                        viewModel.updateTask(
                            existingTask!!.copy(
                                title = title,
                                description = description,
                                dueDate = dueDate
                            )
                        )
                    } else {
                        viewModel.addTask(title, description, dueDate)
                    }
                    onTaskSaved()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PurpleDark
            ),
            enabled = title.isNotBlank() && description.isNotBlank() && dueDate.isNotBlank()
        ) {
            Text(
                text = if (isEditing) "Update Task" else "Save Task",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
