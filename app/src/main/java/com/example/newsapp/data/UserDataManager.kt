package com.example.newsapp.data

import android.content.Context
import android.util.Log
import android.widget.TextView
import com.example.newsapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.getField
import java.util.Calendar

data class UserData(
    val firstName: String?,
    val surname: String?,
    val email: String?
)

class UserDataManager {
    private val db = FirebaseFirestore.getInstance() // Sets up the firestore database
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid // Gets the currently logged in user's id

    private val calendar: Calendar = Calendar.getInstance()
    private val currentHour = calendar.get(Calendar.HOUR_OF_DAY) // Gets the current hour based on the device clock

    private val navHeaderGreeting = when(currentHour) {
        in 0..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    } // Depending on the current time, alters the string to display an appropriate message for that time

    fun getUserData(completion: (UserData?) -> Unit) {
        currentUserId?.let { uid ->
            val docRef = db.collection("users").document(uid) // Get appropriate document from firebase based on uid

            docRef.get()
                .addOnSuccessListener {
                    if (it != null) { // If field is found via that uid, get the data
                        val firstName = it.getField<String>("firstName")
                        val surname = it.getField<String>("surname")
                        val emailAddress = it.getField<String>("email")

                        val userData = UserData(firstName, surname, emailAddress) // Store data into UserData object

                        completion(userData) // Returns userData
                    }
                    else {
                        completion(null) // Returns nothing if the document was not found
                    }
                }.addOnFailureListener { exception ->
                    Log.e("UserDataManager", "Error getting user data: $exception")
                    completion(null) // Handles error
                }
        }
    }

    // Handles setting up the user greeting on the side nav menu
    // For example, if the device time is 3PM then it should return "Good afternoon, <firstName>!"
    fun setupGreeting(context: Context, textView: TextView) {
        getUserData { userData ->
            if (userData?.firstName != null) { // If user data is retrieved
                val firstName = userData.firstName
                // Update text with appropriate name
                textView.text =
                    context.getString(R.string.navHeaderGreeting, navHeaderGreeting, firstName)
            }
            else {
                textView.text = context.getString(R.string.navHeaderGreetingNoName, navHeaderGreeting)
            }
        }
    }
}