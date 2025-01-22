package com.example.weatherapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GeoCodingApiInstance {

    val geoCodingApi : GeoCodingApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeoCodingApi::class.java)
    }
}