package com.example.weatherapp.models

import java.io.Serializable

data class GeoCoding(
    val name:String,
    val lat : Double,
    val lon : Double,
    val country : String,
    val state : String?
): Serializable


