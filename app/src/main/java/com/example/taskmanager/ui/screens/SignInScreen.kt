package com.example.taskmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.taskmanager.ui.theme.CyanAccent
import com.example.taskmanager.ui.theme.DarkCard
import com.example.taskmanager.ui.theme.DarkSurfaceVariant
import com.example.taskmanager.ui.theme.NeonPink
import com.example.taskmanager.ui.theme.PurpleAccent
import com.example.taskmanager.ui.theme.PurpleDark
import com.example.taskmanager.ui.theme.TextDimGrey
import com.example.taskmanager.ui.theme.TextGrey
import com.example.taskmanager.viewmodel.AuthViewModel

@Composable
fun SignInScreen(
    authViewModel: AuthViewModel,
    onSignInSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    val authError by authViewModel.authError.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            onSignInSuccess()
        }
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
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // App Icon
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(color = DarkCard, shape = CircleShape)
                .border(width = 2.dp, color = PurpleAccent, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.TaskAlt,
                contentDescription = "App Icon",
                tint = CyanAccent,
                modifier = Modifier.size(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Sign in to continue managing your tasks",
            style = MaterialTheme.typography.bodyMedium,
            color = TextGrey,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; localError = null; authViewModel.clearError() },
            label = { Text("Email") },
            placeholder = { Text("Enter your email", color = TextDimGrey) },
            leadingIcon = {
                Icon(Icons.Filled.Email, contentDescription = null, tint = PurpleAccent)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; localError = null; authViewModel.clearError() },
            label = { Text("Password") },
            placeholder = { Text("Enter your password", color = TextDimGrey) },
            leadingIcon = {
                Icon(Icons.Filled.Lock, contentDescription = null, tint = PurpleAccent)
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle password",
                        tint = TextDimGrey
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation()
        )

        // Error message
        val displayError = localError ?: authError
        if (displayError != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = displayError,
                color = NeonPink,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Sign In button
        Button(
            onClick = {
                when {
                    email.isBlank() || password.isBlank() -> {
                        localError = "Please fill in all fields"
                    }
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> {
                        localError = "Please enter a valid email address"
                    }
                    else -> {
                        localError = null
                        authViewModel.signIn(email.trim(), password)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PurpleDark),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Navigate to Sign Up
        TextButton(onClick = onNavigateToSignUp) {
            Text(
                text = "Don't have an account? ",
                color = TextGrey,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Sign Up",
                color = CyanAccent,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
