package com.example.newsapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.getField

class NewsActivity : ComponentActivity() {
    private lateinit var testString: TextView
    private lateinit var logoutButton: Button

    private lateinit var auth: FirebaseAuth

    private val dataSet = listOf("Top Stories", "Election", "Business", "Climate", "Science & Tech",
                                "Sport", "Entertainment", "Art")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newsfeed)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        val newsTypeAdapter = NewsTypeAdapter(dataSet)
        recyclerView.adapter = newsTypeAdapter

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

    class NewsTypeAdapter(private val dataSet: List<String>) : RecyclerView.Adapter<NewsTypeAdapter.NewsTypeHolder>() {

        // ViewHolder class that holds references to the views for each item
        class NewsTypeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val newsTypeButton: TextView = itemView.findViewById(R.id.newsTypeButton)
        }

        // Create new views for the different news types
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsTypeHolder {
            // Create a new view, which defines the UI of the list item
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_view, parent, false)

            return NewsTypeHolder(itemView)
        }

        // Replaces the view content with the list of strings from the data set
        override fun onBindViewHolder(holder: NewsTypeHolder, position: Int) {
            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            holder.newsTypeButton.text = dataSet[position]
        }

        // Gets the size of the data set
        override fun getItemCount() = dataSet.size
    }
}