package com.example.weatherapp.fragments

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.preference.PreferenceFragmentCompat
import com.example.weatherapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsScreen : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences,rootKey)


    }

}