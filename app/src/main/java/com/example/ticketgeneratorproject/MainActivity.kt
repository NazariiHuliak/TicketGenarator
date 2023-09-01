package com.example.ticketgeneratorproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ticketgeneratorproject.Entities.Address
import com.example.ticketgeneratorproject.Entities.Currency
import com.example.ticketgeneratorproject.Entities.DateTime
import com.example.ticketgeneratorproject.Entities.TicketModel

class MainActivity : AppCompatActivity() {

    private var lastBackPressTime: Long = 0
    private val BACK_PRESS_INTERVAL = 2000

    private lateinit var recyclerView: RecyclerView
    private lateinit var ticketsArrayList: ArrayList<TicketModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = RecyclerViewAdapter(
            arrayListOf(TicketModel(
                1,
                "Full name",
                "Trip number",
                Address.parseAddress("Україна, Львів, Січових стрільців, 26"),
                Address.parseAddress("Україна, Львів, Січових стрільців, 26"),
                DateTime("date", "Time"),
                DateTime("date", "Time"),
                1,
                Currency.UAH,
                DateTime("date", "Time")
            )))

        val addBtn = findViewById<Button>(R.id.create_new_ticket)

        addBtn.setOnClickListener {
            val intent = Intent(this, EnterTicketData::class.java)
            startActivity(intent)
        }
    }
    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBackPressTime < BACK_PRESS_INTERVAL) {
            finishAffinity()
        } else {
            lastBackPressTime = currentTime
            Toast.makeText(this, "Натисніть ще раз для виходу", Toast.LENGTH_SHORT).show()
        }
    }
}