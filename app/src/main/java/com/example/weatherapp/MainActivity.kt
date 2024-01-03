package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.fragments.LocationFragment
import com.example.weatherapp.fragments.SettingsFragment
import com.example.weatherapp.fragments.SettingsScreen
import com.example.weatherapp.fragments.WeatherFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding


    companion object{
        const val TAG = "MainActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.bottomNavView.setOnItemSelectedListener {
            fragmentNavigationLogic(it)
        }
        binding.bottomNavView.selectedItemId = R.id.location_screen

    }

    private fun fragmentNavigationLogic(item: MenuItem):Boolean{
        val fragmentManager = supportFragmentManager
        lateinit var  fragment : Fragment
        when(item.itemId){
            R.id.location_screen ->{
                fragment = LocationFragment.newInstance()

            }
            R.id.weather_screen ->{
                Log.d(TAG,"weather")
                fragment = WeatherFragment.newInstance()

            }
        }

        fragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).commit()
        return true
    }
}