package com.example.ticketgeneratorproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract.Root
import android.sax.RootElement
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*

class EnterTicketData : AppCompatActivity() {
    private var lastBackPressTime: Long = 0
    private val BACK_PRESS_INTERVAL = 2000

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_ticket_data)

        val items = listOf("₴ Гривня", "\$ Долар", "€ Євро")
        val autoComplete: AutoCompleteTextView = findViewById(R.id.auto_complete)
        val adapter = ArrayAdapter(this, R.layout.currency_item, items)
        autoComplete.setAdapter(adapter)

        findViewById<LinearLayout>(R.id.back_to_main_menu).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.next_page).setOnClickListener {
            val intent = Intent(this, EnterTicketDataTime::class.java)
            startActivity(intent)
        }

    }
}