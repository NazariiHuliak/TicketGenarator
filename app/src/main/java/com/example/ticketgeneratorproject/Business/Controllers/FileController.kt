package com.example.ticketgeneratorproject.Business.Controllers

import android.content.Context
import android.content.Intent
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FileController {
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

    fun writeFileToStorage(file: File, pdfDocument: PdfDocument){
        try {
            val fileOutputStream = FileOutputStream(file)
            pdfDocument.writeTo(fileOutputStream)
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}