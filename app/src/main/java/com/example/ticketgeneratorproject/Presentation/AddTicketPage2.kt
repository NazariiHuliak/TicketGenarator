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
import com.example.ticketgeneratorproject.Data.SQLiteController
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
import com.example.ticketgeneratorproject.Data.Entities.Address
import com.example.ticketgeneratorproject.databinding.AddTicketPage2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.File
import java.util.*

class AddTicketPage2: AppCompatActivity() {
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var localDatabaseController: SQLiteController
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
        localDatabaseController = SQLiteController(this)

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

        var datePickerState = -1
        var timePickerState = -1
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            when (datePickerState){
                1->updateDateText(calendar, binding.departureDate)
                2->updateDateText(calendar, binding.destinationDate)
                else->Log.e("DatePicker", "Illegal argument was set")
            }
        }
        val timePicker = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            when (timePickerState){
                1->updateTimeText(calendar, binding.departureTime)
                2->updateTimeText(calendar, binding.destinationTime)
                else->Log.e("TimePicker", "Illegal argument was set")
            }
        }

        binding.departureDateLayout.setOnClickListener {
            DatePickerDialog( this,
                R.style.CustomDatePickerDialogTheme, datePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
            datePickerState = 1
        }
        binding.destinationDateLayout.setOnClickListener {
            DatePickerDialog( this,
                R.style.CustomDatePickerDialogTheme, datePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
            datePickerState = 2
        }
        binding.departureTimeLayout.setOnClickListener {
            TimePickerDialog(this, R.style.CustomDatePickerDialogTheme, timePicker, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true).show()
            timePickerState = 1
        }
        binding.destinationTimeLayout.setOnClickListener {
            TimePickerDialog(this, R.style.CustomDatePickerDialogTheme, timePicker, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true).show()
            timePickerState = 2
        }
        binding.currency.setOnClickListener {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.currencyLayout.windowToken, 0)
        }

        binding.goBackButton.setOnClickListener{
            finish()
        }
        binding.saveTicketButton.setOnClickListener{
            if(!checkForEmptyFields(binding)){
                saveOrUpdateTicket(binding, ticketsReference, addressesReference, intentHasExtraToUpdate)
                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)
            }
        }
        binding.generateTicketButton.setOnClickListener {
            if(!checkForEmptyFields(binding)){
                saveOrUpdateTicket(binding, ticketsReference, addressesReference, intentHasExtraToUpdate)

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

    private fun saveOrUpdateTicket(
        binding: AddTicketPage2Binding,
        ticketsReference: DatabaseReference,
        addressesReference: DatabaseReference,
        intentHasExtraToUpdate: Boolean
    ) {
        ticket.price = binding.price.text.toString().toDouble()
        ticket.currency = Currency.parseToCurrency(binding.currency.text.toString())
        ticket.departureDateTime =
            DateTime.parseDateTime("${binding.departureDate.text} ${binding.departureTime.text}")
        ticket.destinationDateTime =
            DateTime.parseDateTime("${binding.destinationDate.text} ${binding.destinationTime.text}")

        if(intentHasExtraToUpdate){
            localDatabaseController.updateTicket(ticket)
            ticketsReference.child(getUniqueIdByTicket(ticket)).updateChildren(ticket.getHashMap())
        } else {
            ticket.purchaseDateTime = DateTime.parseDateTime(getCurrentDateTime())

            localDatabaseController.addTicket(ticket)
            ticketsReference.child(getUniqueIdByTicket(ticket)).setValue(ticket.getHashMap())

            if (ProfileController.saveAddresses) {
                saveUniqueAddress(ticket.departureAddress, addressesReference)
                saveUniqueAddress(ticket.destinationAddress, addressesReference)
            }
        }
    }

    private fun saveUniqueAddress(address: Address, addressesReference: DatabaseReference) {
        if (localDatabaseController.isUniqueAddress(address)) {
            localDatabaseController.addAddress(address)
            addressesReference.child(address.getUniqueId()).setValue(address)
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