package com.example.newsapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.getField

class NewsActivity : ComponentActivity() {
    private lateinit var testString: TextView
    private lateinit var logoutButton: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newsfeed)

        testString = findViewById(R.id.testString)
        logoutButton = findViewById(R.id.logoutButton)

        auth = Firebase.auth
        val db = Firebase.firestore

        val apiKey = BuildConfig.NEWS_API_KEY

        // Gets user data from Firestore
        val docRef = db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)

        docRef.get() // Test code that correct user is logged in
            .addOnSuccessListener {
                if (it != null) {
                    Log.d(TAG, "${it.id} => ${it.data}")

                    val firstName = it.getField<String>("firstName")
                    val surname = it.getField<String>("surname")

                    testString.text = """You are logged in as $firstName $surname"""
                } // Temporary Code
            }

        // Logout functionality
        logoutButton.setOnClickListener {
            Firebase.auth.signOut()

            val logoutIntent = Intent(this, MainActivity::class.java)
            startActivity(logoutIntent)
            finish()
        }
    }
}