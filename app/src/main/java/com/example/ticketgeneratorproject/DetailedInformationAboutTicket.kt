package com.example.ticketgeneratorproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.ticketgeneratorproject.AddTicketPage2.Companion.createPdfForTicket
import com.example.ticketgeneratorproject.AddTicketPage2.Companion.getFileNameForTicket
import com.example.ticketgeneratorproject.AddTicketPage2.Companion.getUniqueIdByTicket
import com.example.ticketgeneratorproject.AddTicketPage2.Companion.writeFileToStorage
import com.example.ticketgeneratorproject.DataBase.DataBaseAdapter
import com.example.ticketgeneratorproject.Entities.TicketModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.File

class DetailedInformationAboutTicket : AppCompatActivity() {
    private lateinit var backToMainButton: LinearLayout
    private lateinit var editButton: ImageView
    private lateinit var deleteButton: ImageView
    private lateinit var downloadButton: Button
    private lateinit var createSimilarButton: Button
    private lateinit var shareButton: Button
    private lateinit var fullScreenViewButton: LinearLayout

    private lateinit var dbAdapter: DataBaseAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase

    private lateinit var ticket: TicketModel
    @SuppressLint("MissingInflatedId", "SetTextI18n", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailed_information_about_ticket_layout)

        if(intent.hasExtra("recyclerViewAdapter_TO_DetailedInformationAboutTicket_ticketData")){
            ticket =
                intent.getSerializableExtra("recyclerViewAdapter_TO_DetailedInformationAboutTicket_ticketData")
                        as TicketModel
        } else {
            ticket =
                intent.getSerializableExtra("TicketPreview_TO_DetailedInformationAboutTicket_ticketData")
                        as TicketModel
        }

        fullScreenViewButton = findViewById(R.id.show_on_full_size_btn)
        backToMainButton = findViewById(R.id.back_to_main_menu)
        editButton = findViewById(R.id.edit_btn)
        downloadButton = findViewById(R.id.download_btn)
        createSimilarButton = findViewById(R.id.create_similar)
        shareButton = findViewById(R.id.share_btn)
        deleteButton = findViewById(R.id.delete_btn)

        dbAdapter = DataBaseAdapter(this)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        val uid = firebaseAuth.currentUser!!.uid
        val ticketsReference = firebaseDatabase.getReference("users").child(uid).child("tickets")


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

        deleteButton.setOnClickListener {
            dbAdapter.deleteTicketById(ticket.id)
            ticketsReference.child(getUniqueIdByTicket(ticket)).removeValue()
            Toast.makeText(this, "Квиток був видалений", Toast.LENGTH_LONG).show()

            finish()
        }

        downloadButton.setOnClickListener {
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, getFileNameForTicket(ticket) + System.currentTimeMillis())

            val pdfDocument = createPdfForTicket(this, ticket)
            writeFileToStorage(file, pdfDocument)

            finish()

            Toast.makeText(this, "Квиток був успішно завантажений", Toast.LENGTH_LONG).show()
        }

        shareButton.setOnClickListener {
            val file = File(this.filesDir, getFileNameForTicket(ticket))

            if(file.exists()){
                sharePdf(this, file)
            } else {
                val pdfDocument = createPdfForTicket(this, ticket)
                writeFileToStorage(file, pdfDocument)
                sharePdf(this, file)
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

        fun sharePdf(context: Context, file: File){
            val fileUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, fileUri)
                type = "application/pdf"
            }
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(Intent.createChooser(shareIntent, "Надіслати квиток"))
        }
    }

}