package com.example.newsapp.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.getField

data class UserData(
    val firstName: String?,
    val surname: String?,
    val email: String?
)

class UserDataManager {
    private val db = FirebaseFirestore.getInstance() // Sets up the firestore database
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid // Gets the currently logged in user's id

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
}