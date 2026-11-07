package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.StudentProfile
import com.example.ui.RoutineViewModel
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: RoutineViewModel,
    onNavigateToSubjects: () -> Unit = {},
    onNavigateToTimetable: () -> Unit = {}
) {
    val profile by viewModel.studentProfile.collectAsState()
    val routineState by viewModel.routineState.collectAsState()
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied: $text", Toast.LENGTH_SHORT).show()
    }

    if (showEditDialog) {
        EditProfileDialog(
            currentProfile = profile,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                viewModel.updateStudentProfile(updated)
                showEditDialog = false
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBackground)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header with Edit Button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "DPI Student Portal",
                        fontSize = 13.sp,
                        color = MutedText,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Student Profile",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkText
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = BoldBlack,
                    modifier = Modifier.clickable { showEditDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Edit Profile",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // 2. Official Student ID Smart Card
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = PureWhite,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Institute header band
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = PureWhite,
                                modifier = Modifier.size(32.dp),
                                shadowElevation = 1.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    androidx.compose.foundation.Image(
                                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.app_logo),
                                        contentDescription = "DPI Logo",
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Daffodil Polytechnic Institute",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                        }

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
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(PastelGreenText)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "VERIFIED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PastelGreenText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Avatar + Student Name + Roll / Reg
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = BoldBlack,
                            modifier = Modifier.size(64.dp)
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
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = profile.department,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MutedText
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = PastelPurple
                                ) {
                                    Text(
                                        text = profile.semester,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PastelPurpleText,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = PastelBlue
                                ) {
                                    Text(
                                        text = profile.shift,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PastelBlueText,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = PastelYellow
                                ) {
                                    Text(
                                        text = profile.group,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PastelYellowText,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = CardBorder.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Roll & Reg Copyable Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SoftBackground,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { copyToClipboard("Roll No", profile.rollNo) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("ROLL NO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                                    Text(profile.rollNo, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkText)
                                }
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Roll", tint = MutedText, modifier = Modifier.size(15.dp))
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SoftBackground,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { copyToClipboard("Registration No", profile.regNo) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("REG NO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                                    Text(profile.regNo, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkText)
                                }
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Reg", tint = MutedText, modifier = Modifier.size(15.dp))
                            }
                        }
                    }
                }
            }
        }

        // 3. Quick Stats Bento Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: Subjects
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = PureWhite,
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToSubjects() }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PastelBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = PastelBlueText,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "${routineState.subjects.size} Subjects",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Text(
                            text = "${routineState.subjects.sumOf { it.credits }} Total Credits",
                            fontSize = 11.sp,
                            color = MutedText
                        )
                    }
                }

                // Card 2: Weekly Classes
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = PureWhite,
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToTimetable() }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PastelGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = PastelGreenText,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "14 Classes",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Text(
                            text = "Weekly Periods",
                            fontSize = 11.sp,
                            color = MutedText
                        )
                    }
                }

                // Card 3: Total Marks
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = PureWhite,
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToSubjects() }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PastelYellow),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Grade,
                                contentDescription = null,
                                tint = PastelYellowText,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "${routineState.subjects.sumOf { it.totalMarks }} Marks",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Text(
                            text = "BTEB 1st Sem ET",
                            fontSize = 11.sp,
                            color = MutedText
                        )
                    }
                }
            }
        }

        // 4. Guide Teacher Spotlight
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GoldAccent
                        ) {
                            Text(
                                text = "OFFICIAL GUIDE TEACHER",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = "1st Semester ET",
                            fontSize = 11.sp,
                            color = MutedText,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = PastelPurple,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = PastelPurpleText,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile.guideTeacher,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                            Text(
                                text = "Instructor • Electronics Technology • R-601/R-603",
                                fontSize = 12.sp,
                                color = MutedText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Call and SMS buttons (strictly teacher section & guide teacher)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+880177235524"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BoldBlack)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Call Guide", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:+880177235524"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Send SMS", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 5. Academic Details List
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
                        text = "ACADEMIC INFORMATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedText,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    ModernProfileDetailRow(label = "Department", value = profile.department)
                    ModernProfileDetailRow(label = "Curriculum", value = "04 Years Diploma in Engineering")
                    ModernProfileDetailRow(label = "Institute Code", value = "50238")
                    ModernProfileDetailRow(label = "Academic Session", value = profile.sessionYear)
                    ModernProfileDetailRow(label = "Campus Location", value = profile.campus)
                    ModernProfileDetailRow(label = "Guide Teacher", value = profile.guideTeacher)
                    ModernProfileDetailRow(label = "Team Leader (Routine)", value = "Md. Emon Khan (Jr. Instructor, Civil)")
                    ModernProfileDetailRow(label = "Principal (Acting)", value = "S. M. Jahirul Islam Farhad")
                    ModernProfileDetailRow(label = "Executive Director", value = "Rathindra Nath Das", isLast = true)
                }
            }
        }

        // 6. Routine Source & Live Sync Card
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "ROUTINE SOURCE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MutedText,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Google Sheet Live Sync",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = BoldBlack,
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
                                        color = Color.White
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.Sync,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (routineState.isSyncing) "Syncing" else "Sync Now",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Sheet: 1QksZyfT0WqYxV9zsaD7HwWuFPahxh_I_ (gid: 222252652)\nLast synchronized: ${routineState.lastSyncTime}",
                        fontSize = 11.sp,
                        color = MutedText,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun ModernProfileDetailRow(
    label: String,
    value: String,
    isLast: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                color = MutedText,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkText
            )
        }
        if (!isLast) {
            HorizontalDivider(color = CardBorder.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun EditProfileDialog(
    currentProfile: StudentProfile,
    onDismiss: () -> Unit,
    onSave: (StudentProfile) -> Unit
) {
    var name by remember { mutableStateOf(currentProfile.name) }
    var roll by remember { mutableStateOf(currentProfile.rollNo) }
    var reg by remember { mutableStateOf(currentProfile.regNo) }
    var shift by remember { mutableStateOf(currentProfile.shift) }
    var group by remember { mutableStateOf(currentProfile.group) }
    var phone by remember { mutableStateOf(currentProfile.phone) }
    var email by remember { mutableStateOf(currentProfile.email) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Student Profile",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = roll,
                        onValueChange = { roll = it },
                        label = { Text("Roll No") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = reg,
                        onValueChange = { reg = it },
                        label = { Text("Reg No") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = shift,
                        onValueChange = { shift = it },
                        label = { Text("Shift") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = group,
                        onValueChange = { group = it },
                        label = { Text("Group / Section") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Institutional Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        currentProfile.copy(
                            name = name.trim().ifEmpty { currentProfile.name },
                            rollNo = roll.trim().ifEmpty { currentProfile.rollNo },
                            regNo = reg.trim().ifEmpty { currentProfile.regNo },
                            shift = shift.trim().ifEmpty { currentProfile.shift },
                            group = group.trim().ifEmpty { currentProfile.group },
                            phone = phone.trim().ifEmpty { currentProfile.phone },
                            email = email.trim().ifEmpty { currentProfile.email }
                        )
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BoldBlack)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MutedText)
            }
        },
        containerColor = PureWhite,
        shape = RoundedCornerShape(22.dp)
    )
}
