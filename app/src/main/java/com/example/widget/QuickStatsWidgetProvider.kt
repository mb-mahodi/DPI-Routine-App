package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.datasource.DefaultRoutineData
import com.example.data.model.ClassSession
import java.util.Calendar
import java.util.Locale

class QuickStatsWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_quick_stats)

            val launchIntent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                1,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.quick_widget_container, pendingIntent)

            val cal = Calendar.getInstance()
            val dayOfWeek = getCurrentDayOfWeek(cal)
            val currentMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)

            val todayClasses = DefaultRoutineData.classes
                .filter { it.dayOfWeek.equals(dayOfWeek, ignoreCase = true) }

            var completedCount = 0
            var remainingCount = 0
            var activeClass: ClassSession? = null

            for (session in todayClasses) {
                val startMin = parseTimeToMinutes(session.startTime)
                val endMin = parseTimeToMinutes(session.endTime)

                if (currentMinutes >= endMin) {
                    completedCount++
                } else {
                    remainingCount++
                }

                if (currentMinutes in startMin until endMin) {
                    activeClass = session
                }
            }

            views.setTextViewText(R.id.quick_widget_done_count, "$completedCount")
            views.setTextViewText(R.id.quick_widget_left_count, "$remainingCount")

            val statusText = when {
                todayClasses.isEmpty() -> "Holiday Off Day"
                activeClass != null -> "Live: ${activeClass.subjectName}"
                remainingCount > 0 -> "$remainingCount classes left"
                else -> "All Done 🎉"
            }
            views.setTextViewText(R.id.quick_widget_status, statusText)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun getCurrentDayOfWeek(cal: Calendar): String {
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

        private fun parseTimeToMinutes(timeStr: String): Int {
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

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, QuickStatsWidgetProvider::class.java)
            val ids = appWidgetManager.getAppWidgetIds(componentName)
            for (id in ids) {
                updateAppWidget(context, appWidgetManager, id)
            }
        }
    }
}
