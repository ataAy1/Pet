package com.app.kayipetapp.util

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import com.app.kayipetapp.domain.models.DateTime
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateUtil {

    fun showDatePicker(
        context: Context,
        onDateSelected: (year: Int, month: Int, day: Int, formattedDate: String, dayOfWeek: String) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val daysOfWeek = arrayOf(
            "Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar"
        )

        val datePickerDialog = DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedCalendar = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay)
            }

            val dateFormatSymbols = DateFormatSymbols.getInstance(Locale("tr"))
            val monthName = dateFormatSymbols.months[selectedMonth]

            val dayOfWeek = if (selectedCalendar.get(Calendar.DAY_OF_WEEK) - 2 <= -1) {
                "Pazar"
            } else {
                daysOfWeek[selectedCalendar.get(Calendar.DAY_OF_WEEK) - 2]
            }

            val formattedDate = "$dayOfWeek, $selectedDay $monthName, $selectedYear"

            onDateSelected(selectedYear, selectedMonth, selectedDay, formattedDate, dayOfWeek)
        }, year, month, day)

        datePickerDialog.show()
    }

    fun showTimePicker(
        context: Context,
        onTimeSelected: (hour: String, minute: String, formattedTime: String) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(context, { _, selectedHour, selectedMinute ->
            val formattedHour = if (selectedHour < 10) "0$selectedHour" else selectedHour.toString()
            val formattedMinute = if (selectedMinute < 10) "0$selectedMinute" else selectedMinute.toString()

            val formattedTime = "$formattedHour:$formattedMinute"

            onTimeSelected(formattedHour, formattedMinute, formattedTime)
        }, hour, minute, true)

        timePickerDialog.show()
    }

        fun DateTime.toCalendar(): Calendar {
            val calendar = Calendar.getInstance()
            calendar.set(
                year ?: 0,
                month ?: 0,
                day ?: 0,
                hour?.toIntOrNull() ?: 0,
                minute?.toIntOrNull() ?: 0
            )
            return calendar
        }


    fun formatDate(calendar: Calendar, locale: Locale = Locale.getDefault()): String {
        val dateFormat = SimpleDateFormat("EEEE, dd/MM/yyyy", locale)
        return dateFormat.format(calendar.time)
    }
}
