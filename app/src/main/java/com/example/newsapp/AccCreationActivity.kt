package com.example.newsapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity

class AccCreationActivity : ComponentActivity() {
    private lateinit var headerText: TextView
    private lateinit var loginRedirect: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        headerText = findViewById(R.id.headerText)
        loginRedirect = findViewById(R.id.loginRedirect)

        loginRedirect.setOnClickListener {
            val redirectIntent = Intent(this, LoginActivity::class.java)
            startActivity(redirectIntent)
        }
    }
}