package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testParseTimeToMinutes() {
    fun parseTimeToMinutes(timeStr: String): Int {
      val parts = timeStr.trim().split(" ")
      val hm = parts[0].split(":")
      var hour = hm[0].toInt()
      val min = hm[1].toInt()
      val ampm = parts[1].uppercase()
      if (ampm == "PM" && hour < 12) hour += 12
      if (ampm == "AM" && hour == 12) hour = 0
      return hour * 60 + min
    }

    assertEquals(525, parseTimeToMinutes("08:45 AM")) // 8*60+45 = 525
    assertEquals(615, parseTimeToMinutes("10:15 AM")) // 10*60+15 = 615
    assertEquals(795, parseTimeToMinutes("01:15 PM")) // 13*60+15 = 795
    assertEquals(900, parseTimeToMinutes("03:00 PM")) // 15*60+0 = 900
  }

  @Test
  fun testClassCompletedAndRemainingCalculation() {
    val times = listOf(
      "08:45 AM" to "10:15 AM",
      "10:15 AM" to "11:45 AM",
      "11:45 AM" to "01:15 PM"
    )

    fun calculate(currentMinutes: Int): Pair<Int, Int> {
      var done = 0
      var remaining = 0
      for ((_, end) in times) {
        val endMin = when (end) {
          "10:15 AM" -> 615
          "11:45 AM" -> 705
          "01:15 PM" -> 795
          else -> 0
        }
        if (currentMinutes >= endMin) done++ else remaining++
      }
      return done to remaining
    }

    // At 8:00 AM (before classes start)
    assertEquals(0 to 3, calculate(480))
    // At 10:30 AM (after 1st class finished)
    assertEquals(1 to 2, calculate(630))
    // At 12:00 PM (after 2nd class finished)
    assertEquals(2 to 1, calculate(720))
    // At 2:00 PM (all 3 classes finished)
    assertEquals(3 to 0, calculate(840))
  }
}
