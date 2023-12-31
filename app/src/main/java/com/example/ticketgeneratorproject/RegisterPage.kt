package com.example.ticketgeneratorproject

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.ticketgeneratorproject.databinding.RegisterPageLayoutBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterPage : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference

    private lateinit var nameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var passwordConfirmInput: TextInputEditText

    private lateinit var registerButton: Button
    private lateinit var signInButton: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_page_layout)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        usersRef = firebaseDatabase.getReference("users")



        nameInput = findViewById(R.id.name_input)
        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        passwordConfirmInput = findViewById(R.id.password_confirm_input)

        registerButton = findViewById(R.id.register_btn)
        signInButton = findViewById(R.id.sign_in)

        registerButton.setOnClickListener {
            val name = nameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val passwordConfirm = passwordConfirmInput.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && passwordConfirm.isNotEmpty()){
                if(password == passwordConfirm){
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if(it.isSuccessful){
                                val uid = firebaseAuth.currentUser!!.uid
                                usersRef.child(uid).child("name").setValue(name)
                                val intent = Intent(this, LoginPage::class.java)
                                startActivity(intent)
                            }
                        }
                        .addOnFailureListener { exception ->
                            if (exception is FirebaseAuthUserCollisionException) {
                                Toast.makeText(this, "Акаунт з такою поштою вже існує", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Помилка при реєстрації: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Паролі не збігаються", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Всі поля повинні бути заповнені", Toast.LENGTH_SHORT).show()
            }
        }
        signInButton.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }
    }
}