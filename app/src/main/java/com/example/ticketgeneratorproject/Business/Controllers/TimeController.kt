package com.example.ticketgeneratorproject.Business.Controllers

import android.icu.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeController {
    const val DOUBLE_CLICK_DELAY = 300

    fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }
}