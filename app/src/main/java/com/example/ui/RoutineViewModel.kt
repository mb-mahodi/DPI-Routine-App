package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ClassSession
import com.example.data.model.RoutineState
import com.example.data.model.Subject
import com.example.data.model.Teacher
import com.example.data.repository.RoutineRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class RoutineFilter {
    ALL,
    THEORY_ONLY,
    PRACTICAL_ONLY
}

data class CurrentClassStatus(
    val activeClass: ClassSession? = null,
    val nextClass: ClassSession? = null,
    val progress: Float = 0f,
    val remainingMinutes: Int = 0,
    val isBreakTime: Boolean = false,
    val isCollegeOver: Boolean = false,
    val currentDayName: String = "Sunday",
    val liveFormattedDate: String = "",
    val liveFormattedDayDate: String = "",
    val liveShortDate: String = "",
    val liveGreeting: String = "Good morning",
    val completedCount: Int = 0,
    val remainingCount: Int = 0,
    val totalTodayCount: Int = 0,
    val currentMinutes: Int = 0
)

class RoutineViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RoutineRepository(application.applicationContext)

    val routineState: StateFlow<RoutineState> = repository.routineState
    val attendance: StateFlow<Map<String, Pair<Int, Int>>> = repository.attendance
    val subjectNotes: StateFlow<Map<String, String>> = repository.subjectNotes
    val studentProfile: StateFlow<com.example.data.model.StudentProfile> = repository.studentProfile

    private val _selectedDay = MutableStateFlow(getCurrentDayOfWeek())
    val selectedDay: StateFlow<String> = _selectedDay.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterType = MutableStateFlow(RoutineFilter.ALL)
    val filterType: StateFlow<RoutineFilter> = _filterType.asStateFlow()

    private val _currentStatus = MutableStateFlow(CurrentClassStatus())
    val currentStatus: StateFlow<CurrentClassStatus> = _currentStatus.asStateFlow()

    // Filtered classes for the currently selected day
    val filteredClasses: StateFlow<List<ClassSession>> = combine(
        routineState,
        _selectedDay,
        _searchQuery,
        _filterType
    ) { state, day, query, filter ->
        val dayClasses = state.classes.filter { it.dayOfWeek.equals(day, ignoreCase = true) }
        dayClasses.filter { session ->
            val matchesFilter = when (filter) {
                RoutineFilter.ALL -> true
                RoutineFilter.THEORY_ONLY -> !session.isPractical
                RoutineFilter.PRACTICAL_ONLY -> session.isPractical
            }
            val matchesQuery = query.isBlank() ||
                    session.subjectName.contains(query, ignoreCase = true) ||
                    session.subjectCode.contains(query, ignoreCase = true) ||
                    session.teacherName.contains(query, ignoreCase = true) ||
                    session.roomNumber.contains(query, ignoreCase = true)

            matchesFilter && matchesQuery
        }.sortedBy { it.periodNumber }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Ticker to update current class status every 30 seconds
        viewModelScope.launch {
            while (isActive) {
                updateClassStatus()
                delay(30_000)
            }
        }
    }

    fun selectDay(day: String) {
        _selectedDay.value = day
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterType(filter: RoutineFilter) {
        _filterType.value = filter
    }

    fun syncFromGoogleSheet() {
        viewModelScope.launch {
            repository.syncWithGoogleSheet()
            updateClassStatus()
        }
    }

    fun markAttendance(subjectCode: String, present: Boolean) {
        repository.markAttendance(subjectCode, present)
    }

    fun resetAttendance(subjectCode: String) {
        repository.resetAttendance(subjectCode)
    }

    fun saveNote(subjectCode: String, text: String) {
        repository.saveNote(subjectCode, text)
    }



    fun updateStudentProfile(profile: com.example.data.model.StudentProfile) {
        repository.updateStudentProfile(profile)
    }

    private fun updateClassStatus() {
        val calendar = Calendar.getInstance()
        val dayOfWeek = getCurrentDayOfWeek()
        val currentMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        val greeting = when (hour) {
            in 4..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..20 -> "Good evening"
            else -> "Good night"
        }

        val liveFormattedDate = SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH).format(calendar.time)
        val liveFormattedDayDate = SimpleDateFormat("EEEE, d MMMM", Locale.ENGLISH).format(calendar.time)
        val liveShortDate = SimpleDateFormat("d MMMM", Locale.ENGLISH).format(calendar.time)

        val todayClasses = routineState.value.classes
            .filter { it.dayOfWeek.equals(dayOfWeek, ignoreCase = true) }
            .sortedBy { it.periodNumber }

        var active: ClassSession? = null
        var next: ClassSession? = null
        var progress = 0f
        var remMin = 0
        var isBreak = false
        var isOver = false
        var completedCount = 0
        var remainingCount = 0

        // Check Prayer & Tiffin Break: 01:15 PM - 01:40 PM (795 to 820 minutes)
        val hasAfternoonClasses = todayClasses.any { it.periodNumber >= 7 }
        if (hasAfternoonClasses && currentMinutes in 795..820) {
            isBreak = true
            remMin = 820 - currentMinutes
        }

        for (session in todayClasses) {
            val startMin = parseTimeToMinutes(session.startTime)
            val endMin = parseTimeToMinutes(session.endTime)

            if (currentMinutes >= endMin) {
                completedCount++
            } else {
                remainingCount++
            }

            if (currentMinutes in startMin until endMin) {
                active = session
                val duration = endMin - startMin
                val elapsed = currentMinutes - startMin
                progress = if (duration > 0) elapsed.toFloat() / duration else 0f
                remMin = endMin - currentMinutes
            } else if (currentMinutes < startMin && next == null) {
                next = session
            }
        }

        if (todayClasses.isNotEmpty()) {
            val lastClassEnd = parseTimeToMinutes(todayClasses.last().endTime)
            if (currentMinutes >= lastClassEnd) {
                isOver = true
            }
        } else {
            isOver = true
        }

        _currentStatus.value = CurrentClassStatus(
            activeClass = active,
            nextClass = next,
            progress = progress.coerceIn(0f, 1f),
            remainingMinutes = remMin,
            isBreakTime = isBreak,
            isCollegeOver = isOver,
            currentDayName = dayOfWeek,
            liveFormattedDate = liveFormattedDate,
            liveFormattedDayDate = liveFormattedDayDate,
            liveShortDate = liveShortDate,
            liveGreeting = greeting,
            completedCount = completedCount,
            remainingCount = remainingCount,
            totalTodayCount = todayClasses.size,
            currentMinutes = currentMinutes
        )

        try {
            com.example.widget.TodayRoutineWidgetProvider.updateAllWidgets(getApplication())
            com.example.widget.QuickStatsWidgetProvider.updateAllWidgets(getApplication())
        } catch (_: Exception) {}
    }

    fun parseTimeToMinutes(timeStr: String): Int {
        // e.g. "08:45 AM" or "01:15 PM"
        try {
            val parts = timeStr.trim().split(" ")
            if (parts.size >= 2) {
                val hm = parts[0].split(":")
                var hour = hm[0].toInt()
                val min = hm[1].toInt()
                val ampm = parts[1].uppercase(Locale.getDefault())
                if (ampm == "PM" && hour < 12) hour += 12
                if (ampm == "AM" && hour == 12) hour = 0
                return hour * 60 + min
            }
        } catch (_: Exception) {}
        return 0
    }

    companion object {
        fun getCurrentDayOfWeek(): String {
            val cal = Calendar.getInstance()
            return when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> "Sunday"
                Calendar.MONDAY -> "Monday"
                Calendar.TUESDAY -> "Tuesday"
                Calendar.WEDNESDAY -> "Wednesday"
                Calendar.THURSDAY -> "Thursday"
                Calendar.FRIDAY -> "Friday"
                Calendar.SATURDAY -> "Saturday"
                else -> "Sunday"
            }
        }
    }
}
