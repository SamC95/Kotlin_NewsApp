package com.example.newsapp

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.getField
import java.util.Locale

class NewsActivity : ComponentActivity(), NewsTypeAdapter.OnItemClickListener {
    private lateinit var logoutButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var fusedLocationClient: FusedLocationProviderClient

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

    private val apiKey = BuildConfig.NEWS_API_KEY

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, proceed with your logic
            getLastLocation()
        } else {
            // Permission denied, handle accordingly
            Log.d(TAG, "Location permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newsfeed)


        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        val newsTypeAdapter = NewsTypeAdapter(dataSet, this)
        recyclerView.adapter = newsTypeAdapter
        logoutButton = findViewById(R.id.logoutButton)

        // Sets up firebase authentication
        auth = Firebase.auth

        // Gets the user's location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Check for permissions and request if not granted
        if (checkPermissions()) {
            // Permissions are already granted, so call getLastLocation
            getLastLocation()
        } else {
            // Request permissions using the new API
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // Loads the initial top stories on Activity start
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

    override fun onItemClicked(position: Int, data: String) {
        if (data == "Top Stories") {
            loadTopStories(apiKey)
        } else {
            apiRequests.categoryRequests("us", data.lowercase(), apiKey) { response ->
                runOnUiThread {
                    val articlesAdapter = ArticlesAdapter(response)
                    val newsRecyclerView: RecyclerView = findViewById(R.id.newsArticlesView)

                    newsRecyclerView.layoutManager = LinearLayoutManager(this)
                    newsRecyclerView.adapter = articlesAdapter
                }
            }
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val addresses =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)

                        if (!addresses.isNullOrEmpty()) {
                            val country = addresses[0].countryName
                            Log.d(TAG, "Country: $country")
                        } else {
                            Log.d(TAG, "No address found for the provided coordinates.")
                        }
                    } else {
                        Log.d(TAG, "Location is null")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error getting location: ${e.message}", e)
                }
        }
    }

    private fun checkPermissions(): Boolean {
        return (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }
}