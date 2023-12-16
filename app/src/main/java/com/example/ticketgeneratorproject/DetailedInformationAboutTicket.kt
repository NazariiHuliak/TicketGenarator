package com.example.ticketgeneratorproject

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.MediaStore
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.ticketgeneratorproject.Entities.TicketModel
import java.io.File
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE

class DetailedInformationAboutTicket : AppCompatActivity() {
    private lateinit var backToMainButton: LinearLayout
    private lateinit var editButton: Button
    private lateinit var downloadButton: Button
    private lateinit var createSimilarButton: Button
    private lateinit var ticket: TicketModel

    private lateinit var fullScreenViewButton: LinearLayout
    @SuppressLint("MissingInflatedId", "SetTextI18n", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_information_about_ticket)

        fullScreenViewButton = findViewById(R.id.show_on_full_size_btn)
        backToMainButton = findViewById(R.id.back_to_main_menu)
        editButton = findViewById(R.id.edit_btn)
        downloadButton = findViewById(R.id.download_btn)
        createSimilarButton = findViewById(R.id.create_similar)

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

        findViewById<TextView>(R.id.ticket_fullName).text = ticket.fullName
        findViewById<TextView>(R.id.ticket_tripNumber).text = ticket.tripNumber
        findViewById<TextView>(R.id.ticket_departureCity).text = ticket.departureAddress.city
        findViewById<TextView>(R.id.ticket_departureAddress).text =
            ticket.departureAddress.street + " " +
                    ticket.departureAddress.number
        findViewById<TextView>(R.id.ticket_departureDate).text = ticket.departureDateTime.Date
        findViewById<TextView>(R.id.ticket_departureTime).text = ticket.departureDateTime.Time
        findViewById<TextView>(R.id.ticket_destinationCity).text = ticket.destinationAddress.city
        findViewById<TextView>(R.id.ticket_destinationAddress).text =
            ticket.destinationAddress.street + " " +
                    ticket.destinationAddress.number
        findViewById<TextView>(R.id.ticket_destinationDate).text = ticket.destinationDateTime.Date
        findViewById<TextView>(R.id.ticket_destinationTime).text = ticket.destinationDateTime.Time
        findViewById<TextView>(R.id.ticket_price).text = ticket.price.toString()
        findViewById<TextView>(R.id.ticket_currency).text = ticket.currency.toString()
        findViewById<TextView>(R.id.ticket_seat).text =  if(ticket.seat == -1) "При посадці" else ticket.seat.toString()
        findViewById<TextView>(R.id.ticket_purchaseDate).text = ticket.purchaseDateTime.Time + " " +
                ticket.purchaseDateTime.Date

        backToMainButton.setOnClickListener {
            finish()
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
        }
        createSimilarButton.setOnClickListener {
            val intent = Intent(this, EnterTicketData::class.java)
            intent.putExtra(
                "DetailedInformationTicket_TO_EnterTicketData_TicketData_CreateSimilar",
                ticket
            )
            startActivity(intent)
        }
        editButton.setOnClickListener {
            val intent = Intent(this, EnterTicketData::class.java)
            intent.putExtra(
                "DetailedInformationTicket_TO_EnterTicketData_TicketData_Update",
                ticket
            )
            startActivity(intent)
        }
        downloadButton.setOnClickListener {
            if(EnterTicketDataTime.convertXmlToPdf(ticket, this)){
                finish()
            }
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