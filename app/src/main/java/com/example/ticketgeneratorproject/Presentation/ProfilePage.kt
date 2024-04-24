package com.example.ticketgeneratorproject.Presentation

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ticketgeneratorproject.Business.Adapters.AddressRecyclerViewAdapter
import com.example.ticketgeneratorproject.Data.SQLiteController
import com.example.ticketgeneratorproject.Business.Controllers.ProfileController
import com.example.ticketgeneratorproject.databinding.ActivityProfilePageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfilePage : AppCompatActivity() {
    private lateinit var dbAdapter: SQLiteController
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var binding: ActivityProfilePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilePageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        dbAdapter = SQLiteController(this)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        val uid = firebaseAuth.currentUser!!.uid
        val userReference = firebaseDatabase.getReference("users").
            child(uid).child("name")
        val addressesReference = firebaseDatabase.getReference("users").
            child(uid).child("commonAddresses")

        val usersAddresses = dbAdapter.getAllAddresses().toMutableList()

        val layoutManager = LinearLayoutManager(this)
        binding.savedAddressRecyclerView.layoutManager = layoutManager
        binding.savedAddressRecyclerView.setHasFixedSize(true)
        val adapter = AddressRecyclerViewAdapter(usersAddresses)
        binding.savedAddressRecyclerView.adapter = adapter

        binding.name.setText(ProfileController.username)
        binding.saveAddresses.isChecked = ProfileController.saveAddresses
        binding.saveButton.setOnClickListener {
            val editedName = binding.name.text.toString()
            ProfileController.username = editedName
            ProfileController.saveAddresses = binding.saveAddresses.isChecked

            binding.name.clearFocus()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.name.windowToken, 0)

            userReference.setValue(editedName).addOnSuccessListener {
                Toast.makeText(this, "Успішно редаговано", Toast.LENGTH_SHORT).show()
            }
        }

        binding.deleteSavedAddresses.setOnClickListener {
            val addressesToDelete = adapter.getSelectedItems()

            val iterator = addressesToDelete.iterator()
            while(iterator.hasNext()){
                val address = iterator.next()
                dbAdapter.deleteAddressById(address.id)
                addressesReference.child(address.getUniqueId()).removeValue()
            }
        }
        binding.goBackButton.setOnClickListener {
            finish()
        }
    }
}