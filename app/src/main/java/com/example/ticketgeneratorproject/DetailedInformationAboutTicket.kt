package com.example.ticketgeneratorproject

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.ticketgeneratorproject.Entities.TicketModel

class DetailedInformationAboutTicket : AppCompatActivity() {

    private lateinit var backToMainButton: LinearLayout
    private lateinit var editButton: Button
    private lateinit var downloadButton: Button

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_information_about_ticket)

        backToMainButton = findViewById(R.id.back_to_main_menu)
        editButton = findViewById(R.id.edit_btn)
        downloadButton = findViewById(R.id.download_btn)

        var ticket = intent.getSerializableExtra("recyclerViewAdapter_TO_DetailedInformationAboutTicket_ticketData")
                as TicketModel

        findViewById<TextView>(R.id.ticket_fullName).text = ticket.fullName
        findViewById<TextView>(R.id.ticket_tripNumber).text = ticket.tripNumber
        findViewById<TextView>(R.id.ticket_departureCity).text = ticket.departureAddress.city
        findViewById<TextView>(R.id.ticket_departureAddress).text = ticket.departureAddress.street + " " +
                ticket.departureAddress.number
        findViewById<TextView>(R.id.ticket_departureDate).text = ticket.departureDateTime.Date
        findViewById<TextView>(R.id.ticket_departureTime).text = ticket.departureDateTime.Time
        findViewById<TextView>(R.id.ticket_destinationCity).text = ticket.destinationAddress.city
        findViewById<TextView>(R.id.ticket_destinationAddress).text = ticket.destinationAddress.street + " " +
                ticket.destinationAddress.number
        findViewById<TextView>(R.id.ticket_destinationDate).text = ticket.destinationDateTime.Date
        findViewById<TextView>(R.id.ticket_destinationTime).text = ticket.destinationDateTime.Time
        findViewById<TextView>(R.id.ticket_price).text = ticket.price.toString()
        findViewById<TextView>(R.id.ticket_currency).text = ticket.currency.toString()
        findViewById<TextView>(R.id.ticket_seat).text = ticket.seat.toString()
        findViewById<TextView>(R.id.ticket_purchaseDate).text = ticket.purchaseTime.Time + " " +
                ticket.purchaseTime.Date

        backToMainButton.setOnClickListener {
            finish()
        }
        editButton.setOnClickListener {
            val intent = Intent(this, EnterTicketData::class.java)
            intent.putExtra("DetailedInformationTicket_TO_EnterTicketData_TicketData_Update", ticket)
            startActivity(intent)
        }
        downloadButton.setOnClickListener {
            EnterTicketDataTime.convertXmlToPdf(ticket, this)
            finish()
        }
    }
}