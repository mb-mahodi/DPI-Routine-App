package com.example.data.parser

import com.example.data.datasource.DefaultRoutineData
import com.example.data.model.ClassSession
import com.example.data.model.InstituteInfo
import com.example.data.model.Subject
import com.example.data.model.Teacher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.StringReader
import java.util.concurrent.TimeUnit

object GoogleSheetParser {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private const val DEFAULT_CSV_URL =
        "https://docs.google.com/spreadsheets/d/1QksZyfT0WqYxV9zsaD7HwWuFPahxh_I_/export?format=csv&gid=222252652"

    suspend fun fetchAndParse(url: String = DEFAULT_CSV_URL): ParseResult = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext ParseResult.Error("HTTP ${response.code}: ${response.message}")
                }
                val body = response.body?.string() ?: return@withContext ParseResult.Error("Empty response body")
                parseCsv(body)
            }
        } catch (e: Exception) {
            ParseResult.Error("Sync failed: ${e.localizedMessage ?: "Unknown network error"}")
        }
    }

    fun parseCsv(csvText: String): ParseResult {
        try {
            val rows = parseCsvRows(csvText)
            if (rows.isEmpty()) {
                return ParseResult.Error("No data rows found in Google Sheet CSV")
            }

            val classes = mutableListOf<ClassSession>()
            val subjects = mutableListOf<Subject>()
            val teachers = mutableMapOf<String, Teacher>()

            // Default fallback lists to fall back to if sheet format slightly changes
            val defaultSubjects = DefaultRoutineData.subjects
            val defaultTeachers = DefaultRoutineData.teachers.associateBy { it.name }.toMutableMap()

            // 1. Parse routine rows
            // Day rows typically: Sunday (row ~16), Monday (row ~20), Tuesday (row ~24), Wednesday (row ~28), Thursday (row ~32)
            for (i in rows.indices) {
                val row = rows[i]
                if (row.isEmpty()) continue
                val firstCol = row[0].trim()

                if (firstCol in listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Saturday")) {
                    parseDayRow(firstCol, row, classes)
                }

                // 2. Parse Subject / Mark distribution table (rows with Sl. No 1..8)
                if (row.size >= 6) {
                    val sl = row[0].trim().toIntOrNull()
                    if (sl != null && sl in 1..20) {
                        // Sl, Teacher's Name, Subject Code, Name, T, P, C, Theory Cont, Theory Final, Practical Cont, Practical Final, Total
                        val teacherName = row.getOrNull(1)?.trim() ?: ""
                        val subCode = row.getOrNull(2)?.trim() ?: ""
                        val subName = row.getOrNull(3)?.trim() ?: ""
                        val tHours = row.getOrNull(4)?.trim()?.toIntOrNull() ?: 0
                        val pHours = row.getOrNull(5)?.trim()?.toIntOrNull() ?: 0
                        val credits = row.getOrNull(6)?.trim()?.toIntOrNull() ?: 0
                        val thCont = row.getOrNull(7)?.trim()?.toIntOrNull() ?: 0
                        val thFinal = row.getOrNull(8)?.trim()?.toIntOrNull() ?: 0
                        val prCont = row.getOrNull(9)?.trim()?.toIntOrNull() ?: 0
                        val prFinal = row.getOrNull(10)?.trim()?.toIntOrNull() ?: 0
                        val total = row.getOrNull(11)?.trim()?.toIntOrNull() ?: (thCont + thFinal + prCont + prFinal)

                        if (subCode.isNotEmpty() && subName.isNotEmpty()) {
                            val banglaName = defaultSubjects.find { it.code == subCode }?.banglaName ?: subName
                            subjects.add(
                                Subject(
                                    slNo = sl,
                                    code = subCode,
                                    name = subName,
                                    banglaName = banglaName,
                                    teacherName = teacherName,
                                    theoryHours = tHours,
                                    practicalHours = pHours,
                                    credits = credits,
                                    theoryCont = thCont,
                                    theoryFinal = thFinal,
                                    practicalCont = prCont,
                                    practicalFinal = prFinal,
                                    totalMarks = total
                                )
                            )
                        }
                    }
                }
            }

            val finalClasses = if (classes.isNotEmpty()) classes else DefaultRoutineData.classes
            val finalSubjects = if (subjects.isNotEmpty()) subjects else DefaultRoutineData.subjects
            val finalTeachers = DefaultRoutineData.teachers

            return ParseResult.Success(
                classes = finalClasses,
                subjects = finalSubjects,
                teachers = finalTeachers,
                instituteInfo = DefaultRoutineData.instituteInfo
            )
        } catch (e: Exception) {
            return ParseResult.Error("Parse error: ${e.message}")
        }
    }

    private fun parseDayRow(day: String, row: List<String>, outClasses: MutableList<ClassSession>) {
        // Col indices in CSV for DPI 1 EnT schedule:
        // Col 1: Period 1-2 (08:45 AM - 10:15 AM)
        // Col 3: Period 3-4 (10:15 AM - 11:45 AM)
        // Col 7: Period 5-6 (11:45 AM - 01:15 PM or 01:05 PM on Thursday)
        // Col 10: Period 7-8 (01:40 PM - 03:00 PM)

        val slotMap = mapOf(
            1 to Triple(1, "08:45 AM", "10:15 AM"),
            3 to Triple(3, "10:15 AM", "11:45 AM"),
            7 to Triple(5, "11:45 AM", if (day == "Thursday") "01:05 PM" else "01:15 PM"),
            10 to Triple(7, "01:40 PM", "03:00 PM")
        )

        for ((colIdx, periodInfo) in slotMap) {
            if (colIdx < row.size) {
                val cell = row[colIdx].trim()
                if (cell.isNotEmpty() && !cell.contains("Prayer Break", ignoreCase = true)) {
                    val parsedSession = parseCellContent(day, periodInfo.first, periodInfo.second, periodInfo.third, cell)
                    if (parsedSession != null) {
                        outClasses.add(parsedSession)
                    }
                }
            }
        }
    }

    private fun parseCellContent(
        day: String,
        period: Int,
        startTime: String,
        endTime: String,
        rawText: String
    ): ClassSession? {
        val lines = rawText.split("\n", "\r").map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.isEmpty()) return null

        val headerLine = lines[0] // e.g. "Math-I(25911)" or "Phy-I(25912)"
        val codeMatch = Regex("""\(([^)]+)\)""").find(headerLine)
        val subjectCode = codeMatch?.groupValues?.get(1) ?: ""
        val subjectName = headerLine.replace(Regex("""\([^)]*\)"""), "").trim()

        val teacher = lines.getOrNull(1) ?: ""
        val room = lines.getOrNull(2) ?: "R-904"
        val typeLine = lines.getOrNull(3) ?: if (rawText.contains("Practical", ignoreCase = true)) "Practical" else "Theory"
        val isPractical = typeLine.contains("Practical", ignoreCase = true)

        return ClassSession(
            id = "${day.lowercase().take(3)}_p${period}_${subjectCode}",
            dayOfWeek = day,
            periodNumber = period,
            periodSpan = 2,
            startTime = startTime,
            endTime = endTime,
            subjectCode = subjectCode,
            subjectName = subjectName,
            teacherName = teacher,
            roomNumber = room,
            isPractical = isPractical,
            note = if (isPractical) "Practical lab session (Periods $period-${period + 1})" else "Theory lecture (Periods $period-${period + 1})"
        )
    }

    private fun parseCsvRows(csvText: String): List<List<String>> {
        val result = mutableListOf<List<String>>()
        val currentField = StringBuilder()
        var currentRow = mutableListOf<String>()
        var inQuotes = false
        var i = 0
        while (i < csvText.length) {
            val c = csvText[i]
            if (c == '\"') {
                if (inQuotes && i + 1 < csvText.length && csvText[i + 1] == '\"') {
                    currentField.append('\"')
                    i++
                } else {
                    inQuotes = !inQuotes
                }
            } else if (c == ',' && !inQuotes) {
                currentRow.add(currentField.toString())
                currentField.setLength(0)
            } else if ((c == '\n' || c == '\r') && !inQuotes) {
                if (c == '\r' && i + 1 < csvText.length && csvText[i + 1] == '\n') {
                    i++
                }
                currentRow.add(currentField.toString())
                currentField.setLength(0)
                result.add(currentRow)
                currentRow = mutableListOf()
            } else {
                currentField.append(c)
            }
            i++
        }
        if (currentField.isNotEmpty() || currentRow.isNotEmpty()) {
            currentRow.add(currentField.toString())
            result.add(currentRow)
        }
        return result
    }

    sealed class ParseResult {
        data class Success(
            val classes: List<ClassSession>,
            val subjects: List<Subject>,
            val teachers: List<Teacher>,
            val instituteInfo: InstituteInfo
        ) : ParseResult()

        data class Error(val message: String) : ParseResult()
    }
}
