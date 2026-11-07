package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.RoutineViewModel
import com.example.ui.screens.*
import com.example.ui.theme.BoldBlackNav
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SoftBackground

enum class Screen(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    TIMETABLE("Today", Icons.Default.CalendarToday),
    SUBJECTS("Subjects", Icons.Default.AutoStories),
    TEACHERS("Teachers", Icons.Default.People),
    PROFILE("Profile", Icons.Default.Person)
}

class MainActivity : ComponentActivity() {

    private val viewModel: RoutineViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                var currentScreen by remember { mutableStateOf(Screen.HOME) }

                // Handle back button to return to HOME if on other screens
                BackHandler(enabled = currentScreen != Screen.HOME) {
                    currentScreen = Screen.HOME
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SoftBackground),
                    contentWindowInsets = WindowInsets.statusBars,
                    bottomBar = {
                        BoldBlackBottomNavigation(
                            currentScreen = currentScreen,
                            onSelectScreen = { currentScreen = it }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(SoftBackground)
                    ) {
                        when (currentScreen) {
                            Screen.HOME -> HomeScreen(
                                viewModel = viewModel,
                                onNavigateToTimetable = { currentScreen = Screen.TIMETABLE },
                                onNavigateToSubjects = { currentScreen = Screen.SUBJECTS },
                                onNavigateToTeachers = { currentScreen = Screen.TEACHERS },
                                onNavigateToProfile = { currentScreen = Screen.PROFILE }
                            )
                            Screen.TIMETABLE -> TimetableScreen(viewModel = viewModel)
                            Screen.SUBJECTS -> SubjectsScreen(viewModel = viewModel)
                            Screen.TEACHERS -> TeachersScreen(viewModel = viewModel)
                            Screen.PROFILE -> ProfileScreen(
                                viewModel = viewModel,
                                onNavigateToSubjects = { currentScreen = Screen.SUBJECTS },
                                onNavigateToTimetable = { currentScreen = Screen.TIMETABLE }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BoldBlackBottomNavigation(
    currentScreen: Screen,
    onSelectScreen: (Screen) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("main_bottom_nav"),
        shape = RoundedCornerShape(28.dp),
        color = BoldBlackNav,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Screen.values().forEach { screen ->
                val isSelected = currentScreen == screen
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onSelectScreen(screen) }
                        .background(if (isSelected) Color(0xFF27272A) else Color.Transparent)
                        .padding(horizontal = if (isSelected) 12.dp else 10.dp, vertical = 8.dp)
                        .testTag("nav_item_${screen.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.title,
                            tint = if (isSelected) Color.White else Color(0xFF9CA3AF),
                            modifier = Modifier.size(20.dp)
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = screen.title,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
