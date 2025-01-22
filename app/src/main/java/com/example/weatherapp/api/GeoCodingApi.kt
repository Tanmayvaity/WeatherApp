package com.example.weatherapp.api

import com.example.weatherapp.models.GeoCoding
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoCodingApi {

    @GET("/geo/1.0/direct")
    suspend fun getCoordinates(
        @Query("q") cityName:String,
        @Query("limit") limit : Int = 5,
        @Query("appid") apiKey:String
    ): Response<List<GeoCoding>>
}