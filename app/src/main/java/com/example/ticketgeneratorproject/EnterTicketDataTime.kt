package com.example.ticketgeneratorproject

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class EnterTicketDataTime : AppCompatActivity() {
    private lateinit var departureDateText: TextView
    private lateinit var departureTimeText: TextView
    private lateinit var destinationDateText: TextView
    private lateinit var destinationTimeText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_ticket_data_time)

        departureDateText = findViewById(R.id.departure_date)
        destinationDateText = findViewById(R.id.destination_date)
        departureTimeText = findViewById(R.id.departure_time)
        destinationTimeText = findViewById(R.id.destination_time)

        var datePickerState = -1
        var timePickerState = -1
        val myCalendar = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            when (datePickerState){
                1->updateDateText(myCalendar, departureDateText)
                2->updateDateText(myCalendar, destinationDateText)
                else -> Log.d("processing", "problem")
            }
        }

        val timePickerListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            myCalendar.set(Calendar.MINUTE, minute)
            when (timePickerState){
                1->updateTimeText(myCalendar, departureTimeText)
                2->updateTimeText(myCalendar, destinationTimeText)
                else -> Log.d("processing", "problem")
            }
        }

        findViewById<RelativeLayout>(R.id.btn_departure_date).setOnClickListener {
            DatePickerDialog( this, R.style.CustomDatePickerDialogTheme, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show()
            datePickerState = 1
        }

        findViewById<RelativeLayout>(R.id.btn_destination_date).setOnClickListener {
            DatePickerDialog( this, R.style.CustomDatePickerDialogTheme, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show()
            datePickerState = 2
        }

        findViewById<RelativeLayout>(R.id.btn_departure_time).setOnClickListener {
            TimePickerDialog(this, R.style.CustomDatePickerDialogTheme, timePickerListener, myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE), true).show()
            timePickerState = 1
        }

        findViewById<RelativeLayout>(R.id.btn_destination_time).setOnClickListener {
            TimePickerDialog(this, R.style.CustomDatePickerDialogTheme, timePickerListener, myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE), true).show()
            timePickerState = 2
        }

        findViewById<LinearLayout>(R.id.back_to_previous_page).setOnClickListener{
            finish()
        }
    }

    private fun updateDateText (myCalendar: Calendar, view: TextView) {
        val sdf = SimpleDateFormat ("dd-MM-yyyy", Locale.UK)
        view.setText(sdf.format(myCalendar.time))
    }
    private fun updateTimeText (myCalendar: Calendar, view: TextView) {
        val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(myCalendar.time)
        view.setText(formattedTime.format(myCalendar.time))
    }
}