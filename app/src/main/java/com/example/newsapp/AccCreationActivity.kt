package com.example.newsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class AccCreationActivity : ComponentActivity() {
    private lateinit var headerText: TextView
    private lateinit var loginRedirect: TextView
    private lateinit var firstNameField: EditText
    private lateinit var firstNameError: TextView
    private lateinit var surnameField: EditText
    private lateinit var surnameError: TextView
    private lateinit var emailAddressField: EditText
    private lateinit var emailAddressError: TextView
    private lateinit var passwordField: EditText
    private lateinit var passwordError: TextView
    private lateinit var confirmPassField: EditText
    private lateinit var confirmPassError: TextView
    private lateinit var createButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        headerText = findViewById(R.id.headerText)
        loginRedirect = findViewById(R.id.loginRedirect)
        firstNameField = findViewById(R.id.firstNameField)
        firstNameError = findViewById(R.id.firstNameError)
        surnameField = findViewById(R.id.surnameField)
        surnameError = findViewById(R.id.surnameError)
        emailAddressField = findViewById(R.id.emailAddressField)
        emailAddressError = findViewById(R.id.emailAddressError)
        passwordField = findViewById(R.id.passwordField)
        passwordError = findViewById(R.id.passwordError)
        confirmPassField = findViewById(R.id.confirmPasswordField)
        confirmPassError = findViewById(R.id.confirmPasswordError)
        createButton = findViewById(R.id.createButton)

        // Redirects to the log in page
        loginRedirect.setOnClickListener {
            val redirectIntent = Intent(this, LoginActivity::class.java)
            startActivity(redirectIntent)
        }

        createButton.setOnClickListener {
            val firstNameValid = validateFirstName(firstNameField)
            val surnameValid = validateSurname(surnameField)
            val emailValid = validateEmailAddress(emailAddressField)
            val passwordValid = validatePassword(passwordField, confirmPassField)

            if (firstNameValid && surnameValid && emailValid && passwordValid) {
                Toast.makeText(this, "All inputs are valid", Toast.LENGTH_SHORT).show()
            } // Temporary toast for checking validation is working as intended
        }
    }

    // Validates the first name input
    private fun validateFirstName(firstNameField: EditText): Boolean {
        val firstNameInput = firstNameField.text.toString().trim()

        when {
            firstNameInput.isEmpty() -> { // Checks if field is empty
                firstNameField.setBackgroundResource(R.drawable.error_background)
                firstNameError.text = getString(R.string.required)

                return false
            }
            firstNameInput.length < 2 -> { // Checks if field is less than 2 characters
                firstNameField.setBackgroundResource(R.drawable.error_background)
                firstNameError.text = getString(R.string.too_short)

                return false
            }
            else -> { // If neither is true, then input is valid
                firstNameField.setBackgroundResource(R.drawable.rounded_corner)
                firstNameError.text = ""

                return true
            }
        }
    }

    // Validates surname input
    private fun validateSurname(surnameField: EditText): Boolean {
        val surnameInput = surnameField.text.toString().trim()

        when {
            surnameInput.isEmpty() -> { // Checks if field is empty
                surnameField.setBackgroundResource(R.drawable.error_background)
                surnameError.text = getString(R.string.required)

                return false
            }
            surnameInput.length < 2 -> { // Checks if field is less than 2 characters
                surnameField.setBackgroundResource(R.drawable.error_background)
                surnameError.text = getString(R.string.too_short)

                return false
            }
            else -> { // If neither are true, input is valid
                surnameField.setBackgroundResource(R.drawable.rounded_corner)
                surnameError.text = ""

                return true
            }
        }
    }

    // Validates email input
    private fun validateEmailAddress(emailAddressField: EditText): Boolean {
        val emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}" // Regex pattern for email validation
        val emailAddress = emailAddressField.text.toString().trim()

        when {
            emailAddress.isEmpty() -> { // Checks if field is empty
                emailAddressField.setBackgroundResource(R.drawable.error_background)
                emailAddressError.text = getString(R.string.required)

                return false
            }
            !emailAddress.matches(emailPattern.toRegex()) -> { // Checks if input passes Regex check in emailPattern
                emailAddressField.setBackgroundResource(R.drawable.error_background)
                emailAddressError.text = getString(R.string.invalid_email)

                return false
            }
            else -> { // If neither are true, then input is valid
                emailAddressField.setBackgroundResource(R.drawable.rounded_corner)
                emailAddressError.text = ""

                return true
            }
        }
    }

    // Validates the password and confirm password fields input
    private fun validatePassword(passwordField: EditText, confirmPassField: EditText): Boolean {
        val passwordInput = passwordField.text.toString().trim()
        val confirmPassInput = confirmPassField.text.toString().trim()

        when {
            passwordInput.length < 8 || confirmPassInput.length < 8 -> { // If either of the fields are too short
                if (passwordInput.length < 8) { // When first field is too short
                    passwordField.setBackgroundResource(R.drawable.error_background)
                    passwordError.text = getString(R.string.too_short)
                }
                else { // When first field is not too short
                    passwordField.setBackgroundResource(R.drawable.rounded_corner)
                    passwordError.text = ""
                }

                if (confirmPassInput.length < 8) { // When second field is too short
                    confirmPassField.setBackgroundResource(R.drawable.error_background)
                    confirmPassError.text = getString(R.string.too_short)
                }
                else { // When second field is not too short
                    confirmPassField.setBackgroundResource(R.drawable.rounded_corner)
                    confirmPassError.text = ""
                }

                return false
            }
            passwordInput.isEmpty() && confirmPassInput.isNotEmpty() -> { // If first field is empty and second field is not
                passwordField.setBackgroundResource(R.drawable.error_background)
                confirmPassField.setBackgroundResource(R.drawable.rounded_corner)
                passwordError.text = getString(R.string.required)
                confirmPassError.text = ""

                return false
            }
            passwordInput.isNotEmpty() && confirmPassInput.isEmpty() -> { // If second field is empty and first field is not
                passwordField.setBackgroundResource(R.drawable.rounded_corner)
                confirmPassField.setBackgroundResource(R.drawable.error_background)
                passwordError.text = ""
                confirmPassError.text = getString(R.string.required)

                return false
            }
            passwordInput.isEmpty() && confirmPassInput.isEmpty() -> { // If both fields are empty
                passwordField.setBackgroundResource(R.drawable.error_background)
                confirmPassField.setBackgroundResource(R.drawable.error_background)
                passwordError.text = getString(R.string.required)
                confirmPassError.text = getString(R.string.required)

                return false
            }
            passwordInput != confirmPassInput -> { // If inputs do not match
                passwordField.setBackgroundResource(R.drawable.error_background)
                confirmPassField.setBackgroundResource(R.drawable.error_background)
                passwordError.text = getString(R.string.password_must_match)
                confirmPassError.text = getString(R.string.password_must_match)

                return false
            }
            else -> { // If none of these conditions are true, then input is valid
                passwordField.setBackgroundResource(R.drawable.rounded_corner)
                confirmPassField.setBackgroundResource(R.drawable.rounded_corner)
                passwordError.text = ""
                confirmPassError.text = ""

                return true
            }
        }
    }
}