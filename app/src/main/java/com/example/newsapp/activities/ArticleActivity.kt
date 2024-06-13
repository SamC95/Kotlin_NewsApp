package com.example.newsapp.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import com.example.newsapp.R
import com.example.newsapp.adapters.formatDateTime
import com.example.newsapp.data.APIRequests
import com.example.newsapp.data.UserDataManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL

class ArticleActivity : ComponentActivity() {
    private lateinit var article: APIRequests.Article

    private lateinit var navView: NavigationView
    private lateinit var navButton: ImageButton
    private lateinit var articleSource: TextView
    private lateinit var articleTitle: TextView
    private lateinit var articleDescription: TextView
    private lateinit var articleContents: TextView
    private lateinit var articleImage: ImageView
    private lateinit var articleDate: TextView
    private lateinit var articleUrl: TextView

    private lateinit var bookmarkButton: ImageButton
    private lateinit var backButton: Button
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navHeaderText: TextView

    private val userDataManager = UserDataManager()

    private val db = Firebase.firestore // Firestore database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_article)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        NavigationMenuListener.setListener(navView, this) // Sets up navigation menu buttons functionality

        val headerView = navView.getHeaderView(0)
        navHeaderText = headerView.findViewById(R.id.navHeaderText)

        // Must not be null
        article = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)  {
            intent.getParcelableExtra("articleData", APIRequests.Article::class.java)!!
        } else {
            intent.getParcelableExtra("articleData")!!
        }

        articleTitle = findViewById(R.id.articleTitle)
        articleDate = findViewById(R.id.articleDate)
        articleSource = findViewById(R.id.articleSource)
        articleImage = findViewById(R.id.articleImage)
        articleDescription = findViewById(R.id.articleDescription)
        articleContents = findViewById(R.id.articleContents)
        articleUrl = findViewById(R.id.articleUrlLink)

        bookmarkButton = findViewById(R.id.bookmarkButton)
        navButton = findViewById(R.id.nav_button)
        backButton = findViewById(R.id.backButton)
        navView = findViewById(R.id.nav_view)

        loadArticle() // Calls the function to load the data

        // Sets up the user greeting on the side bar for this activity
        userDataManager.setupGreeting(this, navHeaderText)

        navButton.setOnClickListener {
            drawerLayout.openDrawer(navView)
        } // Opens the side navigation menu

        backButton.setOnClickListener {
            finish()
        } // Ends the activity and returns to the prior activity

        bookmarkButton.setOnClickListener {
            saveArticle(article)
        } // Attempts to save the article into firestore when the bookmark button is pressed
    }

    // Load the data for the article that was passed to the activity
    @OptIn(DelicateCoroutinesApi::class)
    private fun loadArticle() {
        articleTitle.text = article.title

        val formattedDate = formatDateTime(article.publishDate)
        articleDate.text = formattedDate

        articleSource.text = getString(R.string.articleSource, article.sourceName)

        if (article.urlToImage != null) { // If the article has an image url associated with it
            GlobalScope.launch(Dispatchers.IO) {
                try { // Loads the image from the url outside the main thread
                    val url = article.urlToImage

                    val inputStream = URL(url).openStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)

                    launch(Dispatchers.Main) {
                        articleImage.setImageBitmap(bitmap) // Sets the image
                    }

                    inputStream.close() // Close the input stream once done using it
                }
                catch (error: Exception) {
                    Log.e(TAG, "Error retrieving image: $error")
                }
            }
        }
        else {
            articleImage.isVisible = false
        }

        if (article.description != "null") { // If article has a description, display it
            articleDescription.text = article.description
        }

        if (article.articleContent != "null") { // If article has contents, display it
            articleContents.text = article.articleContent
        }

        val text = "Read more at: ${article.url}"

        val spannableString = SpannableString(text)

        val urlStartIndex = text.indexOf(article.url)
        val urlEndIndex = urlStartIndex + article.url.length
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val urlLinkIntent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                startActivity(urlLinkIntent)
            }
        }

        // Sets the clickable portion of the text containing the URL
        spannableString.setSpan(clickableSpan, urlStartIndex, urlEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Sets the color of the URL text
        spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.backgroundPurple)),
            urlStartIndex, urlEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        articleUrl.text = spannableString
        articleUrl.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun saveArticle(articleDetails: APIRequests.Article) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid // Gets the user id to serve as the document id

        if (userId != null) { // Ensure that the uid is not empty
            val userDocRef = db.collection("userSavedArticles").document(userId) // Finds the document based on the uid

            userDocRef.update("articles", FieldValue.arrayUnion(articleDetails)) // Update the document with the article details
                .addOnSuccessListener {
                    Toast.makeText(baseContext, "Article Saved", Toast.LENGTH_SHORT).show() // Toast to show success
                }
                .addOnFailureListener { error ->
                    // If there is no document found by that user's uid
                    if (error is com.google.firebase.firestore.FirebaseFirestoreException &&
                        error.code == com.google.firebase.firestore.FirebaseFirestoreException.Code.NOT_FOUND) {

                        // Creates an initial hashMap to store the data in an arrayList
                        val initialData = hashMapOf(
                            "articles" to arrayListOf(articleDetails)
                        )
                        userDocRef.set(initialData) // Set the initial data into the firestore document
                            .addOnSuccessListener {
                                Toast.makeText(baseContext, "Article Saved", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { exception -> // Handles case where failed to save the article
                                Toast.makeText(baseContext, "Failed to save article", Toast.LENGTH_SHORT).show()
                                Log.e(TAG, "Error creating document: $exception")
                            }
                    }
                    else { // Handles the case where the error was something other than the specified Firestore exceptions
                        Toast.makeText(baseContext, "Failed to save article", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Error saving document: $error")
                    }
                }
        }
    }
}