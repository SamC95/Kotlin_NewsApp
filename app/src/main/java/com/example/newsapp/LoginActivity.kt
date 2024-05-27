package com.example.newsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.ComponentActivity

class LoginActivity : ComponentActivity() {
    private lateinit var emailTextField: EditText
    private lateinit var passwordTextField: EditText
    private lateinit var signUpRedirect: TextView
    private lateinit var loginBtn: Button
    private lateinit var googleLogin: ImageButton
    private lateinit var appleLogin: ImageButton
    private lateinit var ssoInfo: TextView

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

        val ssoPopUpWindow = SSOPopUpWindow(this)

        ssoInfo.setOnClickListener {
            ssoPopUpWindow.showPopUpWindow(it)
        }

        signUpRedirect.setOnClickListener {
            val redirectIntent = Intent(this, AccCreationActivity::class.java)
            startActivity(redirectIntent)
        }
    }
}