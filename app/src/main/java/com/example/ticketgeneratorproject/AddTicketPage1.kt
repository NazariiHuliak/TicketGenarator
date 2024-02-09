package com.example.ticketgeneratorproject

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.widget.addTextChangedListener
import com.example.ticketgeneratorproject.Adapters.AutoCompleteAddressAdapter
import com.example.ticketgeneratorproject.DataBase.DataBaseAdapter
import com.example.ticketgeneratorproject.Entities.Address
import com.example.ticketgeneratorproject.Entities.Currency
import com.example.ticketgeneratorproject.Entities.DateTime
import com.example.ticketgeneratorproject.Entities.TicketModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AddTicketPage1 : AppCompatActivity() {
    private lateinit var fullnameLayout: TextInputLayout
    private lateinit var tripnumberLayout: TextInputLayout
    private lateinit var seatLayout: TextInputLayout
    private lateinit var departureLayout: TextInputLayout
    private lateinit var destinationLayout: TextInputLayout

    private lateinit var fullname: TextInputEditText
    private lateinit var tripNumber: TextInputEditText
    private lateinit var seat: TextInputEditText
    private lateinit var departure: AutoCompleteTextView
    private lateinit var destination: AutoCompleteTextView

    private lateinit var dbAdapter: DataBaseAdapter
    private lateinit var ticket: TicketModel
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_ticket_page_1)

        val intentHasExtraToUpdate = intent.hasExtra("DetailedInformationTicket_TO_EnterTicketData_TicketData_Update")
        val intentHasExtraToCreateSimilar = intent.hasExtra("DetailedInformationTicket_TO_EnterTicketData_TicketData_CreateSimilar")

        fullnameLayout = findViewById(R.id.fullName_layout)
        tripnumberLayout = findViewById(R.id.trip_number_layout)
        seatLayout = findViewById(R.id.seat_layout)
        departureLayout = findViewById(R.id.departure_layout)
        destinationLayout = findViewById(R.id.destination_layout)

        fullname = findViewById(R.id.fullname)
        tripNumber = findViewById(R.id.trip_number)
        seat = findViewById(R.id.seat)
        departure = findViewById(R.id.departure)
        destination = findViewById(R.id.destination)

        dbAdapter = DataBaseAdapter(this)
        val addresses = dbAdapter.getAllAddresses()
        val modifiedAddress = addresses.map{it.toString().replace(Regex("[.,/: ]"), "")}.map{it.lowercase()}

        val dropDownAdapter = AutoCompleteAddressAdapter(this, R.layout.currency_item, addresses)
        departure.setAdapter(dropDownAdapter)
        destination.setAdapter(dropDownAdapter)
        departure.setMaxVisibleOptions(3, modifiedAddress)
        destination.setMaxVisibleOptions(3, modifiedAddress)

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

        var hasErrors: Boolean = false

        fullname.addTextChangedListener {
            if(it!!.count()>0){
                fullnameLayout.error = null
                hasErrors = false
            }
        }
        tripNumber.addTextChangedListener {
            if(it!!.count()>0){
                tripnumberLayout.error = null
                hasErrors = false
            }
        }
        departure.addTextChangedListener {
            if(it!!.count()>0){
                departureLayout.error = null
                hasErrors = false
            }
        }
        destination.addTextChangedListener {
            if(it!!.count()>0){
                destinationLayout.error = null
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
                fullnameLayout.error = "Введіть дані"
                hasErrors=true
            }
            if(tripNumberText.isEmpty()){
                tripnumberLayout.error = "Введіть дані"
                hasErrors=true
            }
            if(departureText.isEmpty()){
                departureLayout.error = "Введіть дані"
                hasErrors=true
            }
            if(destinationText.isEmpty()){
                destinationLayout.error = "Введіть дані"
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
    private fun AutoCompleteTextView.setMaxVisibleOptions(number: Int, items: List<String>){
        this.addTextChangedListener {
            val counter = items.filter{
                it.contains(this.text.toString().replace(" ", "")
                    .replace(",", "").lowercase()) }.size
            if(counter == 0){
                this.dropDownHeight = 0
            } else if(counter<number){
                this.dropDownHeight = counter*150
            } else {
                this.dropDownHeight = 450
            }
        }
    }
    fun capitalizeWords(input: String): String {
        val words = input.split(" ")
        val capitalizedWords = words.map { it.capitalize() }
        return capitalizedWords.joinToString(" ")
    }
}
