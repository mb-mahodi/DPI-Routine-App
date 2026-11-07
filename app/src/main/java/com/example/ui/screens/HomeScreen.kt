package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClassSession
import com.example.ui.RoutineViewModel
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: RoutineViewModel,
    onNavigateToTimetable: () -> Unit,
    onNavigateToSubjects: () -> Unit = {},
    onNavigateToTeachers: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val routineState by viewModel.routineState.collectAsState()
    val currentStatus by viewModel.currentStatus.collectAsState()
    val profile by viewModel.studentProfile.collectAsState()

    val todayClasses = remember(routineState.classes, currentStatus.currentDayName) {
        routineState.classes
            .filter { it.dayOfWeek.equals(currentStatus.currentDayName, ignoreCase = true) }
            .sortedBy { it.periodNumber }
    }

    var showNotificationDialog by remember { mutableStateOf(false) }

    if (showNotificationDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = DarkText,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DPI Campus Routine Notices",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = PastelBlue,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "🔬 Practical Lab Rooms",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = PastelBlueText
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Electronics Lab: R-603 • Electricity Lab: R-604 • Physics Lab: R-502. Bring practical notebooks.",
                                fontSize = 12.sp,
                                color = DarkText
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = PastelGreen,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "⭐ Official Guide Teacher",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = PastelGreenText
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Eng. Abdullah Al Nisat (Instructor, Electronics Technology) • Mobile: 0177235524.",
                                fontSize = 12.sp,
                                color = DarkText
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = PastelYellow,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "☕ Official Prayer & Tiffin Break",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = PastelYellowText
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "01:15 PM - 01:40 PM on Monday & Wednesday (between Periods 6 & 7).",
                                fontSize = 12.sp,
                                color = DarkText
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showNotificationDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BoldBlack),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Got it")
                }
            },
            containerColor = PureWhite,
            shape = RoundedCornerShape(22.dp)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBackground)
            .testTag("home_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Header with Avatar, Greeting, and Notification Bell
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onNavigateToProfile() }
                ) {
                    // Student Avatar with official DPI logo badge
                    Box {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF6B46C1),
                            modifier = Modifier.size(48.dp),
                            shadowElevation = 2.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                val initials = profile.name.split(" ")
                                    .filter { it.isNotEmpty() }
                                    .take(2)
                                    .map { it.first() }
                                    .joinToString("")
                                    .ifEmpty { "ET" }
                                Text(
                                    text = initials,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                            }
                        }

                        // Small DPI Logo Badge in corner
                        Surface(
                            shape = CircleShape,
                            color = PureWhite,
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.BottomEnd)
                                .offset(x = 2.dp, y = 2.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                androidx.compose.foundation.Image(
                                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.app_logo),
                                    contentDescription = "DPI Logo Badge",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = currentStatus.liveGreeting.ifEmpty { "Good day" },
                            fontSize = 12.sp,
                            color = MutedText,
                            fontWeight = FontWeight.Medium
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Hi, ${profile.name.split(" ").firstOrNull() ?: "Student"}",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "👋", fontSize = 17.sp)
                        }
                    }
                }

                // Notification Bell with badge "2"
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(PureWhite)
                        .clickable { showNotificationDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = "Notifications",
                        tint = DarkText,
                        modifier = Modifier.size(23.dp)
                    )
                    // Badge 2
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 1.dp, y = (-1).dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(BoldBlack),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "3",
                            color = PureWhite,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Real-time live date badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PastelGreen
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(PastelGreenText)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "LIVE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PastelGreenText
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${currentStatus.liveFormattedDayDate.ifEmpty { currentStatus.currentDayName }} • ${if (todayClasses.isEmpty()) "Off Day" else "${todayClasses.size} classes today"}",
                    fontSize = 13.sp,
                    color = DarkText,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // 2. Dynamic Class Progress Bento Cards (ক্লাস কতটুক হয়েছে আর কতটুক বাকি আছে)
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Row 1: কতটুক হয়েছে (Classes Done) & কতটুক বাকি আছে (Classes Left)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Bento 1: ক্লাস কতটুক হয়েছে (Classes Done)
                    BentoCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("card_classes_done"),
                        gradient = Brush.verticalGradient(
                            listOf(Color(0xFF1E3A2F), Color(0xFF0F201A))
                        ),
                        topIcon = Icons.Default.CheckCircle,
                        mainValue = "${currentStatus.completedCount}",
                        title = "Classes Done",
                        subtitle = if (todayClasses.isEmpty()) "Holiday / Off Day" else if (currentStatus.completedCount == todayClasses.size) "All Done (সম্পন্ন 🎉)" else "${currentStatus.completedCount} of ${todayClasses.size} সম্পন্ন",
                        onClick = onNavigateToTimetable
                    )

                    // Bento 2: ক্লাস কতটুক বাকি আছে (Classes Left) - replaces the previous static subjects card
                    BentoCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("card_classes_left"),
                        gradient = Brush.verticalGradient(
                            listOf(Color(0xFF8D5B4C), Color(0xFF3E2723))
                        ),
                        topIcon = Icons.Default.HourglassTop,
                        mainValue = "${currentStatus.remainingCount}",
                        title = "Classes Left",
                        subtitle = if (todayClasses.isEmpty()) "No classes today" else if (currentStatus.activeClass != null) "1 Live (চলছে)" else if (currentStatus.remainingCount == 0) "All Done (বাকি নেই)" else "Next: ${currentStatus.nextClass?.subjectName ?: "Upcoming"}",
                        onClick = onNavigateToTimetable
                    )
                }

                // Row 2: Today's Total Classes & Day Progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Bento 3: Weekly Off Days in the week
                    BentoCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("card_weekly_off_days"),
                        gradient = Brush.verticalGradient(
                            listOf(Color(0xFF4A3B32), Color(0xFF1C1917))
                        ),
                        topIcon = Icons.Default.EventBusy,
                        mainValue = "2 Days",
                        title = "Weekly Off Days",
                        subtitle = "Friday & Saturday (বন্ধ)",
                        onClick = onNavigateToTimetable
                    )

                    // Bento 4: Day Progress Percentage / Status
                    val progressPercent = if (todayClasses.isEmpty()) 0 else (currentStatus.completedCount * 100 / todayClasses.size)
                    BentoCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("card_day_progress"),
                        gradient = Brush.verticalGradient(
                            listOf(Color(0xFF3D3252), Color(0xFF1F182B))
                        ),
                        topIcon = if (currentStatus.activeClass != null) Icons.Default.PlayCircle else if (currentStatus.isBreakTime) Icons.Default.Coffee else if (currentStatus.isCollegeOver) Icons.Default.DoneAll else Icons.Default.TrendingUp,
                        mainValue = if (todayClasses.isEmpty()) "Off" else if (currentStatus.isCollegeOver) "100%" else "$progressPercent%",
                        title = if (currentStatus.activeClass != null) "In Progress" else if (currentStatus.isBreakTime) "Break Time" else if (currentStatus.isCollegeOver) "Finished" else "Day Progress",
                        subtitle = if (todayClasses.isEmpty()) "Self Study Day" else if (currentStatus.activeClass != null) "${currentStatus.activeClass?.roomNumber} • ${currentStatus.remainingMinutes}m left" else if (currentStatus.isBreakTime) "01:15 PM - 01:40 PM" else if (currentStatus.isCollegeOver) "All classes finished" else "Starts 08:45 AM",
                        onClick = onNavigateToTimetable
                    )
                }
            }
        }

        // 2.1 Visual Progress Tracker Bar (ক্লাস কতটুক হয়েছে ও বাকি আছে)
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = PureWhite,
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("live_progress_tracker_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            todayClasses.isEmpty() -> Color(0xFF94A3B8)
                                            currentStatus.isCollegeOver -> Color(0xFF10B981)
                                            currentStatus.activeClass != null -> Color(0xFF3B82F6)
                                            else -> Color(0xFFF59E0B)
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Today's Class Tracker (ক্লাসের অগ্রগতি)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = DarkText
                            )
                        }

                        val pct = if (todayClasses.isEmpty()) 0 else (currentStatus.completedCount * 100 / todayClasses.size)
                        Text(
                            text = if (todayClasses.isEmpty()) "Off Day" else "${currentStatus.completedCount}/${todayClasses.size} Done ($pct%)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MutedText
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val progressVal = if (todayClasses.isEmpty()) 0f else (currentStatus.completedCount.toFloat() / todayClasses.size)
                    LinearProgressIndicator(
                        progress = { progressVal },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF10B981),
                        trackColor = Color(0xFFF1F5F9)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${currentStatus.completedCount} হয়েছে (Completed)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF059669)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${currentStatus.remainingCount} বাকি আছে (Remaining)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFD97706)
                            )
                        }
                    }
                }
            }
        }

        // 3. "Today" Section Title with "Next 3 classes" and "View all >"
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Today",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        if (currentStatus.liveShortDate.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ${currentStatus.liveShortDate}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MutedText
                            )
                        }
                    }
                    Text(
                        text = if (todayClasses.isEmpty()) "No classes scheduled today" else "Today's ${todayClasses.size} scheduled classes",
                        fontSize = 12.sp,
                        color = MutedText
                    )
                }

                Text(
                    text = "View all >",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MutedText,
                    modifier = Modifier
                        .clickable { onNavigateToTimetable() }
                        .padding(4.dp)
                )
            }
        }

        // 4. Pastel Class Pill Rows (matching mockup style)
        if (todayClasses.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = PureWhite,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Weekend,
                            contentDescription = null,
                            tint = PastelBlueText,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No classes scheduled for today (${currentStatus.liveFormattedDayDate.ifEmpty { currentStatus.currentDayName }})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Enjoy your day or view the full week timetable.",
                            fontSize = 12.sp,
                            color = MutedText
                        )
                    }
                }
            }
        } else {
            if (currentStatus.isBreakTime) {
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = PastelYellow,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PureWhite),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Coffee,
                                    contentDescription = "Break",
                                    tint = PastelYellowText,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Prayer & Tiffin Break (নামাজ ও টিফিনের বিরতি)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = DarkText
                                )
                                Text(
                                    text = "01:15 PM - 01:40 PM • ${currentStatus.remainingMinutes} min remaining",
                                    fontSize = 12.sp,
                                    color = MutedText
                                )
                            }
                        }
                    }
                }
            }

            itemsIndexed(todayClasses) { index, session ->
                if (index > 0) {
                    val prev = todayClasses[index - 1]
                    // The ONLY official institute break is Prayer & Tiffin Break (01:15 PM - 01:40 PM) between morning and afternoon
                    if (session.periodNumber >= 7 && prev.periodNumber < 7) {
                        HomeBreakPillRow(
                            label = "Prayer & Tiffin Break • 25 min",
                            time = "01:15 PM - 01:40 PM"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                val endMin = viewModel.parseTimeToMinutes(session.endTime)
                val startMin = viewModel.parseTimeToMinutes(session.startTime)
                val isLiveNow = currentStatus.activeClass?.id == session.id
                val isCompleted = currentStatus.currentMinutes >= endMin
                val isUpcoming = currentStatus.currentMinutes < startMin

                HomeClassPillRow(
                    session = session,
                    isLiveNow = isLiveNow,
                    isCompleted = isCompleted,
                    isUpcoming = isUpcoming,
                    onClick = onNavigateToTimetable
                )
            }
        }

        // 5. Announcement Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PastelYellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = PastelYellowText,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "ANNOUNCEMENT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MutedText,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Practical lab sessions are held in R-502, R-603 & R-604.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkText
                        )
                    }
                }
            }
        }

        // 6. Home Screen Widgets Showcase Card
        item {
            HomeScreenWidgetCard(viewModel = viewModel)
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun BentoCard(
    modifier: Modifier = Modifier,
    gradient: Brush,
    topIcon: androidx.compose.ui.graphics.vector.ImageVector,
    mainValue: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(148.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(16.dp)
        ) {
            // Semi-transparent circle behind icon
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = topIcon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(17.dp)
                )
            }

            // Bottom texts
            Column(
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Text(
                    text = mainValue,
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun HomeClassPillRow(
    session: ClassSession,
    isLiveNow: Boolean,
    isCompleted: Boolean = false,
    isUpcoming: Boolean = false,
    onClick: () -> Unit
) {
    val (bgColor, textColor, iconColor, icon) = when {
        session.subjectName.contains("Math", ignoreCase = true) ->
            Tuple4(PastelPurple, PastelPurpleText, PastelPurpleIcon, Icons.Default.Calculate)
        session.subjectName.contains("Phy", ignoreCase = true) ->
            Tuple4(PastelBlue, PastelBlueText, PastelBlueIcon, Icons.Default.Science)
        session.subjectName.contains("Electronics", ignoreCase = true) || session.subjectName.contains("Ent", ignoreCase = true) ->
            Tuple4(PastelGreen, PastelGreenText, PastelGreenIcon, Icons.Default.Memory)
        session.subjectName.contains("Electricity", ignoreCase = true) || session.subjectName.contains("BE", ignoreCase = true) ->
            Tuple4(PastelYellow, PastelYellowText, PastelYellowIcon, Icons.Default.Bolt)
        session.subjectName.contains("English", ignoreCase = true) ->
            Tuple4(PastelRose, PastelRoseText, PastelRoseIcon, Icons.Default.Translate)
        else ->
            Tuple4(PastelBlue, PastelBlueText, PastelBlueIcon, Icons.Default.Book)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Time on left: start and end time
        Column(
            modifier = Modifier.width(52.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = session.startTime.replace(" AM", "").replace(" PM", ""),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCompleted) MutedText else DarkText
            )
            Text(
                text = session.endTime.replace(" AM", "").replace(" PM", ""),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MutedText
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Pastel Pill Card on right
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = if (isCompleted) bgColor.copy(alpha = 0.65f) else bgColor,
            shadowElevation = if (isLiveNow) 3.dp else 1.dp,
            modifier = Modifier
                .weight(1f)
                .height(68.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular White Icon Badge
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(PureWhite),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(19.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.subjectName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) DarkText.copy(alpha = 0.8f) else DarkText
                    )
                    Text(
                        text = "${session.teacherName} • ${session.roomNumber} (${if (session.isPractical) "Lab" else "Theory"})",
                        fontSize = 12.sp,
                        color = MutedText
                    )
                }

                when {
                    isLiveNow -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PureWhite,
                            shadowElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Live Now",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF059669)
                                )
                            }
                        }
                    }
                    isCompleted -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PureWhite.copy(alpha = 0.85f),
                            shadowElevation = 0.5.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Done",
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Done",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF059669)
                                )
                            }
                        }
                    }
                    isUpcoming -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PureWhite.copy(alpha = 0.85f),
                            shadowElevation = 0.5.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HourglassTop,
                                    contentDescription = "Left",
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Left",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFD97706)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeBreakPillRow(
    label: String,
    time: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = PureWhite,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(PastelYellow),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Coffee,
                        contentDescription = null,
                        tint = PastelYellowText,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$label • $time",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkText
                )
            }
        }
    }
}

@Composable
fun HomeScreenWidgetCard(
    viewModel: RoutineViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var showWidgetDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E242B)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showWidgetDialog = true }
            .testTag("home_screen_widget_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF3B82F6).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Widgets,
                            contentDescription = "Widgets",
                            tint = Color(0xFF60A5FA),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "HOME SCREEN WIDGETS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9CA3AF),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "হোম স্ক্রিন উইজেট যুক্ত করুন",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF3B82F6)
                ) {
                    Text(
                        text = "Setup >",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Add the live DPI Routine widget to your mobile home screen to see active classes, room numbers, and completed/left count directly on your launcher without opening the app!",
                fontSize = 12.sp,
                color = Color(0xFFD1D5DB)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "• 4x2 Detailed Routine Widget  • 2x2 Quick Stats Widget",
                    fontSize = 11.sp,
                    color = Color(0xFF9CA3AF),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    if (showWidgetDialog) {
        AlertDialog(
            onDismissRequest = { showWidgetDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Widgets,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Android Home Screen Widgets",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "ফোনের হোম স্ক্রিনে DPI রুটিন উইজেট যুক্ত করার নিয়ম:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = DarkText
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PastelBlue,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "📱 How to add manually:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = PastelBlueText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "1. Long press on an empty space on your phone home screen.\n2. Tap 'Widgets'.\n3. Scroll down to 'DPI Routine'.\n4. Touch and drag the widget to your home screen!",
                                fontSize = 11.sp,
                                color = DarkText
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PastelGreen,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "✨ Available Widget Sizes:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = PastelGreenText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• 4x2 Today Routine Widget: Shows live period, progress bar, active & next class details with room and teacher name.\n• 2x2 Quick Stats Widget: Compact widget for Done/Left class counts.",
                                fontSize = 11.sp,
                                color = DarkText
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        try {
                            com.example.widget.TodayRoutineWidgetProvider.updateAllWidgets(context)
                            com.example.widget.QuickStatsWidgetProvider.updateAllWidgets(context)
                        } catch (_: Exception) {}
                        showWidgetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BoldBlack),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Sync Widgets")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWidgetDialog = false }) {
                    Text("Close")
                }
            },
            containerColor = PureWhite,
            shape = RoundedCornerShape(22.dp)
        )
    }
}

data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
