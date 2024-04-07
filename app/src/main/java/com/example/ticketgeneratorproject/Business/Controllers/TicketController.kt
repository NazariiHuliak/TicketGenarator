package com.example.ticketgeneratorproject.Business.Controllers

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import com.example.ticketgeneratorproject.Data.Entities.TicketModel
import com.example.ticketgeneratorproject.R
import com.example.ticketgeneratorproject.Presentation.TicketFragment
import com.example.ticketgeneratorproject.databinding.TicketFragmentBinding

object TicketController {
    fun getFileNameForTicket(ticket: TicketModel, addTime: Boolean): String {
        return (TextController.transliterateToEnglish(ticket.fullName).split(" ")[0] + " " +
                TextController.transliterateToEnglish(ticket.fullName).split(" ")[1] + " " +
                TextController.transliterateToEnglish(ticket.departureAddress.city) + "-" +
                TextController.transliterateToEnglish(ticket.destinationAddress.city) + " " +
                ticket.purchaseDateTime.date + if (addTime) System.currentTimeMillis()
            .toString() else "") +
                ".pdf".replace(":", ".")
    }

    fun createPdfForTicket(context: Context, ticket: TicketModel): PdfDocument {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.ticket_fragment, null)
        TicketFragment.setTicketDataInView(TicketFragmentBinding.bind(view), ticket)

        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display!!.getRealMetrics(displayMetrics)
        }
        view.measure(
            View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)

        val document = PdfDocument()
        val viewWidth = 1080
        val viewHeight = 1980
        val pageInfo = PdfDocument.PageInfo.Builder(viewWidth, viewHeight, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()
        paint.color = Color.WHITE
        view.draw(canvas)
        document.finishPage(page)

        return document
    }

    fun getUniqueIdByTicket(ticket: TicketModel): String{
        return ticket.purchaseDateTime.time.replace(":", "") +
                ticket.purchaseDateTime.date.replace("-", "")
    }
}