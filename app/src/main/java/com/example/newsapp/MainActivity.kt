package com.example.newsapp

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.text.HtmlCompat

class MainActivity : ComponentActivity() {
    private lateinit var loginBtn: Button
    private lateinit var signUpBtn: Button
    private lateinit var icons8Text: TextView
    private lateinit var newsApiText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginBtn = findViewById(R.id.loginBtn)
        signUpBtn = findViewById(R.id.signUpBtn)
        icons8Text = findViewById(R.id.icons8)
        newsApiText = findViewById(R.id.newsapi)

        val icons8Html = getString(R.string.icons_by_icons8)
        val newsApiHtml = getString(R.string.news_data_from_newsapi)

        icons8Text.movementMethod = LinkMovementMethod.getInstance()
        newsApiText.movementMethod = LinkMovementMethod.getInstance()

        icons8Text.text = Html.fromHtml(icons8Html, Html.FROM_HTML_MODE_LEGACY)
        newsApiText.text = Html.fromHtml(newsApiHtml, Html.FROM_HTML_MODE_LEGACY)

//        @Composable
//        fun Greeting(name: String, modifier: Modifier = Modifier) {
//            Text(
//                text = "Hello $name!",
//                modifier = modifier
//            )
//        }
//
//        @Composable
//        fun GreetingPreview() {
//            NewsAppTheme {
//                Greeting("Android")
//            }
//        }
    }
}