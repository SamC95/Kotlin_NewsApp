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
    private val db = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    fun getUserData(completion: (UserData?) -> Unit) {
        currentUserId?.let { uid ->
            val docRef = db.collection("users").document(uid)

            docRef.get()
                .addOnSuccessListener {
                    if (it != null) {
                        val firstName = it.getField<String>("firstName")
                        val surname = it.getField<String>("surname")
                        val emailAddress = it.getField<String>("email")

                        val userData = UserData(firstName, surname, emailAddress)

                        completion(userData)
                    }
                    else {
                        completion(null)
                    }
                }.addOnFailureListener { exception ->
                    Log.e("UserDataManager", "Error getting user data: $exception")
                    completion(null)
                }
        }
    }
}