package com.example.ticketgeneratorproject.Presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ticketgeneratorproject.Data.DataBaseAdapter
import com.example.ticketgeneratorproject.Data.Entities.TicketModel
import com.example.ticketgeneratorproject.R
import com.example.ticketgeneratorproject.Business.Controllers.TimeController.DOUBLE_CLICK_DELAY
import com.example.ticketgeneratorproject.Business.Controllers.TicketController.createPdfForTicket
import com.example.ticketgeneratorproject.Business.Controllers.TicketController.getFileNameForTicket
import com.example.ticketgeneratorproject.Business.Controllers.TicketController.getUniqueIdByTicket
import com.example.ticketgeneratorproject.Business.Controllers.FileController.sharePdf
import com.example.ticketgeneratorproject.Business.Controllers.FileController.writeFileToStorage
import com.example.ticketgeneratorproject.databinding.DetailedInformationAboutTicketLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.File

class DetailedInformationAboutTicket : AppCompatActivity() {
    private var wasDoubleClick = false

    private lateinit var dbAdapter: DataBaseAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var ticket: TicketModel

    private lateinit var binding: DetailedInformationAboutTicketLayoutBinding

    @SuppressLint("MissingInflatedId", "SetTextI18n", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DetailedInformationAboutTicketLayoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        ticket = if(intent.hasExtra("recyclerViewAdapter_TO_DetailedInformationAboutTicket_ticketData")){
            intent.getSerializableExtra("recyclerViewAdapter_TO_DetailedInformationAboutTicket_ticketData")
                    as TicketModel
        } else {
            intent.getSerializableExtra("TicketPreview_TO_DetailedInformationAboutTicket_ticketData")
                    as TicketModel
        }

        dbAdapter = DataBaseAdapter(this)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        val uid = firebaseAuth.currentUser!!.uid
        val ticketsReference = firebaseDatabase.getReference("users").child(uid).child("tickets")

        val ticketFragment = TicketFragment()
        val bundle = Bundle().apply {
            putSerializable("Ticket", ticket)
        }
        ticketFragment.arguments = bundle
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.ticketFragment, ticketFragment)
            commit()
        }

        binding.goBackButton.setOnClickListener {
            finish()
        }
        binding.createSimilarButton.setOnClickListener {
            val intent = Intent(this, AddTicketPage1::class.java)
            intent.putExtra(
                "DetailedInformationTicket_TO_EnterTicketData_TicketData_CreateSimilar",
                ticket
            )
            startActivity(intent)
        }
        binding.editButton.setOnClickListener {
            val intent = Intent(this, AddTicketPage1::class.java)
            intent.putExtra(
                "DetailedInformationTicket_TO_EnterTicketData_TicketData_Update",
                ticket
            )
            startActivity(intent)
        }
        binding.deleteButton.setOnClickListener {
            dbAdapter.deleteTicketById(ticket.id)
            ticketsReference.child(getUniqueIdByTicket(ticket)).removeValue()
            Toast.makeText(this, "Квиток був видалений", Toast.LENGTH_LONG).show()

            finish()
        }
        binding.downloadButton.setOnClickListener {
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, getFileNameForTicket(ticket, true))

            val pdfDocument = createPdfForTicket(this, ticket)
            writeFileToStorage(file, pdfDocument)

            finish()

            Toast.makeText(this, "Квиток був успішно завантажений", Toast.LENGTH_LONG).show()
        }
        binding.shareButton.setOnClickListener {
            val file = File(this.filesDir, getFileNameForTicket(ticket, false))

            if(file.exists()){
                sharePdf(this, file)
            } else {
                val pdfDocument = createPdfForTicket(this, ticket)
                writeFileToStorage(file, pdfDocument)
                sharePdf(this, file)
            }
        }
        binding.ticketFragment.setOnClickListener {
            if (wasDoubleClick) {
                val intent = Intent(this, TicketPreview::class.java)
                intent.putExtra("DetailedInformationAboutTicket_TO_TicketPreview_ticketData", ticket)
                startActivity(intent)
                wasDoubleClick = false
            } else {
                wasDoubleClick = true
                Handler(Looper.getMainLooper()).postDelayed({
                    wasDoubleClick = false
                }, DOUBLE_CLICK_DELAY.toLong())
            }
        }
    }
}