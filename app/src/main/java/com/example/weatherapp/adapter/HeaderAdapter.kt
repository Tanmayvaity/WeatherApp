package com.example.weatherapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R

class HeaderAdapter(
    val headerText: String
) : RecyclerView.Adapter<HeaderAdapter.ViewHolder>() {
    inner class ViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHeaderText = itemView.findViewById<TextView>(R.id.tv_top_results)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.locations_header_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvHeaderText.text = headerText.toString()
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.locations_header_item
    }

}