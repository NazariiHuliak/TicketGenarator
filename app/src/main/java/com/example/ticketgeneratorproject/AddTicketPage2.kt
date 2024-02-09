package com.example.ticketgeneratorproject


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
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
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.ticketgeneratorproject.DataBase.DataBaseAdapter
import com.example.ticketgeneratorproject.Entities.Address
import com.example.ticketgeneratorproject.additionalClasses.ApplicationSettings
import com.example.ticketgeneratorproject.Entities.Currency
import com.example.ticketgeneratorproject.Entities.DateTime
import com.example.ticketgeneratorproject.Entities.TicketModel
import com.example.ticketgeneratorproject.databinding.AddTicketPage2Binding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class AddTicketPage2: AppCompatActivity() {
    private lateinit var currencyDropDownMenu: AutoCompleteTextView
    private lateinit var price: TextView
    private lateinit var priceLayout: TextInputLayout
    private lateinit var currencyLayout: TextInputLayout

    private lateinit var departureLayoutTime: RelativeLayout
    private lateinit var departureLayoutDate: RelativeLayout
    private lateinit var destinationLayoutTime: RelativeLayout
    private lateinit var destinationLayoutDate: RelativeLayout

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

    private lateinit var saveButton: Button
    private lateinit var downloadButton: Button

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var dbAdapter: DataBaseAdapter

    private lateinit var binding: AddTicketPage2Binding

    private lateinit var ticket: TicketModel
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddTicketPage2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        //setting list of items for auto complete text view
        currencyDropDownMenu = findViewById<AutoCompleteTextView>(R.id.auto_complete1)
        val items = listOf("₴ Гривня", "\$ Долар", "€ Євро")
        val adapter = ArrayAdapter(this, R.layout.currency_item, items)
        currencyDropDownMenu.setAdapter(adapter)

        price = findViewById<TextInputEditText>(R.id.price)
        priceLayout = findViewById(R.id.price_layout)
        currencyLayout = findViewById(R.id.currency_layout)

        departureLayoutTime = findViewById(R.id.btn_departure_time)
        departureLayoutDate = findViewById(R.id.btn_departure_date)
        destinationLayoutTime = findViewById(R.id.btn_destination_time)
        destinationLayoutDate = findViewById(R.id.btn_destination_date)

        departureDateText = findViewById(R.id.departure_date)
        destinationDateText = findViewById(R.id.destination_date)
        departureTimeText = findViewById(R.id.departure_time)
        destinationTimeText = findViewById(R.id.destination_time)

        saveButton = findViewById(R.id.save_btn)
        downloadButton = findViewById(R.id.generate_ticket)

        errorText1 = findViewById(R.id.errorText1)
        errorText2 = findViewById(R.id.errorText2)
        errorText3 = findViewById(R.id.errorText3)
        errorText4 = findViewById(R.id.errorText4)

        error_icon_1 = findViewById(R.id.error_icon_1)
        error_icon_2 = findViewById(R.id.error_icon_2)
        error_icon_3 = findViewById(R.id.error_icon_3)
        error_icon_4 = findViewById(R.id.error_icon_4)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        dbAdapter = DataBaseAdapter(this)

        val uid = firebaseAuth.currentUser!!.uid
        val ticketsReference = firebaseDatabase.getReference("users").child(uid).child("tickets")
        val addressesReference = firebaseDatabase.getReference("users").child(uid).child("commonAddresses")

        //find intent Extra and set proper data
        val intentHasExtraToUpdate = intent.hasExtra("EnterTicketData_TO_EnterTicketDataTime_TicketData_Update")
        val intentHasExtraToCreateSimilar = intent.hasExtra("EnterTicketData_TO_EnterTicketDataTime_TicketData_CreateSimilar")
        ticket = if (intentHasExtraToUpdate) {
            intent.getSerializableExtra("EnterTicketData_TO_EnterTicketDataTime_TicketData_Update")
                    as TicketModel
        } else if (intentHasExtraToCreateSimilar) {
            intent.getSerializableExtra("EnterTicketData_TO_EnterTicketDataTime_TicketData_CreateSimilar")
                    as TicketModel
        } else {
            intent.getSerializableExtra("EnterTicketData_TO_EnterTicketDataTime_TicketData_Complete")
                    as TicketModel
        }
        if(intentHasExtraToUpdate || intentHasExtraToCreateSimilar) {
            departureDateText.text = ticket.departureDateTime.date
            departureTimeText.text = ticket.departureDateTime.time
            destinationDateText.text = ticket.destinationDateTime.date
            destinationTimeText.text = ticket.destinationDateTime.time
            price.text = ticket.price.toString()
            currencyDropDownMenu.setText(Currency.parseToString(ticket.currency), false)
        }

        var hasInputtingErrors = false
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
        val timePicker = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            myCalendar.set(Calendar.MINUTE, minute)
            when (timePickerState){
                1->updateTimeText(myCalendar, departureTimeText)
                2->updateTimeText(myCalendar, destinationTimeText)
                else -> Log.d("processing", "problem")
            }
        }

        price.addTextChangedListener {
            if(it!!.count()>0){
                priceLayout.error = null
                hasInputtingErrors = false
            }
        }
        currencyDropDownMenu.addTextChangedListener {
            if(it!!.count()>0){
                currencyLayout.error = null
                hasInputtingErrors = false
            }
        }
        departureDateText.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            if(departureDateText.text!!.isNotEmpty()){
                errorText1.visibility = View.INVISIBLE
                error_icon_1.visibility = View.INVISIBLE
                hasInputtingErrors = false
            }
        }
        departureTimeText.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            if(departureDateText.text!!.isNotEmpty()){
                errorText2.visibility = View.INVISIBLE
                error_icon_2.visibility = View.INVISIBLE
                hasInputtingErrors = false
            }
        }
        destinationDateText.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            if(departureDateText.text!!.isNotEmpty()){
                errorText3.visibility = View.INVISIBLE
                error_icon_3.visibility = View.INVISIBLE
                hasInputtingErrors = false
            }
        }
        destinationTimeText.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            if(departureDateText.text!!.isNotEmpty()){
                errorText4.visibility = View.INVISIBLE
                error_icon_4.visibility = View.INVISIBLE
                hasInputtingErrors = false
            }
        }

        departureLayoutDate.setOnClickListener {
            DatePickerDialog( this, R.style.CustomDatePickerDialogTheme, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show()
            datePickerState = 1
        }
        destinationLayoutDate.setOnClickListener {
            DatePickerDialog( this, R.style.CustomDatePickerDialogTheme, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show()
            datePickerState = 2
        }
        departureLayoutTime.setOnClickListener {
            TimePickerDialog(this, R.style.CustomDatePickerDialogTheme, timePicker, myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE), true).show()
            timePickerState = 1
        }
        destinationLayoutTime.setOnClickListener {
            TimePickerDialog(this, R.style.CustomDatePickerDialogTheme, timePicker, myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE), true).show()
            timePickerState = 2
        }
        currencyDropDownMenu.setOnClickListener {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currencyDropDownMenu.windowToken, 0)
        }

        findViewById<LinearLayout>(R.id.back_to_previous_page).setOnClickListener{
            finish()
        }

        saveButton.setOnClickListener {
            val priceText = price.text.toString()
            val currencyText = currencyDropDownMenu.text.toString()

            hasInputtingErrors = checkForEmptyFields(binding)

            if (!hasInputtingErrors) {
                ticket.price = priceText.toDouble()
                ticket.currency = Currency.parseToCurrency(currencyText)

                ticket.departureDateTime =
                    DateTime.parseDateTime("${departureDateText.text} ${departureTimeText.text}")
                ticket.destinationDateTime =
                    DateTime.parseDateTime("${destinationDateText.text} ${destinationTimeText.text}")

                if (intentHasExtraToUpdate) {
                    dbAdapter.updateTicket(ticket)
                    ticketsReference.child(getUniqueIdByTicket(ticket)).updateChildren(ticket.getHashMap())
                } else {
                    ticket.purchaseDateTime = DateTime.parseDateTime(getCurrentDateTime())

                    dbAdapter.addTicket(ticket)
                    ticketsReference.child(getUniqueIdByTicket(ticket)).setValue(ticket.getHashMap())

                    if(ApplicationSettings.saveAddresses){
                        if(dbAdapter.isUniqueAddress(ticket.departureAddress)){
                            dbAdapter.addAddress(ticket.departureAddress)
                            addressesReference.child(ticket.departureAddress.getUniqueId()).setValue(ticket.departureAddress)
                        }
                        if(dbAdapter.isUniqueAddress(ticket.destinationAddress)){
                            dbAdapter.addAddress(ticket.destinationAddress)
                            addressesReference.child(ticket.destinationAddress.getUniqueId()).setValue(ticket.destinationAddress)
                        }
                    }
                }

                Toast.makeText(this, "Квиток був успішно збережений", Toast.LENGTH_LONG).show()

                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)
            }
        }
        downloadButton.setOnClickListener {
            val priceText = price.text.toString()
            val currencyText = currencyDropDownMenu.text.toString()
            hasInputtingErrors = checkForEmptyFields(binding)

            if(!hasInputtingErrors){
                ticket.price = priceText.toDouble()
                ticket.currency = Currency.parseToCurrency(currencyText)

                ticket.departureDateTime =
                    DateTime.parseDateTime("${departureDateText.text} ${departureTimeText.text}")
                ticket.destinationDateTime =
                    DateTime.parseDateTime("${destinationDateText.text} ${destinationTimeText.text}")

                if (intentHasExtraToUpdate) {
                    dbAdapter.updateTicket(ticket)
                    ticketsReference.child(getUniqueIdByTicket(ticket)).updateChildren(ticket.getHashMap())
                } else {
                    ticket.purchaseDateTime = DateTime.parseDateTime(getCurrentDateTime())

                    dbAdapter.addTicket(ticket)
                    ticketsReference.child(getUniqueIdByTicket(ticket)).setValue(ticket.getHashMap())

                    if(dbAdapter.isUniqueAddress(ticket.departureAddress)){
                        dbAdapter.addAddress(ticket.departureAddress)
                        addressesReference.child(ticket.departureAddress.getUniqueId()).setValue(ticket.departureAddress)
                    }
                    if(dbAdapter.isUniqueAddress(ticket.destinationAddress)){
                        dbAdapter.addAddress(ticket.destinationAddress)
                        addressesReference.child(ticket.destinationAddress.getUniqueId()).setValue(ticket.destinationAddress)
                    }
                }

                val downloadsDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, getFileNameForTicket(ticket) + System.currentTimeMillis())

                val pdfDocument = createPdfForTicket(this, ticket)
                writeFileToStorage(file, pdfDocument)

                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)

                Toast.makeText(this, "Квиток був успішно згенерований", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkForEmptyFields(binding: AddTicketPage2Binding): Boolean {
        var hasInputtingErrors = false
        if(binding.price.text!!.isEmpty()){
            binding.priceLayout.error = "Введіть дані"
            hasInputtingErrors = true
        }
        if(binding.autoComplete1.text.isEmpty()){
            binding.currencyLayout.error = "Введіть дані"
            hasInputtingErrors = true
        }
        if (binding.departureDate.text.isEmpty()) {
            binding.errorText1.visibility = View.VISIBLE
            binding.errorIcon1.visibility = View.VISIBLE
            hasInputtingErrors = true
        }
        if (departureTimeText.text.isEmpty()) {
            binding.errorText2.visibility = View.VISIBLE
            binding.errorIcon2.visibility = View.VISIBLE
            hasInputtingErrors = true
        }
        if (destinationDateText.text.isEmpty()) {
            binding.errorText3.visibility = View.VISIBLE
            binding.errorIcon3.visibility = View.VISIBLE
            hasInputtingErrors = true
        }
        if (destinationTimeText.text.isEmpty()) {
            binding.errorText4.visibility = View.VISIBLE
            binding.errorIcon4.visibility = View.VISIBLE
            hasInputtingErrors = true
        }
        return hasInputtingErrors
    }

    private fun updateDateText (myCalendar: Calendar, view: TextView) {
        val sdf = SimpleDateFormat ("dd-MM-yyyy", Locale.UK)
        view.setText(sdf.format(myCalendar.time))
    }

    private fun updateTimeText (myCalendar: Calendar, view: TextView) {
        val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(myCalendar.time)
        view.setText(formattedTime.format(myCalendar.time))
    }

    companion object{
        /*@SuppressLint("MissingInflatedId", "SetTextI18n", "InflateParams")
        fun convertXmlToPdf(ticket: TicketModel, context: Context):Boolean {
            fun askPermissions(): Boolean {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        context as Activity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1
                    )
                }
                return (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED)
            }

            if(askPermissions()){
                val view: View = LayoutInflater.from(context).inflate(R.layout.to_generate_pdf, null)

                if(ticket.fullName.length >= 32){
                    view.findViewById<TextView>(R.id.ticket_fullName).textSize = 14f;
                    if(ticket.fullName.length >= 36 && ticket.tripNumber.length >= 9){
                        view.findViewById<TextView>(R.id.ticket_tripNumber).textSize = 13f;
                    }
                }
                view.findViewById<TextView>(R.id.ticket_fullName).text = ticket.fullName
                view.findViewById<TextView>(R.id.ticket_tripNumber).text = ticket.tripNumber
                view.findViewById<TextView>(R.id.ticket_departureCity).text = ticket.departureAddress.city
                view.findViewById<TextView>(R.id.ticket_departureAddress).text = ticket.departureAddress.street + " " +
                        ticket.departureAddress.number
                view.findViewById<TextView>(R.id.ticket_departureDate).text = ticket.departureDateTime.date
                view.findViewById<TextView>(R.id.ticket_departureTime).text = ticket.departureDateTime.time
                view.findViewById<TextView>(R.id.ticket_destinationCity).text = ticket.destinationAddress.city
                view.findViewById<TextView>(R.id.ticket_destinationAddress).text = ticket.destinationAddress.street + " " +
                        ticket.destinationAddress.number
                view.findViewById<TextView>(R.id.ticket_destinationDate).text = ticket.destinationDateTime.date
                view.findViewById<TextView>(R.id.ticket_destinationTime).text = ticket.destinationDateTime.time
                view.findViewById<TextView>(R.id.ticket_price).text = ticket.price.toString()
                view.findViewById<TextView>(R.id.ticket_currency).text = ticket.currency.toString()
                view.findViewById<TextView>(R.id.ticket_seat).text = if(ticket.seat == -1) "При посадці" else ticket.seat.toString()
                view.findViewById<TextView>(R.id.ticket_purchaseDate).text = ticket.purchaseDateTime.time + " " +
                        ticket.purchaseDateTime.date

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
                val viewHeight = 1920

                val pageInfo = PageInfo.Builder(viewWidth, viewHeight, 1).create()

                val page = document.startPage(pageInfo)

                val canvas = page.canvas

                val paint = Paint()
                paint.color = Color.WHITE

                view.draw(canvas)

                document.finishPage(page)

                val downloadsDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

                val fileName = (transliterateToEnglish(ticket.fullName).split(" ")[0] + " " +
                        transliterateToEnglish(ticket.fullName).split(" ")[1] + " " +
                        transliterateToEnglish(ticket.departureAddress.city) + "-" +
                        transliterateToEnglish(ticket.destinationAddress.city) + " " +
                        ticket.purchaseDateTime.date + " " + System.currentTimeMillis().toString() +
                        ".pdf").replace(":", ".")

                val filePath = File(downloadsDir, fileName)
                try {
                    val fos = FileOutputStream(filePath)
                    document.writeTo(fos)
                    document.close()
                    fos.close()
                    Toast.makeText(context, "Квиток був успішно згенерований", Toast.LENGTH_LONG).show()
                    return true
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(context, "Квиток не був згенерований", Toast.LENGTH_LONG).show()
                    return false
                }
            }
            return false
        }*/

        fun getFileNameForTicket(ticket: TicketModel): String{
            return (transliterateToEnglish(ticket.fullName).split(" ")[0] + " " +
                    transliterateToEnglish(ticket.fullName).split(" ")[1] + " " +
                    transliterateToEnglish(ticket.departureAddress.city) + "-" +
                    transliterateToEnglish(ticket.destinationAddress.city) + " " +
                    ticket.purchaseDateTime.date /*+ " " + System.currentTimeMillis().toString()*/ +
                    ".pdf").replace(":", ".")
        }

        @SuppressLint("CutPasteId", "InflateParams", "SetTextI18n")
        fun createPdfForTicket(context: Context, ticket: TicketModel): PdfDocument{
            val view: View = LayoutInflater.from(context).inflate(R.layout.to_generate_pdf, null)

            if(ticket.fullName.length >= 32){
                view.findViewById<TextView>(R.id.ticket_fullName).textSize = 14f;
                if(ticket.fullName.length >= 36 && ticket.tripNumber.length >= 9){
                    view.findViewById<TextView>(R.id.ticket_tripNumber).textSize = 13f;
                }
            }
            view.findViewById<TextView>(R.id.ticket_fullName).text = ticket.fullName
            view.findViewById<TextView>(R.id.ticket_tripNumber).text = ticket.tripNumber
            view.findViewById<TextView>(R.id.ticket_departureCity).text = ticket.departureAddress.city
            view.findViewById<TextView>(R.id.ticket_departureAddress).text = ticket.departureAddress.street + " " +
                    ticket.departureAddress.number
            view.findViewById<TextView>(R.id.ticket_departureDate).text = ticket.departureDateTime.date
            view.findViewById<TextView>(R.id.ticket_departureTime).text = ticket.departureDateTime.time
            view.findViewById<TextView>(R.id.ticket_destinationCity).text = ticket.destinationAddress.city
            view.findViewById<TextView>(R.id.ticket_destinationAddress).text = ticket.destinationAddress.street + " " +
                    ticket.destinationAddress.number
            view.findViewById<TextView>(R.id.ticket_destinationDate).text = ticket.destinationDateTime.date
            view.findViewById<TextView>(R.id.ticket_destinationTime).text = ticket.destinationDateTime.time
            view.findViewById<TextView>(R.id.ticket_price).text = ticket.price.toString()
            view.findViewById<TextView>(R.id.ticket_currency).text = ticket.currency.toString()
            view.findViewById<TextView>(R.id.ticket_seat).text = if(ticket.seat == -1) "При посадці" else ticket.seat.toString()
            view.findViewById<TextView>(R.id.ticket_purchaseDate).text = ticket.purchaseDateTime.time + " " +
                    ticket.purchaseDateTime.date

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
            val viewHeight = 1920

            val pageInfo = PageInfo.Builder(viewWidth, viewHeight, 1).create()

            val page = document.startPage(pageInfo)

            val canvas = page.canvas

            val paint = Paint()
            paint.color = Color.WHITE

            view.draw(canvas)

            document.finishPage(page)

            return document
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
            val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
            return dateFormat.format(Date())
        }

        fun getUniqueIdByTicket(ticket: TicketModel): String{
            return ticket.purchaseDateTime.time.replace(":", "") +
                    ticket.purchaseDateTime.date.replace("-", "")
        }
    }
}