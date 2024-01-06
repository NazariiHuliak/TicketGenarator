package com.example.ticketgeneratorproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ticketgeneratorproject.DataBase.DataBaseAdapter
import com.example.ticketgeneratorproject.Entities.TicketModel

class DetailedInformationAboutTicket : AppCompatActivity() {
    private lateinit var backToMainButton: LinearLayout
    private lateinit var editButton: Button
    private lateinit var downloadButton: Button
    private lateinit var createSimilarButton: Button
    private lateinit var ticket: TicketModel
    private lateinit var shareButton: Button

    private lateinit var fullScreenViewButton: LinearLayout
    @SuppressLint("MissingInflatedId", "SetTextI18n", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailed_information_about_ticket_layout)

        fullScreenViewButton = findViewById(R.id.show_on_full_size_btn)
        backToMainButton = findViewById(R.id.back_to_main_menu)
        editButton = findViewById(R.id.edit_btn)
        downloadButton = findViewById(R.id.download_btn)
        createSimilarButton = findViewById(R.id.create_similar)
        shareButton = findViewById(R.id.share_btn)

        if(intent.hasExtra("recyclerViewAdapter_TO_DetailedInformationAboutTicket_ticketData")){
            ticket =
                intent.getSerializableExtra("recyclerViewAdapter_TO_DetailedInformationAboutTicket_ticketData")
                        as TicketModel
        } else {
            ticket =
                intent.getSerializableExtra("TicketPreview_TO_DetailedInformationAboutTicket_ticketData")
                        as TicketModel
        }

        var doubleClick = false

        if(ticket.fullName.length >= 32){
            findViewById<TextView>(R.id.ticket_fullName).textSize = 14f;
            if(ticket.fullName.length >= 36 && ticket.tripNumber.length >= 9){
                findViewById<TextView>(R.id.ticket_tripNumber).textSize = 13f;
            }
        }
        findViewById<TextView>(R.id.ticket_fullName).text = ticket.fullName
        findViewById<TextView>(R.id.ticket_tripNumber).text = ticket.tripNumber
        findViewById<TextView>(R.id.ticket_departureCity).text = ticket.departureAddress.city
        findViewById<TextView>(R.id.ticket_departureAddress).text =
            ticket.departureAddress.street + " " +
                    ticket.departureAddress.number
        findViewById<TextView>(R.id.ticket_departureDate).text = ticket.departureDateTime.date
        findViewById<TextView>(R.id.ticket_departureTime).text = ticket.departureDateTime.time
        findViewById<TextView>(R.id.ticket_destinationCity).text = ticket.destinationAddress.city
        findViewById<TextView>(R.id.ticket_destinationAddress).text =
            ticket.destinationAddress.street + " " +
                    ticket.destinationAddress.number
        findViewById<TextView>(R.id.ticket_destinationDate).text = ticket.destinationDateTime.date
        findViewById<TextView>(R.id.ticket_destinationTime).text = ticket.destinationDateTime.time
        findViewById<TextView>(R.id.ticket_price).text = ticket.price.toString()
        findViewById<TextView>(R.id.ticket_currency).text = ticket.currency.toString()
        findViewById<TextView>(R.id.ticket_seat).text =  if(ticket.seat == -1) "При посадці" else ticket.seat.toString()
        findViewById<TextView>(R.id.ticket_purchaseDate).text = ticket.purchaseDateTime.time + " " +
                ticket.purchaseDateTime.date

        backToMainButton.setOnClickListener {
            finish()
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
        }
        createSimilarButton.setOnClickListener {
            val intent = Intent(this, AddTicketPage1::class.java)
            intent.putExtra(
                "DetailedInformationTicket_TO_EnterTicketData_TicketData_CreateSimilar",
                ticket
            )
            startActivity(intent)
        }
        editButton.setOnClickListener {
            val intent = Intent(this, AddTicketPage1::class.java)
            intent.putExtra(
                "DetailedInformationTicket_TO_EnterTicketData_TicketData_Update",
                ticket
            )
            startActivity(intent)
        }

        downloadButton.setOnClickListener {
            if(AddTicketPage2.convertXmlToPdf(ticket, this)){
                finish()
            }
        }

        val dbAdapter = DataBaseAdapter(this)
        shareButton.setOnClickListener {
            /*val pdfFile = AddTicketPage2.convertXMLToPDF(ticket, this)*/
            dbAdapter.addAddress("Test")
            Log.d("myLog", "${dbAdapter.getAllAddresses().size}")
        }

        fullScreenViewButton.setOnClickListener {
            if (doubleClick) {
                val intent = Intent(this, TicketPreview::class.java)
                intent.putExtra("DetailedInformationAboutTicket_TO_TicketPreview_ticketData", ticket)
                startActivity(intent)
                doubleClick = false
            } else {
                doubleClick = true
                Handler(Looper.getMainLooper()).postDelayed({
                    doubleClick = false
                }, DOUBLE_CLICK_DELAY.toLong())
            }
        }
    }
    companion object {
        const val DOUBLE_CLICK_DELAY = 300
    }

}