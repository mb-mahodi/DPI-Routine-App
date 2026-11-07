package com.example.data.datasource

import com.example.data.model.ClassSession
import com.example.data.model.InstituteInfo
import com.example.data.model.Subject
import com.example.data.model.Teacher

object DefaultRoutineData {

    val subjects: List<Subject> = listOf(
        Subject(
            slNo = 1,
            code = "25711",
            name = "Bangla-I",
            banglaName = "বাংলা-১",
            teacherName = "Md. Abdur Rahman Rakib (Rakib Sir)",
            theoryHours = 2,
            practicalHours = 0,
            credits = 2,
            theoryCont = 40,
            theoryFinal = 60,
            practicalCont = 0,
            practicalFinal = 0,
            totalMarks = 100
        ),
        Subject(
            slNo = 2,
            code = "25912",
            name = "Physics-I",
            banglaName = "পদার্থবিজ্ঞান-১",
            teacherName = "Mr. physics-1",
            theoryHours = 3,
            practicalHours = 3,
            credits = 4,
            theoryCont = 60,
            theoryFinal = 90,
            practicalCont = 25,
            practicalFinal = 25,
            totalMarks = 200
        ),
        Subject(
            slNo = 3,
            code = "25712",
            name = "English-I",
            banglaName = "ইংরেজি-১",
            teacherName = "Sirajam Munira (Munira Ma'am)",
            theoryHours = 2,
            practicalHours = 0,
            credits = 2,
            theoryCont = 40,
            theoryFinal = 60,
            practicalCont = 0,
            practicalFinal = 0,
            totalMarks = 100
        ),
        Subject(
            slNo = 4,
            code = "25911",
            name = "Mathematics-I",
            banglaName = "গণিত-১",
            teacherName = "Md. Abdullah Al Roman (Roman Sir)",
            theoryHours = 4,
            practicalHours = 4,
            credits = 4,
            theoryCont = 60,
            theoryFinal = 90,
            practicalCont = 25,
            practicalFinal = 25,
            totalMarks = 200
        ),
        Subject(
            slNo = 5,
            code = "21011",
            name = "Engineering Drawing",
            banglaName = "ইঞ্জিনিয়ারিং ড্রয়িং",
            teacherName = "Imran Hossen (Imran Sir)",
            theoryHours = 0,
            practicalHours = 2,
            credits = 2,
            theoryCont = 0,
            theoryFinal = 0,
            practicalCont = 50,
            practicalFinal = 50,
            totalMarks = 100
        ),
        Subject(
            slNo = 6,
            code = "26711",
            name = "Basic Electricity",
            banglaName = "বেসিক ইলেকট্রিসিটি",
            teacherName = "Rubel Hossen (Rubel Sir)",
            theoryHours = 2,
            practicalHours = 2,
            credits = 4,
            theoryCont = 60,
            theoryFinal = 90,
            practicalCont = 25,
            practicalFinal = 25,
            totalMarks = 200
        ),
        Subject(
            slNo = 7,
            code = "26811",
            name = "Basic Electronics",
            banglaName = "বেসিক ইলেকট্রনিক্স",
            teacherName = "Eng. Abdullah Al Nisat (Eng. Nisat Sir)",
            theoryHours = 2,
            practicalHours = 2,
            credits = 3,
            theoryCont = 40,
            theoryFinal = 60,
            practicalCont = 25,
            practicalFinal = 25,
            totalMarks = 150
        ),
        Subject(
            slNo = 8,
            code = "28567",
            name = "Programming in C",
            banglaName = "সি প্রোগ্রামিং",
            teacherName = "Md Thamid",
            theoryHours = 2,
            practicalHours = 2,
            credits = 3,
            theoryCont = 40,
            theoryFinal = 60,
            practicalCont = 25,
            practicalFinal = 25,
            totalMarks = 150
        )
    )

    val teachers: List<Teacher> = listOf(
        Teacher(
            name = "Eng. Abdullah Al Nisat",
            shortName = "Eng. Nisat Sir",
            designation = "Instructor & Guide Teacher",
            department = "Electronics Technology",
            phone = "+880177235524",
            subjectsTaught = listOf("Basic Electronics (26811)"),
            isGuideTeacher = true
        ),
        Teacher(
            name = "Md. Abdullah Al Roman",
            shortName = "Roman Sir",
            designation = "Instructor",
            department = "Mathematics / Non-Tech",
            phone = "+8801713493158",
            subjectsTaught = listOf("Mathematics-I (25911)"),
            isGuideTeacher = false
        ),
        Teacher(
            name = "Rubel Hossen",
            shortName = "Rubel Sir",
            designation = "Instructor",
            department = "Electrical Technology",
            phone = "+8801713493246",
            subjectsTaught = listOf("Basic Electricity (26711)"),
            isGuideTeacher = false
        ),
        Teacher(
            name = "Mr. physics-1",
            shortName = "Physics Teacher",
            designation = "Instructor",
            department = "Physics / Non-Tech",
            phone = "+8801847140154",
            subjectsTaught = listOf("Physics-I (25912)"),
            isGuideTeacher = false
        ),
        Teacher(
            name = "Imran Hossen",
            shortName = "Imran Sir",
            designation = "Instructor",
            department = "Civil / Mechanical",
            phone = "+8801713493182",
            subjectsTaught = listOf("Engineering Drawing (21011)"),
            isGuideTeacher = false
        ),
        Teacher(
            name = "Sirajam Munira",
            shortName = "Munira Ma'am",
            designation = "Instructor",
            department = "English / Non-Tech",
            phone = "+8809610933937",
            subjectsTaught = listOf("English-I (25712)"),
            isGuideTeacher = false
        ),
        Teacher(
            name = "Md. Abdur Rahman Rakib",
            shortName = "Rakib Sir",
            designation = "Instructor",
            department = "Bangla / Non-Tech",
            phone = "+8809610933937",
            subjectsTaught = listOf("Bangla-I (25711)"),
            isGuideTeacher = false
        ),
        Teacher(
            name = "Md Thamid",
            shortName = "Thamid Sir",
            designation = "Instructor",
            department = "Computer Technology",
            phone = "+8809610933937",
            subjectsTaught = listOf("Programming in C (28567)"),
            isGuideTeacher = false
        )
    )

    val classes: List<ClassSession> = listOf(
        // Sunday (3 continuous classes: 08:45 to 01:15)
        ClassSession(
            id = "sun_1_2",
            dayOfWeek = "Sunday",
            periodNumber = 1,
            periodSpan = 2,
            startTime = "08:45 AM",
            endTime = "10:15 AM",
            subjectCode = "25911",
            subjectName = "Math-I",
            teacherName = "Roman Sir",
            roomNumber = "R-904",
            isPractical = false,
            note = "Theory lecture (Periods 1-2)"
        ),
        ClassSession(
            id = "sun_3_4",
            dayOfWeek = "Sunday",
            periodNumber = 3,
            periodSpan = 2,
            startTime = "10:15 AM",
            endTime = "11:45 AM",
            subjectCode = "25712",
            subjectName = "English-I",
            teacherName = "Munira Ma'am",
            roomNumber = "R-904",
            isPractical = false,
            note = "Theory lecture (Periods 3-4)"
        ),
        ClassSession(
            id = "sun_5_6",
            dayOfWeek = "Sunday",
            periodNumber = 5,
            periodSpan = 2,
            startTime = "11:45 AM",
            endTime = "01:15 PM",
            subjectCode = "26811",
            subjectName = "Basic Electronics (B.Ent.)",
            teacherName = "Eng. Nisat Sir",
            roomNumber = "R-601",
            isPractical = false,
            note = "Theory lecture (Periods 5-6)"
        ),

        // Monday (3 morning classes 08:45 to 01:15, Prayer Break 01:15-01:40, 1 afternoon lab 01:40-03:00)
        ClassSession(
            id = "mon_1_2",
            dayOfWeek = "Monday",
            periodNumber = 1,
            periodSpan = 2,
            startTime = "08:45 AM",
            endTime = "10:15 AM",
            subjectCode = "25911",
            subjectName = "Math-I",
            teacherName = "Roman Sir",
            roomNumber = "R-904",
            isPractical = true,
            note = "Practical session (Periods 1-2)"
        ),
        ClassSession(
            id = "mon_3_4",
            dayOfWeek = "Monday",
            periodNumber = 3,
            periodSpan = 2,
            startTime = "10:15 AM",
            endTime = "11:45 AM",
            subjectCode = "25912",
            subjectName = "Phy-I",
            teacherName = "Mr. physics-1",
            roomNumber = "R-904",
            isPractical = false,
            note = "Theory lecture (Periods 3-4)"
        ),
        ClassSession(
            id = "mon_5_6",
            dayOfWeek = "Monday",
            periodNumber = 5,
            periodSpan = 2,
            startTime = "11:45 AM",
            endTime = "01:15 PM",
            subjectCode = "26711",
            subjectName = "Basic Electricity (BE)",
            teacherName = "Rubel Sir",
            roomNumber = "R-904",
            isPractical = false,
            note = "Theory lecture (Periods 5-6)"
        ),
        ClassSession(
            id = "mon_7_8",
            dayOfWeek = "Monday",
            periodNumber = 7,
            periodSpan = 2,
            startTime = "01:40 PM",
            endTime = "03:00 PM",
            subjectCode = "21011",
            subjectName = "Engineering Drawing (ED)",
            teacherName = "Imran Sir",
            roomNumber = "R-904",
            isPractical = true,
            note = "Practical session (Periods 7-8)"
        ),

        // Tuesday (2 morning classes: 08:45 to 11:45)
        ClassSession(
            id = "tue_1_2",
            dayOfWeek = "Tuesday",
            periodNumber = 1,
            periodSpan = 2,
            startTime = "08:45 AM",
            endTime = "10:15 AM",
            subjectCode = "25912",
            subjectName = "Phy-I",
            teacherName = "Mr. physics-1",
            roomNumber = "R-502 (Physics Lab)",
            isPractical = true,
            note = "Physics Lab practical (Periods 1-2)"
        ),
        ClassSession(
            id = "tue_3_4",
            dayOfWeek = "Tuesday",
            periodNumber = 3,
            periodSpan = 2,
            startTime = "10:15 AM",
            endTime = "11:45 AM",
            subjectCode = "25711",
            subjectName = "Bangla-I",
            teacherName = "Rakib Sir",
            roomNumber = "R-904",
            isPractical = false,
            note = "Theory lecture (Periods 3-4)"
        ),

        // Wednesday (Period 5-6 at 11:45 AM - 01:15 PM, Prayer Break, Period 7-8 at 01:40 PM - 03:00 PM)
        ClassSession(
            id = "wed_5_6",
            dayOfWeek = "Wednesday",
            periodNumber = 5,
            periodSpan = 2,
            startTime = "11:45 AM",
            endTime = "01:15 PM",
            subjectCode = "26811",
            subjectName = "Basic Electronics (B.Ent.)",
            teacherName = "Eng. Nisat Sir",
            roomNumber = "R-603 (Electronics Lab)",
            isPractical = true,
            note = "Electronics Lab practical (Periods 5-6)"
        ),
        ClassSession(
            id = "wed_7_8",
            dayOfWeek = "Wednesday",
            periodNumber = 7,
            periodSpan = 2,
            startTime = "01:40 PM",
            endTime = "03:00 PM",
            subjectCode = "25911",
            subjectName = "Math-I",
            teacherName = "Roman Sir",
            roomNumber = "R-904",
            isPractical = true,
            note = "Practical session (Periods 7-8)"
        ),

        // Thursday (3 continuous classes: 08:45 to 01:05)
        ClassSession(
            id = "thu_1_2",
            dayOfWeek = "Thursday",
            periodNumber = 1,
            periodSpan = 2,
            startTime = "08:45 AM",
            endTime = "10:15 AM",
            subjectCode = "26711",
            subjectName = "Basic Electricity (BE)",
            teacherName = "Rubel Sir",
            roomNumber = "R-604 (Electricity Lab)",
            isPractical = true,
            note = "Electricity Lab practical (Periods 1-2)"
        ),
        ClassSession(
            id = "thu_3_4",
            dayOfWeek = "Thursday",
            periodNumber = 3,
            periodSpan = 2,
            startTime = "10:15 AM",
            endTime = "11:45 AM",
            subjectCode = "25912",
            subjectName = "Phy-I",
            teacherName = "Mr. physics-1",
            roomNumber = "R-904",
            isPractical = false,
            note = "Theory lecture (Periods 3-4)"
        ),
        ClassSession(
            id = "thu_5_6",
            dayOfWeek = "Thursday",
            periodNumber = 5,
            periodSpan = 2,
            startTime = "11:45 AM",
            endTime = "01:05 PM",
            subjectCode = "25911",
            subjectName = "Math-I",
            teacherName = "Roman Sir",
            roomNumber = "R-904",
            isPractical = false,
            note = "Theory lecture (Periods 5-6, Thursday schedule)"
        )
    )

    val instituteInfo: InstituteInfo = InstituteInfo()

    val homeworkList: List<com.example.data.model.HomeworkItem> = listOf(
        com.example.data.model.HomeworkItem(
            id = "hw_1",
            subject = "MATHEMATICS",
            subjectCode = "25911",
            title = "Algebra & Functions — Practice Set 4",
            teacherName = "Roman Sir",
            dueDate = "Due tomorrow",
            progress = 0.75f,
            priority = "High",
            isCompleted = false
        ),
        com.example.data.model.HomeworkItem(
            id = "hw_2",
            subject = "BASIC ELECTRONICS",
            subjectCode = "26811",
            title = "Lab Experiment 2: Diode Characteristics Report",
            teacherName = "Eng. Nisat Sir",
            dueDate = "Due Fri, 22 Nov",
            progress = 0.5f,
            priority = "Low",
            isCompleted = false
        ),
        com.example.data.model.HomeworkItem(
            id = "hw_3",
            subject = "PHYSICS",
            subjectCode = "25912",
            title = "Ohm's Law & Vernier Calipers Worksheet",
            teacherName = "Mr. physics-1",
            dueDate = "Completed",
            progress = 1.0f,
            priority = "Done",
            isCompleted = true
        ),
        com.example.data.model.HomeworkItem(
            id = "hw_4",
            subject = "ENGINEERING DRAWING",
            subjectCode = "21011",
            title = "Sheet 3: Isometric & Orthographic Projection",
            teacherName = "Imran Sir",
            dueDate = "Due Mon, 25 Nov",
            progress = 0.2f,
            priority = "Low",
            isCompleted = false
        )
    )

    val commentsList: List<com.example.data.model.TeacherComment> = listOf(
        com.example.data.model.TeacherComment(
            id = "cm_1",
            teacherName = "Eng. Nisat Sir",
            subject = "Basic Electronics",
            comment = "\"Ensure to bring your practical logbook and circuit components to Room R-603 for Wednesday lab practical.\"",
            timeAgo = "2h"
        ),
        com.example.data.model.TeacherComment(
            id = "cm_2",
            teacherName = "Roman Sir",
            subject = "Mathematics-I",
            comment = "\"Great work on trigonometry formulas! Keep practicing problem set 3 before Sunday lecture in R-904.\"",
            timeAgo = "1d"
        )
    )
}
