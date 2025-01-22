package com.example.weatherapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.models.GeoCoding

class LocationAdapter(
    val locations : List<GeoCoding>,
    val context : Context,
    val locationItemClickListener: LocationItemClickListener
) : RecyclerView.Adapter<LocationAdapter.ViewHolder>() {


    interface LocationItemClickListener{
        fun onclick(location:GeoCoding)
    }
    inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val tvLocation = itemView.findViewById<TextView>(R.id.tv_location_name)
        val tvLatLong = itemView.findViewById<TextView>(R.id.tv_lat_lang_name)
        val tvCountry = itemView.findViewById<TextView>(R.id.tv_country)
        val tvState = itemView.findViewById<TextView>(R.id.tv_state)


        init {
            itemView.setOnClickListener {
                if(locationItemClickListener!=null){
                    val position = adapterPosition
                    if (position!=RecyclerView.NO_POSITION){
                        locationItemClickListener.onclick(location = locations[position])
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.location_item,parent,false)
        return ViewHolder(contactView)
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = locations[position]

        holder.tvLocation.text = location.name.toString()
        val lat = String.format("%.2f",location.lat)
        val lon = String.format("%.2f",location.lon)

        holder.tvLatLong.text = "${lat},${lon}"
        holder.tvCountry.text = location.country.toString()
        holder.tvState.text = location.state.toString()
        Log.d("Recycler","${location.lat.toString()}")


    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.location_item
    }
}