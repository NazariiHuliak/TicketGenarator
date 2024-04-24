package com.example.ticketgeneratorproject.Presentation

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ticketgeneratorproject.databinding.RegisterPageLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase

class RegisterPage : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var binding: RegisterPageLayoutBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterPageLayoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        val usersRef = firebaseDatabase.getReference("users")

        binding.registerButton.setOnClickListener {
            val name = binding.username.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val passwordConfirm = binding.passwordConfirmation.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && passwordConfirm.isNotEmpty()) {
                if (password == passwordConfirm) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val uid = firebaseAuth.currentUser!!.uid
                                usersRef.child(uid).child("name").setValue(name)
                                val intent = Intent(this, LoginPage::class.java)
                                startActivity(intent)
                            }
                        }
                        .addOnFailureListener { exception ->
                            if (exception is FirebaseAuthUserCollisionException) {
                                Toast.makeText(
                                    this,
                                    "Акаунт з такою поштою вже існує",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Помилка при реєстрації: ${exception.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Паролі не збігаються", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Всі поля повинні бути заповнені", Toast.LENGTH_SHORT).show()
            }
        }
        binding.loginButton.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }
    }
}