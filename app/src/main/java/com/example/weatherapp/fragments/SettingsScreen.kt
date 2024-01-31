package com.example.weatherapp.fragments

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.preference.Preference
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback

import androidx.preference.PreferenceFragmentCompat
import com.example.weatherapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsScreen : PreferenceFragmentCompat() {

    private lateinit var aboutPreference: Preference
    private lateinit var developersInfoPreference: Preference


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