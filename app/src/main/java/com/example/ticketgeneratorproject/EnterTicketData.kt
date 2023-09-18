package com.example.ticketgeneratorproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.widget.addTextChangedListener
import com.example.ticketgeneratorproject.Entities.Address
import com.example.ticketgeneratorproject.Entities.Currency
import com.example.ticketgeneratorproject.Entities.DateTime
import com.example.ticketgeneratorproject.Entities.TicketModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EnterTicketData : AppCompatActivity() {
    private lateinit var ticket: TicketModel
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_ticket_data)

        val intentHasExtraToUpdate = intent.hasExtra("DetailedInformationTicket_TO_EnterTicketData_TicketData_Update")
        val intentHasExtraToCreateSimilar = intent.hasExtra("DetailedInformationTicket_TO_EnterTicketData_TicketData_CreateSimilar")

        val fullname = findViewById<TextInputEditText>(R.id.fullname)
        val tripNumber = findViewById<TextInputEditText>(R.id.trip_number)
        val seat = findViewById<TextInputEditText>(R.id.seat)
        val departure = findViewById<TextInputEditText>(R.id.departure)
        val destination = findViewById<TextInputEditText>(R.id.destination)
        val price = findViewById<TextInputEditText>(R.id.price)
        val currency = findViewById<AutoCompleteTextView>(R.id.auto_complete)

        val fullname_layout = findViewById<TextInputLayout>(R.id.fullName_layout)
        val tripNumber_layout = findViewById<TextInputLayout>(R.id.trip_number_layout)
        val seat_layout = findViewById<TextInputLayout>(R.id.seat_layout)
        val departure_layout = findViewById<TextInputLayout>(R.id.departure_layout)
        val destination_layout = findViewById<TextInputLayout>(R.id.destination_layout)
        val price_layout = findViewById<TextInputLayout>(R.id.price_layout)
        val currency_layout = findViewById<TextInputLayout>(R.id.currency_layout)

        var flag: Boolean = true
        val items = listOf("₴ Гривня", "\$ Долар", "€ Євро")
        val adapter = ArrayAdapter(this, R.layout.currency_item, items)
        currency.setAdapter(adapter)

        if(intentHasExtraToUpdate){
            ticket = intent.getSerializableExtra("DetailedInformationTicket_TO_EnterTicketData_TicketData_Update")
                    as TicketModel
        } else if(intentHasExtraToCreateSimilar){
            ticket = intent.getSerializableExtra("DetailedInformationTicket_TO_EnterTicketData_TicketData_CreateSimilar")
                    as TicketModel
        }
        if(intentHasExtraToUpdate || intentHasExtraToCreateSimilar){
            fullname.setText(ticket.fullName)
            tripNumber.setText(ticket.tripNumber)
            seat.setText(ticket.seat.toString())
            departure.setText(ticket.departureAddress.country + ", " + ticket.departureAddress.city + ", " +
                    ticket.departureAddress.street + ", " + ticket.departureAddress.number)
            destination.setText(ticket.destinationAddress.country + ", " + ticket.destinationAddress.city + ", " +
                    ticket.destinationAddress.street + ", " + ticket.destinationAddress.number)
            price.setText(ticket.price.toString())
            currency.setText(ticket.currency.toString())
        }

        fullname.addTextChangedListener {
            if(it!!.count()>0){
                fullname_layout.error = null
                flag = true
            }
        }
        tripNumber.addTextChangedListener {
            if(it!!.count()>0){
                tripNumber_layout.error = null
                flag = true
            }
        }
        seat.addTextChangedListener {
            if(it!!.count()>0){
                seat_layout.error = null
                flag = true
            }
        }
        departure.addTextChangedListener {
            if(it!!.count()>0){
                departure_layout.error = null
                flag = true
            }
        }
        destination.addTextChangedListener {
            if(it!!.count()>0){
                destination_layout.error = null
                flag = true
            }
        }
        price.addTextChangedListener {
            if(it!!.count()>0){
                price_layout.error = null
                flag = true
            }
        }
        currency.addTextChangedListener {
            if(it!!.count()>0){
                currency_layout.error = null
                flag = true
            }
        }

        findViewById<LinearLayout>(R.id.back_to_main_menu).setOnClickListener {
            finish()
        }

        currency.setOnClickListener {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currency.windowToken, 0)
        }

        findViewById<Button>(R.id.next_page).setOnClickListener {
            val fullnameText = fullname.text.toString()
            val tripNumberText = tripNumber.text.toString()
            val seatText = seat.text.toString()
            val departureText = departure.text.toString()
            val destinationText = destination.text.toString()
            val priceText = price.text.toString()
            val currencyText = currency.text.toString()

            if(fullnameText.isEmpty()){
                fullname_layout.error = "Введіть дані"
                flag=false
            }
            if(tripNumberText.isEmpty()){
                tripNumber_layout.error = "Введіть дані"
                flag=false
            }
            if(seatText.isEmpty()){
                seat_layout.error = "Введіть дані"
                flag=false
            }
            if(departureText.isEmpty()){
                departure_layout.error = "Введіть дані"
                flag=false
            }
            if(destinationText.isEmpty()){
                destination_layout.error = "Введіть дані"
                flag=false
            }
            if(priceText.isEmpty()){
                price_layout.error = "Введіть дані"
                flag=false
            }
            if(currencyText.isEmpty()){
                currency_layout.error = "Введіть дані"
                flag=false
            }
            if(flag){
                val intent = Intent(this, EnterTicketDataTime::class.java)
                val ticketToPass = TicketModel(
                    0,
                    fullnameText,
                    tripNumberText,
                    Address.parseAddress(departureText),
                    Address.parseAddress(destinationText),
                    DateTime.parseDateTime("01-01-1991 00:00"),
                    DateTime.parseDateTime("01-01-1991 00:00"),
                    seatText.toInt(),
                    priceText.toDouble(),
                    Currency.parseCurrency(currencyText),
                    DateTime.parseDateTime("01-01-1991 00:00")
                )
                if(intentHasExtraToUpdate){
                    intent.putExtra("EnterTicketData_TO_EnterTicketDataTime_TicketData_Update", ticketToPass.
                    setId(ticket.id).
                    setDepartureDestinationDateTime(DateTime.parseDateTime(ticket.departureDateTime.Date+ " " + ticket.departureDateTime.Time),
                        DateTime.parseDateTime(ticket.destinationDateTime.Date+ " " + ticket.destinationDateTime.Time)).
                    setPurchaseDateTime(ticket.purchaseDateTime))
                } else if(intentHasExtraToCreateSimilar) {
                    intent.putExtra("EnterTicketData_TO_EnterTicketDataTime_TicketData_CreateSimilar", ticketToPass.
                    setDepartureDestinationDateTime(DateTime.parseDateTime(ticket.departureDateTime.Date+ " " + ticket.departureDateTime.Time),
                        DateTime.parseDateTime(ticket.destinationDateTime.Date+ " " + ticket.destinationDateTime.Time)))
                } else {
                    intent.putExtra("EnterTicketData_TO_EnterTicketDataTime_TicketData_Complete", ticketToPass)
                }
                startActivity(intent)
            }
        }
    }
}

