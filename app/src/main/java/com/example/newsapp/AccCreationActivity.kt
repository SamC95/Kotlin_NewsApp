package com.example.newsapp

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity

class AccCreationActivity : ComponentActivity() {
    private lateinit var placeholderText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        placeholderText = findViewById(R.id.placeholderText)
    }
}