package com.example.newsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ArticlesAdapter(private val articles: List<APIRequests.Article>) : RecyclerView.Adapter<ArticlesAdapter.ArticleViewHolder>() {


    class ArticleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sourceName: TextView = view.findViewById(R.id.sourceName)
        val title: TextView = view.findViewById(R.id.title)
        val description: TextView = view.findViewById(R.id.description)
        val urlToImage: ImageView = view.findViewById(R.id.urlToImage)
        val publishedAt: TextView = view.findViewById(R.id.publishedAt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.sourceName.text = article.sourceName
        holder.title.text = article.title

        if (article.description != "null") {
            holder.description.text = article.description
        }
        else {
            holder.description.text = ""
        }

        holder.publishedAt.text = formatDateTime(article.publishDate)

        if (article.urlToImage != "null") {
            holder.urlToImage.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(article.urlToImage)
                .into(holder.urlToImage)
            holder.itemView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        else {
            holder.urlToImage.visibility = View.GONE
            holder.urlToImage.layoutParams.height = 0
        }
    }

    override fun getItemCount() = articles.size
}

fun formatDateTime(dateTimeString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss", Locale.getDefault())
    val date = inputFormat.parse(dateTimeString)
    return outputFormat.format(date ?: Date())
}