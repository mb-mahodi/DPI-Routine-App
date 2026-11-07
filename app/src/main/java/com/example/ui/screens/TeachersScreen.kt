package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Teacher
import com.example.ui.RoutineViewModel
import com.example.ui.theme.*

@Composable
fun TeachersScreen(
    viewModel: RoutineViewModel
) {
    val routineState by viewModel.routineState.collectAsState()
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedDeptFilter by remember { mutableStateOf("All") }

    val deptFilters = listOf("All", "Electronics", "Electrical", "General", "Science")

    val filteredTeachers = remember(routineState.teachers, searchQuery, selectedDeptFilter) {
        routineState.teachers.filter { teacher ->
            val matchesFilter = when (selectedDeptFilter) {
                "Electronics" -> teacher.department.contains("Electronics", ignoreCase = true)
                "Electrical" -> teacher.department.contains("Electrical", ignoreCase = true)
                "General" -> teacher.department.contains("Non-Tech", ignoreCase = true) || teacher.department.contains("General", ignoreCase = true)
                "Science" -> teacher.department.contains("Science", ignoreCase = true) || teacher.department.contains("Math", ignoreCase = true)
                else -> true
            }

            val matchesSearch = searchQuery.isBlank() ||
                    teacher.name.contains(searchQuery, ignoreCase = true) ||
                    teacher.shortName.contains(searchQuery, ignoreCase = true) ||
                    teacher.department.contains(searchQuery, ignoreCase = true) ||
                    teacher.subjectsTaught.any { sub -> sub.contains(searchQuery, ignoreCase = true) }

            matchesFilter && matchesSearch
        }
    }

    val guideTeacher = remember(routineState.teachers) {
        routineState.teachers.find { it.isGuideTeacher }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBackground)
            .testTag("teachers_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    text = "DPI Faculty Directory",
                    fontSize = 13.sp,
                    color = MutedText,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Teachers & Mentors",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkText
                )
            }
        }

        // 2. Guide Teacher Spotlight Hero Card
        if (guideTeacher != null && searchQuery.isBlank() && selectedDeptFilter == "All") {
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = BoldBlack,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("guide_teacher_spotlight")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = GoldAccent
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "OFFICIAL GUIDE TEACHER",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }

                            Text(
                                text = "1st Sem • Electronics",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Avatar
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF27272A),
                                modifier = Modifier.size(56.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = guideTeacher.shortName,
                                        color = GoldAccent,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = guideTeacher.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${guideTeacher.designation} • ${guideTeacher.department}",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Mobile: ${guideTeacher.phone}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GoldAccent
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick Call & SMS Actions (Strictly in Teachers Screen)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${guideTeacher.phone}"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("call_guide_teacher"),
                                colors = ButtonDefaults.buttonColors(containerColor = PureWhite),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    tint = BoldBlack,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Call Guide",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BoldBlack
                                )
                            }

                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${guideTeacher.phone}"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("sms_guide_teacher"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27272A)),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Message,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Send SMS",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Search Bar
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = PureWhite,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MutedText,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search faculty by name, code or subject...",
                                color = MutedText,
                                fontSize = 14.sp
                            )
                        }
                        androidx.compose.foundation.text.BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("teacher_search_field"),
                            singleLine = true
                        )
                    }
                    if (searchQuery.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = MutedText,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { searchQuery = "" }
                        )
                    }
                }
            }
        }

        // 4. Department Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                deptFilters.forEach { filter ->
                    val isSelected = selectedDeptFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { selectedDeptFilter = filter }
                            .background(if (isSelected) BoldBlack else PureWhite)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = filter,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else DarkText
                        )
                    }
                }
            }
        }

        // 5. Faculty List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Faculty Members (${filteredTeachers.size})",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
                Text(
                    text = "Phone & SMS Available",
                    fontSize = 12.sp,
                    color = MutedText,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 6. Teacher Cards
        if (filteredTeachers.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = PureWhite,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = MutedText,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No teachers found",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Try searching by subject name or teacher initials.",
                            fontSize = 12.sp,
                            color = MutedText
                        )
                    }
                }
            }
        } else {
            items(filteredTeachers) { teacher ->
                ModernTeacherCard(
                    teacher = teacher,
                    onCall = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${teacher.phone}"))
                        context.startActivity(intent)
                    },
                    onSms = {
                        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${teacher.phone}"))
                        context.startActivity(intent)
                    }
                )
            }
        }

        // 7. Administration & Routine Committee (from Google Sheet)
        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = PureWhite,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "ROUTINE COMMITTEE & ADMINISTRATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedText,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Md. Emon Khan",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                    Text(
                        text = "Team Leader (Routine Committee) • Jr. Instructor, Dept. of Civil Engineering",
                        fontSize = 12.sp,
                        color = MutedText
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = CardBorder.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "S. M. Jahirul Islam Farhad",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                    Text(
                        text = "Principal (Acting) • Daffodil Polytechnic Institute",
                        fontSize = 12.sp,
                        color = MutedText
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = CardBorder.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Rathindra Nath Das",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                    Text(
                        text = "Executive Director • Daffodil Polytechnic Institute",
                        fontSize = 12.sp,
                        color = MutedText
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SoftBackground,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "DPI Campus Hotlines (Google Sheet):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tel: +8809610933937 • Mobile: 01713-493158, 01713-493246, 01847-140154, 01713-493182",
                                fontSize = 11.sp,
                                color = MutedText
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
fun ModernTeacherCard(
    teacher: Teacher,
    onCall: () -> Unit,
    onSms: () -> Unit
) {
    val (avatarBg, avatarText) = when {
        teacher.isGuideTeacher -> Pair(PastelYellow, PastelYellowText)
        teacher.department.contains("Electronics", ignoreCase = true) -> Pair(PastelGreen, PastelGreenText)
        teacher.department.contains("Electrical", ignoreCase = true) -> Pair(PastelBlue, PastelBlueText)
        teacher.department.contains("Science", ignoreCase = true) || teacher.department.contains("Math", ignoreCase = true) -> Pair(PastelPurple, PastelPurpleText)
        else -> Pair(PastelRose, PastelRoseText)
    }

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = PureWhite,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("teacher_card_${teacher.shortName}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Initials Avatar
                    Surface(
                        shape = CircleShape,
                        color = avatarBg,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = teacher.shortName.ifEmpty {
                                    teacher.name.split(" ").mapNotNull { it.firstOrNull() }.take(2).joinToString("")
                                },
                                fontWeight = FontWeight.Bold,
                                color = avatarText,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = teacher.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = DarkText
                            )
                            if (teacher.isGuideTeacher) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = GoldAccent
                                ) {
                                    Text(
                                        text = "Guide",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "${teacher.designation} • ${teacher.department}",
                            fontSize = 12.sp,
                            color = MutedText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Subjects Taught Chip Row
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SoftBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoStories,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = DarkText
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Teaches: " + teacher.subjectsTaught.joinToString(", "),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkText
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = CardBorder.copy(alpha = 0.6f))

            Spacer(modifier = Modifier.height(10.dp))

            // Contact Row with Call & SMS buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PhoneIphone,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = MutedText
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = teacher.phone,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkText
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Call Button
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = PastelGreen,
                        modifier = Modifier
                            .clickable { onCall() }
                            .size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call",
                                modifier = Modifier.size(17.dp),
                                tint = PastelGreenText
                            )
                        }
                    }

                    // SMS Button
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = PastelBlue,
                        modifier = Modifier
                            .clickable { onSms() }
                            .size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Message,
                                contentDescription = "SMS",
                                modifier = Modifier.size(17.dp),
                                tint = PastelBlueText
                            )
                        }
                    }
                }
            }
        }
    }
}
