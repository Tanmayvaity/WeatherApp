package com.example.weatherapp.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.weatherapp.R

const val CURRENT_FRAGMENT_ID = "CURRENT_FRAGMENT_ID"
class MainActivityViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    var selectedFragment :Int
        get() = savedStateHandle[CURRENT_FRAGMENT_ID]?:R.id.location_screen
        set(value)  = savedStateHandle.set(CURRENT_FRAGMENT_ID,value)
}