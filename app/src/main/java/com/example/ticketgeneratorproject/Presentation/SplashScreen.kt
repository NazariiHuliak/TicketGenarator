package com.example.ticketgeneratorproject.Presentation

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.ticketgeneratorproject.R
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private lateinit var intent: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        val firebaseAuth = FirebaseAuth.getInstance()
        Handler().postDelayed({
            if(firebaseAuth.currentUser != null){
                intent = Intent(this, HomePage::class.java)
            } else {
                intent = Intent(this, LoginPage::class.java)
            }
            startActivity(intent)
            finish()
        }, 300)
    }
}