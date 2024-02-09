package com.example.ticketgeneratorproject

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ticketgeneratorproject.Adapters.AddressRecyclerViewAdapter
import com.example.ticketgeneratorproject.DataBase.DataBaseAdapter
import com.example.ticketgeneratorproject.additionalClasses.ApplicationSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.DelicateCoroutinesApi

class ProfilePage : AppCompatActivity() {
    private val ADDRESSESKEY = "saveAddresses"

    private lateinit var backButton: LinearLayout
    private lateinit var saveButton: Button
    private lateinit var deleteSavedAddresses: Button

    private lateinit var nameInput: EditText
    private lateinit var checkBox: CheckBox
    private lateinit var savedAddressRecyclerView: RecyclerView

    private lateinit var dbAdapter: DataBaseAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)

        backButton = findViewById(R.id.back_to_main_menu)
        saveButton = findViewById(R.id.save_btn)
        deleteSavedAddresses = findViewById(R.id.deleteSavedAddresses)
        nameInput = findViewById(R.id.name_input)
        checkBox = findViewById(R.id.checkBox)
        savedAddressRecyclerView = findViewById(R.id.savedAddressRecyclerView)

        dbAdapter = DataBaseAdapter(this)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        val uid = firebaseAuth.currentUser!!.uid
        val userReference = firebaseDatabase.getReference("users").
            child(uid).child("name")
        val addressesReference = firebaseDatabase.getReference("users").
            child(uid).child("commonAddresses")

        val usersAddresses = dbAdapter.getAllAddresses().toMutableList()

        val layoutManager = LinearLayoutManager(this)
        savedAddressRecyclerView.layoutManager = layoutManager
        savedAddressRecyclerView.setHasFixedSize(true)
        val adapter = AddressRecyclerViewAdapter(usersAddresses)
        savedAddressRecyclerView.adapter = adapter

        nameInput.setText(ApplicationSettings.username)
        checkBox.isChecked = ApplicationSettings.saveAddresses
        saveButton.setOnClickListener {
            val editedName = nameInput.text.toString()
            ApplicationSettings.username = editedName
            ApplicationSettings.saveAddresses = checkBox.isChecked

            nameInput.clearFocus()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(nameInput.windowToken, 0)

            userReference.setValue(editedName).addOnSuccessListener {
                Toast.makeText(this, "Успішно редаговано", Toast.LENGTH_SHORT).show()
            }
        }

        deleteSavedAddresses.setOnClickListener {
            val addressesToDelete = adapter.getSelectedItems()

            val iterator = addressesToDelete.iterator()
            while(iterator.hasNext()){
                val address = iterator.next()
                dbAdapter.deleteAddressById(address.id)
                addressesReference.child(address.getUniqueId()).removeValue()
            }
        }
        backButton.setOnClickListener {
            finish()
        }
    }
}