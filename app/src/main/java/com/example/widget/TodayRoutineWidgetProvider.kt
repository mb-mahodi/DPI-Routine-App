package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.datasource.DefaultRoutineData
import com.example.data.model.ClassSession
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TodayRoutineWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisAppWidget = ComponentName(context.packageName, TodayRoutineWidgetProvider::class.java.name)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
            onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }

    companion object {
        const val ACTION_REFRESH = "com.example.widget.ACTION_REFRESH_ROUTINE"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_today_routine)

            // Intent to launch MainActivity when clicking widget
            val launchIntent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)

            val cal = Calendar.getInstance()
            val dayOfWeek = getCurrentDayOfWeek(cal)
            val currentMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
            val dateFmt = SimpleDateFormat("EEE, d MMM", Locale.ENGLISH).format(cal.time)

            views.setTextViewText(R.id.widget_day_date, "$dayOfWeek • $dateFmt")

            val todayClasses = DefaultRoutineData.classes
                .filter { it.dayOfWeek.equals(dayOfWeek, ignoreCase = true) }
                .sortedBy { it.periodNumber }

            var completedCount = 0
            var remainingCount = 0
            var activeClass: ClassSession? = null
            var nextClass: ClassSession? = null

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
                } else if (currentMinutes < startMin && nextClass == null) {
                    nextClass = session
                }
            }

            val totalCount = todayClasses.size
            val progressPercent = if (totalCount == 0) 0 else (completedCount * 100 / totalCount)

            views.setTextViewText(R.id.widget_completed_txt, "Done: $completedCount")
            views.setTextViewText(R.id.widget_remaining_txt, "Left: $remainingCount")
            views.setProgressBar(R.id.widget_progress_bar, 100, progressPercent, false)

            when {
                todayClasses.isEmpty() -> {
                    views.setTextViewText(R.id.widget_status_badge, "OFF DAY")
                    views.setTextViewText(R.id.widget_class_status_label, "WEEKEND / HOLIDAY")
                    views.setTextViewText(R.id.widget_subject_name, "No Classes Scheduled Today")
                    views.setTextViewText(R.id.widget_class_details, "Enjoy your day off or review notes!")
                    views.setTextViewText(R.id.widget_teacher_name, "DPI Electronics Technology")
                }
                activeClass != null -> {
                    views.setTextViewText(R.id.widget_status_badge, "LIVE NOW")
                    views.setTextViewText(R.id.widget_class_status_label, "CURRENTLY IN SESSION (Period ${activeClass.periodNumber})")
                    views.setTextViewText(R.id.widget_subject_name, activeClass.subjectName)
                    views.setTextViewText(
                        R.id.widget_class_details,
                        "${activeClass.startTime} - ${activeClass.endTime} • Room ${activeClass.roomNumber}"
                    )
                    views.setTextViewText(R.id.widget_teacher_name, "Teacher: ${activeClass.teacherName}")
                }
                nextClass != null -> {
                    views.setTextViewText(R.id.widget_status_badge, "UPCOMING")
                    views.setTextViewText(R.id.widget_class_status_label, "NEXT CLASS (Period ${nextClass.periodNumber})")
                    views.setTextViewText(R.id.widget_subject_name, nextClass.subjectName)
                    views.setTextViewText(
                        R.id.widget_class_details,
                        "${nextClass.startTime} - ${nextClass.endTime} • Room ${nextClass.roomNumber}"
                    )
                    views.setTextViewText(R.id.widget_teacher_name, "Teacher: ${nextClass.teacherName}")
                }
                else -> {
                    views.setTextViewText(R.id.widget_status_badge, "FINISHED")
                    views.setTextViewText(R.id.widget_class_status_label, "ALL CLASSES FINISHED")
                    views.setTextViewText(R.id.widget_subject_name, "Completed All $totalCount Classes 🎉")
                    views.setTextViewText(R.id.widget_class_details, "See you tomorrow!")
                    views.setTextViewText(R.id.widget_teacher_name, "DPI Electronics Technology")
                }
            }

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
            val componentName = ComponentName(context, TodayRoutineWidgetProvider::class.java)
            val ids = appWidgetManager.getAppWidgetIds(componentName)
            for (id in ids) {
                updateAppWidget(context, appWidgetManager, id)
            }
        }
    }
}
