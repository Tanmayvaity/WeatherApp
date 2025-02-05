package com.example.weatherapp.models

import androidx.annotation.DrawableRes

data class Action(
    val topic:String,
    @DrawableRes
    val image:Int

)
