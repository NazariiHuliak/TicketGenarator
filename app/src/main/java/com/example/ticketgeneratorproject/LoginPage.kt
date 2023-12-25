package com.example.ticketgeneratorproject

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginPage : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginInButton: Button

    private lateinit var registerNewAccountButton: TextView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page_layout)

        firebaseAuth = FirebaseAuth.getInstance()

        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        loginInButton = findViewById(R.id.login_btn)

        registerNewAccountButton = findViewById(R.id.create_new_account_btn)

        loginInButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            if(email.isNotEmpty() && password.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(this, "Ви успішно увійшли", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, HomePage::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Неправильний логін або пароль", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Всі поля повинні бути заповнені", Toast.LENGTH_SHORT).show()
            }
        }

        registerNewAccountButton.setOnClickListener {
            val intent = Intent(this, RegisterPage::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        if(firebaseAuth.currentUser!=null){
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
        }
    }
}