package com.telematics.core.common.dialog

import android.app.DatePickerDialog
import android.content.Context
import android.widget.EditText
import com.telematics.core.common.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object EnterDateDropdownDialog {

    private val dataFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
    private val serverDataFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun getServerDate(card: EditText): String {
        return try {
            dataFormat.parse(card.text.toString())?.let {
                serverDataFormat.format(it)
            } ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    fun enterDate(context: Context, card: EditText) {
        val calendar = Calendar.getInstance()

        val date = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val date = dataFormat.format(calendar.time)
            card.setText(date)
        }

        card.setOnClickListener {
            // Set initial state
            try {

                val currentDate: Date =
                    dataFormat.parse(card.text.toString()) ?: Date(System.currentTimeMillis())

                calendar.time = currentDate
            } catch (parseException: ParseException) {
                calendar.time = Date(System.currentTimeMillis())
            }

            val dateDialog = DatePickerDialog(
                context,
                R.style.DatePicker,
                date,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            calendar.time = Date(System.currentTimeMillis())

            dateDialog.datePicker.maxDate = calendar.time.time

            dateDialog.show()
        }
    }

    fun parseDate(str: String?): Date =
        try {
            if (str == null) Date(0)
            else dataFormat.parse(str)
        } catch (e: Exception) {
            Date(0)
        }
}