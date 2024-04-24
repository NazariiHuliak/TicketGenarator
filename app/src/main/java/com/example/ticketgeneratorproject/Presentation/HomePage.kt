package com.example.ticketgeneratorproject.Presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.example.ticketgeneratorproject.Business.Adapters.TicketRecyclerViewAdapter
import com.example.ticketgeneratorproject.Data.SQLiteController
import com.example.ticketgeneratorproject.Data.Entities.Address
import com.example.ticketgeneratorproject.Business.Controllers.ProfileController
import com.example.ticketgeneratorproject.Data.Entities.TicketModel
import com.example.ticketgeneratorproject.databinding.HomePageLayoutBinding
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

    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var ticketRecyclerViewAdapter: TicketRecyclerViewAdapter
    private lateinit var ticketsArrayList: MutableList<TicketModel>

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabaseRef: DatabaseReference
    private lateinit var binding: HomePageLayoutBinding

    @SuppressLint("MissingInflatedId", "CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomePageLayoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val dbAdapter = SQLiteController(this)

        layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        binding.ticketsList.layoutManager = layoutManager
        binding.ticketsList.setHasFixedSize(true)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference("users")
        val uid = firebaseAuth.currentUser!!.uid
        val userReference = firebaseDatabaseRef.child(uid).child("name")
        val ticketsReference = firebaseDatabaseRef.child(uid).child("tickets")
        val addressReference = firebaseDatabaseRef.child(uid).child("commonAddresses")

        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("HomePagePreferences", Context.MODE_PRIVATE)

        if (!sharedPreferences.getBoolean(INITIAL_TICKETS_SYNCHRONIZATION, false)) {
            loadTicketsDataFromFirebase(dbAdapter, ticketsReference, sharedPreferences)
        } else {
            ticketsArrayList = dbAdapter.getAllTickets()
            ticketRecyclerViewAdapter = TicketRecyclerViewAdapter(ticketsArrayList)
            binding.ticketsList.adapter = ticketRecyclerViewAdapter
        }
        if (!sharedPreferences.getBoolean(INITIAL_ADDRESSES_SYNCHRONIZATION, false)) {
            loadAddressesDataFromFirebase(dbAdapter, addressReference, sharedPreferences)
        }

        binding.email.text = firebaseAuth.currentUser?.email.toString()
        userReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("CommitPrefEdits")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val data = snapshot.getValue(String::class.java)
                    binding.username.text = data
                    ProfileController.username = data!!
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("myLog", "Error: ${error.message}")
            }
        })

        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterRecyclerView(
                    s.toString(),
                    ticketRecyclerViewAdapter,
                    ticketsArrayList,
                    layoutManager
                )
            }

            override fun afterTextChanged(s: Editable) {
                layoutManager.reverseLayout = true
                layoutManager.stackFromEnd = true
                layoutManager.scrollToPosition(ticketRecyclerViewAdapter.itemCount - 1)
            }
        })

        binding.mainMenuButton.setOnClickListener {
            animateCardExpansion(binding.mainMenuButton, binding.menuContent)
        }

        binding.logoutButton.setOnClickListener {
            dbAdapter.deleteAllTicket()
            dbAdapter.deleteAllAddresses()

            sharedPreferences.edit().putBoolean(INITIAL_TICKETS_SYNCHRONIZATION, false).apply()
            sharedPreferences.edit().putBoolean(INITIAL_ADDRESSES_SYNCHRONIZATION, false).apply()

            firebaseAuth.signOut()
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }

        binding.editProfileButton.setOnClickListener {
            val intent = Intent(this, ProfilePage::class.java)
            startActivity(intent)
        }

        binding.createTicketButton.setOnClickListener {
            val intent = Intent(this, AddTicketPage1::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val dbAdapter = SQLiteController(this)

        ticketsArrayList = dbAdapter.getAllTickets()
        ticketRecyclerViewAdapter = TicketRecyclerViewAdapter(ticketsArrayList)
        binding.ticketsList.adapter = ticketRecyclerViewAdapter
    }

    private fun loadTicketsDataFromFirebase(
        dbAdapter: SQLiteController,
        ticketsReference: DatabaseReference,
        sharedPreferences: SharedPreferences
    ) {
        ticketsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!sharedPreferences.getBoolean(INITIAL_TICKETS_SYNCHRONIZATION, false)) {
                    val ticketsList = mutableListOf<TicketModel>()
                    for (ticketSnapshot in snapshot.children) {
                        val ticket = ticketSnapshot.getValue(TicketModel::class.java)
                        if (ticket != null) {
                            ticketsList.add(ticket)
                        }
                    }

                    dbAdapter.addAllTickets(ticketsList)

                    ticketsArrayList = ticketsList
                    ticketRecyclerViewAdapter = TicketRecyclerViewAdapter(ticketsArrayList)
                    binding.ticketsList.adapter = ticketRecyclerViewAdapter

                    sharedPreferences.edit().putBoolean(INITIAL_TICKETS_SYNCHRONIZATION, true)
                        .apply()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("myLog", "Error: ${error.message}")
            }
        })
    }

    private fun loadAddressesDataFromFirebase(
        dbAdapter: SQLiteController,
        addressReference: DatabaseReference,
        sharedPreferences: SharedPreferences
    ) {
        addressReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!sharedPreferences.getBoolean(INITIAL_ADDRESSES_SYNCHRONIZATION, false)) {
                    val addressesList = mutableListOf<Address>()
                    for (addressSnapshot in snapshot.children) {
                        val address = addressSnapshot.getValue(Address::class.java)
                        if (address != null) {
                            addressesList.add(address)
                        }
                    }

                    dbAdapter.addAllAddresses(addressesList)
                    sharedPreferences.edit().putBoolean(INITIAL_ADDRESSES_SYNCHRONIZATION, true)
                        .apply()
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
    private fun filterRecyclerView(
        searchText: String,
        ticketRecyclerViewAdapter: TicketRecyclerViewAdapter,
        ticketsArrayList: MutableList<TicketModel>,
        layoutManager: LinearLayoutManager
    ) {
        val filteredList = ticketsArrayList.filter { item ->
            item.fullName.lowercase().contains(searchText.lowercase())
        }
        ticketRecyclerViewAdapter.setFilteredList(filteredList as MutableList<TicketModel>)
        ticketRecyclerViewAdapter.notifyDataSetChanged()

        layoutManager.reverseLayout = false
        layoutManager.stackFromEnd = false
    }
}