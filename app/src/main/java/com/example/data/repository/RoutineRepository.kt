package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.datasource.DefaultRoutineData
import com.example.data.local.*
import com.example.data.model.ClassSession
import com.example.data.model.RoutineState
import com.example.data.model.Subject
import com.example.data.model.Teacher
import com.example.data.parser.GoogleSheetParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RoutineRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("dpi_routine_prefs", Context.MODE_PRIVATE)

    private val db = RoutineDatabase.getInstance(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _routineState = MutableStateFlow(
        RoutineState(
            classes = DefaultRoutineData.classes,
            subjects = DefaultRoutineData.subjects,
            teachers = DefaultRoutineData.teachers,
            instituteInfo = DefaultRoutineData.instituteInfo,
            homeworkList = emptyList(),
            commentsList = DefaultRoutineData.commentsList,
            lastSyncTime = prefs.getString("last_sync_time", "Pre-loaded Official Routine") ?: "Pre-loaded Official Routine"
        )
    )
    val routineState: StateFlow<RoutineState> = _routineState.asStateFlow()

    init {
        // Initialize Room DB cache with DefaultRoutineData if DB is empty, then observe DB changes
        scope.launch {
            db.classSessionDao().getAllClasses().collectLatest { entities ->
                if (entities.isEmpty()) {
                    // Seed initial data into Room DB
                    cacheRoutineData(
                        DefaultRoutineData.classes,
                        DefaultRoutineData.subjects,
                        DefaultRoutineData.teachers
                    )
                } else {
                    _routineState.value = _routineState.value.copy(
                        classes = entities.map { it.toModel() }
                    )
                }
            }
        }

        scope.launch {
            db.subjectDao().getAllSubjects().collectLatest { entities ->
                if (entities.isNotEmpty()) {
                    _routineState.value = _routineState.value.copy(
                        subjects = entities.map { it.toModel() }
                    )
                }
            }
        }

        scope.launch {
            db.teacherDao().getAllTeachers().collectLatest { entities ->
                if (entities.isNotEmpty()) {
                    _routineState.value = _routineState.value.copy(
                        teachers = entities.map { it.toModel() }
                    )
                }
            }
        }
    }

    private suspend fun cacheRoutineData(
        classes: List<ClassSession>,
        subjects: List<Subject>,
        teachers: List<Teacher>
    ) {
        db.classSessionDao().deleteAll()
        db.classSessionDao().insertAll(classes.map { ClassSessionEntity.fromModel(it) })

        db.subjectDao().deleteAll()
        db.subjectDao().insertAll(subjects.map { SubjectEntity.fromModel(it) })

        db.teacherDao().deleteAll()
        db.teacherDao().insertAll(teachers.map { TeacherEntity.fromModel(it) })
    }

    // Attendance map: subjectCode -> Pair(attended, total)
    private val _attendance = MutableStateFlow<Map<String, Pair<Int, Int>>>(loadAttendance())
    val attendance: StateFlow<Map<String, Pair<Int, Int>>> = _attendance.asStateFlow()

    // Subject Notes: subjectCode -> note text
    private val _subjectNotes = MutableStateFlow<Map<String, String>>(loadSubjectNotes())
    val subjectNotes: StateFlow<Map<String, String>> = _subjectNotes.asStateFlow()

    // Student Profile
    private val _studentProfile = MutableStateFlow(loadStudentProfile())
    val studentProfile: StateFlow<com.example.data.model.StudentProfile> = _studentProfile.asStateFlow()

    fun updateStudentProfile(profile: com.example.data.model.StudentProfile) {
        _studentProfile.value = profile
        prefs.edit()
            .putString("profile_name", profile.name)
            .putString("profile_roll", profile.rollNo)
            .putString("profile_reg", profile.regNo)
            .putString("profile_shift", profile.shift)
            .putString("profile_group", profile.group)
            .putString("profile_phone", profile.phone)
            .putString("profile_email", profile.email)
            .apply()
    }

    suspend fun syncWithGoogleSheet(): Boolean {
        _routineState.value = _routineState.value.copy(isSyncing = true, syncError = null)
        val result = GoogleSheetParser.fetchAndParse()
        return when (result) {
            is GoogleSheetParser.ParseResult.Success -> {
                val timeStamp = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
                prefs.edit().putString("last_sync_time", timeStamp).apply()

                // Save into Room DB cache for offline accessibility
                cacheRoutineData(result.classes, result.subjects, result.teachers)

                _routineState.value = _routineState.value.copy(
                    classes = result.classes,
                    subjects = result.subjects,
                    teachers = result.teachers,
                    instituteInfo = result.instituteInfo,
                    isSyncing = false,
                    lastSyncTime = timeStamp,
                    syncError = null
                )
                true
            }
            is GoogleSheetParser.ParseResult.Error -> {
                _routineState.value = _routineState.value.copy(
                    isSyncing = false,
                    syncError = result.message
                )
                false
            }
        }
    }

    fun markAttendance(subjectCode: String, present: Boolean) {
        val current = _attendance.value[subjectCode] ?: Pair(0, 0)
        val updated = if (present) {
            Pair(current.first + 1, current.second + 1)
        } else {
            Pair(current.first, current.second + 1)
        }
        val newMap = _attendance.value.toMutableMap().apply {
            put(subjectCode, updated)
        }
        _attendance.value = newMap
        saveAttendance(newMap)
    }

    fun resetAttendance(subjectCode: String) {
        val newMap = _attendance.value.toMutableMap().apply {
            remove(subjectCode)
        }
        _attendance.value = newMap
        saveAttendance(newMap)
    }

    fun saveNote(subjectCode: String, text: String) {
        val newMap = _subjectNotes.value.toMutableMap().apply {
            if (text.isBlank()) remove(subjectCode) else put(subjectCode, text.trim())
        }
        _subjectNotes.value = newMap
        saveSubjectNotes(newMap)
    }

    private fun loadAttendance(): Map<String, Pair<Int, Int>> {
        val map = mutableMapOf<String, Pair<Int, Int>>()
        for (sub in DefaultRoutineData.subjects) {
            val att = prefs.getInt("att_${sub.code}", -1)
            val tot = prefs.getInt("tot_${sub.code}", -1)
            if (att >= 0 && tot >= 0) {
                map[sub.code] = Pair(att, tot)
            }
        }
        return map
    }

    private fun saveAttendance(map: Map<String, Pair<Int, Int>>) {
        val editor = prefs.edit()
        for ((code, pair) in map) {
            editor.putInt("att_$code", pair.first)
            editor.putInt("tot_$code", pair.second)
        }
        editor.apply()
    }

    private fun loadSubjectNotes(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for (sub in DefaultRoutineData.subjects) {
            val note = prefs.getString("note_${sub.code}", null)
            if (!note.isNullOrBlank()) {
                map[sub.code] = note
            }
        }
        return map
    }

    private fun saveSubjectNotes(map: Map<String, String>) {
        val editor = prefs.edit()
        for (sub in DefaultRoutineData.subjects) {
            val text = map[sub.code]
            if (text != null) {
                editor.putString("note_${sub.code}", text)
            } else {
                editor.remove("note_${sub.code}")
            }
        }
        editor.apply()
    }

    private fun loadStudentProfile(): com.example.data.model.StudentProfile {
        return com.example.data.model.StudentProfile(
            name = prefs.getString("profile_name", "Mahodi Billa") ?: "Mahodi Billa",
            rollNo = prefs.getString("profile_roll", "642109") ?: "642109",
            regNo = prefs.getString("profile_reg", "1502489123") ?: "1502489123",
            department = "1st Electronics Technology",
            semester = "1st Semester",
            shift = prefs.getString("profile_shift", "1st Shift") ?: "1st Shift",
            group = prefs.getString("profile_group", "Group A") ?: "Group A",
            institute = "Daffodil Polytechnic Institute (Code: 50238)",
            campus = "1 Sobhanbag, Mirpur Road, Dhaka 1207",
            email = prefs.getString("profile_email", "mahodibilla106@gmail.com") ?: "mahodibilla106@gmail.com",
            phone = prefs.getString("profile_phone", "+8801713-493158") ?: "+8801713-493158",
            sessionYear = "August 2026 - January 2027",
            guideTeacher = "Eng. Abdullah Al Nisat (0177235524)"
        )
    }
}
