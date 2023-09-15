package com.example.ticketgeneratorproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ticketgeneratorproject.DataBase.DataBaseAdapter
import com.example.ticketgeneratorproject.Entities.TicketModel
import java.lang.Character.toLowerCase
import java.util.*

class MainActivity : AppCompatActivity() {

    private var lastBackPressTime: Long = 0
    private val BACK_PRESS_INTERVAL = 2000

    private lateinit var recyclerView: RecyclerView
    private lateinit var ticketsArrayList: MutableList<TicketModel>
    private lateinit var searchField: EditText
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dbAdapter = DataBaseAdapter(this)
        searchField = findViewById(R.id.search_input)


        layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        ticketsArrayList = dbAdapter.getTickets()
        recyclerViewAdapter = RecyclerViewAdapter(ticketsArrayList)
        recyclerView.adapter = recyclerViewAdapter

        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterRecyclerView(s.toString(), recyclerViewAdapter, ticketsArrayList, layoutManager)
            }
            override fun afterTextChanged(s: Editable) {
                layoutManager.reverseLayout = true
                layoutManager.stackFromEnd = true
                layoutManager.scrollToPosition(recyclerViewAdapter.itemCount-1)
            }
        })

        val addBtn = findViewById<Button>(R.id.create_new_ticket)

        addBtn.setOnClickListener {
            val intent = Intent(this, EnterTicketData::class.java)
            startActivity(intent)
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBackPressTime < BACK_PRESS_INTERVAL) {
            finishAffinity()
        } else {
            lastBackPressTime = currentTime
            Toast.makeText(this, "Натисніть ще раз для виходу", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterRecyclerView(searchText: String,
                                   recyclerViewAdapter: RecyclerViewAdapter,
                                   ticketsArrayList:MutableList<TicketModel>,
                                   layoutManager: LinearLayoutManager) {

        val filteredList = ticketsArrayList.filter { item ->
            item.fullName.lowercase().contains(searchText.lowercase())
        }

        recyclerViewAdapter.setYourDataList(filteredList as MutableList<TicketModel>)
        recyclerViewAdapter.notifyDataSetChanged()

        layoutManager.reverseLayout = false
        layoutManager.stackFromEnd = false
    }
}