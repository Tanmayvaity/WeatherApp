package com.example.weatherapp.models

import android.graphics.drawable.AdaptiveIconDrawable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

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
):Serializable

data class Wind(
    val speed:Double,
    @SerializedName("deg")
    val degree:Int,
    val gust:Double
):Serializable

data class Rain(
    @SerializedName("1h")
    val volumeForOneHour:Double?,
    @SerializedName("3h")
    val volumeForThreeHour: Double?
):Serializable
data class Cloud(
    @SerializedName("all")
    val cloudPercent:Int
):Serializable
data class Snow(
    @SerializedName("1h")
    val volumeForOneHour: Double?,
    @SerializedName("3h")
    val volumeFOrThreeHour:Double?
):Serializable

data class Sys(
    @SerializedName("country")
    val countryCode:String,
    val sunrise:String,
    val sunset:String
):Serializable
data class Coord(val lon:Double,val lat:Double):Serializable
data class WeatherX(
    val id:Int,
    @SerializedName("main")
    val weatherCondition:String,
    val description:String,
    val icon:String
):Serializable

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
):Serializable


