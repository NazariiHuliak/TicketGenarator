package com.example.ticketgeneratorproject

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.widget.addTextChangedListener
import com.example.ticketgeneratorproject.Entities.Address
import com.example.ticketgeneratorproject.Entities.Currency
import com.example.ticketgeneratorproject.Entities.DateTime
import com.example.ticketgeneratorproject.Entities.TicketModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AddTicketPage1 : AppCompatActivity() {
    private lateinit var ticket: TicketModel
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_ticket_page_1)

        val intentHasExtraToUpdate = intent.hasExtra("DetailedInformationTicket_TO_EnterTicketData_TicketData_Update")
        val intentHasExtraToCreateSimilar = intent.hasExtra("DetailedInformationTicket_TO_EnterTicketData_TicketData_CreateSimilar")

        val fullname = findViewById<TextInputEditText>(R.id.fullname)
        val tripNumber = findViewById<TextInputEditText>(R.id.trip_number)
        val seat = findViewById<TextInputEditText>(R.id.seat)
        val departure = findViewById<TextInputEditText>(R.id.departure)
        val destination = findViewById<TextInputEditText>(R.id.destination)

        val fullname_layout = findViewById<TextInputLayout>(R.id.fullName_layout)
        val tripNumber_layout = findViewById<TextInputLayout>(R.id.trip_number_layout)
        val seat_layout = findViewById<TextInputLayout>(R.id.seat_layout)
        val departure_layout = findViewById<TextInputLayout>(R.id.departure_layout)
        val destination_layout = findViewById<TextInputLayout>(R.id.destination_layout)

        var hasErrors: Boolean = false

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

        }

        fullname.addTextChangedListener {
            if(it!!.count()>0){
                fullname_layout.error = null
                hasErrors = false
            }
        }
        tripNumber.addTextChangedListener {
            if(it!!.count()>0){
                tripNumber_layout.error = null
                hasErrors = false
            }
        }
        departure.addTextChangedListener {
            if(it!!.count()>0){
                departure_layout.error = null
                hasErrors = false
            }
        }
        destination.addTextChangedListener {
            if(it!!.count()>0){
                destination_layout.error = null
                hasErrors = false
            }
        }

        findViewById<LinearLayout>(R.id.back_to_main_menu).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.next_page).setOnClickListener {
            val fullnameText = fullname.text.toString()
            val tripNumberText = tripNumber.text.toString()
            val seatText = seat.text.toString()
            val departureText = departure.text.toString()
            val destinationText = destination.text.toString()


            if(fullnameText.isEmpty()){
                fullname_layout.error = "Введіть дані"
                hasErrors=true
            }
            if(tripNumberText.isEmpty()){
                tripNumber_layout.error = "Введіть дані"
                hasErrors=true
            }
            if(departureText.isEmpty()){
                departure_layout.error = "Введіть дані"
                hasErrors=true
            }
            if(destinationText.isEmpty()){
                destination_layout.error = "Введіть дані"
                hasErrors=true
            }

            if(!hasErrors){
                val intent = Intent(this, AddTicketPage2::class.java)
                val ticketToPass = TicketModel(
                    0,
                    capitalizeWords(fullnameText),
                    tripNumberText,
                    Address.parseAddress(departureText),
                    Address.parseAddress(destinationText),
                    DateTime.parseDateTime("01-01-1991 00:00"),
                    DateTime.parseDateTime("01-01-1991 00:00"),
                    if(seatText.isEmpty()) -1 else seatText.toInt(),
                    0.0,
                    Currency.UAH,
                    DateTime.parseDateTime("01-01-1991 00:00:00")
                )
                if(intentHasExtraToUpdate){
                    intent.putExtra("EnterTicketData_TO_EnterTicketDataTime_TicketData_Update",
                        ticketToPass.
                        setId(ticket.id).
                        setDepartureDestinationDateTime(DateTime.parseDateTime(ticket.departureDateTime.date+ " " + ticket.departureDateTime.time),
                            DateTime.parseDateTime(ticket.destinationDateTime.date+ " " + ticket.destinationDateTime.time)).
                        setPurchaseDateTime(ticket.purchaseDateTime).
                        setPrice(ticket.price).
                        setCurrency(ticket.currency))
                } else if(intentHasExtraToCreateSimilar) {
                    intent.putExtra("EnterTicketData_TO_EnterTicketDataTime_TicketData_CreateSimilar",
                        ticketToPass.
                        setDepartureDestinationDateTime(DateTime.parseDateTime(ticket.departureDateTime.date+ " " + ticket.departureDateTime.time),
                            DateTime.parseDateTime(ticket.destinationDateTime.date+ " " + ticket.destinationDateTime.time)).
                        setPrice(ticket.price).
                        setCurrency(ticket.currency))
                } else {
                    intent.putExtra("EnterTicketData_TO_EnterTicketDataTime_TicketData_Complete", ticketToPass)
                }
                startActivity(intent)
            }
        }
    }
    fun capitalizeWords(input: String): String {
        val words = input.split(" ")
        val capitalizedWords = words.map { it.capitalize() }
        return capitalizedWords.joinToString(" ")
    }
}
