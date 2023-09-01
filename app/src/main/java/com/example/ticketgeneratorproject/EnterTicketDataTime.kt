package com.example.ticketgeneratorproject

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.ticketgeneratorproject.DataBase.DataBaseAdapter
import com.example.ticketgeneratorproject.Entities.DateTime
import com.example.ticketgeneratorproject.Entities.TicketModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class EnterTicketDataTime: AppCompatActivity() {
    private lateinit var departureDateText: TextView
    private lateinit var departureTimeText: TextView
    private lateinit var destinationDateText: TextView
    private lateinit var destinationTimeText: TextView

    private lateinit var errorText1: TextView
    private lateinit var errorText2: TextView
    private lateinit var errorText3: TextView
    private lateinit var errorText4: TextView

    private lateinit var error_icon_1: ImageView
    private lateinit var error_icon_2: ImageView
    private lateinit var error_icon_3: ImageView
    private lateinit var error_icon_4: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_ticket_data_time)

        departureDateText = findViewById(R.id.departure_date)
        destinationDateText = findViewById(R.id.destination_date)
        departureTimeText = findViewById(R.id.departure_time)
        destinationTimeText = findViewById(R.id.destination_time)

        errorText1 = findViewById(R.id.errorText1)
        errorText2 = findViewById(R.id.errorText2)
        errorText3 = findViewById(R.id.errorText3)
        errorText4 = findViewById(R.id.errorText4)

        error_icon_1 = findViewById(R.id.error_icon_1)
        error_icon_2 = findViewById(R.id.error_icon_2)
        error_icon_3 = findViewById(R.id.error_icon_3)
        error_icon_4 = findViewById(R.id.error_icon_4)

        var datePickerState = -1
        var timePickerState = -1
        val myCalendar = Calendar.getInstance()

        var flag = true

        var dbAdapter = DataBaseAdapter(this)

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

        departureDateText.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            if(departureDateText.text!!.length>0){
                errorText1.visibility = View.INVISIBLE
                error_icon_1.visibility = View.INVISIBLE
                flag = true
            }
        }

        departureTimeText.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            if(departureDateText.text!!.length>0){
                errorText2.visibility = View.INVISIBLE
                error_icon_2.visibility = View.INVISIBLE
                flag = true
            }
        }

        destinationDateText.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            if(departureDateText.text!!.length>0){
                errorText3.visibility = View.INVISIBLE
                error_icon_3.visibility = View.INVISIBLE
                flag = true
            }
        }

        destinationTimeText.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            if(departureDateText.text!!.length>0){
                errorText4.visibility = View.INVISIBLE
                error_icon_4.visibility = View.INVISIBLE
                flag = true
            }
        }

        val ticket = intent.getSerializableExtra("Object") as TicketModel

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

        findViewById<Button>(R.id.generate_ticket).setOnClickListener {
            if(departureDateText.text.isEmpty()){
                errorText1.visibility = View.VISIBLE
                error_icon_1.visibility = View.VISIBLE
                flag = false
            }
            if(departureTimeText.text.isEmpty()){
                errorText2.visibility = View.VISIBLE
                error_icon_2.visibility = View.VISIBLE
                flag = false
            }
            if(destinationDateText.text.isEmpty()){
                errorText3.visibility = View.VISIBLE
                error_icon_3.visibility = View.VISIBLE
                flag = false
            }
            if(destinationTimeText.text.isEmpty()){
                errorText4.visibility = View.VISIBLE
                error_icon_4.visibility = View.VISIBLE
                flag = false
            }
            if(flag){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

                ticket.departureTime = DateTime.parseDateTime("${departureDateText.text} ${departureTimeText.text}")
                ticket.destinationTime = DateTime.parseDateTime("${destinationDateText.text} ${destinationTimeText.text}")
                ticket.purchaseTime = DateTime.parseDateTime(getCurrentDateTime())

                Log.d("Ticket: ", "${ticket.currency}")
                dbAdapter.addTicket(ticket)
                Toast.makeText(applicationContext, "Квиток був успішно створений", Toast.LENGTH_SHORT).show()
            }
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

    fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }
}