package com.example.ticketgeneratorproject

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ticketgeneratorproject.Entities.TicketModel
import com.example.ticketgeneratorproject.databinding.TicketPreviewBinding

class TicketPreview : AppCompatActivity() {
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

        Log.d("myLog", "TicketPreview works")

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
                }, DetailedInformationAboutTicket.DOUBLE_CLICK_DELAY.toLong())
            }
        }
    }

    companion object {
        private const val EXTRA_KEY = "DetailedInformationAboutTicket_TO_TicketPreview_ticketData"
    }
}