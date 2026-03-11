package com.example.taskmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.taskmanager.navigation.Screen
import com.example.taskmanager.ui.screens.AddEditTaskScreen
import com.example.taskmanager.ui.screens.ManageScreen
import com.example.taskmanager.ui.screens.ProfileScreen
import com.example.taskmanager.ui.screens.SignInScreen
import com.example.taskmanager.ui.screens.SignUpScreen
import com.example.taskmanager.ui.screens.SplashScreen
import com.example.taskmanager.ui.screens.TaskListScreen
import com.example.taskmanager.ui.theme.CyanAccent
import com.example.taskmanager.ui.theme.DarkBackground
import com.example.taskmanager.ui.theme.DarkCard
import com.example.taskmanager.ui.theme.DarkSurface
import com.example.taskmanager.ui.theme.NeonPink
import com.example.taskmanager.ui.theme.PurpleAccent
import com.example.taskmanager.ui.theme.PurpleDark
import com.example.taskmanager.ui.theme.TaskManagerTheme
import com.example.taskmanager.ui.theme.TextDimGrey
import com.example.taskmanager.ui.theme.TextGrey
import com.example.taskmanager.viewmodel.AuthViewModel
import com.example.taskmanager.viewmodel.TaskViewModel
import com.example.taskmanager.viewmodel.TaskViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskManagerTheme {
                val app = application as TaskManagerApp
                val factory = TaskViewModelFactory(app.repository)
                val taskViewModel: TaskViewModel = viewModel(factory = factory)
                val authViewModel: AuthViewModel = viewModel(factory = factory)
                TaskManagerMainScreen(
                    taskViewModel = taskViewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }
}

data class BottomNavItem(
    val label: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskManagerMainScreen(
    taskViewModel: TaskViewModel,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentUser by authViewModel.currentUser.collectAsState()

    // Sync userId to TaskViewModel whenever user changes
    LaunchedEffect(currentUser) {
        taskViewModel.setUserId(currentUser?.userId ?: 0)
    }

    var showSettingsMenu by remember { mutableStateOf(false) }

    val bottomNavItems = listOf(
        BottomNavItem(
            label = "Tasks",
            route = Screen.TaskList.route,
            selectedIcon = Icons.Filled.TaskAlt,
            unselectedIcon = Icons.Outlined.TaskAlt
        ),
        BottomNavItem(
            label = "Manage",
            route = Screen.Manage.route,
            selectedIcon = Icons.Filled.Checklist,
            unselectedIcon = Icons.Outlined.Checklist
        )
    )

    // Auth screens don't show bottom nav or FAB
    val authScreens = listOf(Screen.SignIn.route, Screen.SignUp.route)
    val isAuthScreen = currentRoute in authScreens
    val isMainScreen = currentRoute in listOf(Screen.TaskList.route, Screen.Manage.route)
    val isSplashScreen = currentRoute == Screen.Splash.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            // TopBar visible across ALL screens except Splash
            if (!isSplashScreen) {
                TopAppBar(
                title = {
                    Text(
                        text = "Task Manager",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    if (currentRoute == Screen.Profile.route || currentRoute == Screen.AddTask.route || currentRoute?.startsWith("edit_task") == true) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                },
                actions = {
                    if (!isAuthScreen) {
                        Box {
                            IconButton(onClick = { showSettingsMenu = true }) {
                                Icon(
                                    imageVector = Icons.Filled.Settings,
                                    contentDescription = "Settings",
                                    tint = PurpleAccent
                                )
                            }
                            DropdownMenu(
                                expanded = showSettingsMenu,
                                onDismissRequest = { showSettingsMenu = false },
                                containerColor = DarkCard
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text("Profile", color = Color.White)
                                    },
                                    onClick = {
                                        showSettingsMenu = false
                                        navController.navigate(Screen.Profile.route) {
                                            launchSingleTop = true
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Outlined.Person,
                                            contentDescription = null,
                                            tint = CyanAccent
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text("Logout", color = NeonPink)
                                    },
                                    onClick = {
                                        showSettingsMenu = false
                                        authViewModel.logout()
                                        navController.navigate(Screen.SignIn.route) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Outlined.Logout,
                                            contentDescription = null,
                                            tint = NeonPink
                                        )
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = Color.White
                )
            )
            }
        },
        bottomBar = {
            if (!isAuthScreen && !isSplashScreen && currentRoute != Screen.Profile.route) {
                NavigationBar(
                    containerColor = DarkSurface,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(Screen.TaskList.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PurpleAccent,
                                selectedTextColor = PurpleAccent,
                                unselectedIconColor = TextDimGrey,
                                unselectedTextColor = TextDimGrey,
                                indicatorColor = PurpleDark.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (isMainScreen) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.AddTask.route)
                    },
                    containerColor = PurpleDark,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = PurpleAccent.copy(alpha = 0.4f),
                        spotColor = PurpleAccent.copy(alpha = 0.4f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Task"
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            AppNavHost(
                navController = navController,
                taskViewModel = taskViewModel,
                authViewModel = authViewModel
            )
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    taskViewModel: TaskViewModel,
    authViewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    val isLoggedIn = authViewModel.currentUser.value != null
                    val destination = if (isLoggedIn) Screen.TaskList.route else Screen.SignIn.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Auth screens
        composable(Screen.SignIn.route) {
            SignInScreen(
                authViewModel = authViewModel,
                onSignInSuccess = {
                    navController.navigate(Screen.TaskList.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                authViewModel = authViewModel,
                onSignUpSuccess = {
                    navController.navigate(Screen.TaskList.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToSignIn = {
                    navController.popBackStack()
                }
            )
        }

        // Main screens
        composable(Screen.TaskList.route) {
            TaskListScreen(
                viewModel = taskViewModel,
                onEditTask = { taskId ->
                    navController.navigate(Screen.EditTask.createRoute(taskId))
                }
            )
        }

        composable(Screen.AddTask.route) {
            AddEditTaskScreen(
                viewModel = taskViewModel,
                taskId = null,
                onTaskSaved = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.EditTask.route,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId")
            AddEditTaskScreen(
                viewModel = taskViewModel,
                taskId = taskId,
                onTaskSaved = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Manage.route) {
            ManageScreen(viewModel = taskViewModel)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                authViewModel = authViewModel,
                taskViewModel = taskViewModel
            )
        }
    }
}