package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
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
import com.example.data.model.ClassSession
import com.example.ui.RoutineViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DayDateItem(
    val dayName: String,
    val dayShort: String,
    val dateNumber: Int,
    val formattedDate: String,
    val fullDate: String,
    val isToday: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    viewModel: RoutineViewModel
) {
    val selectedDay by viewModel.selectedDay.collectAsState()
    val routineState by viewModel.routineState.collectAsState()
    var showMoreDaysSheet by remember { mutableStateOf(false) }

    // Rolling 7-day window starting strictly from TODAY + next 6 days (Past days excluded from strip)
    val upcomingDays = remember {
        val list = mutableListOf<DayDateItem>()
        val cal = Calendar.getInstance()
        val dayNameFmt = SimpleDateFormat("EEEE", Locale.ENGLISH)
        val dayShortFmt = SimpleDateFormat("EEE", Locale.ENGLISH)
        val dateFmt = SimpleDateFormat("d MMMM", Locale.ENGLISH)
        val fullDateFmt = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ENGLISH)

        for (i in 0 until 7) {
            val dName = dayNameFmt.format(cal.time)
            val dShort = dayShortFmt.format(cal.time)
            val dNum = cal.get(Calendar.DAY_OF_MONTH)
            val isToday = i == 0
            val formattedDate = dateFmt.format(cal.time)
            val fullDate = fullDateFmt.format(cal.time)

            list.add(
                DayDateItem(
                    dayName = dName,
                    dayShort = dShort,
                    dateNumber = dNum,
                    formattedDate = formattedDate,
                    fullDate = fullDate,
                    isToday = isToday
                )
            )
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        list
    }

    val dayClasses = remember(routineState.classes, selectedDay) {
        routineState.classes
            .filter { it.dayOfWeek.equals(selectedDay, ignoreCase = true) }
            .sortedBy { it.periodNumber }
    }

    val selectedItem = upcomingDays.find { it.dayName.equals(selectedDay, ignoreCase = true) }
    val isSelectedToday = selectedDay.equals(RoutineViewModel.getCurrentDayOfWeek(), ignoreCase = true)
    val isDayFromMore = upcomingDays.none { it.dayName.equals(selectedDay, ignoreCase = true) }
    val displayDate = selectedItem?.formattedDate ?: SimpleDateFormat("d MMMM", Locale.ENGLISH).format(Date())

    if (showMoreDaysSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMoreDaysSheet = false },
            containerColor = PureWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Full Weekly Routine & Archive",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Text(
                            text = "সকল দিনের রুটিন ও পেছনের দিনসমূহ",
                            fontSize = 12.sp,
                            color = MutedText
                        )
                    }
                    IconButton(onClick = { showMoreDaysSheet = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = DarkText)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                val allWeekDays = listOf(
                    "Sunday" to "3 classes (08:45 AM - 01:15 PM) • Math, English, Electronics",
                    "Monday" to "4 classes + Prayer Break (08:45 AM - 03:00 PM) • Math, Phy, BE, ED",
                    "Tuesday" to "2 classes (08:45 AM - 11:45 AM) • Physics Lab, Bangla",
                    "Wednesday" to "2 classes + Prayer Break (11:45 AM - 03:00 PM) • Electronics Lab, Math",
                    "Thursday" to "3 classes (08:45 AM - 01:05 PM) • Electricity Lab, Phy, Math",
                    "Friday" to "Weekly Holiday (সাপ্তাহিক ছুটি - জুম্মা)",
                    "Saturday" to "Institute Off / Self Preparation Day"
                )

                allWeekDays.forEach { (day, subtitle) ->
                    val isCurrentSelected = selectedDay.equals(day, ignoreCase = true)
                    val isDayToday = day.equals(RoutineViewModel.getCurrentDayOfWeek(), ignoreCase = true)

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isCurrentSelected) BoldBlack else SoftBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                viewModel.selectDay(day)
                                showMoreDaysSheet = false
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = day,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrentSelected) Color.White else DarkText
                                )
                                Text(
                                    text = subtitle,
                                    fontSize = 11.sp,
                                    color = if (isCurrentSelected) Color.White.copy(alpha = 0.7f) else MutedText
                                )
                            }

                            if (isDayToday) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isCurrentSelected) Color.White.copy(alpha = 0.2f) else PastelGreen
                                ) {
                                    Text(
                                        text = "TODAY",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCurrentSelected) Color.White else PastelGreenText,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBackground)
            .testTag("timetable_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header: Date and "Today" / Selected Day title
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = displayDate,
                            fontSize = 13.sp,
                            color = MutedText,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (isSelectedToday) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = PastelGreen
                            ) {
                                Text(
                                    text = "LIVE TODAY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PastelGreenText,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        } else if (isDayFromMore) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = PastelPurple
                            ) {
                                Text(
                                    text = "FROM ARCHIVE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PastelPurpleText,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = if (isSelectedToday) "Today" else selectedDay,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkText
                    )
                }

                if (!isSelectedToday) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = PureWhite,
                        modifier = Modifier
                            .clickable {
                                viewModel.selectDay(RoutineViewModel.getCurrentDayOfWeek())
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Today,
                                contentDescription = null,
                                tint = DarkText,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Today",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                        }
                    }
                }
            }
        }

        // Horizontal Date Selector Strip (Today + Next 6 Days + More Capsule)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                upcomingDays.forEach { item ->
                    val isSelected = selectedDay.equals(item.dayName, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .width(58.dp)
                            .height(76.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .clickable { viewModel.selectDay(item.dayName) }
                            .background(if (isSelected) BoldBlack else PureWhite),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.dayShort,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else MutedText
                                )
                                if (item.isToday) {
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) PastelGreenText else Color(0xFF10B981))
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "${item.dateNumber}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else DarkText
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            // Dot indicator for selected day
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color.White else Color.Transparent)
                            )
                        }
                    }
                }

                // "More" button capsule
                Box(
                    modifier = Modifier
                        .width(58.dp)
                        .height(76.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .clickable { showMoreDaysSheet = true }
                        .background(if (isDayFromMore) BoldBlack else PureWhite),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "More",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDayFromMore) Color.White.copy(alpha = 0.8f) else MutedText
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "More days",
                            tint = if (isDayFromMore) Color.White else DarkText,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(if (isDayFromMore) Color.White else Color.Transparent)
                        )
                    }
                }
            }
        }

        if (dayClasses.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventBusy,
                            contentDescription = null,
                            tint = PastelPurpleText,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = when (selectedDay) {
                                "Friday" -> "Friday Weekly Holiday (সাপ্তাহিক ছুটি - জুম্মা)"
                                "Saturday" -> "Saturday Institute Off / Self Preparation Day"
                                else -> "No classes on $selectedDay"
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (selectedDay) {
                                "Friday" -> "No regular academic classes on Friday at DPI."
                                "Saturday" -> "Enjoy your break or review notes for Sunday's classes."
                                else -> "Enjoy your break or review homework & past lectures."
                            },
                            fontSize = 12.sp,
                            color = MutedText
                        )
                    }
                }
            }
        } else {
            // Timeline items
            itemsIndexed(dayClasses) { index, session ->
                if (index > 0) {
                    val prev = dayClasses[index - 1]
                    // ONLY official break: Prayer & Tiffin Break (01:15 PM - 01:40 PM) between morning and afternoon classes
                    if (session.periodNumber >= 7 && prev.periodNumber < 7) {
                        BreakPillItem(
                            label = "Break • 25 min",
                            details = "Prayer & Tiffin: 01:15 PM - 01:40 PM"
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }

                TimelineClassItem(
                    session = session,
                    isLast = index == dayClasses.size - 1
                )
            }

            // End of scheduled classes for selected day
            if (dayClasses.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = PureWhite.copy(alpha = 0.8f)
                        ) {
                            Text(
                                text = "✨ All scheduled classes completed for $selectedDay",
                                fontSize = 11.sp,
                                color = MutedText,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun BreakPillItem(
    label: String = "Break • 25 min",
    details: String = "Prayer & Tiffin"
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = PureWhite,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Coffee,
                    contentDescription = null,
                    tint = PastelYellowText,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$label ($details)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkText
                )
            }
        }
    }
}

@Composable
fun TimelineClassItem(
    session: ClassSession,
    isLast: Boolean
) {
    val (bgColor, textColor, watermarkIcon) = when {
        session.subjectName.contains("Math", ignoreCase = true) ->
            Triple(PastelPurple, PastelPurpleText, Icons.Default.Functions)
        session.subjectName.contains("Phy", ignoreCase = true) ->
            Triple(PastelBlue, PastelBlueText, Icons.Default.Science)
        session.subjectName.contains("Electronics", ignoreCase = true) || session.subjectName.contains("Ent", ignoreCase = true) ->
            Triple(PastelGreen, PastelGreenText, Icons.Default.Memory)
        session.subjectName.contains("Electricity", ignoreCase = true) || session.subjectName.contains("BE", ignoreCase = true) ->
            Triple(PastelYellow, PastelYellowText, Icons.Default.Bolt)
        session.subjectName.contains("Drawing", ignoreCase = true) || session.subjectName.contains("ED", ignoreCase = true) ->
            Triple(PastelRose, PastelRoseText, Icons.Default.Architecture)
        else ->
            Triple(PastelBlue, PastelBlueText, Icons.Default.MenuBook)
    }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Left Column: Times
        Column(
            modifier = Modifier.width(52.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = session.startTime.replace(" AM", "").replace(" PM", ""),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
            Text(
                text = session.endTime.replace(" AM", "").replace(" PM", ""),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MutedText
            )
        }

        // Timeline Node (Hollow Circle and vertical connector line)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            // Hollow Circle Node
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(PureWhite)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(110.dp)
                        .background(Color(0xFFE2E8F0))
                )
            }
        }

        // Right Card: Rounded Pastel Card matching mockup
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = bgColor,
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Background Watermark Icon in bottom-right
                Icon(
                    imageVector = watermarkIcon,
                    contentDescription = null,
                    tint = textColor.copy(alpha = 0.15f),
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = 6.dp, y = 6.dp)
                )

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = session.subjectName,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = null,
                            tint = MutedText,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Period ${session.periodNumber}-${session.periodNumber + session.periodSpan - 1} • ${if (session.isPractical) "Practical Lab" else "Theory"} • Room ${session.roomNumber}",
                        fontSize = 12.sp,
                        color = MutedText
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Teacher row with avatar circle
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(PureWhite),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = textColor,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = session.teacherName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkText
                        )
                    }
                }
            }
        }
    }
}
