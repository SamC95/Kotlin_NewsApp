package com.example.newsapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.getField

class NewsActivity : ComponentActivity() {
    private lateinit var logoutButton: Button

    private lateinit var auth: FirebaseAuth

    private val dataSet = listOf(
        "Top Stories", "Business", "General", "Health", "Entertainment",
        "Science", "Sport", "Technology"
    )

    data class Article(
        val sourceId: String?,
        val sourceName: String,
        val author: String?,
        val title: String,
        val description: String,
        val url: String,
        val urlToImage: String?,
        val publishDate: String,
        val articleContent: String?
    )

    private val apiRequests = APIRequests()
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newsfeed)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        val newsTypeAdapter = NewsTypeAdapter(dataSet)
        recyclerView.adapter = newsTypeAdapter
        logoutButton = findViewById(R.id.logoutButton)

        auth = Firebase.auth

        val apiKey = BuildConfig.NEWS_API_KEY

        loadTopStories(apiKey)

        // Gets user data from Firestore
        val docRef = db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)

        docRef.get() // Test code that correct user is logged in
            .addOnSuccessListener {
                if (it != null) {
                    Log.d(TAG, "${it.id} => ${it.data}")

                    val firstName = it.getField<String>("firstName")
                    val surname = it.getField<String>("surname")

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

    private fun loadTopStories(apiKey: String) {
        apiRequests.topStoriesRequest("us", apiKey) { response ->
            runOnUiThread {
                val articlesAdapter = ArticlesAdapter(response)
                val newsRecyclerView: RecyclerView = findViewById(R.id.newsArticlesView)

                newsRecyclerView.layoutManager = LinearLayoutManager(this)
                newsRecyclerView.adapter = articlesAdapter
            }
        }
    }
}