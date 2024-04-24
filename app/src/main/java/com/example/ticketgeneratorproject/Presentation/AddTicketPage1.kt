package com.example.ticketgeneratorproject.Presentation

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.widget.addTextChangedListener
import com.example.ticketgeneratorproject.Business.Adapters.AutoCompleteAddressAdapter
import com.example.ticketgeneratorproject.Data.SQLiteController
import com.example.ticketgeneratorproject.Data.Entities.Address
import com.example.ticketgeneratorproject.Data.Entities.Currency
import com.example.ticketgeneratorproject.Data.Entities.DateTime
import com.example.ticketgeneratorproject.Data.Entities.TicketModel
import com.example.ticketgeneratorproject.R
import com.example.ticketgeneratorproject.Business.Controllers.TextController.capitalizeWords
import com.example.ticketgeneratorproject.databinding.AddTicketPage1Binding

class AddTicketPage1 : AppCompatActivity() {
    private lateinit var dbAdapter: SQLiteController
    private lateinit var ticket: TicketModel
    private lateinit var binding: AddTicketPage1Binding

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddTicketPage1Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val intentHasExtraToUpdate = intent.hasExtra("DetailedInformationTicket_TO_EnterTicketData_TicketData_Update")
        val intentHasExtraToCreateSimilar = intent.hasExtra("DetailedInformationTicket_TO_EnterTicketData_TicketData_CreateSimilar")

        dbAdapter = SQLiteController(this)
        val addresses = dbAdapter.getAllAddresses()
        val modifiedAddress = addresses.map{it.toString().replace(Regex("[.,/: ]"), "")}.map{it.lowercase()}

        val dropDownAdapter = AutoCompleteAddressAdapter(this, R.layout.currency_item, addresses)
        binding.departure.setAdapter(dropDownAdapter)
        binding.destination.setAdapter(dropDownAdapter)
        binding.departure.setMaxVisibleOptions(3, modifiedAddress)
        binding.destination.setMaxVisibleOptions(3, modifiedAddress)

        if(intentHasExtraToUpdate){
            ticket = intent.getSerializableExtra("DetailedInformationTicket_TO_EnterTicketData_TicketData_Update")
                    as TicketModel
        } else if(intentHasExtraToCreateSimilar){
            ticket = intent.getSerializableExtra("DetailedInformationTicket_TO_EnterTicketData_TicketData_CreateSimilar")
                    as TicketModel
        }
        if(intentHasExtraToUpdate || intentHasExtraToCreateSimilar){
            binding.fullname.setText(ticket.fullName)
            binding.tripNumber.setText(ticket.tripNumber)
            binding.seat.setText(ticket.seat.toString())
            binding.departure.setText(ticket.departureAddress.country + ", " + ticket.departureAddress.city + ", " +
                    ticket.departureAddress.street + ", " + ticket.departureAddress.number)
            binding.destination.setText(ticket.destinationAddress.country + ", " + ticket.destinationAddress.city + ", " +
                    ticket.destinationAddress.street + ", " + ticket.destinationAddress.number)

        }

        var hasInputtingErrors: Boolean = false

        binding.fullname.addTextChangedListener {
            if(binding.fullname.text!!.isNotEmpty()){
                binding.fullNameLayout.error = null
                hasInputtingErrors = false
            }
        }
        binding.tripNumber.addTextChangedListener {
            if(binding.tripNumber.text!!.isNotEmpty()){
                binding.tripNumberLayout.error = null
                hasInputtingErrors = false
            }
        }
        binding.departure.addTextChangedListener {
            if(binding.departure.text!!.isNotEmpty()){
                binding.departureLayout.error = null
                hasInputtingErrors = false
            }
        }
        binding.destination.addTextChangedListener {
            if(binding.destination.text!!.isNotEmpty()){
                binding.destinationLayout.error = null
                hasInputtingErrors = false
            }
        }

        binding.goBackButton.setOnClickListener {
            finish()
        }

        binding.continueButton.setOnClickListener {
            val fullNameText = binding.fullname.text.toString()
            val tripNumberText = binding.tripNumber.text.toString()
            val seatText = binding.seat.text.toString()
            val departureText = binding.departure.text.toString()
            val destinationText = binding.destination.text.toString()

            if(fullNameText.isEmpty()){
                binding.fullNameLayout.error = "Введіть дані"
                hasInputtingErrors=true
            }
            if(tripNumberText.isEmpty()){
                binding.tripNumberLayout.error = "Введіть дані"
                hasInputtingErrors=true
            }
            if(departureText.isEmpty()){
                binding.departureLayout.error = "Введіть дані"
                hasInputtingErrors=true
            }
            if(destinationText.isEmpty()){
                binding.destinationLayout.error = "Введіть дані"
                hasInputtingErrors=true
            }

            if(!hasInputtingErrors){
                val intent = Intent(this, AddTicketPage2::class.java)
                val ticketToPass = TicketModel(
                    0,
                    capitalizeWords(fullNameText),
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
                        setDepartureDestinationDateTime(
                            DateTime.parseDateTime(ticket.departureDateTime.date+ " " + ticket.departureDateTime.time),
                            DateTime.parseDateTime(ticket.destinationDateTime.date+ " " + ticket.destinationDateTime.time)).
                        setPurchaseDateTime(ticket.purchaseDateTime).
                        setPrice(ticket.price).
                        setCurrency(ticket.currency))
                } else if(intentHasExtraToCreateSimilar) {
                    intent.putExtra("EnterTicketData_TO_EnterTicketDataTime_TicketData_CreateSimilar",
                        ticketToPass.
                        setDepartureDestinationDateTime(
                            DateTime.parseDateTime(ticket.departureDateTime.date+ " " + ticket.departureDateTime.time),
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
}