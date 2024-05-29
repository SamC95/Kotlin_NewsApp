package com.example.newsapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : ComponentActivity() {
    private lateinit var emailTextField: EditText
    private lateinit var passwordTextField: EditText
    private lateinit var signUpRedirect: TextView
    private lateinit var loginBtn: Button
    private lateinit var googleLogin: ImageButton
    private lateinit var appleLogin: ImageButton
    private lateinit var ssoInfo: TextView
    private lateinit var errorMsg: TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailTextField = findViewById(R.id.emailAddressField)
        passwordTextField = findViewById(R.id.passwordField)
        signUpRedirect = findViewById(R.id.signUpRedirect)
        loginBtn = findViewById(R.id.loginButton)
        googleLogin = findViewById(R.id.googleLogin)
        appleLogin = findViewById(R.id.appleLogin)
        ssoInfo = findViewById(R.id.SSOInfo)
        errorMsg = findViewById(R.id.errorMsg)

        auth = Firebase.auth
        val ssoPopUpWindow = SSOPopUpWindow(this)

        // Shows pop up window with information for Single Sign On (See SSOPopUpWindow class)
        ssoInfo.setOnClickListener {
            ssoPopUpWindow.showPopUpWindow(it)
        }

        // Redirects to the Sign Up page
        signUpRedirect.setOnClickListener {
            val redirectIntent = Intent(this, AccCreationActivity::class.java)
            startActivity(redirectIntent)
        }

        loginBtn.setOnClickListener {
            // Checks validity of input fields (i.e., not empty)
            val inputValidation = checkInputFields(emailTextField, passwordTextField)

            if (inputValidation) { // If inputs are valid
                auth.signInWithEmailAndPassword( // Starts firebase sign in process using the given details
                    emailTextField.text.toString(),
                    passwordTextField.text.toString()
                )
                    .addOnCompleteListener {
                        if (it.isSuccessful) { // If the details are correct, successfully log in
                            Log.d(TAG, "signInWithEmail:success")
                            val user = auth.currentUser

                            Toast.makeText(baseContext, "User found", Toast.LENGTH_SHORT).show()
                        } else { // If details are not correct, display appropriate errors for user clarity
                            Log.w(TAG, "signInWithEmail:failure", it.exception)

                            emailTextField.setBackgroundResource(R.drawable.error_background)
                            passwordTextField.setBackgroundResource(R.drawable.error_background)

                            errorMsg.text = getString(R.string.no_account_found_by_these_details)
                        }
                    }
            }
        }

        googleLogin.setOnClickListener {

        }
    }

    // Checks the input fields to ensure that front-end input validation conditions are met
    private fun checkInputFields(emailAddressField: EditText, passwordField: EditText): Boolean {
        val emailInput = emailAddressField.text.toString().trim()
        val passwordInput = passwordField.text.toString().trim()

        when {
            emailInput.isEmpty() && passwordInput.isNotEmpty() -> { // If the email field is empty but the password field is not
                emailAddressField.setBackgroundResource(R.drawable.error_background)
                errorMsg.text = getString(R.string.email_address_field_is_empty)

                passwordField.setBackgroundResource(R.drawable.rounded_corner)

                return false
            }
            emailInput.isNotEmpty() && passwordInput.isEmpty() -> { // If the password field is empty but the email field is not
                passwordField.setBackgroundResource(R.drawable.error_background)
                errorMsg.text = getString(R.string.password_field_is_empty)

                emailAddressField.setBackgroundResource(R.drawable.rounded_corner)

                return false
            }
            emailInput.isEmpty() && passwordInput.isEmpty() -> { // If both fields are empty
                emailAddressField.setBackgroundResource(R.drawable.error_background)
                passwordField.setBackgroundResource(R.drawable.error_background)
                errorMsg.text = getString(R.string.email_and_password_fields_are_empty)

                return false
            }
            else -> { // If none of the above conditions are true, then the inputs are valid
                emailAddressField.setBackgroundResource(R.drawable.rounded_corner)
                passwordField.setBackgroundResource(R.drawable.rounded_corner)
                errorMsg.text = ""

                return true
            }
        }
    }
}