package com.example.ticketgeneratorproject

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ticketgeneratorproject.DataBase.DataBaseAdapter
import com.example.ticketgeneratorproject.Entities.DateTime
import com.example.ticketgeneratorproject.Entities.TicketModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class EnterTicketDataTime: AppCompatActivity() {
    private lateinit var departureDateText: TextView
    private lateinit var departureTimeText: TextView
    private lateinit var destinationDateText: TextView
    private lateinit var destinationTimeText: TextView

    private lateinit var errorText1: TextView
    private lateinit var errorText2: TextView
    private lateinit var errorText3: TextView
    private lateinit var errorText4: TextView

    private lateinit var error_icon_1: ImageView
    private lateinit var error_icon_2: ImageView
    private lateinit var error_icon_3: ImageView
    private lateinit var error_icon_4: ImageView

    private lateinit var ticket: TicketModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_ticket_data_time)

        val intentHasExtraToUpdate = intent.hasExtra("EnterTicketData_TO_EnterTicketDataTime_TicketData_Update")
        val intentHasExtraToCreateSimilar = intent.hasExtra("EnterTicketData_TO_EnterTicketDataTime_TicketData_CreateSimilar")

        departureDateText = findViewById(R.id.departure_date)
        destinationDateText = findViewById(R.id.destination_date)
        departureTimeText = findViewById(R.id.departure_time)
        destinationTimeText = findViewById(R.id.destination_time)

        errorText1 = findViewById(R.id.errorText1)
        errorText2 = findViewById(R.id.errorText2)
        errorText3 = findViewById(R.id.errorText3)
        errorText4 = findViewById(R.id.errorText4)

        error_icon_1 = findViewById(R.id.error_icon_1)
        error_icon_2 = findViewById(R.id.error_icon_2)
        error_icon_3 = findViewById(R.id.error_icon_3)
        error_icon_4 = findViewById(R.id.error_icon_4)

        var noError = true

        var dbAdapter = DataBaseAdapter(this)

        var datePickerState = -1
        var timePickerState = -1
        val myCalendar = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            when (datePickerState){
                1->updateDateText(myCalendar, departureDateText)
                2->updateDateText(myCalendar, destinationDateText)
                else -> Log.d("processing", "problem")
            }
        }

        val timePickerListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            myCalendar.set(Calendar.MINUTE, minute)
            when (timePickerState){
                1->updateTimeText(myCalendar, departureTimeText)
                2->updateTimeText(myCalendar, destinationTimeText)
                else -> Log.d("processing", "problem")
            }
        }

        departureDateText.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            if(departureDateText.text!!.isNotEmpty()){
                errorText1.visibility = View.INVISIBLE
                error_icon_1.visibility = View.INVISIBLE
                noError = true
            }
        }

        departureTimeText.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            if(departureDateText.text!!.isNotEmpty()){
                errorText2.visibility = View.INVISIBLE
                error_icon_2.visibility = View.INVISIBLE
                noError = true
            }
        }

        destinationDateText.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            if(departureDateText.text!!.isNotEmpty()){
                errorText3.visibility = View.INVISIBLE
                error_icon_3.visibility = View.INVISIBLE
                noError = true
            }
        }

        destinationTimeText.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            if(departureDateText.text!!.isNotEmpty()){
                errorText4.visibility = View.INVISIBLE
                error_icon_4.visibility = View.INVISIBLE
                noError = true
            }
        }

        if(intentHasExtraToUpdate)
            ticket = intent.getSerializableExtra("EnterTicketData_TO_EnterTicketDataTime_TicketData_Update")
                    as TicketModel
        else if(intentHasExtraToCreateSimilar)
            ticket = intent.getSerializableExtra("EnterTicketData_TO_EnterTicketDataTime_TicketData_CreateSimilar")
                    as TicketModel
        else
            ticket = intent.getSerializableExtra("EnterTicketData_TO_EnterTicketDataTime_TicketData_Complete")
                    as TicketModel

        if(intentHasExtraToUpdate || intentHasExtraToCreateSimilar) {
            departureDateText.text = ticket.departureDateTime.Date
            departureTimeText.text = ticket.departureDateTime.Time

            destinationDateText.text = ticket.destinationDateTime.Date
            destinationTimeText.text = ticket.destinationDateTime.Time
        }

        findViewById<RelativeLayout>(R.id.btn_departure_date).setOnClickListener {
            DatePickerDialog( this, R.style.CustomDatePickerDialogTheme, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show()
            datePickerState = 1
        }

        findViewById<RelativeLayout>(R.id.btn_destination_date).setOnClickListener {
            DatePickerDialog( this, R.style.CustomDatePickerDialogTheme, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show()
            datePickerState = 2
        }

        findViewById<RelativeLayout>(R.id.btn_departure_time).setOnClickListener {
            TimePickerDialog(this, R.style.CustomDatePickerDialogTheme, timePickerListener, myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE), true).show()
            timePickerState = 1
        }

        findViewById<RelativeLayout>(R.id.btn_destination_time).setOnClickListener {
            TimePickerDialog(this, R.style.CustomDatePickerDialogTheme, timePickerListener, myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE), true).show()
            timePickerState = 2
        }

        findViewById<LinearLayout>(R.id.back_to_previous_page).setOnClickListener{
            finish()
        }

        findViewById<Button>(R.id.save_ticket).setOnClickListener {
            if (departureDateText.text.isEmpty()) {
                errorText1.visibility = View.VISIBLE
                error_icon_1.visibility = View.VISIBLE
                noError = false
            }
            if (departureTimeText.text.isEmpty()) {
                errorText2.visibility = View.VISIBLE
                error_icon_2.visibility = View.VISIBLE
                noError = false
            }
            if (destinationDateText.text.isEmpty()) {
                errorText3.visibility = View.VISIBLE
                error_icon_3.visibility = View.VISIBLE
                noError = false
            }
            if (destinationTimeText.text.isEmpty()) {
                errorText4.visibility = View.VISIBLE
                error_icon_4.visibility = View.VISIBLE
                noError = false
            }
            if (noError) {
                ticket.departureDateTime =
                    DateTime.parseDateTime("${departureDateText.text} ${departureTimeText.text}")
                ticket.destinationDateTime =
                    DateTime.parseDateTime("${destinationDateText.text} ${destinationTimeText.text}")

                if (intentHasExtraToUpdate) {
                    dbAdapter.updateTicket(ticket)
                } else {
                    ticket.purchaseDateTime = DateTime.parseDateTime(getCurrentDateTime())
                    dbAdapter.addTicket(ticket)
                }

                Toast.makeText(this, "Квиток був успішно збережений", Toast.LENGTH_LONG).show()

                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)
            }
        }

        findViewById<Button>(R.id.generate_ticket).setOnClickListener {
            if(departureDateText.text.isEmpty()){
                errorText1.visibility = View.VISIBLE
                error_icon_1.visibility = View.VISIBLE
                noError = false
            }
            if(departureTimeText.text.isEmpty()){
                errorText2.visibility = View.VISIBLE
                error_icon_2.visibility = View.VISIBLE
                noError = false
            }
            if(destinationDateText.text.isEmpty()){
                errorText3.visibility = View.VISIBLE
                error_icon_3.visibility = View.VISIBLE
                noError = false
            }
            if(destinationTimeText.text.isEmpty()){
                errorText4.visibility = View.VISIBLE
                error_icon_4.visibility = View.VISIBLE
                noError = false
            }
            if(noError){
                ticket.departureDateTime = DateTime.parseDateTime("${departureDateText.text} ${departureTimeText.text}")
                ticket.destinationDateTime = DateTime.parseDateTime("${destinationDateText.text} ${destinationTimeText.text}")

                if (intentHasExtraToUpdate){
                    dbAdapter.updateTicket(ticket)
                } else {
                    ticket.purchaseDateTime = DateTime.parseDateTime(getCurrentDateTime())
                    dbAdapter.addTicket(ticket)
                }

                if (askPermissions()){
                    convertXmlToPdf(ticket, this)
                }

                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)
            }
        }

    }

    private fun updateDateText (myCalendar: Calendar, view: TextView) {
        val sdf = SimpleDateFormat ("dd-MM-yyyy", Locale.UK)
        view.setText(sdf.format(myCalendar.time))
    }
    private fun updateTimeText (myCalendar: Calendar, view: TextView) {
        val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(myCalendar.time)
        view.setText(formattedTime.format(myCalendar.time))
    }
    private fun askPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
    }
    companion object{
        @SuppressLint("MissingInflatedId", "SetTextI18n", "InflateParams")
        fun convertXmlToPdf(ticket: TicketModel, context: Context) {
            val view: View = LayoutInflater.from(context).inflate(R.layout.to_generate_pdf, null)

            view.findViewById<TextView>(R.id.ticket_fullName).text = ticket.fullName
            view.findViewById<TextView>(R.id.ticket_tripNumber).text = ticket.tripNumber
            view.findViewById<TextView>(R.id.ticket_departureCity).text = ticket.departureAddress.city
            view.findViewById<TextView>(R.id.ticket_departureAddress).text = ticket.departureAddress.street + " " +
                    ticket.departureAddress.number
            view.findViewById<TextView>(R.id.ticket_departureDate).text = ticket.departureDateTime.Date
            view.findViewById<TextView>(R.id.ticket_departureTime).text = ticket.departureDateTime.Time
            view.findViewById<TextView>(R.id.ticket_destinationCity).text = ticket.destinationAddress.city
            view.findViewById<TextView>(R.id.ticket_destinationAddress).text = ticket.destinationAddress.street + " " +
                    ticket.destinationAddress.number
            view.findViewById<TextView>(R.id.ticket_destinationDate).text = ticket.destinationDateTime.Date
            view.findViewById<TextView>(R.id.ticket_destinationTime).text = ticket.destinationDateTime.Time
            view.findViewById<TextView>(R.id.ticket_price).text = ticket.price.toString()
            view.findViewById<TextView>(R.id.ticket_currency).text = ticket.currency.toString()
            view.findViewById<TextView>(R.id.ticket_seat).text = ticket.seat.toString()
            view.findViewById<TextView>(R.id.ticket_purchaseDate).text = ticket.purchaseDateTime.Time + " " +
                    ticket.purchaseDateTime.Date

            val displayMetrics = DisplayMetrics()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.display!!.getRealMetrics(displayMetrics)
            }
            view.measure(
                View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, View.MeasureSpec.EXACTLY)
            )

            view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
            // Create a new PdfDocument instance
            val document = PdfDocument()

            val viewWidth = 1080
            val viewHeight = 1920

            // Create a PageInfo object specifying the page attributes
            val pageInfo = PageInfo.Builder(viewWidth, viewHeight, 1).create()

            // Start a new page
            val page = document.startPage(pageInfo)

            // Get the Canvas object to draw on the page
            val canvas = page.canvas

            // Create a Paint object for styling the view
            val paint = Paint()
            paint.color = Color.WHITE

            // Draw the view on the canvas
            view.draw(canvas)

            // Finish the page
            document.finishPage(page)

            // Specify the path and filename of the output PDF file
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

            var parts_time = ticket.purchaseDateTime.Time.replace(":", " ").split(" ")
            var parts_date = ticket.purchaseDateTime.Date.replace("-", " ").split(" ")
            var uniqueId = ((parts_time[0].toInt() + parts_time[1].toInt() +
                    parts_date[0].toInt() + parts_date[1].toInt() + parts_date[2].toInt()))

            val fileName = transliterateToEnglish(ticket.fullName).split(" ")[0] + " " +
                    transliterateToEnglish(ticket.fullName).split(" ")[1] + " " +
                    transliterateToEnglish(ticket.departureAddress.city) + "-" +
                    transliterateToEnglish(ticket.destinationAddress.city) + " " +
                    ticket.purchaseDateTime.Date + " " + System.currentTimeMillis().toString() +
                    ".pdf"

            val filePath = File(downloadsDir, fileName)
            try {
                val fos = FileOutputStream(filePath)
                document.writeTo(fos)
                document.close()
                fos.close()

                Toast.makeText(context, "Квиток був успішно створений", Toast.LENGTH_LONG).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "Квиток не був завантажений", Toast.LENGTH_LONG).show()
                Log.d("myLog", e.toString())
            }
        }
        fun transliterateToEnglish(input: String): String {
            val ukrainianCharacters = arrayOf(
                "а", "б", "в", "г", "д", "е", "є", "ж", "з", "и", "і", "ї", "й", "к",
                "л", "м", "н", "о", "п", "р", "с", "т", "у", "ф", "х", "ц", "ч", "ш",
                "щ", "ь", "ю", "я",
                "А", "Б", "В", "Г", "Д", "Е", "Є", "Ж", "З", "И", "І", "Ї", "Й", "К",
                "Л", "М", "Н", "О", "П", "Р", "С", "Т", "У", "Ф", "Х", "Ц", "Ч", "Ш",
                "Щ", "Ь", "Ю", "Я"
            )

            val latinTransliteration = arrayOf(
                "a", "b", "v", "h", "d", "e", "ie", "zh", "z", "y", "i", "i", "i", "k",
                "l", "m", "n", "o", "p", "r", "s", "t", "u", "f", "kh", "ts", "ch", "sh",
                "shch", "", "iu", "ia",
                "A", "B", "V", "H", "D", "E", "Ye", "Zh", "Z", "Y", "I", "I", "I", "K",
                "L", "M", "N", "O", "P", "R", "S", "T", "U", "F", "Kh", "Ts", "Ch", "Sh",
                "Shch", "", "Yu", "Ya"
            )

            val stringBuilder = StringBuilder()
            val inputChars = input.toCharArray()

            for (char in inputChars) {
                val index = ukrainianCharacters.indexOf(char.toString())
                if (index != -1) {
                    stringBuilder.append(latinTransliteration[index])
                } else {
                    stringBuilder.append(char)
                }
            }
            return stringBuilder.toString()
        }
        fun getCurrentDateTime(): String {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            return dateFormat.format(Date())
        }
    }
}