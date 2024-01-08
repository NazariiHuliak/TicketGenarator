package com.example.ticketgeneratorproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.example.ticketgeneratorproject.Adapters.RecyclerViewAdapter
import com.example.ticketgeneratorproject.DataBase.DataBaseAdapter
import com.example.ticketgeneratorproject.Entities.TicketModel
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class HomePage : AppCompatActivity() {

    private var lastBackPressTime: Long = 0
    private val BACK_PRESS_INTERVAL = 2000
    private val INITIAL_TICKETS_SYNCHRONIZATION = "initialTicketsSynchronization"
    private val INITIAL_ADDRESSES_SYNCHRONIZATION = "initialAddressesSynchronization"

    private lateinit var recyclerView: RecyclerView
    private lateinit var ticketsArrayList: MutableList<TicketModel>
    private lateinit var searchField: EditText
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var addButton: Button
    private lateinit var logoutButton: Button

    private lateinit var usernameField: TextView
    private lateinit var emailField: TextView

    private lateinit var materialCardView: MaterialCardView
    private lateinit var hiddenMaterialCardContent: LinearLayout

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseDatabaseRef: DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page_layout)
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("HomePagePreferences", Context.MODE_PRIVATE)

        val dbAdapter = DataBaseAdapter(this)

        searchField = findViewById(R.id.search_input)

        layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        addButton = findViewById(R.id.create_new_ticket)
        logoutButton = findViewById(R.id.logout)

        usernameField = findViewById(R.id.username)
        emailField = findViewById(R.id.email)

        materialCardView = findViewById(R.id.main_menu)
        hiddenMaterialCardContent = findViewById(R.id.hiden_card_content)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference("users")
        val uid = firebaseAuth.currentUser!!.uid
        val userReference = firebaseDatabaseRef.child(uid).child("name")
        val ticketsReference = firebaseDatabaseRef.child(uid).child("tickets")
        val addressReference = firebaseDatabaseRef.child(uid).child("commonAddresses")

        if (!sharedPreferences.getBoolean(INITIAL_TICKETS_SYNCHRONIZATION, false)) {
            loadTicketsDataFromFirebase(dbAdapter, ticketsReference, sharedPreferences)
        } else {
            ticketsArrayList = dbAdapter.getAllTickets()
            recyclerViewAdapter = RecyclerViewAdapter(ticketsArrayList)
            recyclerView.adapter = recyclerViewAdapter
        }
        if(!sharedPreferences.getBoolean(INITIAL_ADDRESSES_SYNCHRONIZATION, false)){
            loadAddressesDataFromFirebase(dbAdapter, addressReference, sharedPreferences)
        }

        emailField.text = firebaseAuth.currentUser?.email.toString()
        userReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val data = snapshot.getValue(String::class.java)
                    usernameField.text = data
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("myLog", "Error: ${error.message}")
            }
        })

        materialCardView.setOnClickListener {
            animateCardExpansion(materialCardView, hiddenMaterialCardContent)
        }
        logoutButton.setOnClickListener {
            dbAdapter.deleteAllTicket()
            dbAdapter.deleteAllAddresses()

            sharedPreferences.edit().putBoolean(INITIAL_TICKETS_SYNCHRONIZATION, false).apply()
            sharedPreferences.edit().putBoolean(INITIAL_ADDRESSES_SYNCHRONIZATION, false).apply()

            firebaseAuth.signOut()
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }

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
        addButton.setOnClickListener {
            val intent = Intent(this, AddTicketPage1::class.java)
            startActivity(intent)
        }
    }

    private fun loadTicketsDataFromFirebase(
        dbAdapter: DataBaseAdapter,
        ticketsReference: DatabaseReference,
        sharedPreferences: SharedPreferences
    ){
        ticketsReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!sharedPreferences.getBoolean(INITIAL_TICKETS_SYNCHRONIZATION, false)){
                    Log.d("myLog", "Works2")
                    val ticketsList = mutableListOf<TicketModel>()
                    for (ticketSnapshot in snapshot.children) {
                        val ticket = ticketSnapshot.getValue(TicketModel::class.java)
                        if (ticket != null) {
                            ticketsList.add(ticket)
                        }
                    }

                    dbAdapter.addAllTickets(ticketsList)

                    ticketsArrayList = ticketsList
                    recyclerViewAdapter = RecyclerViewAdapter(ticketsArrayList)
                    recyclerView.adapter = recyclerViewAdapter

                    sharedPreferences.edit().putBoolean(INITIAL_TICKETS_SYNCHRONIZATION, true).apply()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("myLog", "Error: ${error.message}")
            }
        })
    }

    private fun loadAddressesDataFromFirebase(
        dbAdapter: DataBaseAdapter,
        addressReference: DatabaseReference,
        sharedPreferences: SharedPreferences
    ){
        addressReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!sharedPreferences.getBoolean(INITIAL_ADDRESSES_SYNCHRONIZATION, false)){
                    Log.d("myLog", "Works1")
                    val addressesList = mutableListOf<String>()
                    for (addressSnapshot in snapshot.children) {
                        val address = addressSnapshot.getValue(String::class.java)
                        if (address != null) {
                            addressesList.add(address)
                        }
                    }

                    dbAdapter.addAllAddresses(addressesList)
                    sharedPreferences.edit().putBoolean(INITIAL_ADDRESSES_SYNCHRONIZATION, true).apply()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("myLog", "Error: ${error.message}")
            }
        })
    }

    private fun animateCardExpansion(materialCardView: MaterialCardView, content: LinearLayout) {
        val visibility = if (content.visibility == View.GONE) View.VISIBLE else View.GONE

        TransitionManager.beginDelayedTransition(materialCardView, AutoTransition())
        content.visibility = visibility
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBackPressTime < BACK_PRESS_INTERVAL) {
            finishAffinity()
            super.onBackPressed()
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
        recyclerViewAdapter.setFilteredList(filteredList as MutableList<TicketModel>)
        recyclerViewAdapter.notifyDataSetChanged()

        layoutManager.reverseLayout = false
        layoutManager.stackFromEnd = false
    }
}