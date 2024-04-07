package com.example.ticketgeneratorproject.Presentation


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.ticketgeneratorproject.Data.DataBaseAdapter
import com.example.ticketgeneratorproject.Business.Controllers.ProfileController
import com.example.ticketgeneratorproject.Data.Entities.Currency
import com.example.ticketgeneratorproject.Data.Entities.DateTime
import com.example.ticketgeneratorproject.Data.Entities.TicketModel
import com.example.ticketgeneratorproject.R
import com.example.ticketgeneratorproject.Business.Controllers.TicketController.createPdfForTicket
import com.example.ticketgeneratorproject.Business.Controllers.TimeController.getCurrentDateTime
import com.example.ticketgeneratorproject.Business.Controllers.TicketController.getUniqueIdByTicket
import com.example.ticketgeneratorproject.Business.Controllers.FileController.writeFileToStorage
import com.example.ticketgeneratorproject.Business.Controllers.TicketController
import com.example.ticketgeneratorproject.databinding.AddTicketPage2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.File
import java.util.*

class AddTicketPage2: AppCompatActivity() {
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var dbAdapter: DataBaseAdapter

    private lateinit var binding: AddTicketPage2Binding

    private lateinit var ticket: TicketModel
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddTicketPage2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        dbAdapter = DataBaseAdapter(this)

        val uid = firebaseAuth.currentUser!!.uid
        val ticketsReference = firebaseDatabase.getReference("users").child(uid).child("tickets")
        val addressesReference = firebaseDatabase.getReference("users").child(uid).child("commonAddresses")

        val items = listOf("₴ Гривня", "\$ Долар", "€ Євро")
        val adapter = ArrayAdapter(this, R.layout.currency_item, items)
        binding.currency.setAdapter(adapter)

        val intentHasExtraToUpdate = intent.hasExtra("EnterTicketData_TO_EnterTicketDataTime_TicketData_Update")
        val intentHasExtraToCreateSimilar = intent.hasExtra("EnterTicketData_TO_EnterTicketDataTime_TicketData_CreateSimilar")
        ticket = if (intentHasExtraToUpdate) {
            intent.getSerializableExtra("EnterTicketData_TO_EnterTicketDataTime_TicketData_Update")
                    as TicketModel
        } else if (intentHasExtraToCreateSimilar) {
            intent.getSerializableExtra("EnterTicketData_TO_EnterTicketDataTime_TicketData_CreateSimilar")
                    as TicketModel
        } else {
            intent.getSerializableExtra("EnterTicketData_TO_EnterTicketDataTime_TicketData_Complete")
                    as TicketModel
        }
        if(intentHasExtraToUpdate || intentHasExtraToCreateSimilar) {
            binding.departureDate.text = ticket.departureDateTime.date
            binding.departureTime.text = ticket.departureDateTime.time
            binding.destinationDate.text = ticket.destinationDateTime.date
            binding.destinationTime.text = ticket.destinationDateTime.time
            binding.price.setText(ticket.price.toString())
            binding.currency.setText(Currency.parseToString(ticket.currency), false)
        }

        var hasInputtingErrors = false
        var datePickerState = -1
        var timePickerState = -1
        val myCalendar = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            when (datePickerState){
                1->updateDateText(myCalendar, binding.departureDate)
                2->updateDateText(myCalendar, binding.destinationDate)
                else->Log.e("DatePicker", "Illegal argument was set")
            }
        }
        val timePicker = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            myCalendar.set(Calendar.MINUTE, minute)
            when (timePickerState){
                1->updateTimeText(myCalendar, binding.departureTime)
                2->updateTimeText(myCalendar, binding.destinationTime)
                else->Log.e("TimePicker", "Illegal argument was set")
            }
        }

        binding.price.addTextChangedListener {
            if(it!!.count()>0){
                binding.priceLayout.error = null
                hasInputtingErrors = false
            }
        }
        binding.currency.addTextChangedListener {
            if(it!!.count()>0){
                binding.currencyLayout.error = null
                hasInputtingErrors = false
            }
        }
        binding.departureDate.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            if(binding.departureDate.text!!.isNotEmpty()){
                binding.errorText1.visibility = View.INVISIBLE
                binding.errorIcon1.visibility = View.INVISIBLE
                hasInputtingErrors = false
            }
        }
        binding.departureTime.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            if(binding.departureDate.text!!.isNotEmpty()){
                binding.errorText2.visibility = View.INVISIBLE
                binding.errorIcon2.visibility = View.INVISIBLE
                hasInputtingErrors = false
            }
        }
        binding.destinationDate.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            if(binding.destinationDate.text!!.isNotEmpty()){
                binding.errorText3.visibility = View.INVISIBLE
                binding.errorIcon3.visibility = View.INVISIBLE
                hasInputtingErrors = false
            }
        }
        binding.destinationTime.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            if(binding.destinationTime.text!!.isNotEmpty()){
                binding.errorText4.visibility = View.INVISIBLE
                binding.errorIcon4.visibility = View.INVISIBLE
                hasInputtingErrors = false
            }
        }

        binding.departureDateLayout.setOnClickListener {
            DatePickerDialog( this,
                R.style.CustomDatePickerDialogTheme, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show()
            datePickerState = 1
        }
        binding.destinationDateLayout.setOnClickListener {
            DatePickerDialog( this,
                R.style.CustomDatePickerDialogTheme, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show()
            datePickerState = 2
        }
        binding.departureTimeLayout.setOnClickListener {
            TimePickerDialog(this, R.style.CustomDatePickerDialogTheme, timePicker, myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE), true).show()
            timePickerState = 1
        }
        binding.destinationTimeLayout.setOnClickListener {
            TimePickerDialog(this, R.style.CustomDatePickerDialogTheme, timePicker, myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE), true).show()
            timePickerState = 2
        }
        binding.currency.setOnClickListener {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.currencyLayout.windowToken, 0)
        }
        binding.goBackButton.setOnClickListener{
            finish()
        }

        binding.saveTicketButton.setOnClickListener {
            val priceText = binding.price.text.toString()
            val currencyText = binding.currency.text.toString()

            hasInputtingErrors = checkForEmptyFields(binding)

            if (!hasInputtingErrors) {
                ticket.price = priceText.toDouble()
                ticket.currency = Currency.parseToCurrency(currencyText)

                ticket.departureDateTime =
                    DateTime.parseDateTime("${binding.departureDate.text} ${binding.departureTime.text}")
                ticket.destinationDateTime =
                    DateTime.parseDateTime("${binding.destinationDate.text} ${binding.destinationTime.text}")

                if (intentHasExtraToUpdate) {
                    dbAdapter.updateTicket(ticket)
                    ticketsReference.child(getUniqueIdByTicket(ticket)).updateChildren(ticket.getHashMap())
                } else {
                    ticket.purchaseDateTime = DateTime.parseDateTime(getCurrentDateTime())

                    dbAdapter.addTicket(ticket)
                    ticketsReference.child(getUniqueIdByTicket(ticket)).setValue(ticket.getHashMap())

                    if(ProfileController.saveAddresses){
                        if(dbAdapter.isUniqueAddress(ticket.departureAddress)){
                            dbAdapter.addAddress(ticket.departureAddress)
                            addressesReference.child(ticket.departureAddress.getUniqueId()).setValue(ticket.departureAddress)
                        }
                        if(dbAdapter.isUniqueAddress(ticket.destinationAddress)){
                            dbAdapter.addAddress(ticket.destinationAddress)
                            addressesReference.child(ticket.destinationAddress.getUniqueId()).setValue(ticket.destinationAddress)
                        }
                    }
                }

                Toast.makeText(this, "Квиток був успішно збережений", Toast.LENGTH_LONG).show()

                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)
            }
        }
        binding.generateTicketButton.setOnClickListener {
            val priceText = binding.price.text.toString()
            val currencyText = binding.currency.text.toString()
            hasInputtingErrors = checkForEmptyFields(binding)

            if(!hasInputtingErrors){
                ticket.price = priceText.toDouble()
                ticket.currency = Currency.parseToCurrency(currencyText)

                ticket.departureDateTime =
                    DateTime.parseDateTime("${binding.departureDate.text} ${binding.departureTime.text}")
                ticket.destinationDateTime =
                    DateTime.parseDateTime("${binding.destinationDate.text} ${binding.destinationTime.text}")

                if (intentHasExtraToUpdate) {
                    dbAdapter.updateTicket(ticket)
                    ticketsReference.child(getUniqueIdByTicket(ticket)).updateChildren(ticket.getHashMap())
                } else {
                    ticket.purchaseDateTime = DateTime.parseDateTime(getCurrentDateTime())

                    dbAdapter.addTicket(ticket)
                    ticketsReference.child(getUniqueIdByTicket(ticket)).setValue(ticket.getHashMap())

                    if(dbAdapter.isUniqueAddress(ticket.departureAddress)){
                        dbAdapter.addAddress(ticket.departureAddress)
                        addressesReference.child(ticket.departureAddress.getUniqueId()).setValue(ticket.departureAddress)
                    }
                    if(dbAdapter.isUniqueAddress(ticket.destinationAddress)){
                        dbAdapter.addAddress(ticket.destinationAddress)
                        addressesReference.child(ticket.destinationAddress.getUniqueId()).setValue(ticket.destinationAddress)
                    }
                }

                val downloadsDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, TicketController.getFileNameForTicket(ticket, true))

                val pdfDocument = createPdfForTicket(this, ticket)
                writeFileToStorage(file, pdfDocument)

                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)

                Toast.makeText(this, "Квиток був успішно згенерований", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkForEmptyFields(binding: AddTicketPage2Binding): Boolean {
        var hasInputtingErrors = false
        if(binding.price.text!!.isEmpty()){
            binding.priceLayout.error = "Введіть дані"
            hasInputtingErrors = true
        }
        if(binding.currency.text.isEmpty()){
            binding.currencyLayout.error = "Введіть дані"
            hasInputtingErrors = true
        }
        if (binding.departureDate.text.isEmpty()) {
            binding.errorText1.visibility = View.VISIBLE
            binding.errorIcon1.visibility = View.VISIBLE
            hasInputtingErrors = true
        }
        if (binding.departureTime.text.isEmpty()) {
            binding.errorText2.visibility = View.VISIBLE
            binding.errorIcon2.visibility = View.VISIBLE
            hasInputtingErrors = true
        }
        if (binding.destinationDate.text.isEmpty()) {
            binding.errorText3.visibility = View.VISIBLE
            binding.errorIcon3.visibility = View.VISIBLE
            hasInputtingErrors = true
        }
        if (binding.destinationTime.text.isEmpty()) {
            binding.errorText4.visibility = View.VISIBLE
            binding.errorIcon4.visibility = View.VISIBLE
            hasInputtingErrors = true
        }
        return hasInputtingErrors
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