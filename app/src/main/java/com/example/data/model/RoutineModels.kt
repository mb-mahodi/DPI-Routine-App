package com.example.data.model

data class RoutinePeriod(
    val periodNumber: Int,
    val timeLabel: String,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int
) {
    val startTotalMinutes: Int get() = startHour * 60 + startMinute
    val endTotalMinutes: Int get() = endHour * 60 + endMinute
}

data class ClassSession(
    val id: String,
    val dayOfWeek: String, // "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Saturday"
    val periodNumber: Int,
    val periodSpan: Int = 1, // e.g. 2 for 2-period lab
    val startTime: String,
    val endTime: String,
    val subjectCode: String,
    val subjectName: String,
    val teacherName: String,
    val roomNumber: String,
    val isPractical: Boolean,
    val note: String = ""
)

data class Subject(
    val slNo: Int,
    val code: String,
    val name: String,
    val banglaName: String,
    val teacherName: String,
    val theoryHours: Int,
    val practicalHours: Int,
    val credits: Int,
    val theoryCont: Int,
    val theoryFinal: Int,
    val practicalCont: Int,
    val practicalFinal: Int,
    val totalMarks: Int
)

data class Teacher(
    val name: String,
    val shortName: String,
    val designation: String,
    val department: String,
    val phone: String,
    val subjectsTaught: List<String>,
    val isGuideTeacher: Boolean = false
)

data class InstituteInfo(
    val name: String = "Daffodil Polytechnic Institute",
    val instituteCode: String = "50238",
    val campus: String = "1 Sobhanbag, Mirpur Road, Dhaka 1207",
    val cellNumbers: List<String> = listOf(
        "+8801713-493158",
        "+8801713493246",
        "+8801847140154",
        "+8801713493182"
    ),
    val landline: String = "+8809610933937",
    val session: String = "August 2026 - January 2027",
    val program: String = "04 Years Diploma in Engineering",
    val department: String = "1st Electronics Technology",
    val guideTeacher: String = "Eng. Abdullah Al Nisat (0177235524)",
    val teamLeader: String = "Md. Emon Khan (Jr. Instructor, Dept. of Civil Engineering)",
    val principal: String = "S. M. Jahirul Islam Farhad (Principal Acting)",
    val executiveDirector: String = "Rathindra Nath Das (Executive Director)",
    val sheetUrl: String = "https://docs.google.com/spreadsheets/d/1QksZyfT0WqYxV9zsaD7HwWuFPahxh_I_/edit?gid=222252652#gid=222252652",
    val sheetCsvUrl: String = "https://docs.google.com/spreadsheets/d/1QksZyfT0WqYxV9zsaD7HwWuFPahxh_I_/export?format=csv&gid=222252652"
)

data class HomeworkItem(
    val id: String,
    val subject: String,
    val subjectCode: String,
    val title: String,
    val teacherName: String,
    val dueDate: String,
    val progress: Float, // 0.0 to 1.0
    val priority: String, // "High", "Low", "Done"
    val isCompleted: Boolean = false
)

data class TeacherComment(
    val id: String,
    val teacherName: String,
    val subject: String,
    val comment: String,
    val timeAgo: String
)

data class RoutineState(
    val classes: List<ClassSession> = emptyList(),
    val subjects: List<Subject> = emptyList(),
    val teachers: List<Teacher> = emptyList(),
    val instituteInfo: InstituteInfo = InstituteInfo(),
    val homeworkList: List<HomeworkItem> = emptyList(),
    val commentsList: List<TeacherComment> = emptyList(),
    val isSyncing: Boolean = false,
    val lastSyncTime: String = "Pre-loaded Official Routine",
    val syncError: String? = null
)
