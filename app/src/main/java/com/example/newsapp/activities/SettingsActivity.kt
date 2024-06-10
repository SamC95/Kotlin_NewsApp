package com.example.newsapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.newsapp.R
import com.example.newsapp.data.UserDataManager
import com.google.android.material.navigation.NavigationView

class SettingsActivity : ComponentActivity() {
    private lateinit var backButton: Button

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navHeaderText: TextView
    private lateinit var navView: NavigationView
    private lateinit var navButton: ImageButton

    private val userDataManager = UserDataManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        NavigationMenuListener.setListener(navView, this) // Sets up navigation menu buttons functionality

        val headerView = navView.getHeaderView(0)
        navHeaderText = headerView.findViewById(R.id.navHeaderText)
        navButton = findViewById(R.id.nav_button)

        backButton = findViewById(R.id.backButton)

        // Sets up the user greeting on the side bar for this activity
        userDataManager.setupGreeting(this, navHeaderText)

        navButton.setOnClickListener {
            drawerLayout.openDrawer(navView)
        }

        backButton.setOnClickListener {
            val homeIntent = Intent(this, NewsActivity::class.java)
            startActivity(homeIntent)
        }
    }
}