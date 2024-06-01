package com.example.newsapp

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class APIRequests {
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

    @OptIn(DelicateCoroutinesApi::class)
    fun topStoriesRequest(country: String, newsApiKey: String, callback: (MutableList<Article>) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
        val url = URL("https://newsapi.org/v2/top-headlines?country=$country&apiKey=$newsApiKey")

        with(url.openConnection() as HttpsURLConnection) {
            requestMethod = "GET"
            setRequestProperty("User-Agent", "Mozilla/126.0")

            val response = StringBuilder()
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                response.append(line).append("\n")
            }
            reader.close()

            val articles = parseArticles(response.toString())
            callback(articles)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun categoryRequests(country: String, categoryName: String, newsApiKey: String, callback: (MutableList<Article>) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val url = URL("https://newsapi.org/v2/top-headlines?country=$country&category=$categoryName&apiKey=$newsApiKey")

            with(url.openConnection() as HttpsURLConnection) {
                requestMethod = "GET"
                setRequestProperty("User-Agent", "Mozilla/126.0")

                val response = StringBuilder()
                val reader = BufferedReader(InputStreamReader(inputStream))
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    response.append(line).append("\n")
                }
                reader.close()

                val articles = parseArticles(response.toString())
                callback(articles)
            }
        }
    }

    fun storeArticles(articles: List<APIRequests.Article>) {
        val firestore = Firebase.firestore
        val articlesCollection = firestore.collection("newsArticles")

        for (article in articles) {
            val articleMap = hashMapOf(
                "sourceId" to article.sourceId,
                "sourceName" to article.sourceName,
                "author" to article.author,
                "title" to article.title,
                "description" to article.description,
                "url" to article.url,
                "urlToImage" to article.urlToImage,
                "publishedAt" to article.publishDate,
                "content" to article.articleContent
            )

            articlesCollection.add(articleMap)
                .addOnSuccessListener {
                    println("Article added with ID: ${it.id}")
                }
                .addOnFailureListener { error ->
                    println("Error adding article: $error")
                }
        }
    }
}

private fun parseArticles(jsonResponse: String): MutableList<APIRequests.Article> {
    val articles = mutableListOf<APIRequests.Article>()
    val jsonObject = JSONObject(jsonResponse)
    val jsonArray = jsonObject.getJSONArray("articles")

    for (i in 0 until jsonArray.length()) {
        val articleObject = jsonArray.getJSONObject(i)

        val sourceId = articleObject.getJSONObject("source").getString("id")
        val sourceName = articleObject.getJSONObject("source").getString("name")
        val author = articleObject.optString("author")
        val title = articleObject.getString("title")
        val description = articleObject.getString("description")
        val url = articleObject.getString("url")
        val urlToImage = articleObject.optString("urlToImage")
        val publishDate = articleObject.getString("publishedAt")
        val content = articleObject.optString("content")

        articles.add(
            APIRequests.Article(
                sourceId, sourceName, author, title, description,
                url, urlToImage, publishDate, content
            )
        )
    }
    return articles
}
