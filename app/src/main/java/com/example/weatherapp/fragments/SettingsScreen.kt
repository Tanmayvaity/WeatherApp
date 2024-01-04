package com.example.weatherapp.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.annotation.RequiresApi
import androidx.preference.PreferenceFragmentCompat
import com.example.weatherapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsScreen : PreferenceFragmentCompat() {


    private val onBackPressedCallback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            Log.d(TAG,"back pressed in settings screen")
            val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            requireActivity().supportFragmentManager.popBackStack()
            bottomNavView.visibility = View.VISIBLE

        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences,rootKey)

        requireActivity().onBackPressedDispatcher.addCallback(this,onBackPressedCallback)




    }

    companion object{
        const val TAG = "SettingsScreen"
    }


}