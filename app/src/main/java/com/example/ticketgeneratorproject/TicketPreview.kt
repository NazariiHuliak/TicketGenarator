package com.example.ticketgeneratorproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ticketgeneratorproject.Entities.TicketModel


class TicketPreview : AppCompatActivity() {
    private lateinit var goDetailedInformationPage: LinearLayout
    private var doubleClick = false
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket_preview)

        //hide status bar
        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())

        var ticket =
            intent.getSerializableExtra("DetailedInformationAboutTicket_TO_TicketPreview_ticketData")
                    as TicketModel

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

        goDetailedInformationPage = findViewById(R.id.back_to_detailed_activity)
        goDetailedInformationPage.setOnClickListener {
            if (doubleClick) {
                val intent = Intent(this, DetailedInformationAboutTicket::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("TicketPreview_TO_DetailedInformationAboutTicket_ticketData", ticket)
                startActivity(intent)

                doubleClick = false
            } else {
                doubleClick = true
                Handler(Looper.getMainLooper()).postDelayed({
                    doubleClick = false
                }, DetailedInformationAboutTicket.DOUBLE_CLICK_DELAY.toLong())
            }
        }
    }
}