package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Subject
import com.example.ui.RoutineViewModel
import com.example.ui.theme.*

@Composable
fun SubjectsScreen(
    viewModel: RoutineViewModel
) {
    val routineState by viewModel.routineState.collectAsState()
    val subjectNotes by viewModel.subjectNotes.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Subjects, 1: GPA Calculator

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBackground)
            .testTag("subjects_screen")
    ) {
        // 1. Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "DPI Curriculum",
                    fontSize = 13.sp,
                    color = MutedText,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Subjects & Marks",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkText
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = PureWhite,
                shadowElevation = 1.dp,
                modifier = Modifier.clickable(enabled = !routineState.isSyncing) {
                    viewModel.syncFromGoogleSheet()
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (routineState.isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp,
                            color = BoldBlack
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Sync",
                            tint = DarkText,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (routineState.isSyncing) "Syncing" else "Sync",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                }
            }
        }

        // 2. Custom Floating Capsule Pill Tab Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Tab 1: Subjects & Marks
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { selectedTab = 0 }
                    .background(if (selectedTab == 0) BoldBlack else PureWhite)
                    .padding(vertical = 10.dp)
                    .testTag("tab_subjects_marks"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = if (selectedTab == 0) Color.White else MutedText,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Curriculum (${routineState.subjects.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTab == 0) Color.White else DarkText
                    )
                }
            }

            // Tab 2: GPA Calculator
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { selectedTab = 1 }
                    .background(if (selectedTab == 1) BoldBlack else PureWhite)
                    .padding(vertical = 10.dp)
                    .testTag("tab_gpa_calc"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = if (selectedTab == 1) Color.White else MutedText,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GPA Calculator",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTab == 1) Color.White else DarkText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (selectedTab == 0) {
            ModernSyllabusList(
                subjects = routineState.subjects,
                notes = subjectNotes,
                onSaveNote = { code, text -> viewModel.saveNote(code, text) }
            )
        } else {
            ModernBtebGpaCalculator(subjects = routineState.subjects)
        }
    }
}

@Composable
fun ModernSyllabusList(
    subjects: List<Subject>,
    notes: Map<String, String>,
    onSaveNote: (String, String) -> Unit
) {
    val totalCredits = remember(subjects) { subjects.sumOf { it.credits } }
    val totalMarks = remember(subjects) { subjects.sumOf { it.totalMarks } }
    val totalTheory = remember(subjects) { subjects.sumOf { it.theoryHours } }
    val totalPractical = remember(subjects) { subjects.sumOf { it.practicalHours } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("syllabus_list"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Overview Bento Hero Card
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = PureWhite,
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("curriculum_summary_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "BTEB 1ST SEMESTER",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MutedText,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Electronics Technology (ET)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PastelGreen
                        ) {
                            Text(
                                text = "2022 Probidhan",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PastelGreenText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4 Stat Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ModernStatChip(
                            label = "Subjects",
                            value = "${subjects.size}",
                            bgColor = PastelBlue,
                            textColor = PastelBlueText,
                            modifier = Modifier.weight(1f)
                        )
                        ModernStatChip(
                            label = "Credits",
                            value = "$totalCredits C",
                            bgColor = PastelPurple,
                            textColor = PastelPurpleText,
                            modifier = Modifier.weight(1f)
                        )
                        ModernStatChip(
                            label = "Marks",
                            value = "$totalMarks",
                            bgColor = PastelYellow,
                            textColor = PastelYellowText,
                            modifier = Modifier.weight(1f)
                        )
                        ModernStatChip(
                            label = "Weekly",
                            value = "${totalTheory + totalPractical}h",
                            bgColor = PastelRose,
                            textColor = PastelRoseText,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        items(subjects) { subject ->
            ModernSubjectCard(
                subject = subject,
                savedNote = notes[subject.code] ?: "",
                onSaveNote = { onSaveNote(subject.code, it) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun ModernStatChip(
    label: String,
    value: String,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = textColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
fun ModernSubjectCard(
    subject: Subject,
    savedNote: String,
    onSaveNote: (String) -> Unit
) {
    var expandedNotes by remember { mutableStateOf(false) }
    var noteText by remember(savedNote) { mutableStateOf(savedNote) }

    val (badgeBg, badgeText) = when {
        subject.name.contains("Math", ignoreCase = true) -> Pair(PastelPurple, PastelPurpleText)
        subject.name.contains("Phy", ignoreCase = true) -> Pair(PastelBlue, PastelBlueText)
        subject.name.contains("Electronics", ignoreCase = true) -> Pair(PastelGreen, PastelGreenText)
        subject.name.contains("Electricity", ignoreCase = true) -> Pair(PastelYellow, PastelYellowText)
        else -> Pair(PastelRose, PastelRoseText)
    }

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = PureWhite,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("subject_card_${subject.code}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Top row: Code badge, Sl No, and Credits pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BoldBlack
                    ) {
                        Text(
                            text = subject.code,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = badgeBg
                    ) {
                        Text(
                            text = "${subject.credits} Credits",
                            color = badgeText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = "${subject.totalMarks} Marks",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = DarkText
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Subject Title & Bangla Name
            Text(
                text = subject.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
            if (subject.banglaName.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subject.banglaName,
                    fontSize = 13.sp,
                    color = MutedText,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Teacher info row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(badgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = badgeText,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = subject.teacherName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkText
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Marks Breakdown Table
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SoftBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Class Hours: Theory ${subject.theoryHours}h • Practical ${subject.practicalHours}h",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkText
                        )
                        Text(
                            text = "BTEB Formula",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MutedText
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = CardBorder.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MarkColumn(label = "Theory Cont.", marks = subject.theoryCont)
                        MarkColumn(label = "Theory Final", marks = subject.theoryFinal)
                        MarkColumn(label = "Practical Cont.", marks = subject.practicalCont)
                        MarkColumn(label = "Practical Final", marks = subject.practicalFinal)
                    }
                }
            }

            // Note accordion toggle
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { expandedNotes = !expandedNotes }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = null,
                        tint = if (savedNote.isNotBlank()) PastelGreenText else MutedText,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (savedNote.isNotBlank()) "Study Notes (Saved)" else "Add Personal Study Note",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (savedNote.isNotBlank()) DarkText else MutedText
                    )
                }

                Icon(
                    imageVector = if (expandedNotes) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MutedText,
                    modifier = Modifier.size(18.dp)
                )
            }

            AnimatedVisibility(visible = expandedNotes) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Write assignment dates, lab submission tips, or personal notes...") },
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                onSaveNote(noteText)
                                expandedNotes = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BoldBlack),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Note", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MarkColumn(label: String, marks: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$marks",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = DarkText
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = MutedText
        )
    }
}

@Composable
fun ModernBtebGpaCalculator(subjects: List<Subject>) {
    val gradePoints = remember {
        mutableStateMapOf<String, Double>().apply {
            subjects.forEach { put(it.code, 4.0) } // Default A+
        }
    }

    val totalCredits = remember(subjects) { subjects.sumOf { it.credits }.coerceAtLeast(1) }
    val calculatedGpa = remember(gradePoints.values.toList(), subjects) {
        val totalWeightedPoints = subjects.sumOf { sub ->
            val gp = gradePoints[sub.code] ?: 4.0
            gp * sub.credits
        }
        totalWeightedPoints / totalCredits
    }

    val gradeLetters = listOf(
        Pair("A+ (80%+)", 4.0),
        Pair("A (75-79%)", 3.75),
        Pair("A- (70-74%)", 3.50),
        Pair("B+ (65-69%)", 3.25),
        Pair("B (60-64%)", 3.00),
        Pair("B- (55-59%)", 2.75),
        Pair("C+ (50-54%)", 2.50),
        Pair("C (45-49%)", 2.25),
        Pair("D (40-44%)", 2.00),
        Pair("F (<40%)", 0.00)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("gpa_calculator_tab"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero GPA Display Card
        item {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = BoldBlack,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gpa_result_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "PROJECTED SEMESTER GPA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.7f),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = String.format("%.2f", calculatedGpa),
                        fontSize = 52.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = when {
                            calculatedGpa >= 4.0 -> PastelGreen
                            calculatedGpa >= 3.5 -> PastelBlue
                            calculatedGpa >= 3.0 -> PastelYellow
                            else -> PastelRose
                        }
                    ) {
                        Text(
                            text = when {
                                calculatedGpa >= 4.0 -> "Letter Grade: A+ (Outstanding)"
                                calculatedGpa >= 3.75 -> "Letter Grade: A (Excellent)"
                                calculatedGpa >= 3.50 -> "Letter Grade: A- (Very Good)"
                                calculatedGpa >= 3.00 -> "Letter Grade: B (Good)"
                                calculatedGpa >= 2.00 -> "Letter Grade: Passing"
                                else -> "Letter Grade: F (Needs Retake)"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                calculatedGpa >= 4.0 -> PastelGreenText
                                calculatedGpa >= 3.5 -> PastelBlueText
                                calculatedGpa >= 3.0 -> PastelYellowText
                                else -> PastelRoseText
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Calculated on $totalCredits Total Credits across ${subjects.size} subjects (BTEB 4.00 Grading Scale)",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.65f),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        item {
            Text(
                text = "Select Predicted Grades:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
        }

        items(subjects) { subject ->
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = PureWhite,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = subject.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DarkText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Code ${subject.code} • ${subject.credits} Credits • ${subject.totalMarks} Marks",
                            fontSize = 11.sp,
                            color = MutedText
                        )
                    }

                    var expandedMenu by remember { mutableStateOf(false) }
                    val currentGp = gradePoints[subject.code] ?: 4.0

                    Box {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SoftBackground,
                            modifier = Modifier.clickable { expandedMenu = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${gradeLetters.find { it.second == currentGp }?.first?.take(2) ?: "A+"} (${String.format("%.2f", currentGp)})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = DarkText,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = expandedMenu,
                            onDismissRequest = { expandedMenu = false }
                        ) {
                            gradeLetters.forEach { (label, point) ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "$label • ${String.format("%.2f", point)}",
                                            fontWeight = if (point == currentGp) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        gradePoints[subject.code] = point
                                        expandedMenu = false
                                    }
                                )
                            }
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
