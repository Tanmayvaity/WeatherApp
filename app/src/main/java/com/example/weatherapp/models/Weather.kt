package com.example.weatherapp.models

import android.graphics.drawable.AdaptiveIconDrawable
import com.google.gson.annotations.SerializedName

data class Weather(
    val coord : Coord,
    val weather:List<WeatherX>,
    val base : String,
    val main : Main,
    val visibility : Int,
    val wind : Wind,
    val rain : Rain,
    val clouds : Cloud,
    val snow : Snow,
    val dt : Long,
    val sys:Sys,
    val timezone:Int,
    val id : Long,
    val name : String,
    val cod:Int
)

data class Wind(
    val speed:Double,
    @SerializedName("deg")
    val degree:Int,
    val gust:Double
)

data class Rain(
    @SerializedName("1h")
    val volumeForOneHour:Double?,
    @SerializedName("3h")
    val volumeForThreeHour: Double?
)
data class Cloud(
    @SerializedName("all")
    val cloudPercent:Int
)
data class Snow(
    @SerializedName("1h")
    val volumeForOneHour: Double?,
    @SerializedName("3h")
    val volumeFOrThreeHour:Double?
)

data class Sys(
    @SerializedName("country")
    val countryCode:String,
    val sunrise:String,
    val sunset:String
)
data class Coord(val lon:Double,val lat:Double)
data class WeatherX(
    val id:Int,
    @SerializedName("main")
    val weatherCondition:String,
    val description:String,
    val icon:String
)

data class Main(
    val temp : Double,
    @SerializedName("feels_like")
    val feelsLike:Double,
    @SerializedName("temp_min")
    val tempMin:Double,
    @SerializedName("temp_max")
    val tempMax : Double,
    val pressure: Int,
    val humidity : Int,
    @SerializedName("sea_level")
    val seaLevel:Int,
    @SerializedName("grnd_level")
    val groundLevel:Int
)


