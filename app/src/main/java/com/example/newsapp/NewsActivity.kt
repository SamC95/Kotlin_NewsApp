package com.example.newsapp

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.input.key.KeyEvent
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
    private lateinit var noResultsText: TextView
    private lateinit var newsSearchBar: EditText

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
    private val countryCodeChecker = CountryCodeChecker()
    private val db = Firebase.firestore

    private val apiKey = BuildConfig.NEWS_API_KEY
    private var countryCode = "us" // Default case

    // Requests permission from user to use their location for regional news
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getLastLocation() // If location is allowed, parse the location and get news based on that
        } else {
            Log.d(TAG, "Location permission denied")

            loadTopStories("us", apiKey) // If region is not allowed, use default (US) location as a basis
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
        noResultsText = findViewById(R.id.noResults)
        newsSearchBar = findViewById(R.id.newsSearchBar)

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

        newsSearchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE
                || actionId == EditorInfo.IME_ACTION_GO
                || actionId == EditorInfo.IME_ACTION_SEARCH) {

                val userInput = newsSearchBar.text.toString()

                if (userInput != "") {
                    performSearch(apiKey, userInput)
                }

                true
            }
            else {
                false
            }
        }

        // Logout functionality
        logoutButton.setOnClickListener {
            Firebase.auth.signOut()

            val logoutIntent = Intent(this, MainActivity::class.java)
            startActivity(logoutIntent)
            finish()
        }
    }

    // Loads the top stories for a given country based on the location retrieved
    private fun loadTopStories(countryCode: String, apiKey: String) {
        apiRequests.topStoriesRequest(countryCode, apiKey) { response, errorMessage: String? ->
            runOnUiThread {
                if (errorMessage != null) { // If API limit has been reached
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
                else {
                    val articlesAdapter = ArticlesAdapter(response)
                    val newsRecyclerView: RecyclerView = findViewById(R.id.newsArticlesView)

                    newsRecyclerView.layoutManager = LinearLayoutManager(this)
                    newsRecyclerView.adapter = articlesAdapter

                    if (response.isEmpty()) { // If no results, display a message informing the user.
                        // Shouldn't happen in normal use of application for this function
                        noResultsText.text = getString(R.string.no_results_found)
                    }
                    else {
                        noResultsText.text = ""
                    }
                }
            }
        }
    }

    // Loads news articles for a specific search request
    private fun performSearch(apiKey: String, userInput: String) {
        apiRequests.searchRequests(apiKey, userInput) { response, errorMessage: String? ->
            runOnUiThread {
                if (errorMessage != null) { // If API limit has been reached
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
                else {
                    val articlesAdapter = ArticlesAdapter(response)
                    val newsRecyclerView: RecyclerView = findViewById(R.id.newsArticlesView)

                    newsRecyclerView.layoutManager = LinearLayoutManager(this)
                    newsRecyclerView.adapter = articlesAdapter

                    if (response.isEmpty()) { // If no results for search, display appropriate message to user
                        noResultsText.text = getString(R.string.no_results_found)
                    }
                    else {
                        noResultsText.text = ""
                    }
                }
            }
        }
    }

    // When one of the horizontal bar options for news type are selected
    override fun onItemClicked(position: Int, data: String) {
        if (data == "Top Stories") { // if top stories button is pressed, load top stories again
            loadTopStories(countryCode, apiKey)
        } else { // If any other button is pressed, category is selected
            apiRequests.categoryRequests(countryCode, data.lowercase(), apiKey) { response, errorMessage: String? -> // data is set to lowercase to match api fields
                runOnUiThread {
                    if (errorMessage != null) { // If API limit has been reached
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    }
                    else {
                        val articlesAdapter = ArticlesAdapter(response)
                        val newsRecyclerView: RecyclerView = findViewById(R.id.newsArticlesView)

                        newsRecyclerView.layoutManager = LinearLayoutManager(this)
                        newsRecyclerView.adapter = articlesAdapter

                        if (response.isEmpty()) { // If there are no results, display appropriate message
                            // Shouldn't happen in normal use of this application
                            noResultsText.text = getString(R.string.no_results_found)
                        }
                        else {
                            noResultsText.text = ""
                        }
                    }
                }
            }
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission( // Re-check location permission
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
                            geocoder.getFromLocation(location.latitude, location.longitude, 1) // Get address based on lat/lon of location

                        if (!addresses.isNullOrEmpty()) {
                            val country = addresses[0].countryName // Get the country name string from what geocoder provided

                            // Converts from country name to country code for supported regions, if region is unsupported then defaults to US
                            // For example, "United Kingdom" would return "gb" or China would return "cn"
                            countryCode = countryCodeChecker.checkCountryCode(country)

                            loadTopStories(countryCode, apiKey) // Load top stories based on the region retrieved

                        } else {
                            Log.d(TAG, "No address found for the provided coordinates.")

                            loadTopStories(countryCode, apiKey) // Load with default (US) data if no region found
                        }
                    } else {
                        Log.d(TAG, "Location is null")

                        loadTopStories(countryCode, apiKey) // Load with default (US) data if no region found
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error getting location: ${e.message}", e)

                    loadTopStories(countryCode, apiKey) // Load with default (US) data if no region found
                }
        }
    }

    // Used to check if fine or coarse location permission has been given
    private fun checkPermissions(): Boolean {
        return (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }
}