package com.example.newsapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.example.newsapp.R

class ProfileActivity : ComponentActivity() {
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            val homeIntent = Intent(this, NewsActivity::class.java)
            startActivity(homeIntent)
        }
    }
}