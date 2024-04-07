package com.example.ticketgeneratorproject.Presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ticketgeneratorproject.Data.Entities.TicketModel
import com.example.ticketgeneratorproject.R
import com.example.ticketgeneratorproject.Business.Controllers.TimeController.DOUBLE_CLICK_DELAY
import com.example.ticketgeneratorproject.databinding.TicketPreviewBinding

class TicketPreview : AppCompatActivity() {
    private val EXTRA_KEY = "DetailedInformationAboutTicket_TO_TicketPreview_ticketData"
    private var doubleClick = false
    private lateinit var binding: TicketPreviewBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TicketPreviewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())

        if (intent.hasExtra(EXTRA_KEY)){
            val ticket =
                intent.getSerializableExtra(EXTRA_KEY)
                        as TicketModel

            val ticketFragment = TicketFragment()
            val bundle = Bundle().apply {
                putSerializable("Ticket", ticket)
            }
            ticketFragment.arguments = bundle
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.ticketFragment, ticketFragment)
                commit()
            }
        }


        binding.ticketFragment.setOnClickListener {
            if (doubleClick) {
                finish()
                doubleClick = false
            } else {
                doubleClick = true
                Handler(Looper.getMainLooper()).postDelayed({
                    doubleClick = false
                }, DOUBLE_CLICK_DELAY.toLong())
            }
        }
    }
}