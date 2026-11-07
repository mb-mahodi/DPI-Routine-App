package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClassSession
import com.example.ui.RoutineFilter
import com.example.ui.RoutineViewModel
import com.example.ui.theme.AmberPractical
import com.example.ui.theme.GreenTheory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyRoutineScreen(
    viewModel: RoutineViewModel
) {
    val selectedDay by viewModel.selectedDay.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterType by viewModel.filterType.collectAsState()
    val filteredClasses by viewModel.filteredClasses.collectAsState()
    val attendance by viewModel.attendance.collectAsState()

    val days = listOf(
        Pair("Sunday", "রবি"),
        Pair("Monday", "সোম"),
        Pair("Tuesday", "মঙ্গল"),
        Pair("Wednesday", "বুধ"),
        Pair("Thursday", "বৃহস্পতি"),
        Pair("Saturday", "শনি")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("weekly_routine_screen")
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("routine_search_input"),
            placeholder = { Text("Search by subject, teacher, room, or code...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Day Selector Tabs
        ScrollableTabRow(
            selectedTabIndex = days.indexOfFirst { it.first == selectedDay }.coerceAtLeast(0),
            edgePadding = 16.dp,
            divider = {},
            containerColor = Color.Transparent
        ) {
            days.forEach { (dayEn, dayBn) ->
                val isSelected = selectedDay.equals(dayEn, ignoreCase = true)
                Tab(
                    selected = isSelected,
                    onClick = { viewModel.selectDay(dayEn) },
                    modifier = Modifier.testTag("tab_day_$dayEn")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = dayEn,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = dayBn,
                            fontSize = 11.sp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        // Filter chips (All, Theory Only, Practical Only)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filterType == RoutineFilter.ALL,
                onClick = { viewModel.setFilterType(RoutineFilter.ALL) },
                label = { Text("All Classes") },
                leadingIcon = if (filterType == RoutineFilter.ALL) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null,
                modifier = Modifier.testTag("filter_all_chip")
            )

            FilterChip(
                selected = filterType == RoutineFilter.THEORY_ONLY,
                onClick = { viewModel.setFilterType(RoutineFilter.THEORY_ONLY) },
                label = { Text("Theory Only") },
                leadingIcon = if (filterType == RoutineFilter.THEORY_ONLY) {
                    { Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GreenTheory.copy(alpha = 0.2f),
                    selectedLabelColor = GreenTheory
                ),
                modifier = Modifier.testTag("filter_theory_chip")
            )

            FilterChip(
                selected = filterType == RoutineFilter.PRACTICAL_ONLY,
                onClick = { viewModel.setFilterType(RoutineFilter.PRACTICAL_ONLY) },
                label = { Text("Practical Lab Only") },
                leadingIcon = if (filterType == RoutineFilter.PRACTICAL_ONLY) {
                    { Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AmberPractical.copy(alpha = 0.2f),
                    selectedLabelColor = AmberPractical
                ),
                modifier = Modifier.testTag("filter_practical_chip")
            )
        }

        // Routine list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("weekly_routine_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$selectedDay Schedule",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "${filteredClasses.size} Period(s)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (filteredClasses.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = if (selectedDay == "Saturday") Icons.Default.Mosque else Icons.Default.EventBusy,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (selectedDay == "Saturday") "Saturday Prayer Break / Off Day" else "No classes match your filter",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (selectedDay == "Saturday") "No academic periods scheduled in the routine for Saturday." else "Try clearing your search query or selecting a different filter.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredClasses) { session ->
                    ClassSessionCard(
                        session = session,
                        attendanceRecord = attendance[session.subjectCode],
                        onMarkAttendance = { present ->
                            viewModel.markAttendance(session.subjectCode, present)
                        }
                    )
                }
            }

            // Routine timing guidance footer
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Standard Period Timings",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• Periods 1-4: 08:45 AM - 11:45 AM\n• Periods 5-6: 11:45 AM - 01:15 PM (Thu: 11:45 - 01:05 PM)\n• Prayer & Tiffin Break: 01:15 PM - 01:40 PM\n• Periods 7-9: 01:40 PM - 03:40 PM",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
