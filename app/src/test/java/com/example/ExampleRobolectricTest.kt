package com.example

import com.example.data.datasource.DefaultRoutineData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

    @Test
    fun `verify app name`() {
        val appName = "DPI Routine"
        assertEquals("DPI Routine", appName)
    }

    @Test
    fun `verify subjects curriculum data`() {
        val subjects = DefaultRoutineData.subjects
        assertEquals(8, subjects.size)
        val totalCredits = subjects.sumOf { it.credits }
        assertEquals(24, totalCredits)
        val totalMarks = subjects.sumOf { it.totalMarks }
        assertEquals(1200, totalMarks)
    }

    @Test
    fun `verify guide teacher details`() {
        val guide = DefaultRoutineData.teachers.find { it.isGuideTeacher }
        assertNotNull(guide)
        assertEquals("Eng. Abdullah Al Nisat", guide?.name)
        assertTrue(guide?.phone?.contains("177235524") == true)
    }

    @Test
    fun `verify classes routine schedule`() {
        val classes = DefaultRoutineData.classes
        assertTrue(classes.isNotEmpty())
        val days = classes.map { it.dayOfWeek }.distinct()
        assertTrue(days.contains("Sunday"))
        assertTrue(days.contains("Monday"))
        assertTrue(days.contains("Tuesday"))
        assertTrue(days.contains("Wednesday"))
        assertTrue(days.contains("Thursday"))
    }
}
