package com.example.newsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NewsTypeAdapter(private val dataSet: List<String>) :
    RecyclerView.Adapter<NewsTypeAdapter.NewsTypeHolder>() {

    // ViewHolder class that holds references to the views for each item
    class NewsTypeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsTypeButton: TextView = itemView.findViewById(R.id.newsTypeButton)
    }

    // Create new views for the different news types
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsTypeHolder {
        // Create a new view, which defines the UI of the list item
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view, parent, false)

        return NewsTypeHolder(itemView)
    }

    // Replaces the view content with the list of strings from the data set
    override fun onBindViewHolder(holder: NewsTypeHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        holder.newsTypeButton.text = dataSet[position]
    }

    // Gets the size of the data set
    override fun getItemCount() = dataSet.size
}