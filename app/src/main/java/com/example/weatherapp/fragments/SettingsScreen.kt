package com.example.weatherapp.fragments

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.preference.Preference
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels

import androidx.preference.PreferenceFragmentCompat
import com.example.weatherapp.R
import com.example.weatherapp.viewmodels.MainActivityViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsScreen : PreferenceFragmentCompat() {

    private lateinit var aboutPreference: Preference
    private lateinit var developersInfoPreference: Preference



    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences,rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        aboutPreference = findPreference<Preference>("about")!!
        developersInfoPreference = findPreference<Preference>("devs_about")!!
        aboutPreference.setOnPreferenceClickListener(object: Preference.OnPreferenceClickListener{
            override fun onPreferenceClick(preference: Preference): Boolean {
                startIntentFromUrl("https://github.com/Tanmayvaity/WeatherApp")
//                val bottomSheetDialog = BottomSheetFragment()
//                val fm = activity?.supportFragmentManager
//
//                bottomSheetDialog.show(fm!!,"about")

                return true
            }
        })


        developersInfoPreference.setOnPreferenceClickListener(object: androidx.preference.Preference.OnPreferenceClickListener{
            override fun onPreferenceClick(preference: Preference): Boolean {
                startIntentFromUrl("https://github.com/Tanmayvaity/")
                return true
            }
        })
    }

    private fun startIntentFromUrl(url:String){
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = uri
        startActivity(intent)
    }

    companion object{
        const val TAG = "SettingsScreen"
    }


}