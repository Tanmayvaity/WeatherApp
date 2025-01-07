package com.example.weatherapp.viewmodels

import androidx.lifecycle.ViewModel
import com.example.weatherapp.models.Weather

class WeatherFragmentViewModel : ViewModel() {
    var weather : Weather? = null
}